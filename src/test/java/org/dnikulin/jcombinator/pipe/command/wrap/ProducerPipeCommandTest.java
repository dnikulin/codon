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

package org.dnikulin.jcombinator.pipe.command.wrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.dnikulin.jcombinator.log.CountingLogger;
import org.dnikulin.jcombinator.pipe.command.ProducerCommand;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;
import org.dnikulin.jcombinator.pipe.test.TestPipe;
import org.dnikulin.jcombinator.pipe.test.TestProducerCommand;
import org.junit.Test;

public class ProducerPipeCommandTest {
    @Test
    public void testProducerCommand() {
        ProducerCommand cmd = TestProducerCommand.INSTANCE;
        ProducerPipeCommand pcmd = new ProducerPipeCommand(cmd);

        // Must retain output type
        assertSame(cmd.getOutputType(), pcmd.getOutputType());

        for (int i = 0; i < 5; i++) {
            String[] args = new String[i];
            for (int j = 0; j < i; j++)
                args[j] = "test-" + j;

            try {
                CountingLogger log = new CountingLogger();
                TestPipe tpipe = new TestPipe();

                // Must produce pipe silently
                Pipe pipe = pcmd.makePipe(args, log);
                assertEquals(0, log.count());

                // Pipe must have correct output type
                assertSame(cmd.getOutputType(), pipe.getOutputType());

                for (int j = 1; j <= 5; j++) {
                    pipe.addConsumer(tpipe);

                    // Must log once per consumer add
                    assertEquals(j, log.count());

                    // Must produce each argument once
                    assertEquals(j * i, tpipe.count());

                    Object last = tpipe.last();
                    if (last != null)
                        assertSame(String.class, last.getClass());
                }
            } catch (PipeFactoryException ex) {
                fail();
            }
        }
    }
}
