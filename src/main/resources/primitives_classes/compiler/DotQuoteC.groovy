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

package primitives_classes.compiler


import com.rajames.forth.compiler.AbstractCompilerDirective
import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.ForthInterpreter
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ConcurrentLinkedQueue

// : test 2 + ." adding x + 2 = " . cr ." Done!" ;
class DotQuoteC extends AbstractCompilerDirective {


    public static final String QUOTATION_MARK = "\""

    @Override
    @Transactional
    Object execute(Word word, ForthCompiler compiler, ForthInterpreter interpreter) {
        ConcurrentLinkedQueue<Word> words = interpreter.words
        StringBuilder sb = new StringBuilder()
        Word nextWord = null
        try {
            compiler.forthWordsBuffer.add(word.name)
            while (!compiler.tokens.isEmpty()) {
                String token = compiler.tokens.poll()
                if (token == QUOTATION_MARK) {
                    break
                }
                if (token.endsWith(QUOTATION_MARK)) {
                    sb.append(token)
                    break
                }

                sb.append(token).append(" ")
            }
            String stringLiteral = sb.toString()
            compiler.compileLiteral(stringLiteral)
        } catch (Exception e) {
            throw new ForthCompilerException("${this.class.simpleName} failed.", e)
        }
        return false
    }

}
