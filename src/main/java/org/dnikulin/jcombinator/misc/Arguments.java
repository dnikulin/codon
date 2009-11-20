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

package org.dnikulin.jcombinator.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** CLI style string arguments. */
public class Arguments {
    private final List<String> thetokens;
    private final List<String> argv;
    private final Set<String> flags;

    /** Number of raw tokens. */
    public final int tokens;

    /** Number of positional (non-flag) arguments. */
    public final int args;

    /**
     * Interpret token array as CLI arguments.
     * 
     * @param tokens
     *            Token array
     */
    public Arguments(String[] tokens) {
        thetokens = new ArrayList<String>(tokens.length);
        argv = new ArrayList<String>(tokens.length);
        flags = new TreeSet<String>();

        boolean argsOnly = false;
        for (String token : tokens) {
            this.thetokens.add(token);

            if (token.equals("--"))
                argsOnly = true;
            else if (!argsOnly && isFlag(token))
                flags.add(token.substring(1));
            else
                argv.add(token);
        }

        this.tokens = thetokens.size();
        args = argv.size();
    }

    /**
     * Check if a parameterless flag was used. Example: check for "-test" with
     * flag("test")
     * 
     * @param flag
     *            Flag string (without the leading '-')
     * @return true if the flag was used and without a parameter
     */
    public boolean flag(String flag) {
        return flags.contains(flag);
    }

    /**
     * Check if a flag was used with a parameter. Example: check for "-i3" with
     * flagHasArg("i")
     * 
     * @param flag
     *            Flag string (without the leading '-')
     * @return true if the flag was used and with a parameter
     */
    public boolean flagHasArg(String flag) {
        String value = get(flag);
        return (value != null) && (!value.isEmpty());
    }

    /**
     * Check if a flag was used with or without a parameter.
     * 
     * @param flag
     *            Flag string (without the leading '-')
     * @return true if the flag was used, with or without a parameter
     */
    public boolean flagOrHasArg(String flag) {
        return flag(flag) || flagHasArg(flag);
    }

    /**
     * Return specific raw token as given in constructor.
     * 
     * @param i
     *            Token index
     * @return Token string
     */
    public String token(int i) {
        return thetokens.get(i);
    }

    /**
     * Return specific positional (non-flag) argument.
     * 
     * @param i
     *            Argument index
     * @return Argument string
     */
    public String get(int i) {
        return argv.get(i);
    }

    /**
     * Return specific flag argument.
     * 
     * @param flag
     *            Flag prefix (without the leading '-')
     * @return Flag argument value (null if flag not found)
     */
    public String get(String flag) {
        for (String keyString : flags) {
            if (keyString.startsWith(flag))
                return keyString.substring(flag.length());
        }
        return null;
    }

    /**
     * Construct an array of the positional (non-flag) arguments.
     * 
     * @return Array of positional arguments
     */
    public String[] getPositional() {
        String[] out = new String[args];
        for (int i = 0; i < out.length; i++)
            out[i] = get(i);
        return out;
    }

    /**
     * Return integer represented by specific positional argument.
     * 
     * @param i
     *            Argument index
     * @return Flag argument value (def if flag not found)
     */
    public int getInt(int i, int def) {
        return parseIntOr(get(i), def);
    }

    /**
     * Return double-precision float represented by specific positional
     * argument.
     * 
     * @param i
     *            Argument index
     * @return Flag argument value (def if flag not found)
     */
    public double getDouble(int i, double def) {
        return parseDoubleOr(get(i), def);
    }

    /**
     * Return integer represented by specific flag argument.
     * 
     * @param flag
     *            Flag prefix (without the leading '-')
     * @return Flag argument value (def if flag not found)
     */
    public int getInt(String flag, int def) {
        return parseIntOr(get(flag), def);
    }

    /**
     * Return double-precision float represented by specific flag argument.
     * 
     * @param flag
     *            Flag prefix (without the leading '-')
     * @return Flag argument value (def if flag not found)
     */
    public double getDouble(String flag, double def) {
        return parseDoubleOr(get(flag), def);
    }

    /**
     * Check if a token string represents a flag. Flags start with a - and
     * follow with a non-digit character.
     * 
     * @param token
     *            Token to check
     * @return true if the token is a flag
     */
    public static boolean isFlag(String token) {
        if (token.length() < 2)
            return false;

        if (!token.startsWith("-"))
            return false;

        return !Character.isDigit(token.charAt(1));
    }

    /**
     * Parse an integer string or return a default value.
     * 
     * @param value
     *            String to parse
     * @param def
     *            Default value
     * @return Parsed integer or default
     */
    public static int parseIntOr(String value, int def) {
        if (value == null || value.isEmpty())
            return def;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    /**
     * Parse a double float string or return a default value.
     * 
     * @param value
     *            String to parse
     * @param def
     *            Default value
     * @return Parsed float or default
     */
    public static double parseDoubleOr(String value, double def) {
        if (value == null || value.isEmpty())
            return def;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }
}
