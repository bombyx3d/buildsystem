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
package com.zapolnov.buildsystem.project;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** An individual namespace of a project. */
public class ProjectScope
{
    /** Parent scope or `null`. */
    public final ProjectScope parent;
    /** Base directory of this scope. */
    public final File directory;
    /** Set to `true` if this scope should share symbols with a parent scope. */
    public final boolean transparent;

    /** List of directives in this scope. */
    private final List<ProjectDirective> directives = new ArrayList<>();

    /**
     * Constructor.
     * @param directory Base directory of this scope.
     * @param parent Parent scope.
     * @param transparent Set to `true` if this scope should share symbols with a parent scope.
     */
    public ProjectScope(File directory, ProjectScope parent, boolean transparent)
    {
        this.directory = directory;
        this.parent = parent;
        this.transparent = transparent;
    }

    /**
     * Adds directive to this scope.
     * @param directive Directive to add.
     */
    public void addDirective(ProjectDirective directive)
    {
        directives.add(directive);
    }

    /** Clears cached values. */
    public void clearCaches() throws Throwable
    {
        for (ProjectDirective directive : directives)
            directive.clearCaches();
    }

    /**
     * Performs pre-build actions implemented by directives in this scope.
     * @param projectBuilder Project builder.
     */
    public void preBuild(ProjectBuilder projectBuilder) throws Throwable
    {
        for (ProjectDirective directive : directives)
            directive.preBuild(projectBuilder);
    }

    /**
     * Performs build actions implemented by directives in this scope.
     * @param projectBuilder Project builder.
     */
    public void build(ProjectBuilder projectBuilder) throws Throwable
    {
        for (ProjectDirective directive : directives)
            directive.build(projectBuilder);
    }

    /**
     * Visits this scope with the specified visitor.
     * @param visitor Visitor.
     */
    public void visit(ProjectVisitor visitor)
    {
        for (ProjectDirective directive : directives)
            directive.visit(visitor);
    }
}
