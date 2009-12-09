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

package org.dnikulin.codon.daemon.thread;

import static org.dnikulin.codon.daemon.thread.DaemonThread.ABORTED;
import static org.dnikulin.codon.daemon.thread.DaemonThread.COMPLETED;
import static org.dnikulin.codon.daemon.thread.DaemonThread.WAITING;
import static org.dnikulin.codon.misc.TimeTools.sleepFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dnikulin.codon.daemon.test.TestDaemon;
import org.dnikulin.codon.log.NullLogger;
import org.dnikulin.codon.pipe.test.TestPipe;
import org.junit.Test;

public class DaemonThreadTest {
    private static final NullLogger LOG = NullLogger.INSTANCE;
    private static final int ID = 7;
    private static final int TICKS = 9;

    @Test
    public void testExit() {
        TestPipe consumer = new TestPipe();
        TestDaemon daemon = new TestDaemon(LOG, consumer, TICKS, 1);
        DaemonThread thread = new DaemonThread(daemon, ID);

        // Must keep ID and name
        assertEquals(ID, thread.getPID());
        assertEquals(daemon.getDaemonName(), thread.getDaemonName());

        // Must not have started yet
        assertEquals(0, daemon.getTicksDone());
        assertEquals(WAITING, thread.getDaemonState());
        assertTrue(thread.isRunning());

        // Start and wait for completion
        thread.start();
        thread.waitForJoin();

        // Must have completed all loops
        assertEquals(TICKS, daemon.getTicksDone());
        assertEquals(TICKS, consumer.count());
        assertEquals(COMPLETED, thread.getDaemonState());
        assertFalse(thread.isRunning());
    }

    @Test
    public void testAbort() {
        TestPipe consumer = new TestPipe();
        TestDaemon daemon = new TestDaemon(LOG, consumer, Integer.MAX_VALUE, 10);
        DaemonThread thread = new DaemonThread(daemon, ID);

        // Must keep ID and name
        assertEquals(ID, thread.getPID());
        assertEquals(daemon.getDaemonName(), thread.getDaemonName());

        // Must not have started yet
        assertEquals(0, daemon.getTicksDone());
        assertEquals(WAITING, thread.getDaemonState());
        assertTrue(thread.isRunning());

        // Start and abort soon after
        thread.start();
        sleepFor(10);
        thread.cancel();
        thread.waitForJoin();

        // Must record aborted state
        assertEquals(ABORTED, thread.getDaemonState());
        assertFalse(thread.isRunning());
    }
}
