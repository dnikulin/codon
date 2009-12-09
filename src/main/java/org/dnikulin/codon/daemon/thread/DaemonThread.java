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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.except.DaemonAbortException;
import org.dnikulin.codon.daemon.except.DaemonExitException;
import org.dnikulin.codon.misc.Cancellable;

/** A single thread executing a single daemon. */
public class DaemonThread extends Thread implements Cancellable {
    /** Daemon logic has not been started. */
    public static final int WAITING = 0;
    /** Daemon logic is executing normally. */
    public static final int RUNNING = 1;
    /** Daemon logic has completed normally. */
    public static final int COMPLETED = 2;
    /** Daemon thread or logic was aborted. */
    public static final int ABORTED = 3;
    /** Daemon logic threw an exception. */
    public static final int ERROR = 4;

    private final Daemon daemon;
    private final int pid;
    private final AtomicBoolean running;
    private final AtomicInteger state;

    /**
     * Construct a daemon thread.
     * 
     * @param daemon
     *            Daemon logic
     * @param pid
     *            Process identifier
     */
    public DaemonThread(Daemon daemon, int pid) {
        super("codon-daemon-" + pid);
        setDaemon(true);

        this.daemon = daemon;
        this.pid = pid;
        this.running = new AtomicBoolean(true);
        this.state = new AtomicInteger(WAITING);
    }

    /**
     * Query daemon thread process identifier (PID).
     * 
     * @return Daemon thread PID
     */
    public int getPID() {
        return pid;
    }

    /**
     * Query daemon name.
     * 
     * @return Daemon name
     */
    public String getDaemonName() {
        return daemon.getDaemonName();
    }

    /**
     * Query daemon thread state.
     * 
     * @return Daemon thread state
     */
    public int getDaemonState() {
        return state.get();
    }

    /**
     * Query daemon termination state. A daemon is "running" if it has not
     * exited, been aborted or thrown an error.
     * 
     * @return true iff the daemon logic is waiting or running.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Abort the daemon thread. If the daemon logic has not yet completed, its
     * state will be considered aborted.
     */
    @Override
    public void cancel() {
        running.set(false);
    }

    /**
     * Suspend execution of the current thread until the daemon thread has
     * terminated in some way. Note that daemon logic may block indefinitely.
     */
    public void waitForJoin() {
        while (true) {
            try {
                join();
                return;
            } catch (InterruptedException ex) {
                // Ignore
            }
        }
    }

    @Override
    public void run() {
        try {
            // Only transition here once
            if (state.compareAndSet(WAITING, RUNNING) == false)
                return;

            // Loop until cancel or exception
            while (isRunning())
                daemon.resumeDaemon();

            // To reach here, must have been cancel()ed
            daemon.cancel();
            state.compareAndSet(RUNNING, ABORTED);
        } catch (DaemonExitException ex) {
            state.compareAndSet(RUNNING, COMPLETED);
        } catch (DaemonAbortException ex) {
            state.compareAndSet(RUNNING, ABORTED);
        } catch (Exception ex) {
            state.compareAndSet(RUNNING, ERROR);
        } finally {
            running.set(false);
        }
    }
}
