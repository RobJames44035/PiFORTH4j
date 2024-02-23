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
 * The "If" class is an integral part of the runtime mechanism of the Forth-like language interpreter.
 * This class extends from "AbstractRuntime", the abstract class that forms the base for all the runtime classes.
 *
 * The runtime mechanism undertakes the actual execution of the Forth words during the interpretation process.
 * During the parsing and compilation stage, Forth words are identified and formed into a list of words (word list) within each 'Word' object.
 * For words like 'IF', they are registered into the dictionary with their corresponding runtime classes (in this case, the 'If' class).
 *
 * When the interpreter encounters a defined Word during the interpretation process, it looks up the associated runtime object and calls its 'execute' method.
 *
 * In the 'If' class, the overridden 'execute' method implements the behavior for the 'IF' control structure of the language, including handling 'ELSE' and 'THEN' cases.
 * Here's how it happens:
 * - Pop the top element from the data stack and consider it as a condition value.
 * -  If the condition value equals 0 (i.e. is false) then depending upon the existence and location of an 'ELSE' command,
 *     either skip over the commands before 'ELSE' or skip over all commands till 'THEN'.
 * - If the condition value does not equal 0 (i.e. is true), let the normal execution flow continue which means execute all commands till 'ELSE' is encountered.
 *     If 'ELSE' is encountered, skip to 'THEN'.
 *
 * This mechanism enables the use of 'IF', 'ELSE' and 'THEN' control structures in the language.
 *
 * Our test cases included:
 * : test1 5 = if ." Five " then ;
 * : test2 5 = if ." Five " else ." Not Five " then ;
 */
package primitives_classes.runtime

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

/**
 * The 'If' class extends the 'AbstractRuntime' super class.
 * It handles the logic of the 'IF' keyword in the interpreter.
 */
class If extends AbstractRuntime {

    /**
     * The `execute` method is responsible for executing the 'IF' operation in the forth interpreter.
     * It takes in the interpreter, the 'IF' keyword and the parent word in which the 'IF' keyword
     * is used as parameters.
     *
     * @param interpreter The ForthInterpreter Spring bean
     * @param word The word that is being interpreted.
     * @param parentWord The word from which 'IF' is being executed.
     * @return Will 'normaly' be Boolean true or false indicating if ForthRepl should print a new line.
     */
    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        // The value popped from the data stack is used as the condition for the 'IF' statement.
        Integer conditionValue = interpreter.dataStack.pop() as Integer

        // Find the indices of 'ELSE' and 'THEN' command in parent word's forthWords list
        Integer elseIndex = parentWord.forthWords.indexOf("else")
        Integer thenIndex = parentWord.forthWords.indexOf("then")

        // If the condition value equals 0 (i.e. is false), and the index of 'ELSE' command is between the 'IF'
        // and 'THEN' commands,
        // then it sets the execution index to the elseIndex, thus skips the commands before 'ELSE' block.
        if (conditionValue == 0 && elseIndex > -1 && elseIndex < thenIndex) {
            parentWord.executionIndex = elseIndex // Skip to 'else'
        }

        // If the condition value equals 0 and the 'ELSE' command does not exist or is located after the 'THEN' command,
        // then it sets the execution index to the thenIndex, thus skips the commands before 'THEN' block.
        else if (conditionValue == 0 && thenIndex > -1) { // If the condition is false
            parentWord.executionIndex = thenIndex // Skip to 'then'
        }

        // When the condition is true or false, it does nothing,
        // allowing the normal execution flow to continue until an `ELSE` is encountered.
        // It is currently just a flag to pass to ForthRepl indicating it should or should not
        // print a newline at the end of execution.
        // Since it is an object we can return something to the interpreter under special
        // circumstances, BUT the interpreter must still return a proper Boolean value to ForthRepl.
        return null
    }
}
