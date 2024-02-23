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

package primitives_classes.compiler

import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.CompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter

import java.util.concurrent.ConcurrentLinkedQueue

class WhileC extends AbstractCompilerDirective {

    /**
     * Execute a compiler Directive on a word.
     * @param newWord The new word we are creating.
     * @param compiler The FORTH compiler.
     * @param interpreter The FORTH interpreter.
     * @return an arbitrary `anything`. Usually null or Boolean false.
     */
    @Override
    Object execute(Word word, ForthCompiler compiler, ForthInterpreter interpreter) {
        ConcurrentLinkedQueue<Word> words = interpreter.words
        Word nextWord = null

        if (!compiler.tokens.contains("repeat")) {
            interpreter.words.clear()
            throw new ForthCompilerException("No matching 'REPEAT'")
        }

        try {
            compiler.forthWordsBuffer.add(word.name)
            while (!compiler.tokens.isEmpty()) {
                String token = compiler.tokens.poll()
                if (token == "repeat") {
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
