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

import static org.dnikulin.codon.command.CommandTools.printUsage;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.thread.DaemonThread;
import org.dnikulin.codon.daemon.thread.DaemonThreads;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Consumer;
import org.dnikulin.codon.pipe.command.ProducerCommand;

/** A command to initiate a TestDaemon. */
public class TestDaemonCommand implements ProducerCommand {
    private final DaemonThreads threads;

    /**
     * Create a test daemon command.
     * 
     * @param threads
     *            Daemon thread registry.
     */
    public TestDaemonCommand(DaemonThreads threads) {
        this.threads = threads;
    }

    @Override
    public void produce(String[] args, LineLogger log, Consumer consumer) {
        if (args.length != 2) {
            printUsage(log, this);
            return;
        }

        int ticks = Integer.parseInt(args[0]);
        int sleep = Integer.parseInt(args[1]);

        Daemon daemon = new TestDaemon(log, consumer, ticks, sleep);
        DaemonThread thread = threads.start(daemon);

        log.print("Started test daemon in ID " + thread.getPID());
    }

    @Override
    public String getCommandTopic() {
        return "test";
    }

    @Override
    public String getCommandName() {
        return "testdaemon";
    }

    @Override
    public String getCommandUsage() {
        return "<ticks> <milliseconds>";
    }

    @Override
    public Class<?> getOutputType() {
        return String.class;
    }
}
