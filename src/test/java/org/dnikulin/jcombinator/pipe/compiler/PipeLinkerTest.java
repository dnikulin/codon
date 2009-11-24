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

import static org.dnikulin.jcombinator.pipe.compiler.PipeLinker.isPipeNameValid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.dnikulin.jcombinator.pipe.compiler.PipeLinker;
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.core.Producer;
import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.except.PipeNotFoundException;
import org.dnikulin.jcombinator.pipe.except.PipeTypeException;
import org.dnikulin.jcombinator.pipe.test.TestPipe;
import org.junit.Test;

public class PipeLinkerTest {
    @Test
    public void testIsPipeNameValid() {
        // Must not accept empty string
        assertFalse(isPipeNameValid(""));

        // Must accept single letter
        assertTrue(isPipeNameValid("a"));
        assertTrue(isPipeNameValid("Z"));

        // Must not accept single number or underscore
        assertFalse(isPipeNameValid("2"));
        assertFalse(isPipeNameValid("_"));

        // Must accept letter followed by letters, numbers or underscores
        assertTrue(isPipeNameValid("az"));
        assertTrue(isPipeNameValid("a2"));
        assertTrue(isPipeNameValid("a_"));
        assertTrue(isPipeNameValid("aZ2"));
        assertTrue(isPipeNameValid("a2Z"));
        assertTrue(isPipeNameValid("a2Z_"));

        // Must not accept any character that is not
        // a letter, number or underscore
        assertFalse(isPipeNameValid("#"));
        assertFalse(isPipeNameValid("a#"));
        assertFalse(isPipeNameValid("a_%"));
    }

    @Test
    public void testAddPipe() {
        final String name1 = "test1";
        final String name2 = "test2";
        final String name3 = "test3";
        final String badname = "3";

        PipeLinker linker = new PipeLinker();
        Pipe pipe1 = new TestPipe();
        Pipe pipe2 = new TestPipe();

        // Must start with no pipes registered
        assertTrue(linker.getPipeNames().isEmpty());

        try {
            // Must add pipes with a valid name
            linker.addPipe(name1, pipe1);
            linker.addPipe(name2, pipe2);

            // Must be able to add same pipe under different names
            linker.addPipe(name3, pipe1);
        } catch (PipeException ex) {
            // Failed
            assertTrue(false);
        }

        try {
            // Must fail to add pipes with invalid name
            linker.addPipe(badname, pipe1);

            // Must not reach this assertion
            assertTrue(false);
        } catch (PipeNameInvalidException ex) {
            // Correct
        } catch (PipeNameInUseException ex) {
            assertTrue(false);
        }

        try {
            // Must fail to add pipes with existing name
            linker.addPipe(name1, pipe1);

            // Must not reach this assertion
            assertTrue(false);
        } catch (PipeNameInvalidException ex) {
            assertTrue(false);
        } catch (PipeNameInUseException ex) {
            // Correct
        }

        // Must record only the names that were added successfully
        Set<String> pipeNames = linker.getPipeNames();
        assertEquals(3, pipeNames.size());
        assertTrue(pipeNames.contains(name1));
        assertTrue(pipeNames.contains(name2));
        assertTrue(pipeNames.contains(name3));
        assertFalse(pipeNames.contains(badname));

        try {
            // Must associate pipe instances correctly
            assertNotSame(pipe1, pipe2);
            assertSame(pipe1, linker.getPipe(name1));
            assertSame(pipe1, linker.getPipe(name3));
            assertSame(pipe2, linker.getPipe(name2));
        } catch (PipeNotFoundException ex) {
            assertTrue(false);
        }

        try {
            // Must remove pipes correctly
            linker.removePipe(name1);
            assertEquals(2, linker.getPipeNames().size());

            // Must throw an exception here
            linker.getPipe(name1);

            // Must not reach this assertion
            assertTrue(false);
        } catch (PipeNotFoundException ex) {
            // Correct
        }
    }

    @Test
    public void testLinkPipes() {
        TestPipe pipe1 = new TestPipe(Object.class, Pipe.class);
        TestPipe pipe2A = new TestPipe(Pipe.class, Pipe.class);
        TestPipe pipe2B = new TestPipe(Consumer.class, Producer.class);
        TestPipe pipe3 = new TestPipe(Pipe.class, Object.class);

        PipeLinker linker = new PipeLinker();

        try {
            // Must add pipes without exceptions
            linker.addPipe("pipe1", pipe1);
            linker.addPipe("pipe2A", pipe2A);
            linker.addPipe("pipe2B", pipe2B);
            linker.addPipe("pipe3", pipe3);
        } catch (PipeNameInUseException ex) {
            assertTrue(false);
        } catch (PipeNameInvalidException ex) {
            assertTrue(false);
        }

        // Must have added exactly those 4 pipes
        assertEquals(4, linker.getPipeNames().size());

        try {
            // Must link correctly typed pipes without exceptions
            linker.linkPipes("pipe1", "pipe2A");
            linker.linkPipes("pipe1", "pipe2B");
            linker.linkPipes("pipe2A", "pipe3");

            assertTrue(pipe1.hasConsumer());
            assertTrue(pipe2A.hasConsumer());
            assertFalse(pipe2B.hasConsumer());
            assertFalse(pipe3.hasConsumer());
        } catch (PipeNotFoundException ex) {
            assertTrue(false);
        } catch (PipeTypeException ex) {
            assertTrue(false);
        }

        try {
            // Must not link incorrectly typed pipes
            linker.linkPipes("pipe2B", "pipe3");

            // Must not reach this assertion
            assertTrue(false);
        } catch (PipeNotFoundException ex) {
            assertTrue(false);
        } catch (PipeTypeException ex) {
            // Correct
        }

        try {
            // Must not link missing pipes
            linker.linkPipes("pipe2B", "none");

            // Must not reach this assertion
            assertTrue(false);
        } catch (PipeNotFoundException ex) {
            // Correct
        } catch (PipeTypeException ex) {
            assertTrue(false);
        }

        // Must not pass values by default
        pipe1.consume(pipe1);
        assertEquals(1, pipe1.count());
        assertEquals(0, pipe2A.count());
        assertEquals(0, pipe2B.count());
        assertEquals(0, pipe3.count());

        // Must pass values when configured to
        pipe1.setPass(true);
        pipe1.consume(pipe1);
        assertEquals(2, pipe1.count());
        assertEquals(1, pipe2A.count());
        assertEquals(1, pipe2B.count());
        assertEquals(0, pipe3.count());
        assertSame(pipe1, pipe2A.last());
        assertSame(pipe1, pipe2B.last());

        // Must pass values when configured to
        pipe2A.setPass(true);
        pipe2B.setPass(true);
        pipe1.consume(pipe1);
        assertEquals(3, pipe1.count());
        assertEquals(2, pipe2A.count());
        assertEquals(2, pipe2B.count());
        assertEquals(1, pipe3.count()); // From 2A but not 2B
        assertSame(pipe1, pipe3.last());
    }
}
