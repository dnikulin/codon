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

package org.dnikulin.codon.pipe.command.wrap;

import org.dnikulin.codon.command.EffectCommand;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Pipe;
import org.dnikulin.codon.pipe.command.PipeCommand;
import org.dnikulin.codon.pipe.except.PipeFactoryException;
import org.dnikulin.codon.pipe.nulled.NullPipe;

/**
 * A pipe command that allows an effect command to be executed as part of a
 * pipeline.
 */
public class EffectPipeCommand implements PipeCommand, EffectCommand {
    private final EffectCommand command;

    /**
     * Wrap an effect command in a pipe command.
     * 
     * @param command
     *            Effect command
     */
    public EffectPipeCommand(EffectCommand command) {
        this.command = command;
    }

    @Override
    public void execute(String[] args, LineLogger log) {
        command.execute(args, log);
    }

    @Override
    public Pipe makePipe(String[] args, LineLogger log)
            throws PipeFactoryException {

        command.execute(args, log);
        return NullPipe.INSTANCE;
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
