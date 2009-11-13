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

package org.dnikulin.jcombinator.plugin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.dnikulin.jcombinator.plugin.PluginLoader.classToPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.log.PrintLogger;
import org.junit.Test;

public class PluginLoaderTest {
    /**
     * Default constructor must use the system ClassLoader as a parent,
     * constructor with ClassLoader argument must use it as the parent. Each
     * must have null line logger.
     */
    @Test
    public void testConstructorWithLogger() {
        PluginLoader parent = new PluginLoader();
        assertSame(ClassLoader.getSystemClassLoader(), parent.getParent());
        assertSame(NullLogger.INSTANCE, parent.getLineLogger());

        PluginLoader child = new PluginLoader(parent);
        assertSame(parent, child.getParent());
        assertSame(NullLogger.INSTANCE, child.getLineLogger());

        PluginLoader withNullParent = new PluginLoader((ClassLoader) null);
        assertSame(null, withNullParent.getParent());
        assertSame(NullLogger.INSTANCE, withNullParent.getLineLogger());
    }

    /** Constructors with given logger must connect that logger. */
    @Test
    public void testConstructorWithoutLogger() {
        PluginLoader parent = new PluginLoader(PrintLogger.SYSOUT);
        assertSame(PrintLogger.SYSOUT, parent.getLineLogger());

        PluginLoader child = new PluginLoader(parent, PrintLogger.SYSERR);
        assertSame(parent, child.getParent());
        assertSame(PrintLogger.SYSERR, child.getLineLogger());
    }

    /** Constructor with null logger must throw a NullPointerException. */
    @Test
    public void testConstructorNullLogger() {
        boolean threw = false;

        try {
            new PluginLoader(ClassLoader.getSystemClassLoader(), null);
        } catch (NullPointerException ex) {
            threw = true;
        }

        assertTrue(threw);
    }

    /** Must be able to read any byte stream into an array. */
    public void testReadStream(int size) throws IOException {
        byte[] data = new byte[size];

        for (int i = 0; i < data.length; i++)
            data[i] = (byte) i;

        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        assertEquals(data.length, stream.available());

        PluginLoader loader = new PluginLoader();
        byte[] ndata = loader.readStream(stream);

        assertEquals(data.length, ndata.length);
        assertEquals(size, ndata.length);
        assertArrayEquals(data, ndata);
    }

    /** Must be able to read any byte stream into an array. */
    @Test
    public void testReadStream() throws IOException {
        testReadStream(7);
        testReadStream(4 * 1024);
        testReadStream(8 * 1024);
        testReadStream(419713);
    }

    /**
     * Must be able to convert a class name to a resource path. Invalid inputs
     * are unimportant.
     */
    @Test
    public void testClassToPath() {
        assertEquals("Test.class", classToPath("Test"));
        assertEquals("org/test/Test.class", classToPath("org.test.Test"));
    }
}
