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

import com.zapolnov.buildsystem.project.ProjectDirective;
import com.zapolnov.buildsystem.project.ProjectVisitor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A 'source_files' or '3rdparty_source_files' directive in the project file. */
public final class SourceFilesDirective extends ProjectDirective
{
    /** List of source files. */
    private final List<File> sourceFiles;
    /** Set to `true` if this directive is a '3rdparty_source_files' directive. */
    public final boolean thirdparty;

    /**
     * Constructor.
     * @param sourceFiles List of source files.
     * @param thirdparty Set to `true` if this directive is a '3rdparty_source_files' directive.
     */
    public SourceFilesDirective(List<File> sourceFiles, boolean thirdparty)
    {
        this.sourceFiles = new ArrayList<>(sourceFiles);
        this.thirdparty = thirdparty;
    }

    /**
     * Retrieves a list of source files.
     * @return List of source files.
     */
    public List<File> sourceFiles()
    {
        return Collections.unmodifiableList(sourceFiles);
    }

    @Override public void visit(ProjectVisitor visitor)
    {
        visitor.visitSourceFiles(this);
    }
}
