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

package org.dnikulin.codon.pipe.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.codon.pipe.Consumer;
import org.dnikulin.codon.pipe.Pipe;
import org.dnikulin.codon.pipe.except.PipeTypeException;
import org.dnikulin.codon.pipe.test.TestPipe;
import org.junit.Test;

public class CompoundPipeTest {
    @Test
    public void testEmpty() {
        try {
            new CompoundPipe(new ArrayList<Pipe>());

            // Must not reach
            fail();
        } catch (PipeTypeException ex) {
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // Correct
        }
    }

    @Test
    public void testGoodTypes() {
        TestPipe pipe1 = new TestPipe(Object.class, Pipe.class);
        TestPipe pipe2 = new TestPipe(Pipe.class, Pipe.class);
        TestPipe pipe3 = new TestPipe(Consumer.class, Object.class);

        TestPipe pipe4 = new TestPipe(Object.class, Object.class);

        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);

        CompoundPipe cpipe = null;

        try {
            cpipe = new CompoundPipe(pipes);
        } catch (PipeTypeException ex) {
            fail();
        }

        // Must have connected only in between pipes
        assertTrue(pipe1.hasConsumer());
        assertTrue(pipe2.hasConsumer());
        assertFalse(pipe3.hasConsumer());
        assertFalse(cpipe.hasConsumer());

        // Must connect outputs to last pipe
        cpipe.addConsumer(pipe4);
        assertTrue(pipe3.hasConsumer());
        assertTrue(cpipe.hasConsumer());

        // Enable propagation
        pipe1.setPass(true);
        pipe2.setPass(true);
        pipe3.setPass(true);

        // Must not have passed anything yet
        assertEquals(0, pipe1.count());
        assertEquals(0, pipe2.count());
        assertEquals(0, pipe3.count());
        assertEquals(0, pipe4.count());
        assertSame(null, pipe4.last());

        // Must pass objects correctly
        pipe1.consume(pipe1);
        assertEquals(1, pipe1.count());
        assertEquals(1, pipe2.count());
        assertEquals(1, pipe3.count());
        assertEquals(1, pipe4.count());
        assertSame(pipe1, pipe4.last());
    }

    @Test
    public void testBadTypes() {
        TestPipe pipe1 = new TestPipe(Object.class, Object.class);
        // Connection fine here
        TestPipe pipe2 = new TestPipe(Object.class, List.class);
        // Connection bad here
        TestPipe pipe3 = new TestPipe(String.class, Object.class);
        // Connection fine here (but won't reach)
        TestPipe pipe4 = new TestPipe(Object.class, Object.class);

        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);
        pipes.add(pipe4);

        try {
            new CompoundPipe(pipes);

            // Must not reach
            fail();
        } catch (PipeTypeException ex) {
            // Correct
        }

        // Must have connected only *until* the first type error
        assertFalse(pipe1.hasConsumer());
        assertFalse(pipe2.hasConsumer());
        assertTrue(pipe3.hasConsumer());
        assertFalse(pipe4.hasConsumer());
    }
}
