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

package org.dnikulin.jcombinator.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArgumentsTest {
    @Test
    public void testOnlyTokens() {
        String[] tokens = new String[] { "a", "b", "c" };
        Arguments args = new Arguments(tokens);

        // Must store only positional arguments
        assertEquals(tokens.length, args.args);
        assertEquals(tokens.length, args.tokens);
        assertArrayEquals(tokens, args.getPositional());

        for (int i = 0; i < tokens.length; i++) {
            // Must return positional arguments exactly
            assertEquals(tokens[i], args.get(i));

            // Must not confuse positional arguments with flags
            assertFalse(args.flag(tokens[i]));
            assertFalse(args.flagHasArg(tokens[i]));
            assertFalse(args.flagOrHasArg(tokens[i]));
        }
    }

    @Test
    public void testOnlyFlags() {
        String[] tokens = new String[] { "-a", "-b", "-c" };
        Arguments args = new Arguments(tokens);

        // Must not store any positional arguments
        assertEquals(0, args.args);
        assertEquals(0, args.getPositional().length);
        assertEquals(tokens.length, args.tokens);

        // Must return false for absent flags
        assertFalse(args.flag("none"));

        // Must return true for present flags
        for (int i = 0; i < tokens.length; i++)
            assertTrue(args.flag(tokens[i].substring(1)));
    }

    @Test
    public void testNamedArguments() {
        String[] tokens = new String[] { "-a1", "-b2", "-c3" };
        String[] flags = new String[] { "a", "b", "c" };
        Arguments args = new Arguments(tokens);

        // Must not store any positional arguments
        assertEquals(0, args.args);
        assertEquals(0, args.getPositional().length);
        assertEquals(tokens.length, args.tokens);

        for (String flag : flags) {
            // Must not consider argumented flags as pure flags
            assertFalse(args.flag(flag));
            assertTrue(args.flagHasArg(flag));
            assertTrue(args.flagOrHasArg(flag));
        }

        // Must retrieve string values
        assertEquals("1", args.get("a"));
        assertEquals("2", args.get("b"));
        assertEquals("3", args.get("c"));

        // Must retrieve integer values
        assertEquals(1, args.getInt("a", 7));
        assertEquals(2, args.getInt("b", 7));
        assertEquals(3, args.getInt("c", 7));
        assertEquals(7, args.getInt("d", 7));
    }

    @Test
    public void testMixed() {
        String[] tokens = new String[] { "-a1", "a", "-b2", "--", "-c3", "b2" };
        String[] pos = new String[] { tokens[1], tokens[4], tokens[5] };
        Arguments args = new Arguments(tokens);

        // Must not store any positional arguments
        assertEquals(pos.length, args.args);
        assertArrayEquals(pos, args.getPositional());
        assertEquals(tokens.length, args.tokens);

        // Must retrieve string and integer values
        assertEquals("1", args.get("a"));
        assertEquals(1, args.getInt("a", 7));

        // Must treat -c3 as a positional because it follows --
        assertFalse(args.flag("c"));
        assertFalse(args.flag("c3"));

        for (int i = 0; i < pos.length; i++) {
            // Must return positional arguments exactly
            assertEquals(pos[i], args.get(i));
        }
    }
}
