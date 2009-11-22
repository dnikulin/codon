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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PipeShellParserTest {
    @Test
    public void testBlankString() {
        assertParse("");
        assertParse(" \t  ");
    }

    @Test
    public void testSimpleTokens() {
        assertParse("foo", "foo");
    }

    @Test
    public void testPaddedTokens() {
        assertParse(" foo  \t", "foo");
        assertParse(" foo  bar  \t", "foo", "bar");
    }

    @Test
    public void testEscapedTokens() {
        assertParse("\\");
        assertParse("\\ ", " ");
        assertParse("\\\\", "\\");
        assertParse(" foo\\  ba\\r \\\\ ", "foo ", "bar", "\\");
    }

    @Test
    public void testQuotedTokens() {
        assertParse("\"foo\"", "foo");
        assertParse(" \"foo bar\"  ", "foo bar");
        assertParse("\"foo \\\" bar\"", "foo \" bar");
        assertParse("\"foo \\\\ bar\"", "foo \\ bar");

        // Test unterminated quotation as well
        assertParse("\"foo \\\\ bar  ", "foo \\ bar  ");
        assertParse("\"foo \\\\ bar  \\", "foo \\ bar  ");
    }

    @Test
    public void testComment() {
        // Comments must start with ' #' or '\t#'
        assertParse("foo bar # ignore rest", "foo", "bar");
        assertParse("\"foo bar\"\t# ignore rest", "foo bar");

        // If following token, quote or escape, not a comment
        assertParse("foo bar# baz", "foo", "bar#", "baz");
        assertParse(" \"foo bar\"# baz  ", "foo bar#", "baz");
        assertParse("foo \\# bar", "foo", "#", "bar");
    }

    /**
     * Assert that a parse results in the tokens expected.
     * 
     * @param line
     *            Line to parse
     * @param expect
     *            Tokens expected
     */
    public static void assertParse(String line, String... expect) {
        String[] result = parse(line);
        assertEquals(expect.length, result.length);
        assertArrayEquals(expect, result);
    }

    /**
     * Return the tokens parsed from a string.
     * 
     * @param line
     *            String to parse
     * @return Tokens parsed
     */
    public static String[] parse(String line) {
        PipeShellParser parser = new PipeShellParser();

        // Must start with no tokens
        assertEquals(0, parser.getTokens().length);

        parser.feed(line);
        return parser.getTokens();
    }
}
