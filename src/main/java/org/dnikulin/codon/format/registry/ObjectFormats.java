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

package org.dnikulin.codon.format.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectFormatNotFoundException;

/** Object format registry. */
public class ObjectFormats {
    private final Map<String, ObjectFormat> byName;
    private final Map<String, ObjectFormat> byClass;

    /** Construct empty object format registry. */
    public ObjectFormats() {
        byName = new TreeMap<String, ObjectFormat>();
        byClass = new TreeMap<String, ObjectFormat>();
    }

    /**
     * Query registered object formats.
     * 
     * @return List of object formats
     */
    public synchronized List<ObjectFormat> getFormats() {
        return new ArrayList<ObjectFormat>(byClass.values());
    }

    /**
     * Register an object format.
     * 
     * @param format
     *            Object format
     */
    public synchronized void add(ObjectFormat format) {
        String formatName = format.getFormatName();
        String className = format.getObjectClass().getName();

        byName.put(formatName, format);
        byClass.put(className, format);
    }

    /**
     * Find object format by format name.
     * 
     * @param formatName
     *            Format name
     * @return Object format
     * @exception ObjectFormatNotFoundException
     *                If the format was not found
     */
    public synchronized ObjectFormat getByName(String formatName)
            throws ObjectFormatNotFoundException {
        return find(byName, formatName);
    }

    /**
     * Find object format by class name.
     * 
     * @param className
     *            Class name
     * @return Object format
     * @exception ObjectFormatNotFoundException
     *                If the format was not found
     */
    public synchronized ObjectFormat getByClass(String className)
            throws ObjectFormatNotFoundException {
        return find(byClass, className);
    }

    /**
     * Find object format by class.
     * 
     * @param klass
     *            Class
     * @return Object format
     * @exception ObjectFormatNotFoundException
     *                If the format was not found
     */
    public ObjectFormat getByClass(Class<?> klass)
            throws ObjectFormatNotFoundException {
        return getByClass(klass.getName());
    }

    private static ObjectFormat find(Map<String, ObjectFormat> map, String key)
            throws ObjectFormatNotFoundException {
        ObjectFormat format = map.get(key);
        if (format == null)
            throw new ObjectFormatNotFoundException();
        return format;
    }
}
