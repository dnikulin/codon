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

package org.dnikulin.jcombinator.pipe.command;

import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.plugin.PluginNode;
import org.dnikulin.jcombinator.plugin.PluginSlot;

/** A plugin slot allowing pipe commands to be added to a registry. */
public class PipeCommandsPluginSlot implements PluginSlot {
    private final PipeCommands commands;

    /**
     * Construct a plugin slot with the given command registry.
     * 
     * @param commands
     *            Command registry
     */
    public PipeCommandsPluginSlot(PipeCommands commands) {
        this.commands = commands;
    }

    @Override
    public String getPluginSlotName() {
        return "Pipe commands";
    }

    @Override
    public Class<? extends PluginNode> getPluginInterface() {
        return PipeCommandsPluginNode.class;
    }

    @Override
    public void installPlugin(PluginNode plugin) {
        try {
            ((PipeCommandsPluginNode) plugin).addPipeCommands(commands);
        } catch (PipeException ex) {
            ex.printStackTrace();
        }
    }
}
