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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dnikulin.codon.log.LineLogger;
import org.dnikulin.codon.log.NullLogger;

/**
 * A plugin slot and node registry. Supports automatic type-safe plugin
 * installation.
 */
public class PluginLinker {
    private final LineLogger logger;
    private final List<PluginSlot> slots;
    private final List<PluginNode> nodes;
    private final Map<Integer, Set<Integer>> installed;
    private final Set<String> nodeClasses;

    /**
     * Construct a PluginLinker with the given line logger.
     * 
     * @param logger
     *            Line logger (may be null)
     */
    public PluginLinker(LineLogger logger) {
        this.logger = NullLogger.or(logger);

        slots = new ArrayList<PluginSlot>();
        nodes = new ArrayList<PluginNode>();
        installed = new TreeMap<Integer, Set<Integer>>();
        nodeClasses = new TreeSet<String>();
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

        int islot = slots.size();
        slots.add(slot);

        String name = slot.getPluginSlotName();
        logger.print("Registered plugin slot '" + name + "'");

        for (int inode = 0; inode < nodes.size(); inode++)
            installAndRegister(islot, inode);

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

        int inode = nodes.size();
        nodeClasses.add(node.getClass().getName());
        nodes.add(node);

        String name = node.getPluginName();
        logger.print("Registered plugin node '" + name + "'");

        for (int islot = 0; islot < slots.size(); islot++)
            installAndRegister(islot, inode);

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
     * Check if the linker has registered at least one node of this precise
     * class.
     * 
     * @param klass
     *            Node class (exact, not superclass)
     * @return true iff at least one node is registered with this exact class
     */
    public synchronized boolean hasPluginNodeForClass(Class<?> klass) {
        return nodeClasses.contains(klass.getName());
    }

    /**
     * Instantiate and add any plugin node classes that have no added instances.
     * Only classes that implement PluginNode will be used.
     * 
     * @param classes
     *            Collection of candidate classes
     */
    public synchronized void makePluginNodes(Collection<Class<?>> classes) {
        for (Class<?> klass : classes) {
            // Only consider PluginNode subclasses
            if (!PluginNode.class.isAssignableFrom(klass))
                continue;

            // Cannot instantiate an interface
            if (klass.isInterface())
                continue;

            // Cannot instantiate an abstract class
            if (Modifier.isAbstract(klass.getModifiers()))
                continue;

            // Do not auto-add if an instance is already added
            // (Whether or not it was added by this method!)
            // This is the most expensive check so do it last
            if (nodeClasses.contains(klass.getName()))
                continue;

            try {
                Object nodeObject = klass.newInstance();
                PluginNode node = (PluginNode) nodeObject;

                boolean added = addPluginNode(node);
                assert (added == true);
                assert (nodeClasses.contains(klass.getName()));
            } catch (Exception ex) {
                String name = klass.getSimpleName();
                String msg = ex.getLocalizedMessage();
                logger.print("Failed to make " + name + ": " + msg);
            }
        }
    }

    // Package-private
    synchronized boolean installAndRegister(int islot, int inode) {
        PluginSlot slot = slots.get(islot);
        PluginNode node = nodes.get(inode);

        if (isCompatible(slot, node) == false)
            return false;

        Set<Integer> installedNodes = installed.get(islot);

        if (installedNodes == null) {
            installedNodes = new TreeSet<Integer>();
            installed.put(islot, installedNodes);
        } else if (installedNodes.contains(inode)) {
            return false;
        }

        if (installPlugin(slot, node)) {
            installedNodes.add(inode);
            return true;
        }

        return false;
    }

    // Package-private
    boolean installPlugin(PluginSlot slot, PluginNode node) {
        String slotName = slot.getPluginSlotName();
        String nodeName = node.getPluginName();
        String combo = "'" + nodeName + "' into '" + slotName + "'";

        try {
            slot.installPlugin(node);
            logger.print("Installed " + combo);
            return true;
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            logger.print("Failed to install " + combo + ": " + msg);
            return false;
        }
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
