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

/** An AST node for a class. */
public class CxxClass extends CxxSymbol implements Serializable
{
    /** Translation unit containing this class. */
    public final CxxTranslationUnit translationUnit;
    /** A scope for this class. */
    public final CxxScope scope;
    /** List of parent classes. */
    private final List<CxxParentClass> parentClasses;
    /** Type of this class. */
    public CxxClassType type = CxxClassType.DEFAULT;
    /** Set to `true` if this class is a template specialization. */
    public boolean isTemplateSpecialization;

    /**
     * Constructor.
     * @param translationUnit Translation unit containing this class.
     * @param name Name of the class.
     * @param parentClasses List of parent classes.
     * @param isTemplateSpecialization Set to `true` if this class is a template specialization.
     */
    public CxxClass(CxxTranslationUnit translationUnit, CxxFullyQualifiedName name,
        List<CxxParentClass> parentClasses, boolean isTemplateSpecialization)
    {
        super(name);
        this.translationUnit = translationUnit;
        this.scope = new CxxScope(translationUnit);
        this.parentClasses = new ArrayList<>(parentClasses);
        this.isTemplateSpecialization = isTemplateSpecialization;
    }

    /**
     * Retrieves a list of parent classes of this class.
     * @return List of parent classes.
     */
    public List<CxxParentClass> parentClasses()
    {
        return Collections.unmodifiableList(parentClasses);
    }

    /**
     * Visits this class with the specified visitor.
     * @param visitor Visitor.
     */
    @Override public void visit(final CxxAstVisitor visitor)
    {
        visitor.enterClass(this);
        scope.visit(visitor);
        visitor.leaveClass(this);
    }
}
