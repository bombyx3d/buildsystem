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

/** Body of a namespace. */
public class CxxNamespaceBody implements Serializable
{
    /** List of inner namespaces. */
    private final List<CxxNamespace> namespaces = new ArrayList<>();
    /** List of classes. */
    private final List<CxxClass> classes = new ArrayList<>();

    /** Constructor. */
    public CxxNamespaceBody()
    {
    }

    /**
     * Adds an inner namespace to this namespace.
     * @param member Namespace to add.
     */
    public void addNamespace(CxxNamespace member)
    {
        namespaces.add(member);
    }

    /**
     * Adds a class to this namespace.
     * @param member Class to add.
     */
    public void addClass(CxxClass member)
    {
        classes.add(member);
    }

    /**
     * Retrieves a list of inner namespaces in this namespace.
     * @return List of inner namespaces in this namespace.
     */
    public List<CxxNamespace> namespaces()
    {
        return Collections.unmodifiableList(namespaces);
    }

    /**
     * Retrieves a list of classes in this namespace.
     * @return List of classes in this namespace.
     */
    public List<CxxClass> classes()
    {
        return Collections.unmodifiableList(classes);
    }

    /**
     * Visits this namespace body with the specified visitor.
     * @param visitor Visitor.
     */
    public void visit(final CxxAstVisitor visitor)
    {
        namespaces.forEach(namespace -> {
            visitor.enterNamespace(namespace);
            namespace.visit(visitor);
            visitor.leaveNamespace(namespace);
        });

        classes.forEach(cxxClass -> {
            visitor.enterClass(cxxClass);
            cxxClass.visit(visitor);
            visitor.leaveClass(cxxClass);
        });
    }
}
