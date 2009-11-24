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

import static org.dnikulin.jcombinator.pipe.compiler.EarlyPipeShellCompilerTest.makeTestCommands;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.dnikulin.jcombinator.pipe.command.PipeCommands;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.pipe.except.PipeNotFoundException;
import org.dnikulin.jcombinator.pipe.simple.CompoundPipe;
import org.dnikulin.jcombinator.pipe.test.TestPipe;
import org.junit.Test;

public class PipeShellParserTest {
    @Test
    public void testBlankString() {
        assertParse("");
        assertParse(" \t  ");
    }

    @Test
    public void testSimpleTokens() {
        assertParse("foo", "foo");
    }

    @Test
    public void testPaddedTokens() {
        assertParse(" foo  \t", "foo");
        assertParse(" foo  bar  \t", "foo", "bar");
    }

    @Test
    public void testEscapedTokens() {
        assertParse("\\");
        assertParse("\\ ", " ");
        assertParse("\\\\", "\\");
        assertParse(" foo\\  ba\\r \\\\ ", "foo ", "bar", "\\");
    }

    @Test
    public void testQuotedTokens() {
        assertParse("\"foo\"", "foo");
        assertParse(" \"foo bar\"  ", "foo bar");
        assertParse("\"foo \\\" bar\"", "foo \" bar");
        assertParse("\"foo \\\\ bar\"", "foo \\ bar");

        // Test unterminated quotation as well
        assertParse("\"foo \\\\ bar  ", "foo \\ bar  ");
        assertParse("\"foo \\\\ bar  \\", "foo \\ bar  ");
    }

    @Test
    public void testComment() {
        // Comments must start with ' #' or '\t#'
        assertParse("foo bar # ignore rest", "foo", "bar");
        assertParse("\"foo bar\"\t# ignore rest", "foo bar");

        // If following token, quote or escape, not a comment
        assertParse("foo bar# baz", "foo", "bar#", "baz");
        assertParse(" \"foo bar\"# baz  ", "foo bar#", "baz");
        assertParse("foo \\# bar", "foo", "#", "bar");
    }

    @Test
    public void testCompile() {
        try {
            // Must not register any pipes
            PipeLinker linker = compile("test");
            assertTrue(linker.getPipeNames().isEmpty());

            // Must name a single pipe
            compile("~pipe1 test java.lang.String java.util.List", linker);
            assertEquals(1, linker.getPipeNames().size());

            Pipe pipe1 = linker.getPipe("pipe1");
            assertTrue(pipe1 instanceof TestPipe);
            assertFalse(pipe1.hasConsumer());
            assertSame(String.class, pipe1.getInputType());
            assertSame(List.class, pipe1.getOutputType());

            // Must refer to a single pipe and name a new pipe
            compile("~pipe1 | ~pipe2 test", linker);

            // Must have linked pipe1 to pipe2
            assertTrue(pipe1.hasConsumer());

            Pipe pipe2 = linker.getPipe("pipe2");
            assertTrue(pipe2 instanceof TestPipe);
            assertNotSame(pipe1, pipe2);
            assertFalse(pipe2.hasConsumer());
            assertSame(Object.class, pipe2.getInputType());
            assertSame(Object.class, pipe2.getOutputType());

            // Must connect a new anonymous pipe
            compile("~pipe2 | test", linker);
            assertTrue(pipe2.hasConsumer());

            // Must create a named group
            compile("~pipe3[test | test | test]", linker);
            Pipe pipe3 = linker.getPipe("pipe3");
            assertTrue(pipe3 instanceof CompoundPipe);
            assertFalse(pipe3.hasConsumer());

            // Must created a nested named group
            compile("test [ ~pipe4 [ test | test", linker);
            Pipe pipe4 = linker.getPipe("pipe4");
            assertTrue(pipe4 instanceof CompoundPipe);
            assertFalse(pipe4.hasConsumer());

            // Must created a nested named group containing a back reference
            compile("test [ ~pipe5 [ test | ~pipe4", linker);
            Pipe pipe5 = linker.getPipe("pipe5");
            assertTrue(pipe5 instanceof CompoundPipe);
            assertFalse(pipe5.hasConsumer());

            // Must support comments after pipelines
            compile("test # A test pipe");

            // Must support comment-only lines
            compile("# Comment");
        } catch (PipeNotFoundException ex) {
            ex.printStackTrace();
            fail();
        }
    }

    public static PipeLinker compile(String line, PipeLinker linker) {
        try {
            PipeCommands commands = makeTestCommands();

            PipeShellCompiler compiler = new EarlyPipeShellCompiler(commands,
                    linker);

            PipeShellParser parser = new PipeShellParser(compiler);
            parser.feed(line);

            return linker;
        } catch (PipeException ex) {
            ex.printStackTrace();
            fail();
            return null;
        }
    }

    public static PipeLinker compile(String line) {
        return compile(line, new PipeLinker());
    }

    /**
     * Assert that a parse results in the tokens expected.
     * 
     * @param line
     *            Line to parse
     * @param expect
     *            Tokens expected
     */
    public static void assertParse(String line, String... expect) {
        String[] result = parse(line);
        assertEquals(expect.length, result.length);
        assertArrayEquals(expect, result);
    }

    /**
     * Return the tokens parsed from a string.
     * 
     * @param line
     *            String to parse
     * @return Tokens parsed
     */
    public static String[] parse(String line) {
        try {
            PipeShellCompiler compiler = NullPipeShellCompiler.INSTANCE;
            PipeShellParser parser = new PipeShellParser(compiler);

            // Must start with no tokens
            assertEquals(0, parser.getTokens().length);

            parser.feed(line);
            return parser.getTokens();
        } catch (PipeException ex) {
            fail();
            return new String[] {};
        }
    }
}
