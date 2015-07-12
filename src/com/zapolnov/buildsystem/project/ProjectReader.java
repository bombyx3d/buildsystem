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
import com.zapolnov.buildsystem.project.directives.TargetNameDirective;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.StringUtils;
import com.zapolnov.buildsystem.utility.yaml.YamlError;
import com.zapolnov.buildsystem.utility.yaml.YamlParser;
import com.zapolnov.buildsystem.utility.yaml.YamlValue;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Project file reader. */
public class ProjectReader
{
    /** YAML directive parser. */
    public interface DirectiveParser
    {
        /**
         * Reads directive from YAML.
         * @param reader Project reader.
         * @param directive Directive.
         * @param value Directive value.
         */
        void readDirective(ProjectReader reader, YamlValue directive, YamlValue value);
    }


    /** Project begin read. */
    private final Project project;
    /** Current project scope. */
    private ProjectScope scope;
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
        Project project = new Project(directory);

        ProjectReader reader = new ProjectReader(project);
        reader.read(project.scope);

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
     */
    private void read(ProjectScope scope)
    {
        ProjectScope previousScope = this.scope;
        this.scope = scope;

        try {
            File file = new File(scope.directory, Project.PROJECT_FILE_NAME);

            YamlValue root = YamlParser.readFile(file);
            if (root == null)
                return;

            String moduleName = FileUtils.getCanonicalPath(file);
            if (!moduleImportStack.contains(moduleName)) {
                moduleImportStack.add(moduleName);

                try {
                    readDirectives(root.toMapping());
                } catch (YamlError e) {
                    throw e;
                } catch (Throwable t) {
                    String fileName = FileUtils.getCanonicalPath(file);
                    String message = StringUtils.getShortExceptionMessage(t);
                    throw new RuntimeException(
                        String.format("Unable to parse YAML file \"%s\".\nError: %s", fileName, message), t);
                } finally {
                    moduleImportStack.remove(moduleName);
                }
            }
        } finally {
            this.scope = previousScope;
        }
    }

    /**
     * Retrieves current project scope.
     * @return Current project scope.
     */
    public ProjectScope currentScope()
    {
        return scope;
    }

    /**
     * Reads directives from YAML.
     * @param directives List of directives.
     */
    public void readDirectives(Map<YamlValue, YamlValue> directives)
    {
        Map<String, DirectiveParser> standardDirectives = standardDirectives();

        for (Map.Entry<YamlValue, YamlValue> directive : directives.entrySet()) {
            String key = directive.getKey().toString();

            DirectiveParser parser = standardDirectives.get(key);
            if (parser != null) {
                parser.readDirective(this, directive.getKey(), directive.getValue());
                continue;
            }

            throw new YamlError(directive.getKey(), String.format("Unknown directive \"%s\".", key));
        }
    }


    private static Map<String, DirectiveParser> standardDirectives;
    private static final Pattern RE_TARGET_NAME = Pattern.compile(String.format("^%s$", TargetNameDirective.PATTERN));

    /**
     * Parses directives 'header_search_paths' and '3rdparty_header_search_paths".
     * @param r Instance of the reader.
     * @param k Directive.
     * @param v Value of the directive.
     * @param thirdparty Set to `true` if directive is '3rdparty_header_search_paths'.
     */
    public static void parseHeaderSearchPaths(ProjectReader r, YamlValue k, YamlValue v, boolean thirdparty)
    {
        List<File> directories = new ArrayList<>();
        for (YamlValue path : v.toSequence()) {
            File file = new File(r.currentScope().directory, path.toString());
            directories.add(FileUtils.getCanonicalFile(file));
        }
        r.currentScope().addDirective(new HeaderPathsDirective(directories, thirdparty));
    }

    /**
     * Parses directives 'source_directories' and '3rdparty_source_directories".
     * @param r Instance of the reader.
     * @param k Directive.
     * @param v Value of the directive.
     * @param thirdparty Set to `true` if directive is '3rdparty_source_directories'.
     */
    public static void parseSourceDirectories(ProjectReader r, YamlValue k, YamlValue v, boolean thirdparty)
    {
        List<File> directories = new ArrayList<>();
        for (YamlValue path : v.toSequence()) {
            File file = new File(r.currentScope().directory, path.toString());
            if (!file.exists() || !file.isDirectory()) {
                String fileName = FileUtils.getCanonicalPath(file);
                throw new YamlError(path,
                    String.format("Directory \"%s\" does not exist or is not a directory.", fileName));
            }
            directories.add(FileUtils.getCanonicalFile(file));
        }
        r.currentScope().addDirective(new SourceDirectoriesDirective(directories, thirdparty));
    }

    /**
     * Retrieves a map of standard directives.
     * @return Map of standard directives.
     */
    public static Map<String, DirectiveParser> standardDirectives()
    {
        if (standardDirectives == null) {
            Map<String, DirectiveParser> d = new HashMap<>();

            d.put("import", (r, k, v) -> {
                for (YamlValue module : v.toSequence()) {
                    String moduleName = module.toString();

                    File moduleDirectory = new File(r.currentScope().directory, moduleName);
                    if (!isValidProjectDirectory(moduleDirectory)) {
                        throw new YamlError(module,
                            String.format("Directory \"%s\" does not contain a project file.", moduleName));
                    }

                    try {
                        ProjectScope scope = new ProjectScope(moduleDirectory, r.currentScope(), true);
                        r.read(scope);
                        r.currentScope().addDirective(new ImportDirective(scope));
                    } catch (Throwable t) {
                        throw new YamlError(module, String.format("Unable to import module \"%s\".", moduleName), t);
                    }
                }
            });

            d.put("target-name", (r, k, v) -> {
                String name = v.toString();
                if (!RE_TARGET_NAME.matcher(name).matches())
                    throw new YamlError(v, "Invalid target name.");
                r.currentScope().addDirective(new TargetNameDirective(name));
            });

            d.put("header_search_paths", (r, k, v) -> parseHeaderSearchPaths(r, k, v, false));
            d.put("3rdparty_header_search_paths", (r, k, v) -> parseHeaderSearchPaths(r, k, v, true));

            d.put("source_directories", (r, k, v) -> parseSourceDirectories(r, k, v, false));
            d.put("3rdparty_source_directories", (r, k, v) -> parseSourceDirectories(r, k, v, true));

            d.put("define", (r, k, v) -> {
                List<String> defines = new ArrayList<>();
                for (YamlValue define : v.toSequence())
                    defines.add(define.toString());
                r.currentScope().addDirective(new DefineDirective(defines));
            });

            standardDirectives = d;
        }
        return standardDirectives;
    }
}
