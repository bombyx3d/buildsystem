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

import com.zapolnov.buildsystem.utility.StringUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest extends Assert
{
    @Test public void testExceptionMessage() throws IOException
    {
        String message = StringUtils.getShortExceptionMessage(new NullPointerException());
        assertEquals(message, "java.lang.NullPointerException");

        message = StringUtils.getShortExceptionMessage(new FileNotFoundException("test.file"));
        assertEquals(message, "File not found: test.file");

        message = StringUtils.getShortExceptionMessage(new ClassNotFoundException("TestClass"));
        assertEquals(message, "Class not found: TestClass");

        message = StringUtils.getShortExceptionMessage(new RuntimeException("Test message"));
        assertEquals(message, "Test message");
    }
}
