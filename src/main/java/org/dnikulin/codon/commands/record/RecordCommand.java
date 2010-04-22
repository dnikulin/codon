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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectFormatNotFoundException;
import org.dnikulin.codon.format.registry.ObjectFormats;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Pipe;
import org.dnikulin.codon.pipe.command.PipeCommand;
import org.dnikulin.codon.pipe.except.PipeFactoryException;
import org.dnikulin.codon.pipe.record.RecordPipe;

public class RecordCommand implements PipeCommand {
    private final ObjectFormats formats;

    public RecordCommand(ObjectFormats formats) {
        this.formats = formats;
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
            OutputStream output = new FileOutputStream(path);
            return new RecordPipe(log, format, output);
        } catch (ObjectFormatNotFoundException ex) {
            log.print("Unknown format '" + formatName + "'");
            throw new PipeFactoryException(ex);
        } catch (IOException ex) {
            log.print("Could not open '" + path + "' for writing");
            throw new PipeFactoryException(ex);
        }
    }

    @Override
    public String getCommandTopic() {
        return "record";
    }

    @Override
    public String getCommandName() {
        return "record";
    }

    @Override
    public String getCommandUsage() {
        return "<format> <path>";
    }
}
