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
//file:noinspection unused

package com.rajames.forth.dictionary

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import javax.persistence.*

@EqualsAndHashCode
@TupleConstructor(includeSuperProperties = true, includeFields = true, includeProperties = true)
@Entity
@Table(name = "dictionary")
class Dictionary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id

    @Version
    private int version

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDateTime

    @Column(unique=true)
    private String name

    @OneToMany(mappedBy = "dictionary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Word> vocabulary

    @PrePersist
    void onCreate() {
        this.setCreateDateTime(new Date())
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    List<Word> getWordList() {
        return vocabulary
    }

    void setWordList(List<Word> wordList) {
        this.vocabulary.clear()
        if (wordList != null) {
            this.vocabulary.addAll(wordList)
        }
    }

    Date getCreateDateTime() {
        return createDateTime
    }

    void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime
    }
}

