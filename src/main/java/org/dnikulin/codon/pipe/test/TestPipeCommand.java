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

package org.dnikulin.codon.pipe.test;

import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Pipe;
import org.dnikulin.codon.pipe.command.PipeCommand;
import org.dnikulin.codon.pipe.except.PipeFactoryException;

/** A pipe command that produces instances of TestPipe. */
public class TestPipeCommand implements PipeCommand {
    /** Stateless singleton instance. */
    public static final TestPipeCommand INSTANCE = new TestPipeCommand();

    @Override
    public Pipe makePipe(String[] args, LineLogger log)
            throws PipeFactoryException {

        switch (args.length) {
        case 0:
            return new TestPipe();

        case 1:
            return makePipe(args[0], args[0]);

        case 2:
            return makePipe(args[0], args[1]);

        default:
            throw new PipeFactoryException("Usage: " + getCommandUsage());
        }
    }

    /**
     * Construct a TestPipe using classes loaded by name. This uses the
     * ClassLoader that loaded TestPipeCommand.
     * 
     * @param inputClassName
     *            Input type class name
     * @param outputClassName
     *            Output type class name
     * @return TestPipe instance
     */
    public static Pipe makePipe(String inputClassName, String outputClassName)
            throws PipeFactoryException {

        // TODO: Enable use of specific ClassLoader
        ClassLoader loader = TestPipeCommand.class.getClassLoader();

        try {
            Class<?> inputClass = loader.loadClass(inputClassName);
            Class<?> outputClass = loader.loadClass(outputClassName);

            return new TestPipe(inputClass, outputClass);
        } catch (ClassNotFoundException ex) {
            String msg = "Could not create TestPipe with given types";
            throw new PipeFactoryException(msg, ex);
        }
    }

    @Override
    public String getCommandTopic() {
        return "test";
    }

    @Override
    public String getCommandName() {
        return "testpipe";
    }

    @Override
    public String getCommandUsage() {
        return "[itype [otype]]";
    }
}
