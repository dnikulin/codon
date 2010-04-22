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

package org.dnikulin.codon.netpipe.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import org.dnikulin.codon.misc.Resettable;

public class ChannelObjectReader implements Resettable {
    public static final int BUFFER_SIZE = ChannelObjectWriter.BUFFER_SIZE;
    public static final byte[] EMPTY = new byte[] {};

    private final ReadableByteChannel channel;

    private final ObjectListener listener;

    private final ByteBuffer buffer;
    private byte[] next;
    private int cursor;

    public ChannelObjectReader(ReadableByteChannel channel,
            ObjectListener listener) {

        this.channel = channel;
        this.listener = listener;

        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.next = null;
        this.cursor = 0;
    }

    @Override
    public synchronized void reset() {
        buffer.clear();
        next = null;
        cursor = 0;
    }

    public synchronized void consume() throws IOException {
        channel.read(buffer);

        buffer.flip();

        while (true) {
            if (next == null) {
                assert (cursor == 0);

                if (buffer.remaining() < 4)
                    break;

                int nextSize = buffer.getInt();

                if (nextSize == 0) {
                    listener.takeObject(EMPTY);
                    continue;
                }

                next = new byte[nextSize];

                System.err.println("Reading object of size " + nextSize);
            }

            int space = buffer.remaining();
            if (space < 1)
                break;

            int count = Math.min(space, next.length - cursor);
            buffer.get(next, cursor, count);
            cursor += count;

            assert (cursor <= next.length);
            if (cursor == next.length) {
                System.err.println("Read object of size " + next.length);

                listener.takeObject(next);
                next = null;
                cursor = 0;
            }
        }

        buffer.compact();
    }
}
