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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.dnikulin.jcombinator.log.CountingLogger;
import org.dnikulin.jcombinator.log.NullLogger;
import org.dnikulin.jcombinator.log.PrintLogger;
import org.dnikulin.jcombinator.plugin.mock.NullPluginNode;
import org.dnikulin.jcombinator.plugin.mock.NullPluginSlot;
import org.junit.Test;

public class PluginLinkerTest {
    /** Default constructor must connect a NullLogger. */
    @Test
    public void testConstructorDefault() {
        PluginLinker linker = new PluginLinker();
        assertSame(NullLogger.INSTANCE, linker.getLineLogger());
    }

    /** Constructor with non-null logger must connect given logger. */
    @Test
    public void testConstructorArgument() {
        PluginLinker linker = new PluginLinker(PrintLogger.SYSOUT);
        assertSame(PrintLogger.SYSOUT, linker.getLineLogger());
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

    /** Linker must include a plugin slot list. */
    @Test
    public void testPluginSlotList() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot();

        // Must not log anything yet
        assertEquals(0, log.getCount());

        // Must return true when adding a new slot, and log exactly once
        assertTrue(linker.addPluginSlot(slot1));
        assertEquals(1, log.getCount());

        assertTrue(linker.addPluginSlot(slot2));
        assertEquals(2, log.getCount());

        // Must return false when re-adding an old slot, and not log
        assertFalse(linker.addPluginSlot(slot1));
        assertEquals(2, log.getCount());
        assertFalse(linker.addPluginSlot(slot2));
        assertEquals(2, log.getCount());

        // Must be able to return slot list
        List<PluginSlot> slots = linker.getPluginSlots();
        assertNotNull(slots);
        assertEquals(2, slots.size());
        assertTrue(slots.contains(slot1));
        assertTrue(slots.contains(slot2));

        // Slot list must be a copy, not reflected in the linker
        slots.clear();
        assertFalse(linker.addPluginSlot(slot1));
        assertFalse(linker.addPluginSlot(slot2));
    }

    /** Linker must include a plugin node list. */
    @Test
    public void testPluginNodeList() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginNode node2 = new NullPluginNode();

        // Must not log anything yet
        assertEquals(0, log.getCount());

        // Must return true when adding a new node, and log exactly once
        assertTrue(linker.addPluginNode(node1));
        assertEquals(1, log.getCount());

        assertTrue(linker.addPluginNode(node2));
        assertEquals(2, log.getCount());

        // Must return false when re-adding an old node, and not log
        assertFalse(linker.addPluginNode(node1));
        assertEquals(2, log.getCount());
        assertFalse(linker.addPluginNode(node2));
        assertEquals(2, log.getCount());

        // Must be able to return node list
        List<PluginNode> nodes = linker.getPluginNodes();
        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));

        // Node list must be a copy, not reflected in the linker
        nodes.clear();
        assertFalse(linker.addPluginNode(node1));
        assertFalse(linker.addPluginNode(node2));
    }

    /** Linker must be able to determine slot/node compatibility. */
    @Test
    public void testIsCompatible() {
        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginNode node1 = NullPluginNode.INSTANCE;

        PluginSlot slot2 = new NullPluginSlot2();
        PluginNode node2 = new NullPluginNode2();
        PluginNode node3 = new NullPluginNode3();

        // First, verify class identities
        assertSame(slot1.getPluginInterface(), node1.getClass());
        assertSame(slot2.getPluginInterface(), node2.getClass());
        assertNotSame(node1.getClass(), node2.getClass());
        assertNotSame(node1.getClass(), node3.getClass());
        assertNotSame(node2.getClass(), node3.getClass());

        // Must be compatible because class is identical
        assertTrue(PluginLinker.isCompatible(slot1, node1));

        // Must be compatible because class is subclass
        assertTrue(PluginLinker.isCompatible(slot1, node2));

        // Must be compatible because class is identical
        assertTrue(PluginLinker.isCompatible(slot2, node2));

        // Must be incompatible because class is superclass
        assertFalse(PluginLinker.isCompatible(slot2, node1));

        // Must be incompatible because class is a different child class
        assertFalse(PluginLinker.isCompatible(slot2, node3));
    }

    /** Linker must be able to install nodes into slots. */
    @Test
    public void testInstallPlugin() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot2();

        // Must not log anything yet
        assertEquals(0, log.getCount());

        // Must install returning true if no exception is thrown
        // Must log exactly once
        assertTrue(linker.installPlugin(slot1, node1));
        assertEquals(1, log.getCount());

        // Must install returning false if an exception is thrown
        // Must log exactly once
        assertFalse(linker.installPlugin(slot2, node1));
        assertEquals(2, log.getCount());
    }

    /** Linker must be able to register installed plugins. */
    @Test
    public void testRegisterPlugin() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot2();
        PluginNode node2 = new NullPluginNode2();

        // Must not log anything yet
        assertEquals(0, log.getCount());

        // Must return true when adding a new slot or node
        // Must log exactly once per addition
        assertTrue(linker.addPluginSlot(slot1)); // slot 0
        assertTrue(linker.addPluginSlot(slot2)); // slot 1
        assertTrue(linker.addPluginNode(node1)); // node 0
        assertTrue(linker.addPluginNode(node2)); // node 1
        assertEquals(4, log.getCount());

        // Must return true when first installing a working plugin
        // Must log exactly once per installation
        assertTrue(linker.installAndRegister(0, 0));
        assertEquals(5, log.getCount());
        assertTrue(linker.installAndRegister(0, 1));
        assertEquals(6, log.getCount());

        // Must return false when re-installing a plugin, and not log
        assertFalse(linker.installAndRegister(0, 0));
        assertFalse(linker.installAndRegister(0, 1));
        assertEquals(6, log.getCount());

        // Must return false when installing an incompatible plugin, and not log
        assertFalse(linker.installAndRegister(1, 0));
        assertEquals(6, log.getCount());

        // Must return false when installing a failing plugin, and log
        assertFalse(linker.installAndRegister(1, 1));
        assertEquals(7, log.getCount());
    }

    private static class NullPluginNode2 extends NullPluginNode {
    }

    private static class NullPluginNode3 extends NullPluginNode {
    }

    private static class NullPluginSlot2 extends NullPluginSlot {
        @Override
        public Class<? extends PluginNode> getPluginInterface() {
            return NullPluginNode2.class;
        }

        @Override
        public void installPlugin(PluginNode plugin) {
            throw new NullPointerException();
        };
    }
}
