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

class Current extends AbstractRuntime {

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
        if (interpreter.dataStack.size() != 0) {
            // If there is an item on the data stack
            // Pop the topmost item from the data stack.
            Long wordId = interpreter.dataStack.pop() as Long
            // Fetch the vocabulary associated with the popped item.
            Word vocab = interpreter.wordService.getById(wordId)
            // Set the current vocabulary to the fetched vocabulary 'name'.
            interpreter.forthRepl.CURRENT = vocab.name
            interpreter.dataStack.push(vocab.id)
        } else {
            // If the data stack is empty
            // Fetch the current vocabulary.
            String current = interpreter.forthRepl.CURRENT
            // Get the word associated with the 'current' vocabulary.
            Word currentWord = interpreter.wordService.findByName(current)
            // Fetch the 'stackValue' of the fetched word.
            Long stackValue = currentWord.stackValue
            // Push this 'stackValue' onto the data stack.
            interpreter.dataStack.push(stackValue)
        }
        return null
    }
}
