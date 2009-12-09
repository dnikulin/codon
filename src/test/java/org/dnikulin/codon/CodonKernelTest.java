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

package org.dnikulin.codon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.dnikulin.codon.log.CountingLogger;
import org.dnikulin.codon.log.NullLogger;
import org.dnikulin.codon.pipe.command.registry.PipeCommands;
import org.junit.Test;

public class CodonKernelTest {
    @Test
    public void testCodonKernel() {
        CodonKernel ck = new CodonKernel();

        // All associated objects must be non-null
        assertNotNull(ck.getPluginLinker());
        assertNotNull(ck.getPluginLoader());
        assertNotNull(ck.getPipeLinker());
        assertNotNull(ck.getPipeCommands());
        assertNotNull(ck.getObjectFormats());

        // Must start with null logger
        assertSame(NullLogger.INSTANCE, ck.getLineLogger());

        // Must associate line logger correctly
        CountingLogger log = new CountingLogger();
        ck.setLineLogger(log);
        assertSame(log, ck.getLineLogger());

        // Must not yet have "testplug" command
        PipeCommands cmds = ck.getPipeCommands();
        assertFalse(cmds.has("testplug"));

        // Must not have logged anything yet
        assertEquals(0, log.count());

        // Must have working "plugin" command
        // Import specific jar
        ck.runCommand("plugin bin/codon-testplugin.jar");
        // Import entire bin directory (including all jars)
        ck.runCommand("plugin bin");

        // Must log for installations
        assertTrue(log.count() > 0);

        // Must now have "testplug" command
        assertTrue(cmds.has("testplug"));

        // Must log when testplug is run
        log.reset();
        ck.runCommand("testplug");
        assertTrue(log.count() > 0);
    }
}
