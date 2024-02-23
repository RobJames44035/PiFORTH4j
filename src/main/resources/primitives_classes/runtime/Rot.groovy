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

package primitives_classes.runtime

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter
import com.rajames.forth.runtime.ForthInterpreterException

class Rot extends AbstractRuntime {

    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        if (interpreter.dataStack.size() < 3) {
            throw new ForthInterpreterException("Not enough elements on the stack for ROT")
        }

        Integer x1 = interpreter.dataStack.pop() as Integer
        Integer x2 = interpreter.dataStack.pop() as Integer
        Integer x3 = interpreter.dataStack.pop() as Integer

        interpreter.dataStack.push(x2)
        interpreter.dataStack.push(x1)
        interpreter.dataStack.push(x3)

        return null
    }
}
