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

package org.dnikulin.codon.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.dnikulin.codon.format.primitive.StringObjectFormat;
import org.junit.Test;

public class PrimitiveFormatsTest {
    @Test
    public void testString() {
        testFormat(StringObjectFormat.INSTANCE, "", "test", "1232s сдфadrde");
    }

    public static void testFormat(ObjectFormat format, Object... objects) {
        // Test must be called with non-null format
        assertNotNull(format);

        // Test must be called with at least one object
        assertTrue(objects.length > 0);

        // Must have non-null non-empty name string
        String name = format.getFormatName();
        assertNotNull(name);
        assertFalse(name.isEmpty());

        // Must have non-null object class
        Class<?> klass = format.getObjectClass();
        assertNotNull(klass);

        for (Object object : objects) {
            // Test must be called with non-null objects of the correct type
            assertNotNull(object);
            assertTrue(klass.isAssignableFrom(object.getClass()));

            // Object must have consistent equals()
            assertEquals(object, object);

            // Must encode to non-null bytes without errors
            // Empty array is fine
            byte[] bytes = format.encode(object);
            assertNotNull(bytes);

            // Must decode to non-null object without errors
            // Object must equal original object
            // (as defined by the object class)
            // May return same reference, assuming state is immutable
            Object object2 = format.decode(bytes);
            assertNotNull(object2);
            assertEquals(object, object2);

            // Decoded object must encode to the SAME non-null bytes,
            // but NOT the same reference, as it may be modified
            byte[] bytes2 = format.encode(object2);
            assertNotNull(bytes2);
            assertNotSame(bytes, bytes2);
            assertEquals(bytes.length, bytes2.length);
            assertTrue(Arrays.equals(bytes, bytes2));

            // Subsequent encodings must still be identical,
            // but NOT the same reference, as it may be modified
            byte[] bytes3 = format.encode(object2);
            assertNotNull(bytes3);
            assertNotSame(bytes, bytes3);
            assertNotSame(bytes2, bytes3);
            assertEquals(bytes.length, bytes3.length);
            assertTrue(Arrays.equals(bytes, bytes3));
            assertTrue(Arrays.equals(bytes2, bytes3));
        }
    }
}
