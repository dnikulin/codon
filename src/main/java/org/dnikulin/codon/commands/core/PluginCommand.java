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

package org.dnikulin.codon.commands.core;

import static org.dnikulin.codon.command.CommandTools.printUsage;

import java.io.File;

import org.dnikulin.codon.command.EffectCommand;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.plugin.PluginLinker;
import org.dnikulin.codon.plugin.PluginLoader;

/** Command to import and install plugins. */
public class PluginCommand implements EffectCommand {
    private final PluginLinker linker;
    private final PluginLoader loader;

    /**
     * Construct a plugin command with the given plugin linker and loader.
     * 
     * @param linker
     *            Plugin Linker
     * @param loader
     *            Plugin loader
     */
    public PluginCommand(PluginLinker linker, PluginLoader loader) {
        this.linker = linker;
        this.loader = loader;
    }

    @Override
    public void execute(String[] args, LineLogger log) {
        if (args.length != 1) {
            printUsage(log, this);
            return;
        }

        String path = args[0];
        File file = new File(path);

        if (!file.exists()) {
            log.print("Path '" + path + "' does not exist");
            return;
        }

        // importTree performs importJar automatically
        loader.importTree(file);
        loader.givePluginLinkerNodes(linker);
    }

    @Override
    public String getCommandTopic() {
        return "core";
    }

    @Override
    public String getCommandName() {
        return "plugin";
    }

    @Override
    public String getCommandUsage() {
        return "<jar or directory>";
    }
}
