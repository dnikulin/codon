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

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.codon.daemon.Daemon;

/** PID-based registry of daemon threads. */
public class DaemonThreads {
    private final List<DaemonThread> threads;

    /** Construct new registry. */
    public DaemonThreads() {
        threads = new ArrayList<DaemonThread>();
    }

    /**
     * Start a daemon thread.
     * 
     * @param daemon
     *            Daemon logic
     * @return Daemon thread
     */
    public synchronized DaemonThread start(Daemon daemon) {
        DaemonThread thread = new DaemonThread(daemon, threads.size());
        threads.add(thread);
        thread.start();
        return thread;
    }

    /**
     * List all daemon threads.
     * 
     * @return List of daemon threads
     */
    public synchronized List<DaemonThread> get() {
        return new ArrayList<DaemonThread>(threads);
    }

    /**
     * Find a daemon thread by its PID.
     * 
     * @param pid
     *            Daemon thread PID
     * @return Daemon thread
     */
    public synchronized DaemonThread get(int pid) {
        return threads.get(pid);
    }
}
