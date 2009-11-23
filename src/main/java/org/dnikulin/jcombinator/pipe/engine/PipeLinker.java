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

package org.dnikulin.jcombinator.pipe.engine;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.dnikulin.jcombinator.pipe.core.Consumer;
import org.dnikulin.jcombinator.pipe.core.Pipe;
import org.dnikulin.jcombinator.pipe.core.Producer;
import org.dnikulin.jcombinator.pipe.except.PipeNameInUseException;
import org.dnikulin.jcombinator.pipe.except.PipeNameInvalidException;
import org.dnikulin.jcombinator.pipe.except.PipeNotFoundException;
import org.dnikulin.jcombinator.pipe.except.PipeTypeException;

/** A named pipe registry. Allows pipe lookup and connection by name. */
public class PipeLinker {
    /** Valid pipe name regular expression. */
    public static final String PIPE_NAME_EX = "[a-zA-Z][a-zA-Z0-9_]*";

    /** Valid pipe name regular expression as Pattern. */
    public static final Pattern PIPE_NAME_RE = Pattern.compile(PIPE_NAME_EX);

    private final Map<String, Pipe> pipes;

    /** Construct a pipe linker with an empty pipe registry. */
    public PipeLinker() {
        pipes = new TreeMap<String, Pipe>();
    }

    /**
     * Add a pipe by the given name.
     * 
     * @param name
     *            Name to register pipe under
     * @param pipe
     *            Pipe to register
     */
    public synchronized void addPipe(String name, Pipe pipe)
            throws PipeNameInvalidException, PipeNameInUseException {

        if (!isPipeNameValid(name))
            throw new PipeNameInvalidException("Pipe name is invalid");

        if (pipes.containsKey(name))
            throw new PipeNameInUseException("Pipe name is already in use");

        pipes.put(name, pipe);
    }

    /**
     * Remove a pipe with the given name. Does nothing for non-existent name.
     * 
     * @param name
     *            Pipe to remove
     */
    public synchronized void removePipe(String name) {
        pipes.remove(name);
    }

    /**
     * Query pipe name set. Returns a copy that has no effect on the internal
     * set.
     * 
     * @return Set of pipe names as a copy
     */
    public synchronized Set<String> getPipeNames() {
        return new TreeSet<String>(pipes.keySet());
    }

    /**
     * Find a pipe by its name. Throws an exception if the pipe is not found.
     * 
     * @param name
     *            Pipe name
     * @return Pipe by this name
     */
    public synchronized Pipe getPipe(String name) throws PipeNotFoundException {
        Pipe pipe = pipes.get(name);
        if (pipe == null)
            throw new PipeNotFoundException("Pipe '" + name + "' not found");
        return pipe;
    }

    /**
     * Link pipes found by name. Throws on type error.
     * 
     * @param producerName
     *            Name of producer pipe
     * @param consumerName
     *            Name of consumer pipe
     */
    public synchronized void linkPipes(String producerName, String consumerName)
            throws PipeNotFoundException, PipeTypeException {

        Producer producer = getPipe(producerName);
        Consumer consumer = getPipe(consumerName);

        linkPipes(producer, consumer);
    }

    /**
     * Link pipes. Throws on type error.
     * 
     * @param producer
     *            Producer pipe
     * @param consumer
     *            Consumer pipe
     */
    public static void linkPipes(Producer producer, Consumer consumer)
            throws PipeTypeException {
        Class<?> producerType = producer.getOutputType();
        Class<?> consumerType = consumer.getInputType();

        if (!consumerType.isAssignableFrom(producerType)) {
            String pname = producerType.getSimpleName();
            String cname = consumerType.getSimpleName();
            String msg = "Type " + pname + " is not assignable to " + cname;
            throw new PipeTypeException(msg);
        }

        producer.addConsumer(consumer);
    }

    /**
     * Checks if a pipe name is valid. Valid pipe names start with a letter and
     * may follow with letters, numbers or underscores.
     * 
     * @return true iff the pipe name is valid
     */
    public static boolean isPipeNameValid(String name) {
        return PIPE_NAME_RE.matcher(name).matches();
    }
}
