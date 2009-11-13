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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;

/**
 * A ClassLoader which imports from files and archives, combining many dynamic
 * paths into one tree.
 */
public class PluginLoader extends ClassLoader {
    private final LineLogger logger;
    private final byte[] streamBuffer;
    private final Map<String, byte[]> bytes;

    /**
     * Construct a PluginLoader with the given parent loader and line logger.
     * 
     * @param parent
     *            Parent ClassLoader
     * 
     * @param logger
     *            Line logger
     */
    public PluginLoader(ClassLoader parent, LineLogger logger) {
        super(parent);

        if (logger == null)
            throw new NullPointerException("logger is null");

        this.logger = logger;

        streamBuffer = new byte[8192];
        bytes = new HashMap<String, byte[]>();
    }

    /**
     * Construct a PluginLoader with the given parent ClassLoader and no line
     * logger.
     * 
     * @param parent
     *            Parent class loader
     */
    public PluginLoader(ClassLoader parent) {
        this(parent, NullLogger.INSTANCE);
    }

    /**
     * Construct a PluginLoader with the default parent ClassLoader and the
     * given line logger.
     * 
     * @param logger
     *            Line logger
     */
    public PluginLoader(LineLogger logger) {
        this(getSystemClassLoader(), logger);
    }

    /**
     * Construct a PluginLoader with the default parent ClassLoader and no line
     * logger.
     */
    public PluginLoader() {
        this(getSystemClassLoader(), NullLogger.INSTANCE);
    }

    /**
     * Query connected LineLogger.
     * 
     * @return Connected LineLogger
     */
    public LineLogger getLineLogger() {
        return logger;
    }

    /**
     * Load a class from stored bytes or parent loader.
     * 
     * @param className
     *            Name of class to load
     * @param resolve
     *            Resolve class or not
     * @return Loaded class
     */
    @Override
    public synchronized Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {

        Class<?> klass;

        // Check for class for which bytes are available
        byte[] classBytes = bytes.get(classToPath(className));

        if (classBytes == null) {
            try {
                // Check for class in parent class loader instead
                return getParent().loadClass(className);
            } catch (ClassNotFoundException ex) {
                logger.print("Loading class " + className
                        + ", no bytes and not in parent loader");
                throw ex;
            }
        }

        // Bytes found, interpret as class
        klass = defineClass(className, classBytes, 0, classBytes.length);

        // Interpretation failed
        if (klass == null) {
            String msg = "Failed to interpret bytes for " + className;
            logger.print(msg);
            throw new ClassNotFoundException(msg);
        }

        // Resolve if asked
        if (resolve)
            resolveClass(klass);

        return klass;
    }

    /**
     * Import a resource from an InputStream.
     * 
     * @param stream
     *            Input stream to read
     * @param path
     *            Original resource path
     */
    public synchronized void importStream(InputStream stream, String path)
            throws IOException {
        bytes.put(path, readStream(stream));
    }

    // Package-private
    synchronized byte[] getBytes(String path) {
        return bytes.get(path);
    }

    // Package-private
    synchronized byte[] readStream(InputStream stream) throws IOException {
        ByteArrayOutputStream nbytes = new ByteArrayOutputStream();

        int didread = 0;
        while ((didread = stream.read(streamBuffer)) > 0)
            nbytes.write(streamBuffer, 0, didread);

        nbytes.close();
        return nbytes.toByteArray();
    }

    /**
     * Converts a class path such as org.test.Test to org/test/Test.class
     * 
     * @param className
     *            Class name to convert
     * @return Resource path
     */
    public static String classToPath(String className) {
        return className.replace('.', '/') + ".class";
    }

    /**
     * Converts a resource path such as org/test/Test.class to org.test.Test
     * 
     * @param path
     *            Resource path to convert
     * @return Class name
     */
    public static String pathToClass(String path) {
        assert (path.endsWith(".class"));
        return path.substring(0, path.length() - 6).replace('/', '.');
    }
}
