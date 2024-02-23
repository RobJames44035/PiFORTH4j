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

package com.rajames.forth.dictionary

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findFirstByNameOrderByCreateDateTimeDesc(String name)

    Optional<Word> findFirstByNameAndDictionaryNameOrderByCreateDateTimeDesc(String name, String dictionaryName)

    Optional<Word> findById(Long id)

    @Transactional
    @Modifying
    @Query("DELETE FROM Word w WHERE w.createDateTime >= :date")
    void deleteAllWithCreationDateOnOrAfter(
            @Param("date") Date date)

    @Transactional
    @Modifying
    @Query("DELETE FROM Word w WHERE w.name = :wordName AND w.dictionary.id = :dictionaryId AND w.createDateTime >= :date")
    void deleteAllByWordNameAndDictionaryIdAndCreationDateOnOrAfter(
            @Param("wordName") String wordName,
            @Param("dictionaryId") Integer dictionaryId,
            @Param("date") Date date
    )
}
