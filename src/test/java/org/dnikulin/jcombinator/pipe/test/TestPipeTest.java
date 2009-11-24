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

package org.dnikulin.jcombinator.pipe.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.dnikulin.jcombinator.log.CountingLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.core.Producer;
import org.junit.Test;

public class TestPipeTest {
    @Test
    public void testConstructor() {
        // Default constructor must assign Object as both types
        TestPipe defpipe = new TestPipe();
        assertSame(Object.class, defpipe.getInputType());
        assertSame(Object.class, defpipe.getOutputType());

        // Specific constructor must assign given types
        TestPipe pipe = new TestPipe(String.class, List.class);
        assertSame(String.class, pipe.getInputType());
        assertSame(List.class, pipe.getOutputType());

        // Must construct with null logger and no consumers
        assertFalse(pipe.hasConsumer());
        assertSame(NullLogger.INSTANCE, pipe.getLineLogger());

        // Must construct with count at 0, no saved value, not passing
        assertEquals(0, pipe.count());
        assertSame(null, pipe.last());
        assertFalse(pipe.passes());
    }

    @Test
    public void testConsumeRecord() {
        TestPipe pipe = new TestPipe();

        // Must construct with count at 0, no saved value, not passing
        assertEquals(0, pipe.count());
        assertSame(null, pipe.last());

        // Must increment count on every consume()
        pipe.consume(pipe);
        assertEquals(1, pipe.count());
        pipe.consume(pipe);
        assertEquals(2, pipe.count());

        // Must reset count to specific value
        pipe.resetCount(7);
        assertEquals(7, pipe.count());
        pipe.consume(pipe);
        assertEquals(8, pipe.count());

        // Must reset count to 0
        pipe.reset();
        assertEquals(0, pipe.count());

        // Must save value
        assertSame(pipe, pipe.last());
    }

    @Test
    public void testConnection() {
        TestPipe pipe1 = new TestPipe(Object.class, Pipe.class);
        CountingLogger log1 = new CountingLogger();
        pipe1.setLineLogger(log1);
        assertEquals(0, log1.count());

        TestPipe pipe2A = new TestPipe(Pipe.class, Pipe.class);
        CountingLogger log2A = new CountingLogger();
        pipe2A.setLineLogger(log2A);
        assertEquals(0, log2A.count());

        TestPipe pipe2B = new TestPipe(Consumer.class, Producer.class);
        CountingLogger log2B = new CountingLogger();
        pipe2B.setLineLogger(log2B);
        assertEquals(0, log2B.count());

        TestPipe pipe3 = new TestPipe(Pipe.class, Object.class);
        CountingLogger log3 = new CountingLogger();
        pipe3.setLineLogger(log3);
        assertEquals(0, log3.count());

        // Must connect for identical type (Pipe)
        assertTrue(pipe1.addConsumer(pipe2A));
        assertTrue(pipe1.hasConsumer());
        assertTrue(pipe2A.addConsumer(pipe3));
        assertTrue(pipe2A.hasConsumer());

        // Must connect for supertype (Pipe to Consumer)
        assertTrue(pipe1.addConsumer(pipe2B));

        // Must not connect for subtype (Producer to Pipe)
        assertFalse(pipe2B.addConsumer(pipe3));
        assertFalse(pipe2B.hasConsumer());

        // Must not re-connect old connections
        assertFalse(pipe1.addConsumer(pipe2A));
        assertFalse(pipe2A.addConsumer(pipe3));
        assertFalse(pipe1.addConsumer(pipe2B));

        // Must not log anything yet
        assertEquals(0, log1.count());
        assertEquals(0, log2A.count());
        assertEquals(0, log2B.count());
        assertEquals(0, log3.count());

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

        // Must be able to disconnect consumers (specific or all)
        pipe1.removeConsumers();
        pipe2A.removeConsumer(pipe3);
        assertFalse(pipe1.hasConsumer());
        assertFalse(pipe2A.hasConsumer());
        assertFalse(pipe2B.hasConsumer());
        assertFalse(pipe3.hasConsumer());

        // Must not pass when disconnected
        pipe1.consume(pipe1);
        assertEquals(4, pipe1.count());
        assertEquals(2, pipe2A.count());
        assertEquals(2, pipe2B.count());
        assertEquals(1, pipe3.count());
        assertSame(pipe1, pipe3.last());

        // Must log for each pass only
        // +1 for these pipes because they started without passing
        assertEquals(pipe1.count(), log1.count() + 1);
        assertEquals(pipe2A.count(), log2A.count() + 1);
        assertEquals(pipe2B.count(), log2B.count() + 1);
        assertEquals(0, log3.count());
    }
}
