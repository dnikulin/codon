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

public class StatusToken<StatusEnum> {
    private volatile StatusEnum current;
    private volatile Exception exception;

    public StatusToken(StatusEnum start) {
        current = start;
        exception = null;
    }

    public synchronized Exception waitForStatus(StatusEnum status) {
        while (current != status) {
            try {
                wait();
            } catch (Exception ignored) {
            }
        }

        return exception;
    }

    public synchronized void setStatus(StatusEnum status, Exception ex) {
        current = status;
        exception = ex;
        notifyAll();
    }

    public synchronized void setStatus(StatusEnum from, StatusEnum to,
            Exception ex) {
        if (current == from)
            setStatus(to, ex);
    }

    public void setStatus(StatusEnum status) {
        setStatus(status, (Exception) null);
    }

    public void setStatus(StatusEnum from, StatusEnum to) {
        setStatus(from, to, (Exception) null);
    }
}
