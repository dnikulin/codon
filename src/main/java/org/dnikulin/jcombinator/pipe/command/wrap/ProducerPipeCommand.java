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

package org.dnikulin.jcombinator.pipe.command.wrap;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.pipe.command.PipeCommand;
import org.dnikulin.jcombinator.pipe.command.ProducerCommand;
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;

/**
 * A pipe command that allows a producer command to be executed as part of a
 * pipeline.
 */
public class ProducerPipeCommand implements PipeCommand, ProducerCommand {
    private final ProducerCommand command;

    /**
     * Wrap a producer command in a pipe command.
     * 
     * @param command
     *            Producer command
     */
    public ProducerPipeCommand(ProducerCommand command) {
        this.command = command;
    }

    @Override
    public Class<?> getOutputType() {
        return command.getOutputType();
    }

    @Override
    public void produce(String[] args, LineLogger log, Consumer consumer) {
        command.produce(args, log, consumer);
    }

    @Override
    public Pipe makePipe(String[] args, LineLogger log)
            throws PipeFactoryException {
        return new ProducerCommandPipe(command, args, log);
    }

    @Override
    public String getCommandTopic() {
        return command.getCommandTopic();
    }

    @Override
    public String getCommandName() {
        return command.getCommandName();
    }

    @Override
    public String getCommandUsage() {
        return command.getCommandUsage();
    }
}
