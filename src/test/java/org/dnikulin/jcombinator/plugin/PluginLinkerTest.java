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

package org.dnikulin.jcombinator.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.log.PrintLogger;
import org.junit.Test;

public class PluginLinkerTest {
    /** Default constructor must connect a NullLogger. */
    @Test
    public void testConstructorDefault() {
        PluginLinker linker = new PluginLinker();
        assertEquals(NullLogger.INSTANCE, linker.getLineLogger());
    }

    /** Constructor with non-null logger must connect given logger. */
    @Test
    public void testConstructorArgument() {
        PluginLinker linker = new PluginLinker(PrintLogger.SYSOUT);
        assertEquals(PrintLogger.SYSOUT, linker.getLineLogger());
    }

    /** Constructor with null logger must throw a NullPointerException. */
    @Test
    public void testConstructorNull() {
        boolean threw = false;

        try {
            new PluginLinker(null);
        } catch (NullPointerException ex) {
            threw = true;
        }

        assertTrue(threw);
    }
}
