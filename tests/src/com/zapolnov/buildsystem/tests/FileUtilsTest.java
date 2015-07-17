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
package com.zapolnov.buildsystem.tests;

import com.zapolnov.buildsystem.utility.FileUtils;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest extends Assert
{
    @Test public void testIsCxxSourceFile()
    {
        assertFalse(FileUtils.isCSourceFile(new File("test")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test")));
        assertFalse(FileUtils.isHeaderFile(new File("test")));

        assertTrue(FileUtils.isCSourceFile(new File("test.c")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.c")));
        assertFalse(FileUtils.isHeaderFile(new File("test.c")));

        assertFalse(FileUtils.isCSourceFile(new File("test.cc")));
        assertTrue(FileUtils.isCxxSourceFile(new File("test.cc")));
        assertFalse(FileUtils.isHeaderFile(new File("test.cc")));

        assertFalse(FileUtils.isCSourceFile(new File("test.cpp")));
        assertTrue(FileUtils.isCxxSourceFile(new File("test.cpp")));
        assertFalse(FileUtils.isHeaderFile(new File("test.cpp")));

        assertFalse(FileUtils.isCSourceFile(new File("test.cxx")));
        assertTrue(FileUtils.isCxxSourceFile(new File("test.cxx")));
        assertFalse(FileUtils.isHeaderFile(new File("test.cxx")));

        assertFalse(FileUtils.isCSourceFile(new File("test.h")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.h")));
        assertTrue(FileUtils.isHeaderFile(new File("test.h")));

        assertFalse(FileUtils.isCSourceFile(new File("test.hh")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.hh")));
        assertTrue(FileUtils.isHeaderFile(new File("test.hh")));

        assertFalse(FileUtils.isCSourceFile(new File("test.hpp")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.hpp")));
        assertTrue(FileUtils.isHeaderFile(new File("test.hpp")));

        assertFalse(FileUtils.isCSourceFile(new File("test.hxx")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.hxx")));
        assertTrue(FileUtils.isHeaderFile(new File("test.hxx")));

        assertFalse(FileUtils.isCSourceFile(new File("test.inl")));
        assertFalse(FileUtils.isCxxSourceFile(new File("test.inl")));
        assertTrue(FileUtils.isHeaderFile(new File("test.inl")));
    }
}
