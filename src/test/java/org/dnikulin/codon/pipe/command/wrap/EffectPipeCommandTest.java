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

package org.dnikulin.codon.pipe.command.wrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.dnikulin.codon.command.EffectCommand;
import org.dnikulin.codon.log.CountingLogger;
import org.dnikulin.codon.pipe.core.Pipe;
import org.dnikulin.codon.pipe.except.PipeFactoryException;
import org.dnikulin.codon.pipe.nulled.NullPipe;
import org.dnikulin.codon.pipe.test.TestEffectCommand;
import org.junit.Test;

public class EffectPipeCommandTest {
    @Test
    public void testEffectPipeCommand() {
        EffectCommand ecmd = TestEffectCommand.INSTANCE;
        EffectPipeCommand pcmd = new EffectPipeCommand(ecmd);

        CountingLogger log = new CountingLogger();
        assertEquals(0, log.count());

        for (int i = 0; i < 5; i++) {
            String[] args = new String[i];
            for (int j = 0; j < i; j++)
                args[j] = "test-" + j;

            // Must log each argument if called directly
            log.reset();
            ecmd.execute(args, log);
            assertEquals(i, log.count());

            // Must log each argument if called indirectly
            log.reset();
            pcmd.execute(args, log);
            assertEquals(i, log.count());

            // Must log each argument if called by pipe wrapper
            try {
                log.reset();
                Pipe pipe = pcmd.makePipe(args, log);

                // Must return null pipe
                assertSame(NullPipe.INSTANCE, pipe);

                // Must log each argument exactly once
                assertEquals(i, log.count());
            } catch (PipeFactoryException ex) {
                fail();
            }
        }
    }
}
