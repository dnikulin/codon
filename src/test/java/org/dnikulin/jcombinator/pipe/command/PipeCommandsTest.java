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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.pipe.command.registry.PipeCommands;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.except.PipeNotFoundException;
import org.dnikulin.jcombinator.pipe.test.TestPipe;
import org.dnikulin.jcombinator.pipe.test.TestPipeCommand;
import org.junit.Test;

public class PipeCommandsTest {
    @Test
    public void testPipeCommands() {
        LineLogger log = NullLogger.INSTANCE;
        PipeCommands commands = new PipeCommands();
        PipeCommand cmd = TestPipeCommand.INSTANCE;

        try {
            // Must be able to register commands with their default names
            commands.add(cmd);

            assertSame(cmd, commands.get(cmd.getCommandName()));
        } catch (PipeNameInvalidException ex) {
            fail();
        } catch (PipeNameInUseException ex) {
            fail();
        } catch (PipeNotFoundException ex) {
            fail();
        }

        try {
            // Must be able to register the same command with multiple names
            commands.add("test1", cmd);
            commands.add("test2", cmd);
            commands.add("test3", cmd);
        } catch (PipeNameInvalidException ex) {
            fail();
        } catch (PipeNameInUseException ex) {
            fail();
        }

        try {
            // Must forbid addition with invalid names
            commands.add("7", cmd);

            // Must not reach
            fail();
        } catch (PipeNameInvalidException ex) {
            // Correct
        } catch (PipeNameInUseException ex) {
            fail();
        }

        try {
            // Must forbid re-addition with same name
            // (even using the same instance)
            commands.add("test1", cmd);

            // Must not reach
            fail();
        } catch (PipeNameInvalidException ex) {
            fail();
        } catch (PipeNameInUseException ex) {
            // Correct
        }

        try {
            // Must allow lookup for correct names
            assertSame(cmd, commands.get("test1"));
            assertSame(cmd, commands.get("test2"));
            assertSame(cmd, commands.get("test3"));
        } catch (PipeNotFoundException ex) {
            fail();
        }

        try {
            // Must throw for lookup with incorrect names
            assertSame(cmd, commands.get("test4"));

            // Must not reach
            fail();
        } catch (PipeNotFoundException ex) {
            // Correct
        }

        try {
            // Must allow construction for correct names and tokens
            Pipe pipe = commands.makePipe("test1", new String[] {}, log);

            assertNotNull(pipe);
            assertTrue(pipe instanceof TestPipe);
            assertSame(Object.class, pipe.getInputType());
            assertSame(Object.class, pipe.getOutputType());
        } catch (PipeNotFoundException ex) {
            fail();
        } catch (PipeFactoryException ex) {
            fail();
        }

        try {
            // Must propagate exceptions from commands
            commands.makePipe("test1", new String[] { "org.NoClass" }, log);

            // Must not reach
            fail();
        } catch (PipeNotFoundException ex) {
            fail();
        } catch (PipeFactoryException ex) {
            // Correct
        }
    }
}
