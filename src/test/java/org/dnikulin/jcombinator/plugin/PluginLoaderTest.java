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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.dnikulin.jcombinator.log.CountingLogger;
import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.log.PrintLogger;
import org.dnikulin.jcombinator.pipe.command.PipeCommandsPluginNode;
import org.junit.Test;

public class PluginLoaderTest {
    private static final String NODE_FILE = "TestPluginNode.class";
    private static final String SLOT_FILE = "TestPluginSlot.class";

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
        ClassLoader sysloader = ClassLoader.getSystemClassLoader();

        PluginLoader loader1 = new PluginLoader(sysloader, null);
        assertSame(NullLogger.INSTANCE, loader1.getLineLogger());

        PluginLoader loader2 = new PluginLoader((LineLogger) null);
        assertSame(NullLogger.INSTANCE, loader2.getLineLogger());
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
        assertTrue(loader.getLoadedClasses().isEmpty());

        // File paths
        String head = "test/";
        String path = head + NODE_FILE;

        // Establish and confirm file
        File file = new File("bin/testplugin/" + path);
        assertTrue(file.exists());
        assertTrue(file.canRead());

        // Must be able to import a single file silently
        loader.importFile(file, head);
        assertEquals(0, log.count());

        // Must store file contents
        byte[] data = loader.getBytes(path);
        assertNotNull(data);
        assertTrue(data.length > 0);

        // Must use stored bytes for loadClass, and work silently
        String className = "test.TestPluginNode";
        Class<?> testClass = loader.loadClass(className);
        assertEquals(0, log.count());
        assertEquals(className, testClass.getName());

        // Must have only loaded this class and its dependencies
        assertEquals(4, loader.getLoadedClasses().size());

        // Must no longer attempt to parse class
        // Test by corrupting class bytes
        Arrays.fill(data, (byte) 7);
        boolean threw = false;

        try {
            loader.loadClass(className);
        } catch (ClassFormatError ex) {
            threw = true;
        }

        assertFalse(threw);
        assertEquals(0, log.count());

