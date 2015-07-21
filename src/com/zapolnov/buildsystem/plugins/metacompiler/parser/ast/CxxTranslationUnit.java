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

/** A translation unit. */
public class CxxTranslationUnit implements Serializable
{
    /** List of namespaces in the translation unit. */
    private final List<CxxNamespace> namespaces = new ArrayList<>();
    /** List of classes in the translation unit. */
    private final List<CxxClass> classes = new ArrayList<>();

    /** Constructor. */
    public CxxTranslationUnit()
    {
    }

    /**
     * Adds a namespace to the translation unit.
     * @param member Namespace to add.
     */
    public void addNamespace(CxxNamespace member)
    {
        namespaces.add(member);
    }

    /**
     * Adds a class to the translation unit.
     * @param member Class to add.
     */
    public void addClass(CxxClass member)
    {
        classes.add(member);
    }

    /**
     * Retrieves a list of namespaces in the translation unit.
     * @return List of namespaces in the translation unit.
     */
    public List<CxxNamespace> namespaces()
    {
        return Collections.unmodifiableList(namespaces);
    }

    /**
     * Retrieves a list of classes in the translation unit.
     * @return List of classes in the translation unit.
     */
    public List<CxxClass> classes()
    {
        return Collections.unmodifiableList(classes);
    }

    /**
     * Visits this translation unit with the specified visitor.
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
