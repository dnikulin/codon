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

package org.dnikulin.codon.daemon.test;

import static org.dnikulin.codon.misc.TimeTools.sleepFor;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.except.DaemonAbortException;
import org.dnikulin.codon.daemon.except.DaemonException;
import org.dnikulin.codon.daemon.except.DaemonExitException;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Consumer;

/** A daemon for testing purposes. */
public class TestDaemon implements Daemon {
    private final LineLogger log;
    private final Consumer consumer;

    private final int ticks;
    private final int sleep;

    private boolean running;
    private int tick;

    /**
     * Construct a test daemon.
     * 
     * @param log
     *            Line logger
     * @param consumer
     *            Value consumer
     * @param ticks
     *            Number of times to resume
     * @param sleep
     *            Number of milliseconds to sleep in each resume
     */
    public TestDaemon(LineLogger log, Consumer consumer, int ticks, int sleep) {
        this.log = log;
        this.consumer = consumer;

        this.ticks = ticks;
        this.sleep = sleep;

        this.running = true;
        this.tick = 0;
    }

    /**
     * Query number of times the daemon resumed.
     * 
     * @return Resume count
     */
    public int getTicksDone() {
        return tick;
    }

    @Override
    public String getDaemonName() {
        return "Test daemon (" + ticks + " x " + sleep + " ms)";
    }

    @Override
    public void resumeDaemon() throws DaemonException {
        if (running == false) {
            log.print("Aborted");
            throw new DaemonAbortException();
        }

        if (tick >= ticks) {
            log.print("All ticks completed");
            throw new DaemonExitException();
        }

        sleepFor(sleep);
        tick++;

        String line = "Completed " + tick + " ticks";
        log.print(line);
        consumer.consume(line);
    }

    @Override
    public void cancel() {
        running = false;
    }
}
