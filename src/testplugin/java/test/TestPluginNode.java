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

package test;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.pipe.command.PipeCommand;
import org.dnikulin.jcombinator.pipe.command.registry.PipeCommands;
import org.dnikulin.jcombinator.pipe.command.registry.PipeCommandsPluginNode;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.nulled.NullPipe;
import org.dnikulin.jcombinator.plugin.PluginNode;

public class TestPluginNode implements PipeCommandsPluginNode, PipeCommand {

    @Override
    public String getPluginName() {
        return "Test plugin node";
    }

    @Override
    public String getPluginVersion() {
        return "0";
    }

    @Override
    public void addPipeCommands(PipeCommands commands)
            throws PipeNameInvalidException, PipeNameInUseException {
        commands.add(this);
    }

    @Override
    public String getCommandTopic() {
        return "test";
    }

    @Override
    public String getCommandName() {
        return "testplug";
    }

    @Override
    public String getCommandUsage() {
        return "";
    }

    @Override
    public Pipe makePipe(String[] args, LineLogger log)
            throws PipeFactoryException {

        log.print("Test plugin working");
        return NullPipe.INSTANCE;
    }
}
