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

package org.dnikulin.jcombinator.pipe.compiler;

import org.dnikulin.jcombinator.pipe.except.PipeException;

/** A pipe compiler instructed by parsed shell lines. */
public interface PipeShellCompiler {
    /** Start compiling a new line. */
    public void startCompile();

    /** Finish compiling a line. */
    public void stopCompile() throws PipeException;

    /** Process a complete single-pipe command. */
    public void takeCommand(String command, String[] tokens)
            throws PipeException;

    /**
     * Store a pipe name. This may be a reference to an existing pipe, or a name
     * for a new pipe, depending on following calls.
     */
    public void takePipeName(String name) throws PipeException;

    /** Interpret an explicit pipe link. */
    public void takePipeLink() throws PipeException;

    /** Start a compound pipe. Should act as an implicit link point. */
    public void takeGroupStart() throws PipeException;

    /** End a compound pipe. Should act as an implicit link point. */
    public void takeGroupEnd() throws PipeException;
}
