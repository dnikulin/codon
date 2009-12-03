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

package org.dnikulin.codon;

import java.io.File;
import java.io.IOException;

import org.dnikulin.codon.commands.BatchCommand;
import org.dnikulin.codon.commands.PluginCommand;
import org.dnikulin.codon.format.primitive.DoubleObjectFormat;
import org.dnikulin.codon.format.primitive.FloatObjectFormat;
import org.dnikulin.codon.format.primitive.IntegerObjectFormat;
import org.dnikulin.codon.format.primitive.LongObjectFormat;
import org.dnikulin.codon.format.primitive.StringObjectFormat;
import org.dnikulin.codon.format.registry.ObjectFormats;
import org.dnikulin.codon.format.registry.ObjectFormatsPluginSlot;
import org.dnikulin.codon.log.IndirectLogger;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.log.LogSource;
import org.dnikulin.codon.pipe.command.registry.PipeCommands;
import org.dnikulin.codon.pipe.command.registry.PipeCommandsPluginSlot;
import org.dnikulin.codon.pipe.compiler.EarlyPipeShellCompiler;
import org.dnikulin.codon.pipe.compiler.PipeLinker;
import org.dnikulin.codon.pipe.compiler.PipeShellCompiler;
import org.dnikulin.codon.pipe.compiler.PipeShellParser;
import org.dnikulin.codon.pipe.except.PipeException;
import org.dnikulin.codon.plugin.PluginLinker;
import org.dnikulin.codon.plugin.PluginLinkerPluginSlot;
import org.dnikulin.codon.plugin.PluginLoader;

/** High-level class integrating Codon subsystems. */
public class CodonKernel implements LogSource {
    private final IndirectLogger logger;

    private final PluginLinker pluginLinker;
    private final PluginLoader pluginLoader;

    private final PipeLinker pipeLinker;
    private final PipeCommands commands;

    private final PipeShellCompiler compiler;
    private final PipeShellParser parser;

    private final ObjectFormats formats;

    private final BatchCommand batchCommand;

    /** Construct a Codon kernel. */
    public CodonKernel() {
        logger = new IndirectLogger();

        pluginLinker = new PluginLinker(logger);
        pluginLoader = new PluginLoader(logger);

        pipeLinker = new PipeLinker();
        commands = new PipeCommands();

        compiler = new EarlyPipeShellCompiler(logger, commands, pipeLinker);
        parser = new PipeShellParser(compiler);

        formats = new ObjectFormats();

        batchCommand = new BatchCommand(parser);

        addBaseFormats();
        addBaseCommands();
        addBaseSlots();
    }

    /**
     * Execute a single command.
     * 
     * @param line
     *            Command line
     */
    public synchronized void runCommand(String line) {
        batchCommand.runCommand(line, logger);
    }

    /**
     * Execute all commands in the given batch script file.
     * 
     * @param file
     *            Batch script file
     */
    public synchronized void runCommandFile(File file) throws IOException {
        batchCommand.runFile(file, logger);
    }

    /**
     * Execute all commands in the given batch script file.
     * 
     * @param file
     *            Batch script file path
     */
    public void runCommandFile(String file) throws IOException {
        runCommandFile(new File(file));
    }

    public PluginLinker getPluginLinker() {
        return pluginLinker;
    }

    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public PipeLinker getPipeLinker() {
        return pipeLinker;
    }

    public PipeCommands getPipeCommands() {
        return commands;
    }

    public ObjectFormats getObjectFormats() {
        return formats;
    }

    @Override
    public LineLogger getLineLogger() {
        return logger.getLineLogger();
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        this.logger.setLineLogger(logger);
    }

    private void addBaseFormats() {
        formats.add(StringObjectFormat.INSTANCE);
        formats.add(IntegerObjectFormat.INSTANCE);
        formats.add(LongObjectFormat.INSTANCE);
        formats.add(FloatObjectFormat.INSTANCE);
        formats.add(DoubleObjectFormat.INSTANCE);
    }

    private void addBaseCommands() {
        try {
            commands.add(new PluginCommand(pluginLinker, pluginLoader));
            commands.add(batchCommand);
        } catch (PipeException ex) {
            ex.printStackTrace();
        }
    }

    private void addBaseSlots() {
        pluginLinker.addPluginSlot(new PluginLinkerPluginSlot(pluginLinker));
        pluginLinker.addPluginSlot(new PipeCommandsPluginSlot(commands));
        pluginLinker.addPluginSlot(new ObjectFormatsPluginSlot(formats));
    }
}
