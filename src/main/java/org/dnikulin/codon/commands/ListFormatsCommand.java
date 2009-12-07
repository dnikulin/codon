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

package org.dnikulin.codon.commands;

import org.dnikulin.codon.command.EffectCommand;
import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.registry.ObjectFormats;
import org.dnikulin.codon.log.LineLogger;

/** Command to list installed object formats. */
public class ListFormatsCommand implements EffectCommand {
    private final ObjectFormats formats;

    /**
     * Construct the command with the given format registry.
     * 
     * @param formats
     *            Format registry
     */
    public ListFormatsCommand(ObjectFormats formats) {
        this.formats = formats;
    }

    @Override
    public void execute(String[] args, LineLogger log) {
        StringBuilder out = new StringBuilder();

        out.append("Supported object formats:\n");

        try {
            for (ObjectFormat fmt : formats.getFormats()) {
                String name = fmt.getFormatName();
                String className = fmt.getObjectClass().getSimpleName();

                String line = "  " + name + " (" + className + ")\n";
                out.append(line);
            }
        } finally {
            log.print(out.toString());
        }
    }

    @Override
    public String getCommandTopic() {
        return "help";
    }

    @Override
    public String getCommandName() {
        return "listformats";
    }

    @Override
    public String getCommandUsage() {
        return "";
    }
}
