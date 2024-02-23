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
//file:noinspection GroovyUnusedAssignment

package com.rajames.forth.init

import com.rajames.forth.ForthRepl
import com.rajames.forth.compiler.ForthCompilerException
import com.rajames.forth.dictionary.Word
import com.rajames.forth.dictionary.WordService
import com.rajames.forth.util.DatabaseBackupService
import com.rajames.forth.util.FlushService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager

@Transactional
@Component
class CoreDefinitions {

    private static final Logger log = LogManager.getLogger(this.class.getName())

    public static final String RUNTIME = "primitives_classes/runtime"
    public static final String COMPILE = "primitives_classes/compiler"

    private final WordService wordService

    String getCoreName() {
        return coreName
    }

    void setCoreName(String coreName) {
        this.coreName = coreName
    }
    private String coreName

    private DatabaseBackupService databaseBackupService

    private EntityManager entityManager

    private FlushService flushService

    @Autowired
    ForthRepl forthRepl

    CoreDefinitions(WordService wordService, DatabaseBackupService databaseBackupService, FlushService flushService) {
        this.wordService = wordService
        this.databaseBackupService = databaseBackupService
        this.entityManager = wordService.entityManager
        this.flushService = flushService
    }

    static String getRuntimeContent(String runtimeClass) {
        String runtimeBasePath = "primitives_classes/runtime"
        getGroovyFileContent(runtimeBasePath, runtimeClass)
    }

    static String getCompileContent(String compileClass) {
        String compileBasePath = "primitives_classes/compiler"
        getGroovyFileContent(compileBasePath, compileClass)
    }

    // helper method to retrieve content of Groovy files
    private static String getGroovyFileContent(String basePath, String className) {
        String retVal = null
        if (className != null) {
            String myResourceName = basePath + "/" + className + ".groovy"
            retVal = CoreDefinitions.class.getClassLoader().getResource(myResourceName)?.text
        }
        return retVal
    }

    Word createPrimitiveWord(String wordName, String runtimeClass = null, String compileClass = null, Integer argumentCount = 0, Boolean compileOnly = false, Boolean controlWord = false) {
        try {
            String runtimeContent = getRuntimeContent(runtimeClass)
            String compileContent = getCompileContent(compileClass)
            return wordService.addWordToDictionary(wordName, coreName, null, runtimeContent, compileContent, argumentCount, compileOnly, controlWord)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create primitive word ${wordName}.", e)
        }
    }

    Word createComplexWord(String wordName, List<String> words, Integer argumentCount = 0, Boolean compileOnly = false) {
        try {
            return wordService.addWordToDictionary(wordName, coreName, words as List<String>, null, null, argumentCount, compileOnly)
        } catch (Exception e) {
            throw new ForthCompilerException("Could not create complex word ${wordName}.", e)
        }
    }

