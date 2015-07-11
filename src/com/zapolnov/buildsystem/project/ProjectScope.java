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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** An individual namespace of a project. */
public class ProjectScope
{
    /** Parent scope or `null`. */
    public final ProjectScope parent;

    /** Set to `true` if this scope should share symbols with a parent scope. */
    private final boolean transparent;
    /** List of directives in this scope. */
    private final List<ProjectDirective> directives = new ArrayList<>();
    /** Set of enumeration identifiers used by directives in this scope. */
    private final Set<String> enumerationIDs = new HashSet<>();

    /**
     * Constructor.
     * @param parent Parent scope.
     * @param transparent Set to `true` if this scope should share symbols with a parent scope.
     */
    public ProjectScope(ProjectScope parent, boolean transparent)
    {
        this.parent = parent;
        this.transparent = transparent;
    }

    /**
     * Checks whether the specified enumeration identifier is unused and reserves it if it does.
     * @return `true` if identifier has been successfully reserved or `false` if it has already been reserved.
     */
    public boolean reserveEnumerationID(String id)
    {
        for (ProjectScope scope = this; scope != null; scope = scope.parent) {
            if (scope.enumerationIDs.contains(id))
                return false;
        }

        for (ProjectScope scope = this; scope != null; scope = scope.parent) {
            scope.enumerationIDs.add(id);
            if (!scope.transparent)
                break;
        }

        return true;
    }

    /**
     * Adds directive to this scope.
     * @param directive Directive to add.
     */
    public void addDirective(ProjectDirective directive)
    {
        directives.add(directive);
    }
}
