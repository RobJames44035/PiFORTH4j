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


import com.rajames.forth.ForthRepl
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.runtime.ForthInterpreterException
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

@Service
@Transactional
class WordService {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private final WordRepository wordRepository
    private final DictionaryRepository dictionaryRepository

    ForthRepl forthRepl

    @Autowired
    private DataSource dataSource

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    WordService(WordRepository wordRepository, DictionaryRepository dictionaryRepository, ForthRepl forthRepl) {
        this.wordRepository = wordRepository
        this.dictionaryRepository = dictionaryRepository
        this.forthRepl = forthRepl
    }

    Word getById(Long id) {
        return wordRepository.findById(id).orElseThrow(() ->
                new ForthInterpreterException("Word not found for id: " + id)
        )
    }

    List<Word> list() {
        return wordRepository.findAll()
    }

    @Transactional
    void forget(Word word, Date date) {
        // Because of foreign key constraints we are forced to 'forget' the hard way.
        String vocabulary = forthRepl.CURRENT
        Word vocab = findByName(vocabulary)
        if (vocab != null) {
            try {
                Word delete = findByName(word.name)
                if (delete != null) {
                    Sql sql = new Sql(dataSource)
                    try {
                        List<GroovyRowResult> results =
                                sql.rows(
                                        "select * from word where dictionary_id = ? and createDateTime >= ?",
                                        [delete.dictionary.id, delete.createDateTime]
                                )
                        for (def result : results) {
                            if (result.parent_word_name == null) {
                                sql.execute("delete from forthWords where word_id = ?", [result.id])
                            }
                            sql.execute("delete from word where parent_word_name = ?", [result.id])
                            sql.execute("delete from word where id = ?", [result.id])
                        }
                    } catch (Exception e) {
                        log.error(e)
                    } finally {
                        sql.close()
                    }
                }
            } catch (Exception ignored) {/*meaningless*/
            }
        }
    }

    @Transactional
    Word findByName(String name) {
        log.trace("WordService.findByName(String '${name}')")

        // Explicitly handle CURRENT vocabulary.
        String vocabName = forthRepl.CURRENT

        // If CONTEXT is different from CURRENT, use CONTEXT for the search
        if (forthRepl.CONTEXT != forthRepl.CURRENT) {
            vocabName = forthRepl.CONTEXT
        }

        // Then perform the search
        Optional<Word> optional = wordRepository.findFirstByNameAndDictionaryNameOrderByCreateDateTimeDesc(name, vocabName)

        // If not found in CONTEXT, try CURRENT if they were different
        if (!optional.isPresent() && forthRepl.CONTEXT != forthRepl.CURRENT) {
            optional = wordRepository.findFirstByNameAndDictionaryNameOrderByCreateDateTimeDesc(name, forthRepl.CURRENT)
        }

        // If word is still not found after checking both vocabularies, try the FORTH vocabulary.
        if (!optional.isPresent()) {
            optional = wordRepository.findFirstByNameAndDictionaryNameOrderByCreateDateTimeDesc(name, forthRepl.FORTH_DICTIONARY_NAME)
        }

        log.trace("WordService: Optional<Word> optional = ${optional}")

        // If word is still not found after checking all vocabularies, throw an exception.
        if (!optional.isPresent()) {
            return null
            // throw new ForthException("${name}: No such word.");
        }

        Word foundWord = optional.get()
        log.trace("WordService: Word foundWord = optional.get() name = '${foundWord.name}' forthWords = ${foundWord.forthWords}")

        return foundWord
    }

    @Transactional
    Word save(Word word) {
        Word retrievedWord = wordRepository.save(word)
        entityManager.flush()
        return retrievedWord
    }

    @Transactional
    Boolean isSaved(Word word) {
        return entityManager.contains(word)
    }

    @Transactional
    void deleteWordFromDictionary(Word deleteMe) {
        Word word = findByName(deleteMe.name)
        if (word != null) {
            wordRepository.delete(word)
        }
    }

    @Transactional
    Word addWordToDictionary(String wordName, String dictionaryName,
                             List<String> complexWords = null,
                             String runtimeClass = null, String compileClass = null,
                             Integer argumentCount = 0, Boolean compileOnly = false, Boolean controlWord = false) {
        Word word = null
        try {
            Optional<Dictionary> dictionaryOptional = dictionaryRepository.findByName(dictionaryName) as Optional<Dictionary>

            if (dictionaryOptional.isPresent()) {
                Dictionary dictionary = dictionaryOptional.get()

                word = new Word()
                word.name = wordName
                word.dictionary = dictionary
                word.runtimeClass = runtimeClass
                word.compileClass = compileClass
                word.argumentCount = argumentCount
                word.compileOnly = compileOnly
                word.controlWord = controlWord
                save(word)

                if (complexWords) {
                    complexWords.each { String childWord ->
                        Word forthWord = findByName(childWord)
                        if (word != null && forthWord != null) {
                            forthWord.parentWord = word
                            word.forthWords.add(forthWord.name)
                        } else {
                            throw new ForthCompilerException("Word ${childWord} not found.")
                        }
                    }
                }

                save(word)
            } else {
                throw new ForthCompilerException("Dictionary with name ${dictionaryName} does not exist.")
            }
        } catch (Exception e) {
            throw new ForthCompilerException("${wordName} was NOT added to ${dictionaryName} dictionary.", e)
        }
        log.trace("${wordName} added to ${dictionaryName} dictionary.")
        return word
    }
}
