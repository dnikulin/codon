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

package org.dnikulin.jcombinator.pipe.engine;

import org.dnikulin.jcombinator.pipe.except.PipeException;

/** A PipeShellCompiler that ignores all input. */
public class NullPipeShellCompiler implements PipeShellCompiler {
    /** Singleton instance. */
    public static final NullPipeShellCompiler INSTANCE = new NullPipeShellCompiler();

    @Override
    public void startCompile() {
        // Do nothing
    }

    @Override
    public void stopCompile() throws PipeException {
        // Do nothing
    }

    @Override
    public void takeCommand(String command, String[] tokens)
            throws PipeException {
        // Do nothing
    }

    @Override
    public void takePipeName(String name) throws PipeException {
        // Do nothing
    }

    @Override
    public void takePipeLink() throws PipeException {
        // Do nothing
    }

    @Override
    public void takeGroupEnd() throws PipeException {
        // Do nothing
    }

    @Override
    public void takeGroupStart() throws PipeException {
        // Do nothing
    }
}
