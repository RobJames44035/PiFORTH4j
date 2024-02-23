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

//file:noinspection GroovyAssignabilityCheck
//file:noinspection GroovyUnusedAssignment
package com.rajames.forth

import com.rajames.forth.compiler.ForthCompiler
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.memory.DataStack
import com.rajames.forth.memory.ReturnStack
import com.rajames.forth.runtime.ForthInterpreter
import com.rajames.forth.runtime.ForthInterpreterException
import com.rajames.forth.util.DatabaseBackupService
import com.rajames.forth.util.State
import org.apache.groovy.groovysh.Groovysh
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.codehaus.groovy.tools.shell.IO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component

/**
 * forth4j REPL
 */
@Component
class ForthRepl {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    public static final FORTH_DICTIONARY_NAME = "forth_vocab"

    private Scanner scanner

    @Autowired
    private DataStack dataStack

    @Autowired
    private ReturnStack returnStack

    @Autowired
    ForthInterpreter interpreter

    @Autowired
    DatabaseBackupService databaseBackupService

    StringBuilder pad
    Integer padStart
    Double number
    Integer BASE = 10
    Integer BLK = 0
    Boolean quit = false
    String CURRENT
    String CONTEXT
    State STATE


    ForthRepl() {
        this.scanner = new Scanner(System.in)
    }

    /**
     * Execute the REPL
     */
    void run() {
        Boolean forthOutput
        printPreamble()

        while (true) {
            quit = false
            String line = this.scanner.nextLine().trim()

            if (line == "bye") {
                // TODO for state machine when we get there
//                dataStack.serialize()
//                returnStack.serialize()
                databaseBackupService.backupDatabase(null, "core.sql")
                println("Goodbye!")
                break
            } else if (line == "gsh") { // TODO This will go away at some point.
                Binding binding = new Binding()
                AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.rajames.forth")
                String[] allBeanNames = context.getBeanDefinitionNames()

                allBeanNames.each { beanName ->
                    Object bean = context.getBean(beanName)
                    binding.setProperty(beanName, bean)
                }

                Groovysh groovysh = new Groovysh(binding, new IO())

                groovysh.run(null, [])
                continue
            }

            try {
                forthOutput = interpreter.interpretAndExecute(line)
            } catch (ForthException forthException) {
                log.error("Error: ${forthException?.message}")
            } catch (ForthInterpreterException forthInterpreterException) {
                log.error("Error: ${forthInterpreterException?.message}")
            } catch (ForthCompilerException forthCompilerException) {
                log.error("Error: ${forthCompilerException.message}")
            } catch (Exception exception) {
                log.error("Error: ${exception.message}")
            } finally {
                resetInterpreter()
                resetCompiler()
            }

            if (forthOutput) {
                println()
            }

            if (!this.quit) {
                print("\u001B[1A")  // Move cursor up one line
                print("\u001B[" + (line.length()) + "C")  // Move cursor to the end of existing user input + 2 spaces
                print("\033[94m ok\033[0m\n")
                this.quit = true
            }
        }

        this.scanner.close()
        System.exit(0)
    }

    /**
     * Reset the interpreter to it's known starting state.
     */
    private void resetInterpreter() {
        interpreter.tokens.clear()
        interpreter.words.clear()
        interpreter.token = null
        interpreter.instructionPointer = 0
    }

    /**
     * Reset the compiler to it's known starting state.
     */
    private void resetCompiler() {
        ForthCompiler compiler = interpreter.forthCompiler
        compiler.newWord = null
        compiler.literal = null
        compiler.nextTokenToCompile = null
        compiler.forthWordsBuffer = new ArrayList<String>()
    }

    /**
     * Print the starting Banner
     */
    static void printPreamble() {
        print("\u001B[2J")
        print("\u001B[H")
        println("=================================================================")
        println("|      forth4j 1.0.0, Copyright (C) 2024 Robert A. James.       |")
        println("|          forth4j comes with NO ABSOLUTELY WARRANTY.           |")
        println("| For details see `https://www.apache.org/licenses/LICENSE-2.0' |")
        println("=================================================================")
        println("Type `bye' to exit.")
    }
}
