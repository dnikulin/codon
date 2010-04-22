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

package org.dnikulin.codon.commands.record;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.thread.DaemonThreads;
import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.log.IndirectLogger;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Consumer;
import org.dnikulin.codon.pipe.nulled.NullPipe;
import org.dnikulin.codon.pipe.record.ReplayDaemon;

/** A pipe that creates a replay daemon for every consumer added. */
public class ReplayDaemonPipe extends NullPipe {
    private final DaemonThreads threads;
    private final IndirectLogger log;
    private final ObjectFormat format;
    private final String path;

    /**
     * Construct a replay daemon pipe.
     * 
     * @param threads
     *            Daemon thread registry
     * @param log
     *            Line logger
     * @param format
     *            Object format
     * @param path
     *            File path
     */
    public ReplayDaemonPipe(DaemonThreads threads, LineLogger log,
            ObjectFormat format, String path) {
        this.threads = threads;
        this.log = new IndirectLogger(log);
        this.format = format;
        this.path = path;
    }

    @Override
    public Class<?> getOutputType() {
        return format.getObjectClass();
    }

    @Override
    public boolean addConsumer(Consumer consumer) {
        try {
            InputStream input = new FileInputStream(path);
            Daemon daemon = new ReplayDaemon(consumer, log, format, input, path);
            threads.start(daemon);
            return true;
        } catch (IOException ex) {
            log.print("Could not start replay: " + ex.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public LineLogger getLineLogger() {
        return log.getLineLogger();
    }

    @Override
    public void setLineLogger(LineLogger logger) {
        this.log.setLineLogger(logger);
    }
}
