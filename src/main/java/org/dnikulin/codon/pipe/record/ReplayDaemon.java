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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.dnikulin.codon.daemon.Daemon;
import org.dnikulin.codon.daemon.except.DaemonException;
import org.dnikulin.codon.daemon.except.DaemonExitException;
import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectCorruptException;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.Consumer;

/** A daemon that reads objects recorded by RecordPipe. */
public class ReplayDaemon implements Daemon {
    private final Consumer consumer;
    private final LineLogger log;

    private final ObjectFormat format;

    private final String inputName;
    private DataInputStream stream;
    private boolean done;

    /**
     * Construct replay daemon.
     * 
     * @param consumer
     *            Object consumer
     * @param log
     *            Line logger
     * @param format
     *            Object format
     * @param input
     *            Input stream
     * @param inputName
     *            Input stream name (filename, etc)
     */
    public ReplayDaemon(Consumer consumer, LineLogger log, ObjectFormat format,
            InputStream input, String inputName) throws IOException {

        this.consumer = consumer;
        this.log = log;
        this.format = format;
        this.inputName = inputName;

        BufferedInputStream bis1 = new BufferedInputStream(input);
        GZIPInputStream gis = new GZIPInputStream(bis1);
        BufferedInputStream bis2 = new BufferedInputStream(gis);
        this.stream = new DataInputStream(bis2);

        this.done = false;
    }

    @Override
    public String getDaemonName() {
        return "Replay from " + inputName;
    }

    @Override
    public synchronized void resumeDaemon() throws DaemonException {
        if (done == true)
            return;

        try {
            int size = stream.readInt();
            byte[] bytes = new byte[size];
            stream.read(bytes);

            Object object = format.decode(bytes);
            consumer.consume(object);
        } catch (EOFException ex) {
            done = true;
            close();
            log.print("Replay complete");

            throw new DaemonExitException();
        } catch (IOException ex) {
            log.print("Replay error: " + ex.getLocalizedMessage());
            close();
        } catch (ObjectCorruptException ex) {
            log.print("Replay ignoring corrupt object");
        }
    }

    private synchronized void close() {
        if (stream == null)
            return;

        try {
            stream.close();
        } catch (IOException ex) {
            // Ignore
        } finally {
            stream = null;
        }
    }

    @Override
    public void cancel() {
        close();
    }
}
