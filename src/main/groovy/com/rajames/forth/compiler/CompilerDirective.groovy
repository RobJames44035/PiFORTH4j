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

package com.rajames.forth.compiler

import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter

/**
 * Interface for Compiler Directives.
 */
interface CompilerDirective {

    /**
     * Execute a compiler Directive on a word.
     * @param newWord The new word we are creating.
     * @param compiler The FORTH compiler.
     * @param interpreter The FORTH interpreter.
     * @return an arbitrary `anything`. Usually null or Boolean false.
     */
    Object execute(Word newWord, ForthCompiler compiler, ForthInterpreter interpreter)
}
