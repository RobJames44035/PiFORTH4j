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

package com.rajames.forth.memory

import org.springframework.stereotype.Component

@Component
class ReturnStack extends AbstractStack {
    @Override
    Object pop() {
        if (stack.empty()) {
            println "Warning: Return Stack Underflow"
            return 0  // Or however you want to handle underflows
        } else {
            return stack.pop()
        }
    }

}
