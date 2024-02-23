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

class Over extends AbstractRuntime {

    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        // 'peek' method is assumed to have same behavior as in java.util.Stack
        // where peek() gets the object at the top of the stack without removing it from the stack.
        // We go two elements down in stack, hence -2
        Object overObject = interpreter.dataStack.get(interpreter.dataStack.size() - 2)
        interpreter.dataStack.push(overObject)

        return null
    }
}
