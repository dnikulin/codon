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

package org.dnikulin.codon.log;

import java.io.PrintStream;

/** A LineLogger printing to a PrintStream. */
public class PrintLogger implements LineLogger {
    /** PrintLogger that prints to System.out */
    public static final PrintLogger SYSOUT = new PrintLogger(System.out);

    /** PrintLogger that prints to System.err */
    public static final PrintLogger SYSERR = new PrintLogger(System.err);

    private final PrintStream stream;

    /** Construct a PrintLogger that prints to System.out */
    public PrintLogger() {
        this(System.out);
    }

    /**
     * Construct a PrintLogger that prints to the given stream
     * 
     * @param stream
     *            The stream to print to
     */
    public PrintLogger(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public synchronized void print(String line) {
        stream.println(line);
    }
}
