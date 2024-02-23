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

interface StackInterface {

    void push(Object value)
    Object pop()

    Object peek()
    void clear()
    int size()
    boolean isEmpty()
    int get(Integer i)

    int[] popDouble()

    void pushDouble(Integer low, Integer high)

    void serialize()
    void deserialize()
}
