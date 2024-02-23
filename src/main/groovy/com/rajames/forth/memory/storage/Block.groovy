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
//file:noinspection JpaDataSourceORMInspection

package com.rajames.forth.memory.storage

import javax.persistence.*

@Entity
@Table(name = "block", indexes = [@Index(name = "index_blockNumber", columnList = "blockNumber", unique = false)])
class Block implements Serializable {

    Block() {
        bytes = new byte[1024]
        Arrays.fill(bytes, 32 as byte)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id

    @Version
    private int version

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDateTime

    @Column(unique = true)
    private Integer blockNumber

    @Lob()
    private byte[] bytes

///////////////////////////////////Getters & Setters\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    Integer getBlockNumber() {
        return blockNumber
    }

    void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber
    }

    byte[] getBytes() {
        return bytes
    }

    void setBytes(byte[] bytes) {
        this.bytes = bytes
    }

}
