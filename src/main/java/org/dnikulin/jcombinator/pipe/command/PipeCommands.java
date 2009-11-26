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

package org.dnikulin.jcombinator.pipe.command;

import java.util.Map;
import java.util.TreeMap;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.pipe.compiler.PipeLinker;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.except.PipeNotFoundException;

/** Pipe command registry, invokes pipe commands by name. */
public class PipeCommands {
    private final Map<String, PipeCommand> commands;

    /** Construct empty registry. */
    public PipeCommands() {
        commands = new TreeMap<String, PipeCommand>();
    }

    /**
     * Register a pipe command with a specific name.
     * 
     * @param commandName
     *            Command name to register
     * @param command
     *            Command object to register
     */
    public synchronized void add(String commandName, PipeCommand command)
            throws PipeNameInvalidException, PipeNameInUseException {

        if (!PipeLinker.isPipeNameValid(commandName))
            throw new PipeNameInvalidException("Command name is invalid");

        if (commands.containsKey(commandName))
            throw new PipeNameInUseException("Command name is already in use");

        commands.put(commandName, command);
    }

    /**
     * Register a pipe command with its default name.
     * 
     * @param command
     *            Command object to register
     */
    public void add(PipeCommand command) throws PipeNameInvalidException,
            PipeNameInUseException {

        add(command.getCommandName(), command);
    }

    /**
     * Confirm the presence of a pipe command.
     * 
     * @param commandName
     *            Command name to search
     * @return true iff the command name is in use
     */
    public synchronized boolean has(String commandName) {
        return commands.containsKey(commandName);
    }

    /**
     * Find a pipe command. Throws PipeNotFoundException if none found.
     * 
     * @param commandName
     *            Command name to search
     * @return Pipe command object
     */
    public synchronized PipeCommand get(String commandName)
            throws PipeNotFoundException {
        PipeCommand command = commands.get(commandName);
        if (command == null)
            throw new PipeNotFoundException("Command not found");
        return command;
    }

    /**
     * Invoke a pipe command. Throws PipeNotFoundException if the command is not
     * found. Throws PipeFactoryException if pipe creation failed.
     * 
     * @param commandName
     *            Command name to search
     * @param tokens
     *            Argument tokens
     * 
     * @return Pipe object
     */
    public Pipe makePipe(String commandName, String[] tokens, LineLogger log)
            throws PipeNotFoundException, PipeFactoryException {
        return get(commandName).makePipe(tokens, log);
    }
}
