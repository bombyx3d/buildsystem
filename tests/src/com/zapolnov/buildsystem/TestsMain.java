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
package com.zapolnov.buildsystem;

import com.zapolnov.buildsystem.tests.AbstractPluginTest;
import com.zapolnov.buildsystem.tests.ColorsTest;
import com.zapolnov.buildsystem.tests.CxxParserTest;
import com.zapolnov.buildsystem.tests.DatabaseTests;
import com.zapolnov.buildsystem.tests.FileBuilderTests;
import com.zapolnov.buildsystem.tests.FileUtilsTest;
import com.zapolnov.buildsystem.tests.GeneratorTest;
import com.zapolnov.buildsystem.tests.LogTests;
import com.zapolnov.buildsystem.tests.StringUtilsTest;
import com.zapolnov.buildsystem.tests.SystemUtilsTest;
import com.zapolnov.buildsystem.tests.TemplateTests;
import com.zapolnov.buildsystem.tests.YamlParserTests;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/** Entry point for tests. */
public final class TestsMain
{
    /**
     * Application entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        Result result = junit.run(
            LogTests.class,
            YamlParserTests.class,
            DatabaseTests.class,
            FileBuilderTests.class,
            TemplateTests.class,
            StringUtilsTest.class,
            AbstractPluginTest.class,
            ColorsTest.class,
            SystemUtilsTest.class,
            FileUtilsTest.class,
            GeneratorTest.class,
            CxxParserTest.class
        );

        System.exit(!result.wasSuccessful() ? 1 : 0);
    }
}
