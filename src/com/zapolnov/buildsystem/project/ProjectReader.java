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

import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.StringUtils;
import com.zapolnov.buildsystem.utility.yaml.YamlError;
import com.zapolnov.buildsystem.utility.yaml.YamlParser;
import com.zapolnov.buildsystem.utility.yaml.YamlValue;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/** Project file reader. */
public class ProjectReader
{
    /** Project begin read. */
    private final Project project;
    /** Current stack of module imports. */
    private final Set<String> moduleImportStack = new LinkedHashSet<>();


    /**
     * Checks whether the specified directory is a valid project directory.
     * @param directory Path to the directory.
     * @return `true` if given directory contains project file, or `false` if it is not.
     */
    public static boolean isValidProjectDirectory(File directory)
    {
        File file = new File(directory, Project.PROJECT_FILE_NAME);
        return file.exists() && !file.isDirectory();
    }

    /**
     * Reads project at the specified directory.
     * @param directory Path to the project directory.
     */
    public static Project read(File directory)
    {
        Project project = new Project(new File(directory, Project.PROJECT_FILE_NAME));

        ProjectReader reader = new ProjectReader(project);
        reader.read(project.scope, project.file);

        return project;
    }


    /**
     * Constructor.
     * @param project Project.
     */
    private ProjectReader(Project project)
    {
        this.project = project;
    }

    /**
     * Reads the project file.
     * @param scope Project scope.
     * @param file Project file.
     */
    private void read(ProjectScope scope, File file)
    {
        YamlValue root = YamlParser.readFile(file);
        if (root == null)
            return;

        String moduleName = FileUtils.getCanonicalPath(file);
        if (!moduleImportStack.contains(moduleName)) {
            moduleImportStack.add(moduleName);
            try {
                if (!root.isMapping())
                    throw new YamlError(root, "Expected mapping at the root level.");
                //processOptions(file.getAbsoluteFile().getParentFile(), scope, root.toMapping());
            } catch (YamlError e) {
                throw e;
            } catch (Throwable t) {
                String fileName = FileUtils.getCanonicalPath(file);
                String msg = StringUtils.getShortExceptionMessage(t);
                throw new RuntimeException(String.format("Unable to parse YAML file \"%s\".\nError: %s", fileName, msg), t);
            } finally {
                moduleImportStack.remove(moduleName);
            }
        }
    }
}
