// Copyright (c) 2009 Dmitri Nikulin
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.dnikulin.codon.commands.core;

import static org.dnikulin.codon.command.CommandTools.printUsage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.dnikulin.codon.command.EffectCommand;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.compiler.PipeShellParser;
import org.dnikulin.codon.pipe.except.PipeException;

/** Command to execute all commands in a batch script. */
public class BatchCommand implements EffectCommand {
    private final PipeShellParser parser;

    /**
     * Construct a batch command to feed the given parser.
     * 
     * @param parser
     *            Parser to feed commands
     */
    public BatchCommand(PipeShellParser parser) {
        this.parser = parser;
    }

    @Override
    public void execute(String[] args, LineLogger log) {
        if (args.length != 1) {
            printUsage(log, this);
            return;
        }

        String path = args[0];
        File file = new File(path);

        if (!file.exists()) {
            log.print("Path '" + path + "' does not exist");
            return;
        }

        try {
            runFile(file, log);
        } catch (IOException ex) {
            log.print("Could not read batch file: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Execute all commands in the given batch script file.
     * 
     * @param file
     *            Batch script file
     * @param log
     *            Line logger
     */
    public void runFile(File file, LineLogger log) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));

        try {
            String line;
            while ((line = in.readLine()) != null)
                runCommand(line, log);
        } finally {
            in.close();
        }
    }

    /**
     * Execute a single command.
     * 
     * @param line
     *            Command line
     * @param log
     *            Line logger
     */
    public void runCommand(String line, LineLogger log) {
        log.print("> " + line + "\n");

        try {
            parser.feed(line);
        } catch (PipeException ex) {
            log.print("Error: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String getCommandTopic() {
        return "core";
    }

    @Override
    public String getCommandName() {
        return "batch";
    }

    @Override
    public String getCommandUsage() {
        return "<batch script path>";
    }
}
