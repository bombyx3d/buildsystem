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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A list of parent classes in a class declaration. */
public class CxxParentClassList
{
    /** List of parent classes. */
    private final List<CxxParentClass> parentClasses = new ArrayList<>();

    /** Constructs an empty list. */
    public CxxParentClassList()
    {
    }

    /**
     * Constructs a list with a single parent class.
     * @param parent Parent class.
     */
    public CxxParentClassList(CxxParentClass parent)
    {
        parentClasses.add(parent);
    }

    /**
     * Adds parent class to this list.
     * @param parent Parent class.
     */
    public void add(CxxParentClass parent)
    {
        parentClasses.add(parent);
    }

    /**
     * Retrieves a list of parent classes.
     * @return List of parent classes.
     */
    public List<CxxParentClass> classList()
    {
        return Collections.unmodifiableList(parentClasses);
    }
}
