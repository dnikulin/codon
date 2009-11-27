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

package org.dnikulin.codon.pipe.compiler;

import java.util.ArrayList;
import java.util.List;

import org.dnikulin.codon.misc.ParserToken;
import org.dnikulin.codon.pipe.except.PipeException;

// Sample pipelines:
// cmd1 arg1 arg2 | cmd2 arg1 arg2
// ~pipe1 cmd1 arg1 arg2 | ~pipe2 cmd2 arg1 arg2
// cmd1 arg1 arg2 | ~pipe1
// ~pipe1 [ cmd1 arg1 arg2 | cmd2 arg1 arg2 ]

/** Parses a string definition of a pipeline. */
public class PipeShellParser {
    public static final char QUOTE = '"';
    public static final char BACKSLASH = '\\';
    public static final char SPACE = ' ';
    public static final char TAB = '\t';
    public static final char SHARP = '#';

    public static final char PIPENAME = '~';
    public static final char LINK = '|';
    public static final char GROUP_START = '[';
    public static final char GROUP_END = ']';

    private enum State {
        WHITE, TOKEN, ESCAPED, QUOTED, QUOTED_ESCAPED, COMMENT, NAMING
    }

    private final PipeShellCompiler compiler;

    private final ParserToken token;
    private final List<String> tokens;
    private State state;

    private String command;
    private final List<String> arguments;

    /** Construct an initial parser. */
    public PipeShellParser(PipeShellCompiler compiler) {
        this.compiler = compiler;

        token = new ParserToken();
        tokens = new ArrayList<String>();
        state = State.WHITE;

        command = null;
        arguments = new ArrayList<String>();
    }

    /**
     * Query pipe compiler.
     * 
     * @return Associated pipe compiler
     */
    public PipeShellCompiler getCompiler() {
        return compiler;
    }

    /** Reset parser state. */
    public void reset() {
        token.reset();
        tokens.clear();
        state = State.WHITE;
    }

    /** Parse an entire string. State is kept from previous parses. */
    public void feed(String str) throws PipeException {
        compiler.startCompile();

        for (int i = 0; i < str.length(); i++)
            feed(str.charAt(i));
        feedEnd();

        compiler.stopCompile();
    }

    /** Parse a single character. */
    public void feed(char ch) throws PipeException {
        switch (state) {

        case COMMENT:
            break;

        case NAMING:
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

            case QUOTE:
                state = State.QUOTED;
                break;

            case LINK:
                endCommand();
                compiler.takePipeLink();
                state = State.WHITE;
                break;

            case GROUP_START:
                endCommand();
                compiler.takeGroupStart();
                state = State.WHITE;
                break;

            case GROUP_END:
                endCommand();
                compiler.takeGroupEnd();
                state = State.WHITE;
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

            case QUOTE:
                state = State.QUOTED;
                break;

            case SHARP:
                state = State.COMMENT;
                break;

            case LINK:
                endCommand();
                compiler.takePipeLink();
                break;

            case PIPENAME:
                state = State.NAMING;
                break;

            case GROUP_START:
                endCommand();
                compiler.takeGroupStart();
                break;

            case GROUP_END:
                endCommand();
                compiler.takeGroupEnd();
                break;

            default:
                state = State.TOKEN;
                token.append(ch);
            }
            break;

        case QUOTED:
            switch (ch) {
            case QUOTE:
                state = State.TOKEN;
                break;

            case BACKSLASH:
                state = State.QUOTED_ESCAPED;
                break;

            default:
                token.append(ch);
            }
            break;

        case QUOTED_ESCAPED:
            token.append(ch);
            state = State.QUOTED;
            break;

        case ESCAPED:
            token.append(ch);
            state = State.TOKEN;
            break;

        default:
            assert (false);
        }
    }

    /** Note the termination of a string. */
    public void feedEnd() throws PipeException {
        endCommand();
    }

    // Package-private
    String[] getTokens() {
        String[] out = new String[tokens.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = tokens.get(i);
        return out;
    }

    private void endCommand() throws PipeException {
        try {
            storeToken();

            if (command != null) {
                String[] ntokens = new String[arguments.size()];

                for (int i = 0; i < ntokens.length; i++)
                    ntokens[i] = arguments.get(i);

                compiler.takeCommand(command, ntokens);
            }
        } finally {
            command = null;
            arguments.clear();
        }
    }

    private void storeToken() throws PipeException {
        String ntoken = token.drain();

        if (ntoken.isEmpty())
            return;

        tokens.add(ntoken);

        switch (state) {
        case NAMING:
            compiler.takePipeName(ntoken);
            break;

        case TOKEN:
            if (command == null)
                command = ntoken;
            else
                arguments.add(ntoken);
            break;
        }
    }
}
