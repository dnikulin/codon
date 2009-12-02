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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.dnikulin.codon.format.ObjectFormat;
import org.dnikulin.codon.format.except.ObjectFormatException;
import org.dnikulin.codon.format.except.ObjectFormatNotFoundException;
import org.dnikulin.codon.format.primitive.StringObjectFormat;
import org.junit.Test;

public class ObjectFormatsTest {
    @Test
    public void testGoodGet() throws ObjectFormatException {
        ObjectFormat format1 = StringObjectFormat.INSTANCE;

        // Must add object without errors, any number of times
        ObjectFormats formats = new ObjectFormats();
        formats.add(format1);
        formats.add(format1);
        formats.add(format1);

        // Must return same object by either handle
        assertSame(format1, formats.getByName(format1.getFormatName()));
        assertSame(format1, formats.getByClass(format1.getObjectClass()));
    }

    @Test(expected = ObjectFormatNotFoundException.class)
    public void testBadGet() throws ObjectFormatException {
        ObjectFormat format1 = StringObjectFormat.INSTANCE;
        ObjectFormats formats = new ObjectFormats();

        // Must throw for unregistered format
        formats.getByName(format1.getFormatName());

        // Must not reach here
        fail();
    }
}
