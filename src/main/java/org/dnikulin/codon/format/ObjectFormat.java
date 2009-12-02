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

import org.dnikulin.codon.format.except.ObjectCorruptException;

/** Serialisation format for objects of a specific class. */
public interface ObjectFormat {
    /**
     * Query format name. This is necessary as a platform-neutral type and
     * format identifier.
     * 
     * @return Format name
     */
    public String getFormatName();

    /**
     * Query supported object class. It is required that encode() supports any
     * instance of this class, and decode() returns only instances of this
     * class.
     * 
     * @return Object class
     */
    public Class<?> getObjectClass();

    /**
     * Serialise object. Must return the same array contents for the same object
     * contents.
     * 
     * @param object
     *            Object to serialise
     * @return Serialised bytes
     */
    public byte[] encode(Object object);

    /**
     * Interpret serialised array. Must return the same object contents for the
     * same array contents.
     * 
     * @param bytes
     *            Bytes to interpret
     * @return Object
     */
    public Object decode(byte[] bytes) throws ObjectCorruptException;
}
