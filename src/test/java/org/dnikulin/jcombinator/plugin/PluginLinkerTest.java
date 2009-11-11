package org.dnikulin.jcombinator.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.log.PrintLogger;
import org.junit.Test;

public class PluginLinkerTest {
    /** Default constructor must connect a NullLogger. */
    @Test
    public void testConstructorDefault() {
        PluginLinker linker = new PluginLinker();
        assertEquals(NullLogger.INSTANCE, linker.getLineLogger());
    }

    /** Constructor with non-null logger must connect given logger. */
    @Test
    public void testConstructorArgument() {
        PluginLinker linker = new PluginLinker(PrintLogger.SYSOUT);
        assertEquals(PrintLogger.SYSOUT, linker.getLineLogger());
    }

    /** Constructor with null logger must throw a NullPointerException. */
    @Test
    public void testConstructorNull() {
        boolean threw = false;

        try {
            new PluginLinker(null);
        } catch (NullPointerException ex) {
            threw = true;
        }

        assertTrue(threw);
    }
}
