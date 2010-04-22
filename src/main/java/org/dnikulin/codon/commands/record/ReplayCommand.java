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

import static org.dnikulin.codon.command.CommandTools.printPipeUsage;

import org.dnikulin.codon.daemon.thread.DaemonThreads;
import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectFormatNotFoundException;
import org.dnikulin.codon.format.registry.ObjectFormats;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Pipe;
import org.dnikulin.codon.pipe.command.PipeCommand;
import org.dnikulin.codon.pipe.except.PipeFactoryException;

public class ReplayCommand implements PipeCommand {
    private final ObjectFormats formats;
    private final DaemonThreads threads;

    public ReplayCommand(ObjectFormats formats, DaemonThreads threads) {
        this.formats = formats;
        this.threads = threads;
    }

    @Override
    public Pipe makePipe(String[] args, LineLogger log)
            throws PipeFactoryException {

        if (args.length != 2)
            return printPipeUsage(log, this);

        String formatName = args[0];
        String path = args[1];

        try {
            ObjectFormat format = formats.getByName(formatName);
            return new ReplayDaemonPipe(threads, log, format, path);
        } catch (ObjectFormatNotFoundException ex) {
            log.print("Unknown format '" + formatName + "'");
            throw new PipeFactoryException(ex);
        }
    }

    @Override
    public String getCommandTopic() {
        return "record";
    }

    @Override
    public String getCommandName() {
        return "replay";
    }

    @Override
    public String getCommandUsage() {
        return "<format> <path>";
    }
}
