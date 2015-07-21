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
package com.zapolnov.buildsystem.plugins.metacompiler.parser.ast;

import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxToken;
import java.io.Serializable;

/** AST node for a fully qualified name. */
public class CxxFullyQualifiedName implements Serializable
{
    /** First token of the name. */
    public final CxxToken firstToken;
    /** Text of the name. */
    public final String text;

    /**
     * Constructor.
     * @param firstToken First token of the name.
     * @param text Text of the name.
     */
    public CxxFullyQualifiedName(CxxToken firstToken, String text)
    {
        this.firstToken = firstToken;
        this.text = text;
    }

    /**
     * Retrieves last component of this fully qualified name.
     * @return Last component of this fully qualified name.
     */
    public String lastComponent()
    {
        if (text == null || text.isEmpty())
            return "";

        String[] parts = text.split("::");
        int last = parts.length - 1;
        return (last >= 0 ? parts[last] : "");
    }

    /**
     * Merges this name with the specified name.
     * @param other Fully qualified name to merge with.
     * @return Merged name.
     */
    public CxxFullyQualifiedName mergeWith(CxxFullyQualifiedName other)
    {
        if (other.text.startsWith("::"))
            return other;

        if (firstToken == null && text.isEmpty())
            return other;

        return new CxxFullyQualifiedName(firstToken, text + "::" + other.text);
    }
}
