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

import org.dnikulin.codon.format.except.ObjectCorruptException;
import org.dnikulin.codon.netpipe.hello.HelloObjectFormat;
import org.dnikulin.codon.netpipe.hello.NetPipeHello;

public class PipeServerToClientLink extends NetPipeLink {
    private final PipeServer server;
    private ObjectChannel pipeChannel;

    public PipeServerToClientLink(PipeServer server, SocketChannel channel)
            throws IOException {
        super(channel);

        this.server = server;
        this.pipeChannel = null;
    }

    @Override
    protected synchronized void receivedObject(byte[] body) {
        if (pipeChannel == null) {
            try {
                readHello(body);
            } catch (ObjectCorruptException ex) {
                // Disconnect
            }
        } else if (willReceive) {
            pipeChannel.takeObject(body);
        }
    }

    protected synchronized void readHello(byte[] body) throws ObjectCorruptException {
        NetPipeHello hello = (NetPipeHello) HelloObjectFormat.INSTANCE
                .decode(body);
        pipeChannel = server.makeChannel(hello.channelName);

        willSend = hello.sendToClient;
        willReceive = hello.receiveFromClient;

        if (willSend)
            pipeChannel.addListener(this);
    }

    @Override
    public synchronized void connectionMade(InetAddress address) {
        assert (pipeChannel == null);
        super.connectionMade(address);
    }

    @Override
    public synchronized void connectionLost() {
        if (pipeChannel != null)
            pipeChannel.removeListener(this);

        super.connectionLost();
    }
}
