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

package org.dnikulin.codon.netpipe.hello;

import static org.dnikulin.codon.format.primitive.StringObjectFormat.readString;
import static org.dnikulin.codon.format.primitive.StringObjectFormat.writeString;
import static org.dnikulin.codon.misc.FlagTools.getFlag;
import static org.dnikulin.codon.misc.FlagTools.setFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.dnikulin.codon.format.except.ObjectCorruptException;
import org.dnikulin.codon.format.tools.StreamObjectFormat;

public class HelloObjectFormat extends StreamObjectFormat<NetPipeHello> {
    public static final HelloObjectFormat INSTANCE = new HelloObjectFormat();

    @Override
    public String getFormatName() {
        return "netpipe-hello";
    }

    @Override
    public Class<?> getObjectClass() {
        return NetPipeHello.class;
    }

    @Override
    public NetPipeHello read(DataInputStream in) throws IOException,
            ObjectCorruptException {

        byte code = in.readByte();
        String channelName = readString(in);

        boolean fs = getFlag(code, 1);
        boolean fr = getFlag(code, 2);
        boolean fl = getFlag(code, 4);

        return new NetPipeHello(channelName, fs, fr, fl);
    }

    @Override
    public void write(DataOutputStream out, NetPipeHello hello)
            throws IOException {

        int fs = setFlag(1, hello.sendToClient);
        int fr = setFlag(2, hello.receiveFromClient);
        int fl = setFlag(4, hello.listToClient);

        out.writeByte(fs | fr | fl);
        writeString(out, hello.channelName);
    }
}
