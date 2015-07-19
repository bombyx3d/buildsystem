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
package com.zapolnov.buildsystem.build.qt5;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.build.Generator;
import com.zapolnov.buildsystem.build.TargetPlatform;
import com.zapolnov.buildsystem.build.cmake.CMakeGeneratorUtilities;

/** Qt5 project generator. */
public class Qt5CMakeGenerator extends Generator
{
    @Override public TargetPlatform targetPlatform()
    {
        return TargetPlatform.QT5;
    }

    @Override public String outputDirectoryName()
    {
        return "qt5-cmake";
    }

    @Override public void generate(ProjectBuilder projectBuilder) throws Throwable
    {
        CMakeGeneratorUtilities.writeCMakeLists(projectBuilder);
        CMakeGeneratorUtilities.generateCLionProject(projectBuilder);
    }
}
