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
import com.zapolnov.buildsystem.utility.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A 'source_directories' or '3rdparty_source_directories' directive in the project file. */
public final class SourceDirectoriesDirective extends ProjectDirective
{
    /** List of directories with source files. */
    private final List<File> sourceDirectories;
    /** Cached list of source files. */
    private List<File> sourceFiles;
    /** Set to `true` if this directive is a '3rdparty_source_directories' directive. */
    public final boolean thirdparty;

    /**
     * Constructor.
     * @param sourceDirectories List of directories with source files.
     * @param thirdparty Set to `true` if this directive is a '3rdparty_source_directories' directive.
     */
    public SourceDirectoriesDirective(List<File> sourceDirectories, boolean thirdparty)
    {
        this.sourceDirectories = new ArrayList<>(sourceDirectories);
        this.thirdparty = thirdparty;
    }

    /**
     * Retrieves a list of directories with source files.
     * @return List of directories with source files.
     */
    public List<File> sourceDirectories()
    {
        return Collections.unmodifiableList(sourceDirectories);
    }

    /**
     * Enumerates all source files in all directories provided in this directive.
     * @return List of source files.
     */
    public List<File> sourceFiles()
    {
        if (sourceFiles == null) {
            sourceFiles = new ArrayList<>();
            for (File directory : sourceDirectories())
                sourceFiles.addAll(FileUtils.recursivelyEnumerateFilesInDirectory(directory));
        }
        return Collections.unmodifiableList(sourceFiles);
    }

    @Override public void clearCaches() throws Throwable
    {
        sourceFiles = null;
    }

    @Override public void visit(ProjectVisitor visitor)
    {
        visitor.visitSourceDirectories(this);
    }

    /**
     * Visits this directive as a `SourceFilesDirective`.
     * @param visitor Visitor.
     */
    public void visitFiles(ProjectVisitor visitor)
    {
        visitor.visitSourceFiles(new SourceFilesDirective(sourceFiles(), thirdparty));
    }
}
