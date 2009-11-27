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

package org.dnikulin.codon.misc;

/** A token built char-by-char by a parser. */
public class ParserToken implements Resettable {
    /** Default token capacity. */
    public static final int DEFAULT_CAPACITY = 1024;

    private char[] token;
    private int length;

    /**
     * Start an empty token bound by the given capacity.
     * 
     * @param capacity
     *            Token capacity in characters
     */
    public ParserToken(int capacity) {
        token = new char[capacity];
        length = 0;
    }

    /**
     * Start an empty token with the default capacity.
     */
    public ParserToken() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Build a string containing the token.
     * 
     * @return String containing the token
     */
    @Override
    public String toString() {
        return new String(token, 0, length);
    }

    /**
     * Append a character to the token.
     * 
     * @param ch
     *            Character to append
     */
    public void append(char ch) {
        token[length++] = ch;
    }

    /**
     * Query current token length.
     * 
     * @return Token length in characters
     */
    public int length() {
        return length;
    }

    /**
     * Query token capacity.
     * 
     * @return Token capacity in characters
     */
    public int capacity() {
        return token.length;
    }

    /** Clear the token. */
    @Override
    public void reset() {
        length = 0;
    }

    /**
     * Invoke toString() then reset().
     * 
     * @return Result of toString()
     */
    public String drain() {
        String out = toString();
        reset();
        return out;
    }
}
