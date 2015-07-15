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

import com.zapolnov.buildsystem.plugins.Plugin;
import com.zapolnov.buildsystem.utility.FileUtils;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** An in-memory representation of the project. */
public class Project
{
    /** Name of a project file. */
    public static final String PROJECT_FILE_NAME = "project.yml";

    /** Project directory. */
    public final File directory;
    /** Root scope of the project. */
    public final ProjectScope scope;

    /** List of plugins loaded by the project. */
    private final Map<Class<Plugin>, Plugin> plugins = new HashMap<>();

    /**
     * Constructor.
     * @param directory Project directory.
     */
    public Project(File directory)
    {
        this.directory = FileUtils.getCanonicalFile(directory);
        this.scope = new ProjectScope(this.directory, null, false);
    }

    /**
     * Retrieves a collection of plugins loaded by the project.
     * @return A collection of plugins.
     */
    public Collection<Plugin> plugins()
    {
        return plugins.values();
    }

    /**
     * Adds the specified plugin to the project.
     * @param pluginClass Plugin to add.
     */
    public void addPlugin(Class<Plugin> pluginClass) throws InstantiationException, IllegalAccessException
    {
        if (plugins.get(pluginClass) == null)
            plugins.put(pluginClass, pluginClass.newInstance());
    }
}
