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
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter

import javax.transaction.Transactional

class VariableC extends AbstractCompilerDirective {

    @Override
    @Transactional
    Object execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter) {

        Integer n1 = 0
        if (interpreter.dataStack.size() > 0) {
            n1 = interpreter.dataStack.pop() as Integer
        }

        compiler.newWord.name = compiler.tokens.poll()
        compiler.newWord.runtimeClass = null
        compiler.newWord.compileClass = null
        compiler.wordService.save(compiler.newWord)
        compiler.compileIntegerVariable(n1.toString())
        return false
    }
}
