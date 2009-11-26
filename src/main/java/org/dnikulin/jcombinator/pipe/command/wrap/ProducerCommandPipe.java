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

import org.dnikulin.jcombinator.log.IndirectLogger;
import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.pipe.command.ProducerCommand;
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.nulled.NullPipe;

/**
 * A wrapper pipe that allows a producer command to be executed as part of a
 * pipeline.
 */
public class ProducerCommandPipe extends NullPipe {
    private final ProducerCommand command;
    private final String[] arguments;
    private final IndirectLogger logger;

    /**
     * Create a pipe that executes the given command whenever addConsumer() is
     * called.
     * 
     * @param command
     *            Producer command
     * @param arguments
     *            Command arguments
     * @param logger
     *            Initial line logger
     */
    public ProducerCommandPipe(ProducerCommand command, String[] arguments,
            LineLogger logger) {
        this.command = command;
        this.arguments = arguments;
        this.logger = new IndirectLogger(logger);
    }

    @Override
    public Class<?> getOutputType() {
        return command.getOutputType();
    }

    @Override
    public boolean addConsumer(Consumer consumer) {
        command.produce(arguments, logger, consumer);
        return true;
    }

    @Override
    public LineLogger getLineLogger() {
        return logger.getLineLogger();
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        this.logger.setLineLogger(logger);
    }
}
