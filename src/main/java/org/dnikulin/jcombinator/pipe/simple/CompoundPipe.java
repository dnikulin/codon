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

package org.dnikulin.jcombinator.pipe.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.pipe.compiler.PipeLinker;
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.core.Producer;
import org.dnikulin.jcombinator.pipe.except.PipeTypeException;

/** A pipe that is implemented by a list of linked pipes. */
public class CompoundPipe implements Pipe {
    private final List<Pipe> pipes;
    private final Consumer first;
    private final Producer last;

    private final AtomicReference<LineLogger> logger;

    /**
     * Construct a compound pipe from the list of pipes. The pipes are linked
     * immediately. Throws IndexOutOfBoundsException if the list is empty.
     * 
     * @param pipes
     *            Pipes to form a compound pipe
     */
    public CompoundPipe(List<Pipe> pipes) throws PipeTypeException {
        this.pipes = new ArrayList<Pipe>(pipes);

        this.first = this.pipes.get(0);
        this.last = this.pipes.get(this.pipes.size() - 1);

        logger = new AtomicReference<LineLogger>(NullLogger.INSTANCE);

        linkPipes();
    }

    /** Connect adjacent pipes in the pipeline. Throws on type error. */
    private void linkPipes() throws PipeTypeException {
        Pipe prev = null;

        for (Pipe pipe : pipes) {
            if (prev != null)
                PipeLinker.linkPipes(prev, pipe);
            prev = pipe;
        }
    }

    // From Consumer

    @Override
    public Class<?> getInputType() {
        return first.getInputType();
    }

    @Override
    public void consume(Object value) {
        first.consume(value);
    }

    // From Producer

    @Override
    public Class<?> getOutputType() {
        return last.getOutputType();
    }

    @Override
    public boolean addConsumer(Consumer consumer) {
        return last.addConsumer(consumer);
    }

    @Override
    public boolean hasConsumer() {
        return last.hasConsumer();
    }

    @Override
    public void removeConsumer(Consumer consumer) {
        last.removeConsumer(consumer);
    }

    @Override
    public void removeConsumers() {
        last.removeConsumers();
    }

    // From LogSource

    @Override
    public LineLogger getLineLogger() {
        return logger.get();
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        this.logger.set(NullLogger.or(logger));
    }

    // From Resettable

    @Override
    public void reset() {
        for (Pipe pipe : pipes)
            pipe.reset();
    }
}
