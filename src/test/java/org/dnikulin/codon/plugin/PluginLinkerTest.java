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

package org.dnikulin.codon.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.codon.log.CountingLogger;
import org.dnikulin.codon.log.NullLogger;
import org.dnikulin.codon.log.PrintLogger;
import org.dnikulin.codon.plugin.PluginLinker;
import org.dnikulin.codon.plugin.PluginNode;
import org.dnikulin.codon.plugin.PluginSlot;
import org.dnikulin.codon.plugin.mock.NullPluginNode;
import org.dnikulin.codon.plugin.mock.NullPluginSlot;
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

    /** Constructor with null logger must use NullLogger. */
    @Test
    public void testConstructorNull() {
        PluginLinker linker = new PluginLinker(null);
        assertSame(NullLogger.INSTANCE, linker.getLineLogger());
    }

    /** Linker must include a plugin slot list. */
    @Test
    public void testPluginSlotList() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot();

        // Must not log anything yet
        assertEquals(0, log.count());

        // Must return true when adding a new slot, and log exactly once
        assertTrue(linker.addPluginSlot(slot1));
        assertEquals(1, log.count());

        assertTrue(linker.addPluginSlot(slot2));
        assertEquals(2, log.count());

        // Must return false when re-adding an old slot, and not log
        assertFalse(linker.addPluginSlot(slot1));
        assertEquals(2, log.count());
        assertFalse(linker.addPluginSlot(slot2));
        assertEquals(2, log.count());

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

        // Must not consider slots in the node class check
        assertFalse(linker.hasPluginNodeForClass(NullPluginSlot.class));
        assertFalse(linker.hasPluginNodeForClass(NullPluginSlot2.class));
    }

    /** Linker must include a plugin node list. */
    @Test
    public void testPluginNodeList() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginNode node2 = new NullPluginNode2();

        // Must not log anything yet
        assertEquals(0, log.count());

        // Must recall which node classes are registered (nothing yet)
        assertFalse(linker.hasPluginNodeForClass(NullPluginNode.class));
        assertFalse(linker.hasPluginNodeForClass(NullPluginNode2.class));

        // Must return true when adding a new node, and log exactly once
        assertTrue(linker.addPluginNode(node1));
        assertEquals(1, log.count());

        assertTrue(linker.addPluginNode(node2));
        assertEquals(2, log.count());

        // Must return false when re-adding an old node, and not log
        assertFalse(linker.addPluginNode(node1));
        assertEquals(2, log.count());
        assertFalse(linker.addPluginNode(node2));
        assertEquals(2, log.count());

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

        // Must recall which node classes are registered
        assertTrue(linker.hasPluginNodeForClass(NullPluginNode.class));
        assertTrue(linker.hasPluginNodeForClass(NullPluginNode2.class));
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
        assertEquals(0, log.count());

        // Must install returning true if no exception is thrown
        // Must log exactly once
        assertTrue(linker.installPlugin(slot1, node1));
        assertEquals(1, log.count());

        // Must install returning false if an exception is thrown
        // Must log exactly once
        assertFalse(linker.installPlugin(slot2, node1));
        assertEquals(2, log.count());
    }

    /** Linker must be able to auto-install when adding nodes. */
    @Test
    public void testSlotAutoInstall() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot2();
        PluginNode node2 = new NullPluginNode2();

        // Must not log anything yet
        assertEquals(0, log.count());

        // Must return true when adding new slots
        // Must log exactly once per addition
        assertTrue(linker.addPluginSlot(slot1));
        assertEquals(1, log.count());
        assertTrue(linker.addPluginSlot(slot2));
        assertEquals(2, log.count());

        // Must return true and install into working slot, logging for both
        assertTrue(linker.addPluginNode(node1));
        assertEquals(4, log.count());

        // Must return false and not log for re-add
        assertFalse(linker.addPluginNode(node1));
        assertEquals(4, log.count());

        // Must return true, install into working slot,
        // and fail for non-working slot, logging for all
        assertTrue(linker.addPluginNode(node2));
        assertEquals(7, log.count());
    }

    /** Linker must be able to auto-install when adding slots. */
    @Test
    public void testNodeAutoInstall() {
        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginSlot slot1 = NullPluginSlot.INSTANCE;
        PluginNode node1 = NullPluginNode.INSTANCE;
        PluginSlot slot2 = new NullPluginSlot2();
        PluginNode node2 = new NullPluginNode2();

        // Must not log anything yet
        assertEquals(0, log.count());

        // Must return true when adding new nodes
        // Must log exactly once per addition
        assertTrue(linker.addPluginNode(node1));
        assertEquals(1, log.count());
        assertTrue(linker.addPluginNode(node2));
        assertEquals(2, log.count());

        // Must return true and install both plugins, logging for all
        assertTrue(linker.addPluginSlot(slot1));
        assertEquals(5, log.count());

        // Must return false and not log for re-add
        assertFalse(linker.addPluginSlot(slot1));
        assertEquals(5, log.count());

        // Must return true and fail to install for one node,
        // logging for both
        assertTrue(linker.addPluginSlot(slot2));
        assertEquals(7, log.count());
    }

    /** Linker must be able to automatically make nodes from classes. */
    @Test
    public void testMakeNodes() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(NullPluginNode.class);
        classes.add(NullPluginNode2.class);
        classes.add(String.class);

        CountingLogger log = new CountingLogger();
        PluginLinker linker = new PluginLinker(log);

        PluginNode node = NullPluginNode.INSTANCE;

        // Must not log anything yet
        assertEquals(0, log.count());

        // Must return true when adding new nodes
        // Must log exactly once per addition
        assertTrue(linker.addPluginNode(node));
        assertEquals(1, log.count());

        // Must have only the class added
        assertTrue(linker.hasPluginNodeForClass(NullPluginNode.class));
        assertFalse(linker.hasPluginNodeForClass(NullPluginNode2.class));

        // Must be able to auto-add node from classes, and log for those added
        linker.makePluginNodes(classes);
        assertTrue(linker.hasPluginNodeForClass(NullPluginNode.class));
        assertTrue(linker.hasPluginNodeForClass(NullPluginNode2.class));
        assertFalse(linker.hasPluginNodeForClass(String.class));
        assertEquals(2, log.count());

        // Must log when instantiation fails
        classes.add(NullPluginNode4.class);
        linker.makePluginNodes(classes);
        assertFalse(linker.hasPluginNodeForClass(NullPluginNode4.class));
        assertEquals(3, log.count());
    }

    public static class NullPluginNode2 extends NullPluginNode {
    }

    public static class NullPluginNode3 extends NullPluginNode {
    }

    private static class NullPluginNode4 extends NullPluginNode {
        private NullPluginNode4() {
            // Will fail to create
        }
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
