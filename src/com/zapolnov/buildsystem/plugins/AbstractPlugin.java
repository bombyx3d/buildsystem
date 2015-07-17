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
package com.zapolnov.buildsystem.plugins;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.project.ProjectReader;
import java.util.HashMap;
import java.util.Map;

/** Abstract base class for plugins. */
public abstract class AbstractPlugin
{
    /**
     * Invoked before project build.
     * @param projectBuilder Project builder.
     */
    @SuppressWarnings("unused") public void preBuild(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    /**
     * Invoked after project has been built but before IDE files has been generated.
     * @param projectBuilder Project builder.
     */
    @SuppressWarnings("unused") public void preGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    /**
     * Invoked after IDE files has been generated.
     * @param projectBuilder Project builder.
     */
    @SuppressWarnings("unused") public void postGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    /**
     * Retrieves a map of custom directives supported by this plugin.
     * @return Map of directives.
     */
    public Map<String, ProjectReader.DirectiveParser> customDirectives()
    {
        return new HashMap<>();
    }
}