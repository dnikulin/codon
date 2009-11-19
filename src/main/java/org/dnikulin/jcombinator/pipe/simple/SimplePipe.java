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
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;

/**
 * A simple pipe base class that manages a consumer list and line logger. All of
 * its methods are thread-safe.
 */
public abstract class SimplePipe implements Pipe {
    private final List<Consumer> consumers;
    private final AtomicReference<LineLogger> logger;

    /** Construct a simple pipe with an empty consumer list and a null logger. */
    public SimplePipe() {
        consumers = new ArrayList<Consumer>();
        logger = new AtomicReference<LineLogger>(NullLogger.INSTANCE);
    }

    /**
     * Log a line to the assigned line logger.
     * 
     * @param line
     *            Line to log
     */
    protected void log(String line) {
        logger.get().print(line);
    }

    /**
     * Issue each consumer the given object value.
     * 
     * @param value
     *            Object value to give to consumers.
     */
    protected void produce(Object value) {
        synchronized (consumers) {
            for (Consumer consumer : consumers) {
                try {
                    consumer.consume(value);
                } catch (Exception ex) {
                    log("Consumer exception: " + ex);
                }
            }
        }
    }

    // From Resettable

    @Override
    public void reset() {
        // Do nothing by default
    }

    // From Producer

    @Override
    public boolean addConsumer(Consumer consumer) {
        Class<?> outType = getOutputType();
        Class<?> inType = consumer.getInputType();

        // Must be of compatible type
        if (!inType.isAssignableFrom(outType))
            return false;

        synchronized (consumers) {
            // Must not be already registered
            if (consumers.contains(consumer))
                return false;

            consumers.add(consumer);
        }

        return true;
    }

    @Override
    public boolean hasConsumer() {
        synchronized (consumers) {
            return !consumers.isEmpty();
        }
    }

    @Override
    public void removeConsumer(Consumer consumer) {
        synchronized (consumers) {
            consumers.remove(consumer);
        }
    }

    @Override
    public void removeConsumers() {
        synchronized (consumers) {
            consumers.clear();
        }
    }

    // From LogSource

    @Override
    public void setLineLogger(LineLogger logger) {
        this.logger.set(NullLogger.or(logger));
    }

    @Override
    public LineLogger getLineLogger() {
        return logger.get();
    }
}
