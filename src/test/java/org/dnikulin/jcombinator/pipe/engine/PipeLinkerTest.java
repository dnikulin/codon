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

import static org.dnikulin.jcombinator.pipe.engine.PipeLinker.isPipeNameValid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.simple.TestPipe;
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
            assertTrue(true);
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
            assertTrue(true);
        }

        // Must record only the names that were added successfully
        Set<String> pipeNames = linker.getPipeNames();
        assertEquals(3, pipeNames.size());
        assertTrue(pipeNames.contains(name1));
        assertTrue(pipeNames.contains(name2));
        assertTrue(pipeNames.contains(name3));
        assertFalse(pipeNames.contains(badname));

        // Must associate pipe instances correctly
        assertNotSame(pipe1, pipe2);
        assertSame(pipe1, linker.getPipe(name1));
        assertSame(pipe1, linker.getPipe(name3));
        assertSame(pipe2, linker.getPipe(name2));

        // Must remove pipes correctly
        linker.removePipe(name1);
        assertSame(null, linker.getPipe(name1));
        assertEquals(2, linker.getPipeNames().size());
    }
}