    void createCoreDictionary() {
        log.info("\tBuilding core dictionary...")
        println("\tBuilding core dictionary...")

        Word f = createPrimitiveWord("forth_vocab")
        f.stackValue = f.id as Integer
        f.stringLiteral = f.name
        wordService.save(f)

        // Non-Standard words
        Word see = createPrimitiveWord("see", "See")
        Word saveForth = createPrimitiveWord("save-forth", "SaveForth")
        Word loadForth = createPrimitiveWord("load-forth", "LoadForth")
        Word edit = createPrimitiveWord("edit", "Edit")

        // math words
        Word plus = createPrimitiveWord("+", "Plus")
        Word minus = createPrimitiveWord("-", "Minus")
        Word onePlus = createPrimitiveWord("1+", "OnePlus")
        Word oneMinus = createPrimitiveWord("1-", "OneMinus")
        Word twoPlus = createPrimitiveWord("2+", "TwoPlus")
        Word twoMinus = createPrimitiveWord("2-", "TwoMinus")
        Word twoTimes = createPrimitiveWord("2*", "TwoTimes")
        Word twoDivide = createPrimitiveWord("2/", "TwoDivide")
        Word times = createPrimitiveWord("*", "Times")
        Word divide = createPrimitiveWord("/", "Divide")
        Word timesDivide = createPrimitiveWord("*/", "TimesDivide")
        Word abs = createPrimitiveWord("abs", "Abs")
        Word mod = createPrimitiveWord("mod", "Mod")
        Word max = createPrimitiveWord("max", "Max")
        Word min = createPrimitiveWord("min", "Min")
        Word starSlashMod = createPrimitiveWord("*/mod", "StarSlashMod")
        Word slashMod = createPrimitiveWord("/mod", "SlashMod")
        Word negate = createPrimitiveWord("negate", "Negate")

        // Logic words
        Word lessThanZero = createPrimitiveWord("0<", "LessThanZero")
        Word equalZero = createPrimitiveWord("0=", "EqualZero")
        Word greaterThanZero = createPrimitiveWord("0=", "GreaterThanZero")
        Word lessThan = createPrimitiveWord("<", "LessThan")
        Word greaterThan = createPrimitiveWord(">", "GreaterThan")
        Word equal = createPrimitiveWord("=", "Equal")
        Word and = createPrimitiveWord("and", "And")
        Word not = createPrimitiveWord("not", "Not")
        Word or = createPrimitiveWord("or", "Or")
        Word xor = createPrimitiveWord("xor", "Xor")

        // String & output words
        Word dot = createPrimitiveWord(".", "Dot")
        Word cr = createPrimitiveWord("cr", "Cr")
        Word emit = createPrimitiveWord("emit", "Emit")
        Word dotQuote = createPrimitiveWord(".\"", null, "DotQuoteC")
        Word space = createPrimitiveWord("space", "Space")
        Word spaces = createPrimitiveWord("spaces", "Spaces")
        Word type = createPrimitiveWord("type", "Type")
        Word page = createPrimitiveWord("page", "Page")
        Word count = createPrimitiveWord("count", "Count")
        Word trailing = createPrimitiveWord("-trailing", "MinusTrailing")

        // User input words
        Word key = createPrimitiveWord("key", "Key")
        Word expect = createPrimitiveWord("expect", "Expect")

        // Stack words
        Word dup = createPrimitiveWord("dup", "Dup")
        Word drop = createPrimitiveWord("drop", "Drop")
        Word qdup = createPrimitiveWord("?dup", "Qdup")
        Word swap = createPrimitiveWord("swap", "Swap")
        Word overR = createPrimitiveWord(">r", "OverR")
        Word rOver = createPrimitiveWord("r>", "ROver")
        Word question = createPrimitiveWord("?", "Question")
        Word depth = createPrimitiveWord("depth", "Depth")
        Word over = createPrimitiveWord("over", "Over")
        Word pick = createPrimitiveWord("pick", "Pick")
        Word rFetch = createPrimitiveWord("r@", "RFetch")
        Word roll = createPrimitiveWord("roll", "Roll")

        // Flow control
        Word ifWord = createPrimitiveWord("if", "If", "IfC", 1, true, true)
        Word elseWord = createPrimitiveWord("else", "Else", "ElseC", 0, true, true)
        Word thenWord = createPrimitiveWord("then", null, null, 0, true, true)
        Word doWord = createPrimitiveWord("do", "Do", "DoC", 2, true, true)
        Word loopWord = createPrimitiveWord("loop", "Loop", null, 0, true, true)
        Word plusLoopWord = createPrimitiveWord("+loop", "PlusLoop", null, 1, true, true)
        Word leave = createPrimitiveWord("leave", "Leave", null, 0, true)
        Word begin = createPrimitiveWord("begin", "Begin", "BeginC", 0, true)
        Word until = createPrimitiveWord("until", "Until", null, 0, true)
        Word whileW = createPrimitiveWord("while", "While", "WhileC", 0, true)
        Word repeat = createPrimitiveWord("repeat", "Repeat", null, 0, true)

        // Compiler words
        Word colon = createPrimitiveWord(":", "Colon")
        Word semicolon = createPrimitiveWord(";")
        Word literal = createPrimitiveWord("literal", "Literal", "LiteralC", 0, true)
        Word lit = createPrimitiveWord("lit", "Lit", null, 0, true)
        Word abort = createPrimitiveWord("abort", "Abort")
        Word lbracket = createPrimitiveWord("[", null, "LbracketC", 0, true)
        Word rbracket = createPrimitiveWord("]", null, "RbracketC", 0, true)
        Word bracketCompile = createPrimitiveWord("[compile]", null, "CompileC", 0, true)
        Word compile = createPrimitiveWord("compile", null, "CompileC", 0, true)
        Word immediate = createPrimitiveWord("immediate", "Immediate")
        Word constant = createPrimitiveWord("constant", "Constant", "ConstantC")
        Word variable = createPrimitiveWord("variable", "Variable", "VariableC")
        Word create = createPrimitiveWord("create", "Create", "CreateC")

        // Memory words
        Word store = createPrimitiveWord("!", "Store")
        Word fetch = createPrimitiveWord("@", "Fetch")
        Word words = createPrimitiveWord("words", "Words")
        Word plusStore = createPrimitiveWord("+!", "PlusStore")
        Word cStore = createPrimitiveWord("c!", "Store")
        Word cFetch = createPrimitiveWord("c@", "Fetch")
        Word move = createPrimitiveWord("move", "Move")
        Word cMove = createPrimitiveWord("cmove", "CMove")

        // Mass storage words
        Word buffer = createPrimitiveWord("buffer")
        Word block = createPrimitiveWord("block", "Block")
        Word list = createPrimitiveWord("list", "List")
        Word load = createPrimitiveWord("load", "Load")
        Word emptyBuffers = createPrimitiveWord("empty-buffers", "EmptyBuffers")
        Word blk = createPrimitiveWord("blk")
        Word fill = createPrimitiveWord("fill", "Fill")

        // Double precision operations
        Word doublePlus = createPrimitiveWord("d+", "DPlus")
        Word doubleLessThan = createPrimitiveWord("d<", "DlessThan")
        Word dnegate = createPrimitiveWord("dnegate", "DNegate")

        // Dictionary words
        Word definitions = createPrimitiveWord("definitions", "Definitions")
        Word forth = createPrimitiveWord("forth", "Forth")
        Word vocabulary = createPrimitiveWord("vocabulary", "Vocabulary")
        Word forget = createPrimitiveWord("forget", "Forget")
        Word current = createPrimitiveWord("current", "Current")
        Word context = createPrimitiveWord("context", "Context")

        // Unsorted
        Word colon1 = createPrimitiveWord("colon")
        Word comment = createPrimitiveWord("(", "Comment")
        Word i = createPrimitiveWord("i", "I")
        Word j = createPrimitiveWord("j", "J")
        Word pad = createPrimitiveWord("pad", "Pad")
        Word allot = createPrimitiveWord("allot", "Allot")
        Word comma = createPrimitiveWord(",", "Comma")
        Word gtIn = createPrimitiveWord(">in", "GtIn")
        Word tick = createPrimitiveWord("'", "Tick")
        Word execute = createPrimitiveWord("execute", "Execute")
        Word standard79 = createPrimitiveWord("79-standard")
        Word quit = createPrimitiveWord("quit", "Quit")

        Word base = createPrimitiveWord("base", "Base")
        Word hex = createPrimitiveWord("hex", "Hex")
        Word decimal = createPrimitiveWord("decimal", "Decimal")


        // Complex words that are made up of a List<Word> that describes their behavior go here.


        flushService.flush()
        databaseBackupService.backupDatabase(null, null)
    }
}
