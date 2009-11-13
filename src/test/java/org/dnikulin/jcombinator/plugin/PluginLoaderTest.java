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

import static org.dnikulin.jcombinator.plugin.PluginLoader.classToPath;
import static org.dnikulin.jcombinator.plugin.PluginLoader.pathToClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.dnikulin.jcombinator.log.CountingLogger;
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
        byte[] data = makeBytes(size);

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

    /** Must be able to store the contents of any InputStream. */
    @Test
    public void testImportStream() throws IOException {
        byte[] data = makeBytes(419713);

        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        assertEquals(data.length, stream.available());

        PluginLoader loader = new PluginLoader();
        String path = "org.test.bytes";
        loader.importStream(stream, path);
        byte[] ndata = loader.getBytes(path);

        assertEquals(data.length, ndata.length);
        assertArrayEquals(data, ndata);
    }

    /**
     * Must be able to silently import single files. Must be able to loadClass()
     * from them.
     */
    @Test
    public void testImportClassFile() throws IOException,
            ClassNotFoundException {

        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());

        // File paths
        String head = "test/";
        String path = head + "TestPluginNode.class";

        // Establish and confirm file
        File file = new File("bin/testplugin/" + path);
        assertTrue(file.exists());
        assertTrue(file.canRead());

        // Must be able to import a single file silently
        loader.importFile(file, head);
        assertEquals(0, log.getCount());

        // Must store file contents
        byte[] data = loader.getBytes(path);
        assertNotNull(data);
        assertTrue(data.length > 0);

        // Must use stored bytes for loadClass, and work silently
        String className = "test.TestPluginNode";
        Class<?> testClass = loader.loadClass(className);
        assertEquals(0, log.getCount());
        assertEquals(className, testClass.getName());

        // Must log and throw for corrupted class
        Arrays.fill(data, (byte) 7);
        boolean threw = false;

        try {
            loader.loadClass(className);
        } catch (ClassFormatError ex) {
            threw = true;
        }

        assertTrue(threw);
        assertEquals(1, log.getCount());
    }

    /**
     * Must be able to silently import jar archives. Must be able to loadClass()
     * from them.
     */
    @Test
    public void testImportClassJar() throws IOException, ClassNotFoundException {
        String jarPath = "bin/jcombinator-testplugin.jar";
        String nodePath = "test/TestPluginNode.class";
        String slotPath = "test/TestPluginSlot.class";
        String nodeClassName = "test.TestPluginNode";
        String slotClassName = "test.TestPluginSlot";

        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());

        // Must not yet have either TestPlugin* class
        assertFalse(tryLoadClass(loader, nodeClassName));
        assertFalse(tryLoadClass(loader, slotClassName));
        assertEquals(2, log.getCount());
        log.reset();

        // Establish and confirm jar file
        File jarFile = new File(jarPath);
        assertTrue(jarFile.exists());
        assertTrue(jarFile.canRead());

        // Must be able to import a jar file silently
        loader.importJar(jarFile);
        assertEquals(0, log.getCount());

        // Must store file contents (TestPluginNode)
        byte[] nodeBytes = loader.getBytes(nodePath);
        assertNotNull(nodeBytes);
        assertTrue(nodeBytes.length > 0);

        // Must store file contents (TestPluginNode)
        byte[] slotBytes = loader.getBytes(slotPath);
        assertNotSame(nodeBytes, slotBytes);
        assertNotNull(slotBytes);
        assertTrue(slotBytes.length > 0);

        // Must use stored bytes for loadClass (TestPluginNode)
        Class<?> nodeClass = loader.loadClass(nodeClassName);
        assertEquals(0, log.getCount());
        assertEquals(nodeClassName, nodeClass.getName());

        // Must use stored bytes for loadClass (TestPluginSlot)
        Class<?> slotClass = loader.loadClass(slotClassName);
        assertNotSame(nodeClass, slotClass);
        assertEquals(0, log.getCount());
        assertEquals(slotClassName, slotClass.getName());
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

    /**
     * Must be able to convert a resource path to a class name. Invalid inputs
     * are unimportant.
     */
    @Test
    public void testPathToClass() {
        assertEquals("Test", pathToClass("Test.class"));
        assertEquals("org.test.Test", pathToClass("org/test/Test.class"));
    }

    /** Must search internal byte array for loadClass(). Must log errors. */
    @Test
    public void testLoadClassWhenEmpty() throws ClassNotFoundException {
        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());

        // Must be able to load from parent without exceptions or logging
        Class<?> stringClass = loader.loadClass("java.lang.String");
        assertSame(String.class, stringClass);
        assertEquals(0, log.getCount());

        // Must log and throw for unavailable class
        assertFalse(tryLoadClass(loader, "org.NoSuchClass"));
        assertEquals(1, log.getCount());
    }

    /** Attempt to load a class and check for ClassNotFoundException. */
    public boolean tryLoadClass(ClassLoader loader, String className) {
        try {
            loader.loadClass("org.NoSuchClass");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * Generate a byte array for stream testing.
     * 
     * @param size
     *            Size of byte array
     * @return Generated byte array
     */
    public static byte[] makeBytes(int size) {
        byte[] data = new byte[size];
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) i;
        return data;
    }
}
