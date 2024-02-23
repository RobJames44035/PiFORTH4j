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

package com.rajames.forth;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * forth4j
 */
public class Forth4J {

    private static final Logger log = LogManager.getLogger(Forth4J.class.getName());

    public static void main(final String[] args) {
        final Options options = getOptions();

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException parseException) {
            System.err.println(parseException.getMessage());
            System.exit(1);
            return;
        }
        if (cmd.hasOption("help")) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("forth4j", options);
            System.exit(0);
        }

        if (cmd.hasOption("load-dictionary")) {
            System.out.println("Unimplemented");
            System.exit(0);
        }

        if (cmd.hasOption("execute-word")) {
            System.out.println("Unimplemented");
            System.exit(0);
        }

        if (cmd.hasOption("log-level")) {
            final String logLevel = cmd.getOptionValue("log-level");
            System.out.println("Setting log level to " + logLevel);
            Configurator.setRootLevel(Level.getLevel(logLevel));
        }

        try {
            try (final AnnotationConfigApplicationContext context =
                         new AnnotationConfigApplicationContext("com.rajames.forth")) {

                // Get ForthRepl from the context
                final ForthRepl repl = context.getBean(ForthRepl.class);

                // Run it
                try {
                    repl.run();
                } catch (final Exception e) {
                    log.error(e.getMessage());
                }
            }
        } catch (final BeansException beansException) {
            log.error("Error initializing application", beansException);
        }

        log.info("All Done! Bye, bye!!");
    }

    private static Options getOptions() {
        final Options options = new Options();
        final Option dictionary = new Option("d", "load-dictionary", true, "Load dictionary.");
        dictionary.setRequired(false);
        options.addOption(dictionary);
        final Option execute = new Option("e", "execute-word", true, "Execute FORTH word.");
        execute.setRequired(false);
        options.addOption(execute);
        final Option logging = new Option("l", "log-level", true, "Set Log Level.");
        logging.setRequired(false);
        options.addOption(logging);
        final Option help = new Option("h", "help", false, "This message.");
        help.setRequired(false);
        options.addOption(help);
        return options;
    }
}
