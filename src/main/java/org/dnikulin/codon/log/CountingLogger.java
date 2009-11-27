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

package org.dnikulin.codon.log;

import java.util.concurrent.atomic.AtomicLong;

import org.dnikulin.codon.misc.Resettable;

/** A LineLogger that only counts its invocations. */
public class CountingLogger implements LineLogger, Resettable {
    private final AtomicLong prints;

    /** Construct a LineLogger with a zero print count. */
    public CountingLogger() {
        prints = new AtomicLong(0);
    }

    /**
     * Reset print count.
     * 
     * @param count
     *            New print count
     */
    public void setCount(long count) {
        prints.set(count);
    }

    /** Reset print count to 0. */
    @Override
    public void reset() {
        setCount(0);
    }

    /**
     * Query print count.
     * 
     * @return Current print count
     */
    public long count() {
        return prints.get();
    }

    /**
     * Increment count.
     * 
     * @param line
     *            Print line (ignored)
     */
    @Override
    public void print(String line) {
        prints.incrementAndGet();
    }
}
