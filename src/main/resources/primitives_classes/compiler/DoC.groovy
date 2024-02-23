/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The "DoC" class is an integral part of the compilation process of the Forth-like language interpreter.
 * This class extends from "AbstractCompilerDirective" which forms the foundation for all the compiler directive classes.
 *
 * The compile-time mechanism analyzes and translates the defined Forth terms during the parsing process.
 *
 * Our test cases included:
 * : test 10 0 do cr ." hello " loop ;
*/

package primitives_classes.compiler

import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.CompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The 'DoC' class extends the 'AbstractCompilerDirective' super class.
 * This compiler directive class handles the logic of the 'DO' keyword at compile time in the interpreter.
 */
class DoC extends AbstractCompilerDirective {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    ForthCompiler compiler
    ForthInterpreter interpreter

/**
 * The `execute` method is responsible for performing the compile-time operations for 'DO' in the Forth compiler.
 *
 * @param newWord The word that is being compiled.
 * @param compiler The ForthCompiler instance.
 * @param interpreter The ForthInterpreter instance.
 * @return Boolean indicating if a new line needs to be printed or not in the Forth REPL.
 * @exception ForthCompilerException if there's no matching 'IF' or 'THEN' for 'ELSE'.
 */
    @Override
    Object execute(Word word, ForthCompiler compiler, ForthInterpreter interpreter) {
        // Fail Fast
        if (!compiler.tokens.contains("loop") && !compiler.tokens.contains("+loop")) {
            interpreter.words.clear()
            throw new ForthCompilerException("No matching 'loop' or '+loop'")
        }

        ConcurrentLinkedQueue<Word> words = interpreter.words
        Word nextWord = null

        try {
            compiler.forthWordsBuffer.add(word.name)
            while (!compiler.tokens.isEmpty()) {
                String token = compiler.tokens.poll()
                if (token == "loop") {
                    compiler.forthWordsBuffer.add(token)
                    break
                }

                if (token == "+loop") {
                    compiler.forthWordsBuffer.add(token)
                    break
                }

                nextWord = compiler.wordService.findByName(token)
                if (nextWord != null) {
                    // if nextWord has a defined compiler directive we need to insure it's executed as well.
                    if (nextWord.compileClass != null && !nextWord.compileClass.isEmpty() && !nextWord.compileClass.isBlank()) {
                        def classLoader = new GroovyClassLoader()
                        Class groovyClass = classLoader.parseClass(nextWord.compileClass)
                        CompilerDirective compileTime = groovyClass.getDeclaredConstructor().newInstance() as CompilerDirective
                        Boolean output = compileTime.execute(nextWord, compiler, interpreter)
                        continue
                    }
                    compiler.forthWordsBuffer.add(nextWord.name)
                } else if (compiler.canParseToInt(token)) {
                    compiler.compileIntegerLiteral(token)
                }
            }
        } catch (Exception e) {
            throw new ForthCompilerException("${this.class.simpleName} failed.", e)
        }
        return false
    }
}
