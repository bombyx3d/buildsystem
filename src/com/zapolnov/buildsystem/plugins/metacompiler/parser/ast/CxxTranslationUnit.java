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
import java.io.File;
import java.io.Serializable;

/** AST node for a translation unit. */
public class CxxTranslationUnit implements Serializable
{
    /** Path to the source file. */
    public final File file;
    /** Global scope of the translation unit. */
    public final CxxScope globalScope;

    /**
     * Constructor.
     * @param file Path to the source file.
     */
    public CxxTranslationUnit(File file)
    {
        this.file = file;
        this.globalScope = new CxxScope(this);
    }

    /**
     * Visits this translation unit with the specified visitor.
     * @param visitor Visitor.
     */
    public void visit(CxxAstVisitor visitor)
    {
        globalScope.visit(visitor);
    }
}
