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

import static org.dnikulin.jcombinator.pipe.engine.PipeLinker.isPipeNameValid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PipeLinkerTest {
    @Test
    public void testIsPipeNameValid() {
        // Must not accept empty string
        assertFalse(isPipeNameValid(""));

        // Must accept single letter
        assertTrue(isPipeNameValid("a"));
        assertTrue(isPipeNameValid("Z"));

        // Must not accept single number or underscore
        assertFalse(isPipeNameValid("2"));
        assertFalse(isPipeNameValid("_"));

        // Must accept letter followed by letters, numbers or underscores
        assertTrue(isPipeNameValid("az"));
        assertTrue(isPipeNameValid("a2"));
        assertTrue(isPipeNameValid("a_"));
        assertTrue(isPipeNameValid("aZ2"));
        assertTrue(isPipeNameValid("a2Z"));
        assertTrue(isPipeNameValid("a2Z_"));

        // Must not accept any character that is not
        // a letter, number or underscore
        assertFalse(isPipeNameValid("#"));
        assertFalse(isPipeNameValid("a#"));
        assertFalse(isPipeNameValid("a_%"));
    }
}
