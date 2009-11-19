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

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.jcombinator.misc.ParserToken;

/** Parses a string definition of a pipeline. */
public class PipeBuildParser {
    public static final char BACKSLASH = '\\';
    public static final char SPACE = ' ';
    public static final char TAB = '\t';

    private enum State {
        WHITE, TOKEN, ESCAPED
    }

    private final ParserToken token;
    private final List<String> tokens;
    private State state;

    /** Construct an initial parser. */
    public PipeBuildParser() {
        token = new ParserToken();
        tokens = new ArrayList<String>();
        state = State.WHITE;
    }

    /** Reset parser state. */
    public void reset() {
        token.reset();
        tokens.clear();
        state = State.WHITE;
    }

    /** Parse an entire string. State is kept from previous parses. */
    public void feed(String str) {
        for (int i = 0; i < str.length(); i++)
            feed(str.charAt(i));
        feedEnd();
    }

    /** Parse a single character. */
    public void feed(char ch) {
        switch (state) {
        case ESCAPED:
            token.append(ch);
            state = State.TOKEN;
            break;

        case TOKEN:
            switch (ch) {
            case SPACE:
            case TAB:
                storeToken();
                state = State.WHITE;
                break;

            case BACKSLASH:
                state = State.ESCAPED;
                break;

            default:
                token.append(ch);
            }
            break;

        case WHITE:
            switch (ch) {
            case SPACE:
            case TAB:
                break;

            case BACKSLASH:
                state = State.ESCAPED;
                break;

            default:
                state = State.TOKEN;
                token.append(ch);
            }
            break;

        default:
            assert (false);
        }
    }

    /** Note the termination of a string. */
    public void feedEnd() {
        storeToken();
    }

    // Package-private
    String[] getTokens() {
        String[] out = new String[tokens.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = tokens.get(i);
        return out;
    }

    private void storeToken() {
        String ntoken = token.drain();
        if (ntoken.length() > 0)
            tokens.add(ntoken);
    }
}
