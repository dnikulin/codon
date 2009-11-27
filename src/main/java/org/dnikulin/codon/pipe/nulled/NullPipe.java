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

package org.dnikulin.codon.pipe.nulled;

import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.log.NullLogger;
import org.dnikulin.codon.pipe.core.Consumer;
import org.dnikulin.codon.pipe.core.Pipe;

/** A pipe that performs no function and maintains no connections or state. */
public class NullPipe implements Pipe {
    /** Singleton instance. */
    public static final NullPipe INSTANCE = new NullPipe();

    // From Consumer

    @Override
    public Class<?> getInputType() {
        return NullPipeType.class;
    }

    @Override
    public void consume(Object value) {
        // Do nothing
    }

    // From Producer

    @Override
    public Class<?> getOutputType() {
        return NullPipeType.class;
    }

    @Override
    public boolean addConsumer(Consumer consumer) {
        return false;
    }

    @Override
    public boolean hasConsumer() {
        return false;
    }

    @Override
    public void removeConsumer(Consumer consumer) {
        // Do nothing
    }

    @Override
    public void removeConsumers() {
        // Do nothing
    }

    // From LogSource

    @Override
    public LineLogger getLineLogger() {
        return NullLogger.INSTANCE;
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        // Do nothing
    }

    // From Resettable

    @Override
    public void reset() {
        // Do nothing
    }
}
