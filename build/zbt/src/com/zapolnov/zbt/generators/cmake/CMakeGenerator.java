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
package com.zapolnov.zbt.generators.cmake;

import com.zapolnov.zbt.generators.Generator;
import com.zapolnov.zbt.project.Project;
import com.zapolnov.zbt.project.parser.ProjectDirectiveVisitor;
import java.io.File;

public class CMakeGenerator extends Generator
{
    public static final String NAME = "CMake 3.2+";

    @Override public String name()
    {
        return NAME;
    }

    @Override public void generate(final Project project)
    {
        ProjectDirectiveVisitor visitor = new ProjectDirectiveVisitor(project) {
            @Override protected void visitDefine(String name, String value) {
                //System.out.println(String.format("%s=%s", name, value));
            }
            @Override protected void visitSourceFile(File file) {
                //System.out.println(String.format("%s", Utility.getCanonicalPath(file)));
            }
        };
        project.directives().visitDirectives(visitor);
    }
}
