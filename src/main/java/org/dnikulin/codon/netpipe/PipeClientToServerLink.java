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

import org.dnikulin.codon.netpipe.hello.HelloObjectFormat;
import org.dnikulin.codon.netpipe.hello.NetPipeHello;
import org.dnikulin.codon.netpipe.packet.NullObjectListener;
import org.dnikulin.codon.netpipe.packet.ObjectListener;

public class PipeClientToServerLink extends NetPipeLink {
    private final String channelName;

    private ObjectListener listener;

    public PipeClientToServerLink(String channelName, boolean willSend,
            boolean willReceive) throws IOException {
        super(SocketChannel.open());

        this.channelName = channelName;
        this.listener = NullObjectListener.INSTANCE;

        this.willSend = willSend;
        this.willReceive = willReceive;
    }

    public synchronized void setListener(ObjectListener listener) {
        this.listener = listener;
    }

    @Override
    protected void receivedObject(byte[] body) {
        listener.takeObject(body);
    }

    @Override
    public void connectionMade(InetAddress address) {
        super.connectionMade(address);

        sendHello();
    }

    protected synchronized void sendHello() {
        NetPipeHello hello = new NetPipeHello(channelName, willReceive,
                willSend);
        byte[] bytes = HelloObjectFormat.INSTANCE.encode(hello);

        writer.takeObject(bytes);

        System.err.println("Scheduled hello");
    }
}
