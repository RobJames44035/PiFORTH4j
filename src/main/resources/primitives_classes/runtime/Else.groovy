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
 * The "Else" class is part of the runtime mechanism of the Forth-like language interpreter.
 * This class extends from "AbstractRuntime", the abstract class that forms the base for all runtime classes.
 *
 * This class handles the 'ELSE' keyword. During normal execution, when 'IF' block is entered, and 'ELSE' command
 * is encountered, execution is always skipped to 'THEN' excluding the instructions between 'ELSE' and 'THEN' from execution.
 * This happens whether or not the condition for 'IF' was true or false.
 *
 * The presence of the commands 'IF', 'ELSE', and 'THEN' introduces the concept of nested execution control into the language,
 * thereby providing programmers using the language with greater flexibility in designing their code.
 *
 * The word example given below demonstrates how a condition is tested and based on the result it selects a string to print.
 * : test 5 = if ." Five " else ." Not Five " then ;
 */
package primitives_classes.runtime

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

/**
 * The 'Else' class extends the 'AbstractRuntime' super class.
 * It handles the logic of the 'ELSE' keyword in the interpreter.
 */
class Else extends AbstractRuntime {

    /**
     * The `execute` method is responsible for implementing the 'ELSE' operation in the forth interpreter.
     * When an 'ELSE' is encountered during execution, it skips the commands and jumps to the 'THEN' command.
     *
     * @param interpreter The ForthInterpreter Spring bean
     * @param word The word that is being interpreted.
     * @param parentWord The word from which 'ELSE' is being executed.
     * @return It will usually be null as this command represents a control structure rather than producing a value.
     */
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        // Find the index of 'THEN' command in parent word's forthWords list
        Integer thenIndex = parentWord.forthWords.indexOf("then")

        // If 'ELSE' is hit during execution, it always skips to 'THEN'
        if (thenIndex > 0) {
            parentWord.executionIndex = thenIndex
        }

        // The 'else' block does not produce a result by itself, so method returns null
        return null
    }
}
