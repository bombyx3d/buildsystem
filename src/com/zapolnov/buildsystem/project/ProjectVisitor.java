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

import com.zapolnov.buildsystem.project.directives.DefineDirective;
import com.zapolnov.buildsystem.project.directives.HeaderPathsDirective;
import com.zapolnov.buildsystem.project.directives.ImportDirective;
import com.zapolnov.buildsystem.project.directives.SourceDirectoriesDirective;
import com.zapolnov.buildsystem.project.directives.SourceFilesDirective;
import com.zapolnov.buildsystem.project.directives.TargetNameDirective;
import com.zapolnov.buildsystem.project.directives.TargetPlatformSelectorDirective;

/** Base class for project scope directive enumerations. */
public abstract class ProjectVisitor
{
    @SuppressWarnings("unused") public void visitImport(ImportDirective directive) {}
    @SuppressWarnings("unused") public void visitTargetName(TargetNameDirective directive) {}
    @SuppressWarnings("unused") public void visitDefine(DefineDirective directive) {}
    @SuppressWarnings("unused") public void visitHeaderPaths(HeaderPathsDirective directive) {}
    @SuppressWarnings("unused") public void visitSourceDirectories(SourceDirectoriesDirective directive) {}
    @SuppressWarnings("unused") public void visitSourceFiles(SourceFilesDirective directive) {}

    /**
     * Called for each visited platform-specific section in the project file.
     * @param d Directive.
     * @return `true` if this section should be visited, otherwise `false`.
     */
    @SuppressWarnings("unused") public abstract boolean visitTargetPlatformSelector(TargetPlatformSelectorDirective d);
}
