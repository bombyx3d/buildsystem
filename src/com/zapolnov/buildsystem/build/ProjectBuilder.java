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
package com.zapolnov.buildsystem.build;

import com.zapolnov.buildsystem.project.Project;
import com.zapolnov.buildsystem.utility.Database;
import com.zapolnov.buildsystem.utility.FileUtils;
import java.io.File;

/** Project builder. */
public class ProjectBuilder
{
    public static final String BUILD_DIRECTORY_NAME = ".build";

    /** A project being built. */
    public final Project project;
    /** Path to the output directory for generated files. */
    public final File outputDirectory;
    /** Build database. */
    public final Database database;

    /**
     * Constructor.
     * @param project Project to build.
     */
    public ProjectBuilder(Project project)
    {
        this.project = project;
        this.outputDirectory = new File(project.directory, BUILD_DIRECTORY_NAME);
        this.database = new Database(outputDirectory);

        FileUtils.ensureDirectoryExists(outputDirectory);
        FileUtils.makeDirectoryHidden(outputDirectory);
    }
}
