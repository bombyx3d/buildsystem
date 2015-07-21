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

import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxAstVisitor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Body of a class. */
public class CxxClassBody implements Serializable
{
    /** List of inner classes. */
    private final List<CxxClass> innerClasses = new ArrayList<>();

    /** Constructor. */
    public CxxClassBody()
    {
    }

    /**
     * Adds an inner class to this class.
     * @param member Class to add.
     */
    public void addInnerClass(CxxClass member)
    {
        innerClasses.add(member);
    }

    /**
     * Retrieves a list of inner classes in this class.
     * @return List of inner classes in this class.
     */
    public List<CxxClass> innerClasses()
    {
        return Collections.unmodifiableList(innerClasses);
    }

    /**
     * Visits this class body with the specified visitor.
     * @param visitor Visitor.
     */
    public void visit(final CxxAstVisitor visitor)
    {
        innerClasses.forEach(innerClass -> {
            visitor.enterClass(innerClass);
            innerClass.visit(visitor);
            visitor.leaveClass(innerClass);
        });
    }
}
