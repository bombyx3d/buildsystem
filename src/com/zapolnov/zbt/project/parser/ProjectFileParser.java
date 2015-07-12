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
package com.zapolnov.zbt.project.parser;

public final class ProjectFileParser
{
    /*
    private static final Pattern ENUM_REG_EXP = Pattern.compile(String.format("^\\^(%s)\\((%s(,%s)*)\\)$",
        EnumerationDirective.NAME_PATTERN, EnumerationDirective.VALUE_PATTERN, EnumerationDirective.VALUE_PATTERN));
    private static final Pattern EXE_NAME_REG_EXP = Pattern.compile(String.format("^%s$",
        TargetNameDirective.PATTERN));

    private final Set<Plugin> plugins = new LinkedHashSet<>();

    public ProjectFileParser(Project project)
    {
        this.project = project;
    }

    private void processOptions(File basePath, ProjectScope directiveList,
        Map<YamlValue, YamlValue> options)
    {
        for (Map.Entry<YamlValue, YamlValue> item : options.entrySet()) {
            YamlValue keyOption = item.getKey();
            YamlValue valueOption = item.getValue();

            String key = keyOption.toString();
            if (key == null)
                throw new YamlError(keyOption, "Mapping key should be a string.");

            ProjectDirective directive = null;

            char firstChar = (key.length() > 0 ? key.charAt(0) : 0);
            switch (firstChar)
            {
            case '^': directive = processSelector(basePath, directiveList, key, keyOption, valueOption); break;

            default:
                switch (key)
                {
                case "+generator": directive = processGeneratorSelector(basePath, directiveList, valueOption); break;
                case "+if(root_project)": directive = processRootProjectSelector(basePath, directiveList, valueOption); break;
                case "enum": directive = processEnum(directiveList, keyOption, valueOption); break;
                case "import": directive = processImport(basePath, directiveList, valueOption); break;
                case "define": directive = processDefine(valueOption); break;
                case "source_directories": directive = processSourceDirectories(basePath, valueOption); break;
                case "3rdparty_source_directories": directive = processThirdPartySourceDirectories(basePath, valueOption); break;
                case "header_search_paths": directive = processHeaderSearchPaths(basePath, valueOption); break;
                case "3rdparty_header_search_paths": directive = processThirdPartyHeaderSearchPaths(basePath, valueOption); break;
                case "target_name": directive = processTargetName(valueOption); break;
                case "cmake-use-opengl": directive = processCMakeUseOpenGL(valueOption); break;
                case "cmake-use-qt5": directive = processCMakeUseQt5(valueOption); break;
                case "plugin": processPlugin(valueOption); break;
                default:
                    for (Plugin plugin : plugins) {
                        CustomDirective pluginDirective =
                            plugin.processDirective(project, basePath, key, keyOption, valueOption);
                        if (pluginDirective != null) {
                            directive = new CustomDirectiveWrapper(pluginDirective);
                            break;
                        }
                    }
                    if (directive == null)
                        throw new YamlError(keyOption, String.format("Unknown option \"%s\".", key));
                }
            }

            if (directive != null)
                directiveList.addDirective(directive);
        }
    }

    private ProjectDirective processSelector(File basePath, ProjectScope directiveList,
        String key, YamlValue keyOption, YamlValue valueOption)
    {
        Matcher matcher = ENUM_REG_EXP.matcher(key);
        if (!matcher.matches() || matcher.groupCount() != 3)
            throw new YamlError(keyOption, "Invalid selector.");

        if (!valueOption.isMapping())
            throw new YamlError(valueOption, "Expected mapping.");

        String enumID = matcher.group(1);

        Set<String> matchingValues = new LinkedHashSet<>();
        Collections.addAll(matchingValues, matcher.group(2).split(","));

        ProjectScope innerDirectives = new ProjectScope(directiveList, false);
        processOptions(basePath, innerDirectives, valueOption.toMapping());

        return new SelectorDirective(enumID, matchingValues, innerDirectives);
    }

    private ProjectDirective processGeneratorSelector(File basePath, ProjectScope directiveList,
        YamlValue valueOption)
    {
        if (!valueOption.isMapping())
            throw new YamlError(valueOption, "Expected mapping.");

        Map<String, ProjectScope> mapping = new HashMap<>();
        for (Map.Entry<YamlValue, YamlValue> item : valueOption.toMapping().entrySet()) {
            String key = item.getKey().toString();
            if (key == null)
                throw new RuntimeException("Expected string.");

            if (!key.startsWith("+"))
                throw new RuntimeException("Keys in generator selector should begin with '+'.");

            String name = key.substring(1);
            if (!item.getValue().isMapping())
                throw new YamlError(item.getValue(), "Expected mapping.");

            ProjectScope innerDirectives = new ProjectScope(directiveList, false);
            processOptions(basePath, innerDirectives, item.getValue().toMapping());

            if (mapping.containsKey(name))
                throw new YamlError(item.getKey(), String.format("Duplicate key \"%s\".", name));
            mapping.put(name, innerDirectives);
        }

        return new GeneratorSelectorDirective(mapping);
    }

    private ProjectDirective processRootProjectSelector(File basePath, ProjectScope directiveList,
        YamlValue valueOption)
    {
        if (!valueOption.isMapping())
            throw new YamlError(valueOption, "Expected mapping.");

        ProjectScope innerDirectives = new ProjectScope(directiveList, false);
        processOptions(basePath, innerDirectives, valueOption.toMapping());

        return new RootProjectSelectorDirective(innerDirectives, moduleImportStack.size() == 1);
    }

    private ProjectDirective processCMakeUseOpenGL(YamlValue valueOption)
    {
        String name = valueOption.toString();

        boolean value;
        if ("true".equals(name))
            value = true;
        else if ("false".equals(name))
            value = false;
        else
            throw new YamlError(valueOption, "Expected 'true' or 'false'.");

        return new CMakeUseOpenGLDirective(value);
    }

    private ProjectDirective processCMakeUseQt5(YamlValue valueOption)
    {
        String name = valueOption.toString();

        boolean value;
        if ("true".equals(name))
            value = true;
        else if ("false".equals(name))
            value = false;
        else
            throw new YamlError(valueOption, "Expected 'true' or 'false'.");

        return new CMakeUseQt5Directive(value);
    }

    private void processPlugin(YamlValue valueOption)
    {
        String className = valueOption.toString();
        if (className == null)
            throw new YamlError(valueOption, "Expected string.");

        try {
            Plugin plugin = project.loadPlugin(className);
            plugins.add(plugin);
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
            throw new YamlError(valueOption, e);
        }
    }
    */
}
