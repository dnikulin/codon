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

package org.dnikulin.codon.netpipe;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

import org.dnikulin.codon.net.SocketLink;
import org.dnikulin.codon.netpipe.packet.ChannelObjectReader;
import org.dnikulin.codon.netpipe.packet.ChannelObjectWriter;
import org.dnikulin.codon.netpipe.packet.ObjectListener;

public abstract class NetPipeLink implements SocketLink, ObjectListener {
    protected final SocketChannel channel;

    protected final ChannelObjectReader reader;
    protected final ChannelObjectWriter writer;

    protected boolean willSend;
    protected boolean willReceive;

    public NetPipeLink(SocketChannel channel) throws IOException {
        this.channel = channel;

        this.reader = new ChannelObjectReader(channel, new ObjectListener() {
            @Override
            public void takeObject(byte[] body) {
                receivedObject(body);
            }
        });

        this.writer = new ChannelObjectWriter(channel);

        this.willSend = false;
        this.willReceive = false;
    }

    @Override
    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public synchronized void takeObject(byte[] body) {
        if (willSend)
            writer.takeObject(body);
    }

    @Override
    public synchronized boolean wantsWrite() {
        return writer.wantsWrite();
    }

    @Override
    public synchronized void canWrite() throws IOException {
        writer.flush();
    }

    @Override
    public synchronized boolean wantsRead() {
        return willReceive;
    }

    @Override
    public synchronized void canRead() throws IOException {
        reader.consume();
    }

    @Override
    public synchronized void connectionMade(InetAddress address) {
        System.err.println("Connected to " + address);
    }

    @Override
    public synchronized void connectionLost() {
        reader.reset();
        writer.reset();

        System.err.println("Disconnected");
    }

    protected abstract void receivedObject(byte[] body);
}
