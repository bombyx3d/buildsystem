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
package com.zapolnov.buildsystem.build.generators;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.utility.Database;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

/** Base class for project file generators. */
public abstract class Generator
{
    /** Retrieves a name of this generator. */
    public abstract String name();

    /**
     * Creates an UI panel with generator configuration options.
     * @param database Database.
     * @return UI panel with generator configuration options.
     */
    public JPanel createSettingsPanel(Database database)
    {
        return null;
    }

    /**
     * Generates the project.
     * @param projectBuilder Project builder.
     * @param build Set to `true` to also build the project after generating project files.
     */
    public abstract void generate(ProjectBuilder projectBuilder, boolean build) throws Throwable;


    /**
     * Retrieves a map of all supported generators.
     * @return Map of all generators.
     */
    public static Map<String, Generator> allGenerators()
    {
        if (allGenerators == null) {
            Map<String, Generator> g = new LinkedHashMap<>();
            /*
            g.put(DummyGenerator.NAME, new DummyGenerator());
            g.put(CMakeGenerator.NAME, new CMakeGenerator());
            */
            allGenerators = g;
        }
        return allGenerators;
    }

    /** A map of all supported generators. */
    private static Map<String, Generator> allGenerators;
}
