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

package com.rajames.forth.init

import com.rajames.forth.ForthRepl
import com.rajames.forth.dictionary.DictionaryService
import com.rajames.forth.dictionary.WordService
import com.rajames.forth.memory.storage.BlockService
import com.rajames.forth.util.DatabaseBackupService
import com.rajames.forth.util.FlushService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class Bootstrap implements InitializingBean {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    private DictionaryService dictionaryService
    private WordService wordService
    private FlushService flushService
    private String coreName
    private BlockService blockService
    private DatabaseBackupService databaseBackupService

    @Autowired
    ForthRepl forthRepl

    @Autowired
    Bootstrap(DictionaryService dictionaryService, WordService wordService, DatabaseBackupService databaseBackupService, FlushService flushService, BlockService blockService) {
        this.dictionaryService = dictionaryService
        this.wordService = wordService
        this.databaseBackupService = databaseBackupService
        this.flushService = flushService
        this.blockService = blockService
    }

    @Override
    @Transactional
    void afterPropertiesSet() {
        log.info("Bootstrap started...")
        println("Bootstrap started...")

        coreName = dictionaryService.createDictionary("forth_vocab")
        forthRepl.CONTEXT = coreName
        forthRepl.CURRENT = coreName

        CoreDefinitions coreDefinitions = new CoreDefinitions(wordService, databaseBackupService, flushService)
        coreDefinitions.coreName = this.coreName
        coreDefinitions.createCoreDictionary()

        CoreMemory coreMemory = new CoreMemory(blockService)
        coreMemory.buildCoreMemory()

        // more initialization code here...
        log.info("...Bootstrap finished.")
        println("...Bootstrap finished.")
    }

    String getCoreName() {
        return coreName
    }
}
