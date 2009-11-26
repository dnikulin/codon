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

package org.dnikulin.jcombinator.pipe.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.pipe.command.registry.PipeCommands;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.pipe.except.PipeSyntaxException;
import org.dnikulin.jcombinator.pipe.simple.CompoundPipe;

/** A pipe compiler that performs all actions as early as possible. */
public class EarlyPipeShellCompiler implements PipeShellCompiler {
    private final LineLogger logger;

    private final PipeCommands commands;
    private final PipeLinker linker;

    private final Stack<List<Pipe>> lineStack;
    private final Stack<String> nameStack;

    private String pipeName;

    /**
     * Construct a compiler with the given logger, command registry and pipe
     * linker.
     * 
     * @param logger
     *            Line logger
     * @param commands
     *            Pipe command registry
     * @param linker
     *            Pipe linker
     */
    public EarlyPipeShellCompiler(LineLogger logger, PipeCommands commands,
            PipeLinker linker) {

        this.logger = logger;
        this.commands = commands;
        this.linker = linker;

        lineStack = new Stack<List<Pipe>>();
        nameStack = new Stack<String>();
        pipeName = null;
    }

    /**
     * Construct a compiler with the given command registry and pipe linker. A
     * null logger is used.
     * 
     * @param commands
     *            Pipe command registry
     * @param linker
     *            Pipe linker
     */
    public EarlyPipeShellCompiler(PipeCommands commands, PipeLinker linker) {
        this(NullLogger.INSTANCE, commands, linker);
    }

    /**
     * Query pipe command registry.
     * 
     * @return Associated pipe command registry
     */
    public PipeCommands getPipeCommands() {
        return commands;
    }

    /**
     * Query pipe linker.
     * 
     * @return Associated pipe linker
     */
    public PipeLinker getPipeLinker() {
        return linker;
    }

    private void reset() {
        lineStack.clear();
        nameStack.clear();
        pipeName = null;
    }

    @Override
    public void startCompile() {
        reset();

        lineStack.push(new ArrayList<Pipe>());
    }

    @Override
    public void stopCompile() throws PipeException {
        // Count as an implicit link point
        takePipeLink();

        assert (lineStack.size() >= 1);

        while (lineStack.size() > 1)
            takeGroupEnd();

        if (!lineStack.peek().isEmpty())
            finishCurrentLine();

        reset();
    }

    @Override
    public void takeCommand(String command, String[] tokens)
            throws PipeException {

        Pipe pipe = commands.makePipe(command, tokens, logger);

        List<Pipe> line = lineStack.peek();
        line.add(pipe);

        if (pipeName != null) {
            linker.addPipe(pipeName, pipe);
            pipeName = null;
        }
    }

    @Override
    public void takePipeName(String name) throws PipeException {
        if (pipeName != null)
            throw new PipeSyntaxException("Pipe name following pipe name");

        pipeName = name;
    }

    @Override
    public void takePipeLink() throws PipeException {
        if (pipeName != null) {
            lineStack.peek().add(linker.getPipe(pipeName));
            pipeName = null;
        }
    }

    @Override
    public void takeGroupStart() throws PipeException {
        nameStack.push(pipeName);
        lineStack.push(new ArrayList<Pipe>());

        pipeName = null;
    }

    @Override
    public void takeGroupEnd() throws PipeException {
        if (lineStack.size() < 2)
            throw new PipeSyntaxException("Unmatched pipeline end");

        String name = nameStack.pop();
        Pipe pipe = finishCurrentLine();

        lineStack.peek().add(pipe);

        if (name != null)
            linker.addPipe(name, pipe);
    }

    private Pipe finishCurrentLine() throws PipeException {
        List<Pipe> line = lineStack.pop();

        if (line.isEmpty())
            throw new PipeSyntaxException("Empty pipeline");

        if (line.size() == 1)
            return line.get(0);

        return new CompoundPipe(line);
    }
}
