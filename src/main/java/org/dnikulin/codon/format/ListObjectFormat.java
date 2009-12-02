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

package org.dnikulin.codon.format;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dnikulin.codon.format.except.ObjectCorruptException;

/**
 * Object format for lists containing objects that are all supported by a
 * specific format.
 */
public class ListObjectFormat extends StreamObjectFormat<List<?>> {
    private final ObjectFormat format;

    /**
     * Construct a list object format with the given element format.
     * 
     * @param format
     *            Element format
     */
    public ListObjectFormat(ObjectFormat format) {
        this.format = format;
    }

    @Override
    public String getFormatName() {
        return "list(" + format.getFormatName() + ")";
    }

    @Override
    public Class<?> getObjectClass() {
        return List.class;
    }

    @Override
    public void write(DataOutputStream out, List<?> list) throws IOException {
        for (Object object : list) {
            byte[] bytes = format.encode(object);

            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }

    @Override
    public List<?> read(DataInputStream in) throws IOException,
            ObjectCorruptException {
        List<Object> list = new ArrayList<Object>();

        while (in.available() > 0) {
            int size = in.readInt();
            byte[] bytes = new byte[size];
            in.read(bytes);

            Object object = format.decode(bytes);
            list.add(object);
        }

        return list;
    }
}
