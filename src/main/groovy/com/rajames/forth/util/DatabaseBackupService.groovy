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

package com.rajames.forth.util

import com.rajames.forth.runtime.ForthInterpreterException
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired

//import org.h2.tools.Script

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

@Service
class DatabaseBackupService {

    @Autowired
    private DataSource dataSource

    @Autowired
    FlushService flushService

    @PersistenceContext
    private EntityManager entityManager

    void backupDatabase(String path, String filename) throws SQLException {
        // saving the backup to an SQL file
        if (filename == null || filename.isBlank() || filename.isEmpty()) {
            filename = "core.sql"
        }
        if (path == null || path.isBlank() || path.isEmpty()) {
            path = "./"
        }
        final String backupFilePath = path + "/" + filename
        File file = new File(backupFilePath)
        String absolutePath = file.absolutePath

        entityManager.clear()
        try (final Connection conn = this.dataSource.getConnection()) {
// TODO
//            Script.process(conn, absolutePath, "", "")
        }
    }

    @Transactional
    void loadDatabase(String path, String filename) throws SQLException {
        // loading the backup from the SQL file
        if (filename == null || filename.isBlank() || filename.isEmpty()) {
            filename = "core.sql"
        }
        if (path == null || path.isBlank() || path.isEmpty()) {
            path = "./"
        }
        String backupFilePath = path + "/" + filename
        File file = new File(backupFilePath)
        String absolutePath = file.absolutePath

        try {
            Sql sql = new Sql(dataSource)
            if (absolutePath) {
                sql.execute('DROP ALL OBJECTS;')
                String script = new File(absolutePath).text
                sql.execute(script)
            }
        } catch (Exception ignored) {
            throw new ForthInterpreterException("Could not load ${backupFilePath}.")
        }
        flushService.flush()
    }
}
