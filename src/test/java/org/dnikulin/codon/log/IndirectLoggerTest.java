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

package org.dnikulin.codon.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class IndirectLoggerTest {
    @Test
    public void testConstructorDefault() {
        IndirectLogger log = new IndirectLogger();

        // Must associate with null logger
        assertSame(NullLogger.INSTANCE, log.getLineLogger());
    }

    @Test
    public void testConstructorSpecific() {
        CountingLogger log1 = new CountingLogger();
        IndirectLogger log = new IndirectLogger(log1);

        // Must associate correctly
        assertSame(log1, log.getLineLogger());
    }

    @Test
    public void testSetLineLogger() {
        CountingLogger log1 = new CountingLogger();
        CountingLogger log2 = new CountingLogger();

        IndirectLogger log = new IndirectLogger(log1);

        // Must associate correctly
        assertSame(log1, log.getLineLogger());

        // Must forward print calls
        assertEquals(0, log1.count());
        assertEquals(0, log2.count());
        log.print("test");
        assertEquals(1, log1.count());
        assertEquals(0, log2.count());

        // Must re-associate correctly
        log.setLineLogger(log2);
        assertSame(log2, log.getLineLogger());

        // Must forward print calls
        assertEquals(1, log1.count());
        assertEquals(0, log2.count());
        log.print("test");
        assertEquals(1, log1.count());
        assertEquals(1, log2.count());

        // Must translate null to a null logger
        log.setLineLogger(null);
        assertSame(NullLogger.INSTANCE, log.getLineLogger());
    }
}
