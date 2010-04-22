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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.pipe.simple.SimplePipe;

/** Pipe that records objects to a stream. */
public class RecordPipe extends SimplePipe implements Runnable {
    private final LineLogger log;

    private final ObjectFormat format;
    private final Class<?> type;

    private DataOutputStream stream;

    /**
     * Construct a record pipe.
     * 
     * @param log
     *            Line logger
     * @param format
     *            Object format
     * @param output
     *            Output stream
     */
    public RecordPipe(LineLogger log, ObjectFormat format, OutputStream output)
            throws IOException {

        this.log = log;
        this.format = format;
        this.type = format.getObjectClass();

        BufferedOutputStream bos1 = new BufferedOutputStream(output);
        GZIPOutputStream gos = new GZIPOutputStream(bos1);
        BufferedOutputStream bos2 = new BufferedOutputStream(gos);
        this.stream = new DataOutputStream(bos2);

        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    @Override
    public synchronized void consume(Object value) {
        try {
            if (stream != null) {
                byte[] bytes = format.encode(value);

                stream.writeInt(bytes.length);
                stream.write(bytes);
            }
        } catch (IOException ex) {
            log.print("Record error: " + ex.getLocalizedMessage());
            close();
        } finally {
            produce(value);
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
    public void run() {
        close();
    }

    /** Force stream flush. The pipe will no longer record objects. */
    @Override
    public void reset() {
        super.reset();
        close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    @Override
    public Class<?> getInputType() {
        return type;
    }

    @Override
    public Class<?> getOutputType() {
        return type;
    }
}
