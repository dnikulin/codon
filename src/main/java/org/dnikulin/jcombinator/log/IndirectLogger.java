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

package org.dnikulin.jcombinator.log;

import java.util.concurrent.atomic.AtomicReference;

/** A trivial thread-safe LineLogger wrapper. */
public class IndirectLogger implements LineLogger, LogSource {
    private final AtomicReference<LineLogger> logger;

    /**
     * Initialise with the given logger.
     * 
     * @param logger
     *            Line logger
     */
    public IndirectLogger(LineLogger logger) {
        this.logger = new AtomicReference<LineLogger>(logger);
    }

    /** Initialise with a null logger. */
    public IndirectLogger() {
        this(NullLogger.INSTANCE);
    }

    @Override
    public void print(String line) {
        logger.get().print(line);
    }

    @Override
    public LineLogger getLineLogger() {
        return logger.get();
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        this.logger.set(NullLogger.or(logger));
    }
}
