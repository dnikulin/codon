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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** A pipe for test use only. */
public class TestPipe extends SimplePipe {
    private final Class<?> inputType;
    private final Class<?> outputType;

    private final AtomicBoolean pass;
    private final AtomicLong counter;
    private Object lastValue;

    /**
     * Construct a test pipe with the given input and output types.
     * 
     * @param inputType
     *            Input type (returned by getInputType())
     * @param outputType
     *            Output type (returned by getOutputType())
     */
    public TestPipe(Class<?> inputType, Class<?> outputType) {
        this.inputType = inputType;
        this.outputType = outputType;

        pass = new AtomicBoolean(false);
        counter = new AtomicLong(0);
        lastValue = null;
    }

    /**
     * Construct a test pipe Object as its input and output types.
     */
    public TestPipe() {
        this(Object.class, Object.class);
    }

    @Override
    public Class<?> getInputType() {
        return inputType;
    }

    @Override
    public Class<?> getOutputType() {
        return outputType;
    }

    /**
     * Configure pipe to produce values it consumes, and log for each
     * production.
     * 
     * @param pass
     *            If true, produce, if false, ignore
     */
    public void setPass(boolean pass) {
        this.pass.set(pass);
    }

    /**
     * Query whether the pipe is configured to produce values it consumes.
     * 
     * @return true if configured to produce
     */
    public boolean passes() {
        return pass.get();
    }

    /**
     * Increment consume() counter and save value.
     * 
     * @param value
     *            Object value stored for lastValue()
     */
    @Override
    public void consume(Object value) {
        assert (inputType.isAssignableFrom(value.getClass()));

        counter.incrementAndGet();
        lastValue = value;

        if (pass.get() == true) {
            produce(value);
            log("Passed " + value);
        }
    }

    /**
     * Query last value given to consume().
     * 
     * @return Last value given to consume()
     */
    public Object last() {
        return lastValue;
    }

    /**
     * Query consume() counter.
     * 
     * @return Number of times consume() was called
     */
    public long count() {
        return counter.get();
    }

    /**
     * Reset consume() counter to given count.
     * 
     * @param count
     *            New count
     */
    public void resetCount(long count) {
        counter.set(count);
    }

    /**
     * Reset consume() counter to 0.
     */
    @Override
    public void reset() {
        resetCount(0);
    }
}
