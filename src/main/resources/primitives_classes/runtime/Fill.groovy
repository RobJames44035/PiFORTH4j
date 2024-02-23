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

class Fill extends AbstractRuntime {

/**
 * Execute the FORTH word from the interpreter.
 * @param interpreter The FORTH interpreter instance.
 * @param word The word that is being executed.
 * @param parentWord It's parent word (if any).
 * @return An object of any type. By convention we are returning a Boolean to indicate if the REPL
 * should print a newline or not. If you do anything with a returned Object, be sure to set
 * forthOutput to to a Boolean for REPL.
 */
    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        Byte byt = interpreter.dataStack.pop() as Byte
        Integer count = interpreter.dataStack.pop() as Integer
        Integer addr = interpreter.dataStack.pop() as Integer
        for (int index = addr; index < addr + count; index++) {
            interpreter.blockService.store(index, byt, true)
        }
        return null
    }
}
