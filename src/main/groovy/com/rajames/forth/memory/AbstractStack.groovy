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

import com.rajames.forth.ForthException
import com.rajames.forth.memory.storage.BlockService
import com.rajames.forth.runtime.ForthInterpreterException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractStack implements StackInterface, Serializable {

    protected Stack<Object> stack = new Stack<>()

    @Autowired
    BlockService blockService

    @Override
    void serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ObjectOutputStream out = new ObjectOutputStream(bos)
        out.withCloseable { ObjectOutputStream oos ->
            oos.writeObject(this)
            oos.flush()
        }
        bos.withCloseable { ByteArrayOutputStream baos ->
            byte[] serializedData = baos.toByteArray()
            for (int i = 0; i < serializedData.length; i++) {
                Integer address = 0 - Math.abs(i + 1024)
                byte b = serializedData[i]
                blockService.store(address, b, true)
            }
        }
    }

    @Override
    void deserialize() {
        List<Byte> data = []
        int i = 0
        while (true) {
            Integer address = 0 - Math.abs(i + 1024)
            Byte b = null
            try {
                b = blockService.fetch(address, true)
            } catch (ForthException ex) {
                if (ex.getMessage() == "Illegal memory access :(") {
                    break
                }
            }
            data.add(b)
            i++
        }

        byte[] serializedData = data as byte[]
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedData)
        ObjectInputStream ois = new ObjectInputStream(bis)
        ois.withCloseable { ObjectInputStream is ->
            AbstractStack deserializedObject = (AbstractStack) is.readObject()
            this.stack = deserializedObject.stack
        }
    }

    @Override
    void push(Object value) {
        stack.push(value)
    }

    @Override
    Object pop() {
        if (stack.empty()) {
            println "Warning: Underflow"
            return 0  // Or however you want to handle underflows
        } else {
            return stack.pop()
        }
    }

    @Override
    void clear() {
        stack.clear()
    }

    @Override
    int size() {
        return stack.size()
    }

    @Override
    int get(Integer i) {
        if (i >= 0 && i < stack.size()) { // Checking if index is valid
            return stack.get(stack.size() - i - 1) as int
            // Inverting the index because the top of stack is the end of list
        } else {
            throw new ForthInterpreterException("Warning: Invalid Index")
        }
    }

    @Override
    Object peek() {
        if (stack.empty()) {
            return Integer.MAX_VALUE
        } else {
            return stack.peek()
        }
    }

    @Override
    int[] popDouble() {
        // Check if there are enough items on stack
        if (stack.size() < 2) {
            throw new ForthInterpreterException("Warning: Double underflow")
        }
        int high = pop() as int
        int low = pop() as int
        return [low, high] as int[]
    }

    @Override
    void pushDouble(Integer low, Integer high) {
        push(low)
        push(high)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof AbstractStack)) return false

        AbstractStack that = (AbstractStack) o

        if (stack != that.stack) return false

        return true
    }

    int hashCode() {
        return (stack != null ? stack.hashCode() : 0)
    }

    @Override
    boolean isEmpty() {
        return stack.empty
    }
}

