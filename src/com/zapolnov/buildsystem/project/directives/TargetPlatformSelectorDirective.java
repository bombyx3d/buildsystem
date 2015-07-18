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
package com.zapolnov.buildsystem.project.directives;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.build.TargetPlatform;
import com.zapolnov.buildsystem.project.ProjectDirective;
import com.zapolnov.buildsystem.project.ProjectScope;
import com.zapolnov.buildsystem.project.ProjectVisitor;

/** A platform-specific section in the project file. */
public final class TargetPlatformSelectorDirective extends ProjectDirective
{
    /** Target platform. */
    public final TargetPlatform targetPlatform;
    /** Scope. */
    public final ProjectScope scope;

    /**
     * Constructor.
     * @param targetPlatform Target platform.
     * @param scope Scope.
     */
    public TargetPlatformSelectorDirective(TargetPlatform targetPlatform, ProjectScope scope)
    {
        this.targetPlatform = targetPlatform;
        this.scope = scope;
    }

    @Override public void preBuild(ProjectBuilder projectBuilder) throws Throwable
    {
        scope.preBuild(projectBuilder);
    }

    @Override public void build(ProjectBuilder projectBuilder) throws Throwable
    {
        scope.build(projectBuilder);
    }

    @Override public void visit(ProjectVisitor visitor)
    {
        if (visitor.visitTargetPlatformSelector(this))
            scope.visit(visitor);
    }
}
