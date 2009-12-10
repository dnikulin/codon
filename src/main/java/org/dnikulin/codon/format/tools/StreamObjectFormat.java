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

package org.dnikulin.codon.format.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectCorruptException;

/** An ObjectFormat implemented using DataInputStream and DataOutputStream. */
public abstract class StreamObjectFormat<T> implements ObjectFormat {
    @Override
    @SuppressWarnings("unchecked")
    public byte[] encode(Object object) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            write(out, (T) object);
            out.flush();
            return bytes.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public Object decode(byte[] bytes) throws ObjectCorruptException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bis);

        try {
            return read(in);
        } catch (IOException ex) {
            throw new ObjectCorruptException(ex);
        }
    }

    /**
     * Read an object from a data input stream.
     * 
     * @param in
     *            Input stream
     * @return Object
     */
    public abstract T read(DataInputStream in) throws IOException,
            ObjectCorruptException;

    /**
     * Write an object to a data output stream.
     * 
     * @param out
     *            Output stream
     * @param object
     *            Object
     */
    public abstract void write(DataOutputStream out, T object)
            throws IOException;
}
