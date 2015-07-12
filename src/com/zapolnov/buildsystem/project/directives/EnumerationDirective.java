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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** An 'enum' directive in the project file. */
public final class EnumerationDirective extends ProjectDirective
{
    /** Regular expression for valid enumeration identifiers. */
    public static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");
    /** Regular expression for valid enumeration values' identifiers. */
    public static Pattern VALUE_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

    /** Unique identifier of the enumeration. */
    public final String id;
    /** Title of the enumeration. */
    public final String title;
    /** Default value of the enumeration (could be `null`). */
    public final String defaultValue;
    /** Map of enumeration values. */
    private final Map<String, String> values;

    /**
     * Constructor.
     * @param id Unique identifier of the enumeration.
     * @param title Title of the enumeration.
     * @param defaultValue Default value of the enumeration.
     * @param values Map of enumeration values.
     */
    public EnumerationDirective(String id, String title, String defaultValue, Map<String, String> values)
    {
        this.id = id;
        this.title = title;
        this.defaultValue = defaultValue;
        this.values = new LinkedHashMap<>(values);
    }

    /**
     * Retrieves a map of enumeration values.
     * @return A map of enumeration values.
     */
    public Map<String, String> values()
    {
        return Collections.unmodifiableMap(values);
    }

    @Override public void visit(ProjectVisitor visitor)
    {
        visitor.visitEnumeration(this);
    }
}
