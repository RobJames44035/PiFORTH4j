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

import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Colon extends AbstractRuntime {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        // Fail Fast
        if (!interpreter.line.contains(";"))
            throw new ForthCompilerException("No matching ';' for ':'")
        // Invoke the compiler
        interpreter.forthCompiler.reset()
        interpreter.forthCompiler.compile(interpreter.line)
        return false
    }
}
