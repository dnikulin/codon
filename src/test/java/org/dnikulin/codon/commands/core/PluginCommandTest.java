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

package org.dnikulin.codon.commands.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.dnikulin.codon.log.CountingLogger;
import org.dnikulin.codon.plugin.PluginLinker;
import org.dnikulin.codon.plugin.PluginLoader;
import org.junit.Test;

public class PluginCommandTest {
    public static final String NODE_CLASS = "test.TestPluginNode";

    @Test
    public void testImportJar() {
        testGoodImport("bin/codon-testplugin.jar");
    }

    @Test
    public void testImportTree() {
        testGoodImport("bin");
    }

    @Test
    public void testImportLibraryJar() {
        testGoodImport("bin/codon.jar", "bin/codon-testplugin.jar");
    }

    @Test
    public void testImportAll() {
        testGoodImport("bin", "bin/codon.jar", "bin/codon-testplugin.jar");
    }

    @Test
    public void testMissingFile() {
        testBadImport("bin/fail");
    }

    public static void testGoodImport(String... paths) {
        PluginLinker linker = new PluginLinker();
        PluginLoader loader = new PluginLoader();
        PluginCommand command = new PluginCommand(linker, loader);

        // Loader must not yet have test node class
        try {
            loader.loadClass(NODE_CLASS);

            // Must not reach
            fail();
        } catch (ClassNotFoundException ex) {
            // Correct
        }

        CountingLogger log = new CountingLogger();
        for (String path : paths)
            command.execute(new String[] { path }, log);

        // Must not log anything for correct operation
        // Note that the linker and loader still have null loggers
        assertEquals(0, log.count());

        // Loader must have bytes for the test node class
        try {
            loader.loadClass(NODE_CLASS);
        } catch (ClassNotFoundException ex) {
            fail();
        }
    }

    public static void testBadImport(String path) {
        PluginLinker linker = new PluginLinker();
        PluginLoader loader = new PluginLoader();
        PluginCommand command = new PluginCommand(linker, loader);

        CountingLogger log = new CountingLogger();

        command.execute(new String[] { path }, log);

        // Must not log exactly once for a failed import
        assertEquals(1, log.count());
    }
}
