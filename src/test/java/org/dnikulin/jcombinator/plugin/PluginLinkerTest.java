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
}
