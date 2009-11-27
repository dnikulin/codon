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

package org.dnikulin.codon.pipe.core;

/** An object producing object values of a specific type. */
public interface Producer {
    /**
     * Query expected output type. All objects released to this object's
     * consumers are expected to conform to this type.
     * 
     * @return Expected output type
     */
    public Class<?> getOutputType();

    /**
     * Add a consumer for this producer.
     * 
     * @param consumer
     *            Consumer to add
     * @return true iff the consumer was added
     */
    public boolean addConsumer(Consumer consumer);

    /**
     * Query whether the producer has at least one consumer.
     * 
     * @return true iff the producer has at least one consumer
     */
    public boolean hasConsumer();

    /**
     * Remove a specific consumer.
     * 
     * @param consumer
     *            Consumer to remove
     */
    public void removeConsumer(Consumer consumer);

    /**
     * Remove all consumers.
     */
    public void removeConsumers();
}
