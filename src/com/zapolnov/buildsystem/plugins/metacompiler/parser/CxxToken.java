/*
 * Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.zapolnov.buildsystem.plugins.metacompiler.parser;

import java.io.Serializable;

/** A token in the C++ file. */
public final class CxxToken implements Serializable
{
    /** End of file. */
    public static final int EOF = 0;
    /** An unrecognized token. */
    public static final int UNRECOGNIZED = -1;

    /** An identifier. */
    public static final int IDENTIFIER = 1;

    /** The '<' symbol. */
    public static final int LESS = 50;
    /** The '>' symbol. */
    public static final int GREATER = 51;
    /** The '(' symbol. */
    public static final int LPAREN = 52;
    /** The ')' symbol. */
    public static final int RPAREN = 53;
    /** The '{' symbol. */
    public static final int LCURLY = 54;
    /** The '}' symbol. */
    public static final int RCURLY = 55;
    /** The ',' symbol. */
    public static final int COMMA = 56;
    /** The '=' symbol. */
    public static final int EQUAL = 57;
    /** The ':' symbol. */
    public static final int COLON = 58;
    /** The '::' symbol. */
    public static final int SCOPE = 59;
    /** The ';' symbol. */
    public static final int SEMICOLON = 60;

    /** The 'class' keyword. */
    public static final int CLASS = 100;
    /** The 'namespace' keyword. */
    public static final int NAMESPACE = 101;
    /** The 'private' keyword. */
    public static final int PRIVATE = 102;
    /** The 'protected' keyword. */
    public static final int PROTECTED = 103;
    /** The 'public' keyword. */
    public static final int PUBLIC = 104;
    /** The 'struct' keyword. */
    public static final int STRUCT = 105;
    /** The 'template' keyword. */
    public static final int TEMPLATE = 106;
    /** The 'typename' keyword. */
    public static final int TYPENAME = 107;
    /** The 'virtual' keyword. */
    public static final int VIRTUAL = 108;

    /** The 'Z_INTERFACE' identifier. */
    public static final int Z_INTERFACE = 200;
    /** The 'Z_IMPLEMENTATION' identifier. */
    public static final int Z_IMPLEMENTATION = 201;
    /** The 'Z_SINGLETON_IMPLEMENTATION' identifier. */
    public static final int Z_SINGLETON_IMPLEMENTATION = 202;
    /** The 'Z_CUSTOM_IMPLEMENTATION' identifier. */
    public static final int Z_CUSTOM_IMPLEMENTATION = 203;


    /** Kind of the token. */
    public final int id;
    /** Line number in the source file. */
    public final int line;
    /** Column number in the source file. */
    public final int column;
    /** Text of the token. */
    public final String text;

    /**
     * Constructor.
     * @param id Kind of the token.
     * @param line Line number in the source file.
     * @param column Column number in the source file.
     * @param text Text of the token.
     */
    public CxxToken(int id, int line, int column, String text)
    {
        this.id = id;
        this.line = line;
        this.column = column;
        this.text = text;
    }

    /**
     * Retrieves a string representation of location of this token in the source file.
     * @return String representation of token location.
     */
    public String location()
    {
        return String.format("(%d,%d)", line, column);
    }
}
