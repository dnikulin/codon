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

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.jcombinator.log.LineLogger;
import org.dnikulin.jcombinator.log.NullLogger;

/**
 * A plugin slot and node registry. Supports automatic type-safe plugin
 * installation.
 */
public class PluginLinker {
    private final LineLogger logger;
    private final List<PluginSlot> slots;
    private final List<PluginNode> nodes;

    /**
     * Construct a PluginLinker with the given line logger.
     * 
     * @param logger
     *            Line logger
     */
    public PluginLinker(LineLogger logger) {
        if (logger == null)
            throw new NullPointerException("logger is null");

        this.logger = logger;

        slots = new ArrayList<PluginSlot>();
        nodes = new ArrayList<PluginNode>();
    }

    /**
     * Construct a PluginLinker with no line logger.
     */
    public PluginLinker() {
        this(NullLogger.INSTANCE);
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
     * Register a plugin slot. This slot will receive compatible plugins.
     * 
     * @param slot
     *            Plugin slot
     */
    public synchronized boolean addPluginSlot(PluginSlot slot) {
        if (slots.contains(slot))
            return false;

        slots.add(slot);

        String name = slot.getPluginSlotName();
        logger.print("Registered plugin slot '" + name + "'");
        return true;
    }

    /**
     * Return a copy of the plugin slot list. Changes to the returned list will
     * not affect the internal slot list.
     * 
     * @return Slot list copy
     */
    public synchronized List<PluginSlot> getPluginSlots() {
        return new ArrayList<PluginSlot>(slots);
    }

    /**
     * Register a plugin node. This node may be installed into compatible slots.
     * 
     * @param node
     *            Plugin node
     */
    public synchronized boolean addPluginNode(PluginNode node) {
        if (nodes.contains(node))
            return false;

        nodes.add(node);

        String name = node.getPluginName();
        logger.print("Registered plugin node '" + name + "'");
        return true;
    }

    /**
     * Return a copy of the plugin node list. Changes to the returned list will
     * not affect the internal node list.
     * 
     * @return Node list copy
     */
    public synchronized List<PluginNode> getPluginNodes() {
        return new ArrayList<PluginNode>(nodes);
    }

    /**
     * Determine if a plugin slot is compatible with a plugin node.
     * 
     * @param slot
     *            Plugin slot
     * @param node
     *            Plugin node
     * @return true if the slot and node are compatible
     */
    public static boolean isCompatible(PluginSlot slot, PluginNode node) {
        return slot.getPluginInterface().isAssignableFrom(node.getClass());
    }
}
