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

import com.zapolnov.buildsystem.build.generators.Generator;
import com.zapolnov.buildsystem.plugins.Plugin;
import com.zapolnov.buildsystem.project.Project;
import com.zapolnov.buildsystem.utility.Database;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.Log;
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
    /** Project generator. */
    private Generator generator;

    /**
     * Constructor.
     * @param project Project to build.
     */
    public ProjectBuilder(Project project)
    {
        this.outputDirectory = new File(project.directory, BUILD_DIRECTORY_NAME);
        FileUtils.ensureDirectoryExists(outputDirectory);
        FileUtils.makeDirectoryHidden(outputDirectory);

        this.project = project;
        this.database = new Database(outputDirectory);
    }

    /**
     * Retrieves a generator used to build the project.
     * @return Generator.
     */
    public Generator generator()
    {
        return generator;
    }

    /**
     * Sets generator to use to build the project.
     * @param generator Generator.
     */
    public void setGenerator(Generator generator)
    {
        this.generator = generator;
    }

    /** Runs the project builder. */
    public void run() throws Throwable
    {
        try {
            if (generator == null)
                throw new RuntimeException("No generator has been set.");

            Log.debug("=== Pre-build phase");
            for (Plugin plugin : project.plugins())
                plugin.preBuild(this);

            Log.debug("=== Building the project");
            project.scope.build(this);

            Log.debug("=== Pre-generate phase");
            for (Plugin plugin : project.plugins())
                plugin.preGenerate(this);

            Log.debug("=== Generating IDE files");
            generator.generate(this);

            Log.debug("=== Post-generate phase");
            for (Plugin plugin : project.plugins())
                plugin.postGenerate(this);

            database.commit();
        } catch (Throwable t) {
            database.rollbackSafe();
            throw t;
        }
    }
}
