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

class J extends AbstractRuntime {

    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        // Discard the current (inner) loop:
        Integer innerLimit = interpreter.returnStack.pop() as Integer
        Integer innerIndex = interpreter.returnStack.pop() as Integer

        // Get the outer loop:
        Integer outerLimit = interpreter.returnStack.pop() as Integer
        Integer outerIndex = interpreter.returnStack.pop() as Integer

        // Push back the inner loop (since we want to preserve it):
        interpreter.returnStack.push(innerIndex)
        interpreter.returnStack.push(innerLimit)

        // Push outer loop's index to data stack:
        interpreter.dataStack.push(outerIndex)

        // Push back the outer loop (since we also want to preserve it):
        interpreter.returnStack.push(outerIndex)
        interpreter.returnStack.push(outerLimit)

        return null
    }
}
