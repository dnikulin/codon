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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.dnikulin.jcombinator.pipe.command.PipeCommands;
import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.except.PipeSyntaxException;
import org.dnikulin.jcombinator.pipe.simple.CompoundPipe;
import org.dnikulin.jcombinator.pipe.test.TestPipe;
import org.dnikulin.jcombinator.pipe.test.TestPipeCommand;
import org.junit.Test;

public class EarlyPipeShellCompilerTest {
    public static final String[] EMPTY = new String[] {};

    @Test
    public void testCorrectSyntax() {
        final String single1Name = "single1";
        final String single2Name = "single2";
        final String group1Name = "group1";
        final String group2Name = "group2";

        try {
            PipeCommands commands = makeTestCommands();
            PipeLinker linker = new PipeLinker();

            EarlyPipeShellCompiler compiler = new EarlyPipeShellCompiler(
                    commands, linker);

            // Must associate commands and linker correctly
            assertSame(commands, compiler.getPipeCommands());
            assertSame(linker, compiler.getPipeLinker());

            compiler.startCompile();

            // Must immediately create single pipes
            compiler.takePipeName(single1Name);
            compiler.takeCommand("test", EMPTY);
            assertTrue(linker.getPipe(single1Name) instanceof TestPipe);

            // Must link to a group
            compiler.takePipeLink();

            // Must create named group pipes
            compiler.takePipeName(group1Name);
            compiler.takeGroupStart();

            // Must immediately create single pipes inside a group
            compiler.takePipeName(single2Name);
            compiler.takeCommand("test", EMPTY);
            assertTrue(linker.getPipe(single2Name) instanceof TestPipe);

            // Must immediately created unnamed single pipes inside a group
            compiler.takePipeLink();
            compiler.takeCommand("test", EMPTY);

            // Must create named nested group
            compiler.takePipeName(group2Name);
            compiler.takeGroupStart();
            compiler.takeCommand("test", EMPTY);
            // Must allow link to nothing
            compiler.takePipeLink();
            compiler.takeGroupEnd();
            compiler.takePipeLink();

            // Must implicitly close groups
            compiler.stopCompile();

            // Must now have both group pipes
            // Because it contains only one pipe,
            // the second group must skip CompoundPipe
            assertTrue(linker.getPipe(group1Name) instanceof CompoundPipe);
            assertTrue(linker.getPipe(group2Name) instanceof TestPipe);
        } catch (PipeException ex) {
            fail();
        }
    }

    @Test
    public void testDoubleName() {
        try {
            PipeCommands commands = makeTestCommands();
            PipeLinker linker = new PipeLinker();

            EarlyPipeShellCompiler compiler = new EarlyPipeShellCompiler(
                    commands, linker);

            compiler.startCompile();

            // Must fail if names appear adjacently
            compiler.takePipeName("test1");
            compiler.takePipeName("test2");

            // Must not reach
            fail();
        } catch (PipeSyntaxException ex) {
            // Correct
        } catch (PipeException ex) {
            fail();
        }
    }

    @Test
    public void testEmptyGroup() {
        try {
            PipeCommands commands = makeTestCommands();
            PipeLinker linker = new PipeLinker();

            EarlyPipeShellCompiler compiler = new EarlyPipeShellCompiler(
                    commands, linker);

            compiler.startCompile();

            // Must fail if a group is closed before it is populated
            compiler.takeGroupStart();
            compiler.takeGroupEnd();

            // Must not reach
            fail();
        } catch (PipeSyntaxException ex) {
            // Correct
        } catch (PipeException ex) {
            fail();
        }
    }

    @Test
    public void testUnmatchedClose() {
        try {
            PipeCommands commands = makeTestCommands();
            PipeLinker linker = new PipeLinker();

            EarlyPipeShellCompiler compiler = new EarlyPipeShellCompiler(
                    commands, linker);

            compiler.startCompile();

            // Must fail if a group is closed before it is opened
            compiler.takeGroupEnd();

            // Must not reach
            fail();
        } catch (PipeSyntaxException ex) {
            // Correct
        } catch (PipeException ex) {
            fail();
        }
    }

    /**
     * Construct PipeCommands with TestPipeCommand registered as "test".
     * 
     * @return Command registry with test command
     */
    public static PipeCommands makeTestCommands()
            throws PipeNameInvalidException, PipeNameInUseException {
        PipeCommands commands = new PipeCommands();
        commands.add("test", TestPipeCommand.INSTANCE);
        return commands;
    }
}
