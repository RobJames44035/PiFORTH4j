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

import com.rajames.forth.Forth4J
import com.rajames.forth.ForthException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.memory.storage.Block
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter
import com.rajames.forth.runtime.ForthInterpreterException
import com.rajames.forth.util.Editor
import com.rajames.forth.util.EditorException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Edit extends AbstractRuntime {

    private static final Logger log = LogManager.getLogger(Forth4J.class.getName())

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
        Integer blockNumber = interpreter.dataStack.pop() as Integer
        interpreter.forthRepl.BLK = blockNumber
        Block block = null
        try {
            block = interpreter.blockService.getBlock(blockNumber)
        } catch (ForthException f) {
            log.error(f.message)
        }
        try {
            Editor editor = new Editor(block, interpreter.blockService)
            editor.editor()
        } catch (EditorException e) {
            if ("Exit editor command received" == e.getMessage()) {
                return null
            } else {
                throw new ForthInterpreterException("The editor incurred an error.")
            }
        }
        return null
    }
}