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

package org.dnikulin.jcombinator.pipe.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.List;

import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.except.PipeFactoryException;
import org.junit.Test;

public class TestPipeCommandTest {
    @Test
    public void testNoArguments() {
        // No arguments -> input and output are Object
        attempt(Object.class, Object.class);
    }

    @Test
    public void testOneArgument() {
        // One argument -> input and output are that class
        attempt(String.class, String.class, "java.lang.String");
    }

    @Test
    public void testBothArguments() {
        // Two argument -> input and output are those arguments
        attempt(String.class, List.class, "java.lang.String", "java.util.List");
    }

    @Test
    public void testBadArguments() {
        // Must throw for bad class name
        String[] tokens = new String[] { "org.NoClass" };

        try {
            TestPipeCommand.INSTANCE.makePipe(tokens);

            // Must not reach
            fail();
        } catch (PipeFactoryException ex) {
            // Correct
        }
    }

    public static void attempt(Class<?> wantInClass, Class<?> wantOutClass,
            String... tokens) {

        try {
            Pipe pipe = TestPipeCommand.INSTANCE.makePipe(tokens);

            assertNotNull(pipe);
            assertSame(wantInClass, pipe.getInputType());
            assertSame(wantOutClass, pipe.getOutputType());
        } catch (PipeFactoryException ex) {
            fail();
        }
    }
}
