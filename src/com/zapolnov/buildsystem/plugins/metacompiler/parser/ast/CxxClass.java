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

import java.io.Serializable;

/** Class declaration. */
public class CxxClass implements Serializable
{
    /** Set to `true` if this class is actually a struct. */
    public final boolean struct;
    /** Name of the class. */
    public final CxxIdentifier name;
    /** List of parent classes. */
    public final CxxParentClassList parentClassList;
    /** Class body. */
    public final CxxClassBody body;

    /**
     * Constructor.
     * @param struct Set to `true` if this class is actually a struct.
     * @param name Name of the class.
     * @param parentClassList List of parent classes.
     * @param body Class body.
     */
    public CxxClass(boolean struct, CxxIdentifier name, CxxParentClassList parentClassList, CxxClassBody body)
    {
        this.struct = struct;
        this.name = name;
        this.parentClassList = parentClassList;
        this.body = body;
    }
}
