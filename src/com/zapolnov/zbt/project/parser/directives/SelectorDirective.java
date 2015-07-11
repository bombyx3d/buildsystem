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
package com.zapolnov.zbt.project.parser.directives;

import com.zapolnov.buildsystem.project.ProjectDirective;
import com.zapolnov.buildsystem.project.ProjectScope;
import com.zapolnov.zbt.project.parser.AbstractProjectDirectiveVisitor;
import java.util.Set;

public final class SelectorDirective extends ProjectDirective
{
    private final String enumerationID;
    private final Set<String> matchingValues;
    private final ProjectScope innerDirectives;

    public SelectorDirective(String enumerationID, Set<String> matchingValues, ProjectScope innerDirectives)
    {
        this.enumerationID = enumerationID;
        this.matchingValues = matchingValues;
        this.innerDirectives = innerDirectives;
    }

    public String enumerationID()
    {
        return enumerationID;
    }

    public Set<String> matchingValues()
    {
        return matchingValues;
    }

    public ProjectScope innerDirectives()
    {
        return innerDirectives;
    }

    /*@Override*/ public void visit(AbstractProjectDirectiveVisitor visitor)
    {
        visitor.visitSelector(this);
    }
}