        // Must have not loaded any additional classes
        assertEquals(4, loader.getLoadedClasses().size());
    }

    /**
     * Shared import test routine.
     * 
     * @param code
     *            Test-specific import code
     */
    public void testImport(ImportCode code) throws IOException,
            ClassNotFoundException {

        String nodePath = "test/" + NODE_FILE;
        String slotPath = "test/" + SLOT_FILE;
        String nodeClassName = "test.TestPluginNode";
        String slotClassName = "test.TestPluginSlot";

        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Must not yet have either TestPlugin* class
        assertFalse(tryLoadClass(loader, nodeClassName));
        assertFalse(tryLoadClass(loader, slotClassName));
        assertEquals(2, log.count());
        log.reset();

        // Run import code, must be silent
        code.run(loader);
        assertEquals(0, log.count());

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
        assertEquals(0, log.count());
        assertEquals(nodeClassName, nodeClass.getName());

        // Must use stored bytes for loadClass (TestPluginSlot)
        Class<?> slotClass = loader.loadClass(slotClassName);
        assertNotSame(nodeClass, slotClass);
        assertEquals(0, log.count());
        assertEquals(slotClassName, slotClass.getName());

        // Must have only loaded those classes and their dependencies
        assertEquals(6, loader.getLoadedClasses().size());
    }

    /**
     * Must be able to silently import jar archives.
     */
    @Test
    public void testImportClassJar() throws IOException, ClassNotFoundException {
        testImport(new ImportCode() {
            public void run(PluginLoader loader) throws IOException {
                File root = new File("bin/jcombinator-testplugin.jar");
                assertTrue(root.exists());
                assertTrue(root.isFile());
                assertTrue(root.canRead());
                loader.importJar(root);
            };
        });
    }

    /**
     * Must be able to silently import directory trees.
     */
    @Test
    public void testImportTree() throws IOException, ClassNotFoundException {
        testImport(new ImportCode() {
            public void run(PluginLoader loader) throws IOException {
                File root = new File("bin/testplugin");
                assertTrue(root.exists());
                assertTrue(root.isDirectory());
                assertTrue(root.canRead());
                loader.importTree(root);
            };
        });
    }

    /**
     * Must be able to silently import single-file trees.
     */
    @Test
    public void testImportFileTree() throws IOException, ClassNotFoundException {
        testImport(new ImportCode() {
            public void run(PluginLoader loader) throws IOException {
                String base = "bin/testplugin/";
                String head = "test/";

                File nodeFile = new File(base + head + NODE_FILE);
                File slotFile = new File(base + head + SLOT_FILE);

                assertTrue(nodeFile.exists());
                assertTrue(nodeFile.isFile());
                assertTrue(nodeFile.canRead());

                assertTrue(slotFile.exists());
                assertTrue(slotFile.isFile());
                assertTrue(slotFile.canRead());

                loader.importTree(nodeFile, head);
                loader.importTree(slotFile, head);
            };
        });
    }

    /**
     * Must throw when loading corrupted class files.
     */
    @Test
    public void testImportCorruptClassFile() throws IOException,
            ClassNotFoundException {

        String nodePath = "test/" + NODE_FILE;
        String nodeClassName = "test.TestPluginNode";

        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Must not yet have either TestPlugin* class
        assertFalse(tryLoadClass(loader, nodeClassName));
        assertEquals(1, log.count());

        // Run jar import code, must be silent
        File root = new File("bin/jcombinator-testplugin.jar");
        assertTrue(root.exists());
        assertTrue(root.isFile());
        assertTrue(root.canRead());
        loader.importJar(root);
        assertEquals(1, log.count());

        // Must store file contents
        byte[] nodeBytes = loader.getBytes(nodePath);
        assertNotNull(nodeBytes);
        assertTrue(nodeBytes.length > 0);

        // Corrupt bytes
        Arrays.fill(nodeBytes, (byte) 7);
        boolean threw = false;

        try {
            loader.loadClass(nodeClassName);
        } catch (ClassFormatError ex) {
            threw = true;
        }

        assertTrue(threw);
        assertEquals(2, log.count());
        assertEquals(0, loader.getLoadedClasses().size());
    }

    /**
     * Must be able to load imported classes by name suffix.
     */
    @Test
    public void testLoadClasses() throws IOException, ClassNotFoundException {
        CountingLogger log = new CountingLogger();
        PluginLoader loader = new PluginLoader(log);

        // Re-check constructor, in case tests are run out of order
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(log, loader.getLineLogger());
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Run jar import code, must be silent
        File root = new File("bin/jcombinator-testplugin.jar");
        assertTrue(root.exists());
        assertTrue(root.isFile());
        assertTrue(root.canRead());
        loader.importJar(root);
        assertEquals(0, log.count());

        // Must not load any classes if given "NoSuffix"
        loader.loadClasses("NoSuffix");
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Must load exactly three classes if given "PluginNode"
        // (TestPluginNode, PluginNode and Object)
        loader.loadClasses("PluginNode");
        assertEquals(4, loader.getLoadedClasses().size());

        // Subsequent invocation must be idempotent
        loader.loadClasses("PluginNode");
        assertEquals(4, loader.getLoadedClasses().size());

        // Must load exactly two more classes if given "PluginSlot"
        // (TestPluginSlot, PluginSlot)
        loader.loadClasses("PluginSlot");
        assertEquals(6, loader.getLoadedClasses().size());

        // Must have logged nothing so far
        assertEquals(0, log.count());

        // Must have loaded correct classes from parent
        List<Class<?>> loaded = loader.getLoadedClasses();
        assertFalse(loaded.contains(null));
        assertEquals(6, loaded.size());
        assertTrue(loaded.contains(Object.class));
        assertTrue(loaded.contains(PipeCommandsPluginNode.class));
        assertTrue(loaded.contains(PluginSlot.class));

        // Must have loaded correct classes from imported bytes
        Class<?> nodeClass = loader.loadClass("test.TestPluginNode");
        Class<?> slotClass = loader.loadClass("test.TestPluginSlot");
        assertNotSame(nodeClass, slotClass);
        assertTrue(loaded.contains(nodeClass));
        assertTrue(loaded.contains(slotClass));
    }

    @Test
    public void testGiveLinkerNodes() throws IOException,
            ClassNotFoundException {

        // Construct loader
        CountingLogger loadlog = new CountingLogger();
        PluginLoader loader = new PluginLoader(loadlog);

        // Check loader constructor
        assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
        assertSame(loadlog, loader.getLineLogger());
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Construct linker
        CountingLogger linklog = new CountingLogger();
        PluginLinker linker = new PluginLinker(linklog);

        // Check linker constructor
        assertSame(linklog, linker.getLineLogger());
        assertTrue(linker.getPluginNodes().isEmpty());
        assertTrue(linker.getPluginSlots().isEmpty());

        // Run jar import code, must be silent
        File root = new File("bin/jcombinator-testplugin.jar");
        assertTrue(root.exists());
        assertTrue(root.isFile());
        assertTrue(root.canRead());
        loader.importJar(root);
        assertEquals(0, loadlog.count());

        // Give linker node classes from loader
        loader.givePluginLinkerNodes(linker);

        // Loader must have logged nothing
        // Linker must have logged that one node class was added
        assertEquals(0, loadlog.count());
        assertEquals(1, linklog.count());

        // Linker must now have the node class
        Class<?> nodeClass = loader.loadClass("test.TestPluginNode");
        assertTrue(linker.hasPluginNodeForClass(nodeClass));

        // Linker must now have the node instance
        List<PluginNode> nodes = linker.getPluginNodes();
        assertEquals(1, nodes.size());
        assertSame(nodeClass, nodes.get(0).getClass());

        // Linker must not have any slots
        assertTrue(linker.getPluginSlots().isEmpty());
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
        assertTrue(loader.getLoadedClasses().isEmpty());

        // Must be able to load from parent without exceptions or logging
        Class<?> stringClass = loader.loadClass("java.lang.String");
        assertSame(String.class, stringClass);
        assertEquals(0, log.count());
        assertEquals(1, loader.getLoadedClasses().size());

        // Must log and throw for unavailable class
        assertFalse(tryLoadClass(loader, "org.NoSuchClass"));
        assertEquals(1, log.count());
        assertEquals(1, loader.getLoadedClasses().size());
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

    private static interface ImportCode {
        public void run(PluginLoader loader) throws IOException;
    }
}
