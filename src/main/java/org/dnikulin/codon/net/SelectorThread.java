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

package org.dnikulin.codon.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dnikulin.codon.misc.Cancellable;
import org.dnikulin.codon.misc.StatusToken;
import org.dnikulin.codon.net.util.ClientBind;
import org.dnikulin.codon.net.util.ConnectStatus;
import org.dnikulin.codon.net.util.HostBind;
import org.dnikulin.codon.net.util.ListenStatus;

public class SelectorThread implements Executor, Cancellable {
    public static final int SOCKET_SIZE = 1024 * 32;

    public final Selector selector;

    private final AtomicBoolean running;
    private final Queue<Runnable> queue;
    private final Thread thread;

    public SelectorThread(Selector selector) {
        this.selector = selector;
        this.running = new AtomicBoolean(true);
        this.queue = new LinkedList<Runnable>();

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runLoop();
            }
        }, "codon-selector");

        this.thread.start();
    }

    public SelectorThread() throws IOException {
        this(Selector.open());
    }

    @Override
    public String toString() {
        return selector.getClass().getName();
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
        }
        selector.wakeup();
    }

    @Override
    public void cancel() {
        running.set(false);
    }

    public void listen(final int port, final LinkFactory factory,
            final StatusToken<ListenStatus> status) {
        execute(new Runnable() {
            public void run() {
                try {
                    ServerSocketChannel listener = ServerSocketChannel.open();
                    HostBind binding = new HostBind(factory, listener);
                    listener.configureBlocking(false);

                    listener.socket().bind(new InetSocketAddress(port));
                    register(listener, SelectionKey.OP_ACCEPT, binding);

                    status.setStatus(ListenStatus.LISTENING);
                } catch (IOException ex) {
                    status.setStatus(ListenStatus.FAILED, ex);
                }
            }
        });

        status.setStatus(ListenStatus.CREATED, ListenStatus.SCHEDULED);
    }

    public void connect(final String host, final int port,
            final SocketLink link, final StatusToken<ConnectStatus> status) {
        execute(new Runnable() {
            public void run() {
                try {
                    SocketChannel channel = link.getChannel();
                    ClientBind binding = new ClientBind(link, status);
                    tuneChannel(channel);

                    register(channel, SelectionKey.OP_CONNECT, binding);
                    channel.connect(new InetSocketAddress(host, port));

                    status.setStatus(ConnectStatus.CONNECTING);
                } catch (IOException ex) {
                    status.setStatus(ConnectStatus.FAILED, ex);
                }
            }
        });

        status.setStatus(ConnectStatus.CREATED, ConnectStatus.SCHEDULED);
    }

    public void updateKey(final SocketLink link) {
        execute(new Runnable() {
            public void run() {
                try {
                    final int ops;

                    if (link.wantsWrite()) {
                        ops = SelectionKey.OP_WRITE;
                        System.err.println("setting " + link + " for write");
                    } else {
                        ops = SelectionKey.OP_READ;
                        System.err.println("setting " + link + " for read");
                    }

                    register(link.getChannel(), ops, link);
                } catch (IOException ex) {
                    // link.exception(ex);
                }
            }
        });
    }

    protected synchronized void register(SelectableChannel channel, int ops,
            Object data) throws IOException {
        if (channel.isOpen())
            channel.register(selector, ops, data);
    }

    public static void tuneChannel(SocketChannel channel) throws IOException,
            SocketException {
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        socket.setReceiveBufferSize(SOCKET_SIZE);
        socket.setSendBufferSize(SOCKET_SIZE);
    }

    protected void runLoop() {
        try {
            while (running.get() == true) {
                runQueue();

                // Blocks here - may be interrupted
                int selected = selector.select();

                if (selected > 0)
                    handleKeys();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void runQueue() {
        Runnable work;

        while (true) {
            synchronized (queue) {
                work = queue.poll();
                if (work == null)
                    return;
            }

            work.run();
        }
    }

    private void handleKeys() {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();

        for (SelectionKey key : selectedKeys) {
            if (!key.isValid())
                continue;

            try {
                handleKey(key);
            } catch (IOException ex) {
                try {
                    ex.printStackTrace();
                    key.channel().close();
                    key.cancel();
                } catch (IOException ex2) {
                    // Ignored
                }
            }
        }

        selectedKeys.clear();
    }

    private void handleKey(SelectionKey key) throws IOException {
        // Handle server socket ready to accept
        if (key.isAcceptable()) {
            HostBind binding = (HostBind) key.attachment();

            SocketChannel channel = binding.channel.accept();
            if (channel == null)
                return;

            SelectorThread.tuneChannel(channel);

            SocketLink link = binding.factory.makeLink(channel);
            link.connectionMade(channel.socket().getInetAddress());
            updateKey(link);
            return;
        }

        // Handle client socket ready to connect
        if (key.isConnectable()) {
            ClientBind binding = (ClientBind) key.attachment();
            SocketLink link = binding.link;

            SocketChannel channel = link.getChannel();
            channel.finishConnect();

            link.connectionMade(channel.socket().getInetAddress());
            key.attach(link);
            updateKey(link);

            binding.status.setStatus(ConnectStatus.CONNECTED);
            return;
        }

        // Handle socket ready to read
        if (key.isReadable()) {
            SocketLink link = (SocketLink) key.attachment();
            link.canRead();
            updateKey(link);
            return;
        }

        // Handle socket ready to write
        if (key.isWritable()) {
            SocketLink link = (SocketLink) key.attachment();
            link.canWrite();
            updateKey(link);
            return;
        }
    }
}
