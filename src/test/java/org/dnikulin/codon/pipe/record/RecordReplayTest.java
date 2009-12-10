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

package org.dnikulin.codon.pipe.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.except.DaemonException;
import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.primitive.StringObjectFormat;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.log.NullLogger;
import org.dnikulin.codon.pipe.test.TestPipe;
import org.junit.Test;

public class RecordReplayTest {
    private static final LineLogger LOG = NullLogger.INSTANCE;
    private static final ObjectFormat FORMAT = StringObjectFormat.INSTANCE;

    @Test
    public void testRecordReplay() throws IOException {
        String[] strings = makeStrings();
        byte[] bytes = record(strings);
        replay(strings, bytes);
    }

    private static byte[] record(String[] strings) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        RecordPipe recorder = new RecordPipe(LOG, FORMAT, stream);

        for (String string : strings)
            recorder.consume(string);

        // Force flush
        recorder.reset();

        // Must have written some bytes
        byte[] bytes = stream.toByteArray();
        assertTrue(bytes.length > 0);
        return bytes;
    }

    private static void replay(String[] strings, byte[] bytes)
            throws IOException {

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestPipe pipe = new TestPipe();

        ReplayDaemon daemon = new ReplayDaemon(pipe, LOG, FORMAT, in,
                "Test stream");

        // Must not have produced anything yet
        assertEquals(0, pipe.count());

        // Run through daemon
        runDaemon(daemon);

        // Must have produced every string
        assertEquals(strings.length, pipe.count());
        assertEquals(strings[strings.length - 1], pipe.last());
    }

    private static void runDaemon(Daemon daemon) {
        try {
            while (true)
                daemon.resumeDaemon();
        } catch (DaemonException ex) {
            // Ignore
        }
    }

    private static String[] makeStrings() {
        final int count = 127;
        String[] out = new String[count];

        for (int i = 0; i < count; i++)
            out[i] = "test-" + i;
        return out;
    }
}
