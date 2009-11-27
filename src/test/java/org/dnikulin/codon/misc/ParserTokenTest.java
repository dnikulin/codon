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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dnikulin.codon.misc.ParserToken;
import org.junit.Test;

public class ParserTokenTest {
    @Test
    public void testParserToken() {
        final String empty = "";

        ParserToken token = new ParserToken();

        // Must start with 0 length
        assertEquals(0, token.length());
        assertEquals(empty, token.toString());
        assertEquals(empty, token.drain());

        // Must start with non-zero capacity
        assertTrue(token.capacity() > 0);

        // Must increment length once per character
        token.append('a');
        assertEquals(1, token.length());
        token.append('b');
        assertEquals(2, token.length());

        // Must harmlessly return token for toString()
        assertEquals("ab", token.toString());
        assertEquals("ab", token.toString());

        // Must return and clear for drain()
        assertEquals("ab", token.drain());
        assertEquals(empty, token.drain());

        // Must reset to 0 on reset()
        token.append('a');
        token.append('b');
        assertEquals(2, token.length());
        token.reset();
        assertEquals(0, token.length());
        assertEquals(empty, token.toString());
    }
}
