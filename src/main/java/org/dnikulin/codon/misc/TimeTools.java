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

package org.dnikulin.codon.misc;

/** Utility class containing time-related methods. */
public final class TimeTools {
    /**
     * Suspend execution for at least a given time.
     * 
     * @param millis
     *            Milliseconds to sleep for
     */
    public static void sleepFor(long millis) {
        long started = System.currentTimeMillis();
        sleepUntil(started + millis, started);
    }

    /**
     * Suspend execution until at least a given time, by the contract of
     * System.currentTimeMillis()
     * 
     * @param time
     *            Milliseconds to sleep until
     */
    public static void sleepUntil(long time) {
        sleepUntil(time, System.currentTimeMillis());
    }

    /**
     * Suspend execution until at least a given time, assuming a specific
     * current time, by the contract of System.currentTimeMillis()
     * 
     * @param time
     *            Milliseconds to sleep until
     * @param start
     *            Starting time
     */
    public static void sleepUntil(long time, long start) {
        long from = start;

        while (true) {
            long duration = time - from;

            if (duration <= 0)
                break;

            try {
                Thread.sleep(duration);
                break;
            } catch (InterruptedException ex) {
                from = System.currentTimeMillis();
            }
        }
    }

    private TimeTools() {
    }
}
