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
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;

import org.dnikulin.codon.misc.Resettable;

public class ChannelObjectWriter implements ObjectListener, Resettable {
    public static final int BUFFER_SIZE = 128 * 1024;

    private final WritableByteChannel channel;

    private final LinkedList<byte[]> packets;
    private final ByteBuffer buffer;
    private int cursor;

    public ChannelObjectWriter(WritableByteChannel channel) {
        this.channel = channel;

        this.packets = new LinkedList<byte[]>();
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.cursor = -1;
    }

    @Override
    public synchronized void reset() {
        this.buffer.clear();
        this.cursor = -1;
    }

    public synchronized boolean wantsWrite() {
        return (buffer.position() > 0) || (packets.isEmpty() == false);
    }

    public synchronized void flush() throws IOException {
        prepare();

        buffer.flip();
        channel.write(buffer);
        buffer.compact();
    }

    @Override
    public synchronized void takeObject(byte[] body) {
        packets.add(body);
    }

    public synchronized void takePriorityObject(byte[] body) {
        packets.addFirst(body);
    }

    private synchronized void prepare() {
        while (packets.isEmpty() == false) {
            int space = buffer.remaining();
            if (space < 1)
                return;

            byte[] next = packets.peek();

            if (cursor == -1) {
                if (buffer.remaining() < 4)
                    return;

                buffer.putInt(next.length);
                cursor = 0;
                space = buffer.remaining();

                System.err.println("Writing object of length " + next.length);
            }

            assert (cursor >= 0);

            int ready = next.length - cursor;
            int count = Math.min(space, ready);
            buffer.put(next, cursor, count);
            cursor += count;

            assert (cursor <= next.length);
            if (cursor == next.length) {
                System.err.println("Wrote object of length " + next.length);
                packets.remove();
                cursor = -1;
            }
        }
    }
}
