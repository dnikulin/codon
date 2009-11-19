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

package org.dnikulin.jcombinator.pipe.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PipeBuildParserTest {
    public static String[] parse(String line) {
        PipeBuildParser parser = new PipeBuildParser();

        // Must start with no tokens
        assertEquals(0, parser.getTokens().length);

        parser.feed(line);
        return parser.getTokens();
    }

    @Test
    public void testBlankString() {
        // Must return empty array for empty string
        String[] blank = parse("");
        assertEquals(0, blank.length);

        // Must return empty array for whitespace string
        String[] white = parse(" \t  ");
        assertEquals(0, white.length);
    }

    @Test
    public void testSingleToken() {
        String tok = "foo";

        // Must return exact string for single token
        String[] exact = parse(tok);
        assertEquals(1, exact.length);
        assertEquals(tok, exact[0]);
    }

    @Test
    public void testPaddedToken() {
        String tok = "foo";
        String pad = " " + tok + "\t ";

        // Must return exact string for padded token
        String[] exact = parse(pad);
        assertEquals(1, exact.length);
        assertEquals(tok, exact[0]);
    }
}
