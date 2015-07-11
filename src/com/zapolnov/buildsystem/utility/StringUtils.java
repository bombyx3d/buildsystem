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
package com.zapolnov.buildsystem.utility;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

/** Utility functions for strings. */
public class StringUtils
{
    /** Instance of the UTF-8 character set. */
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    /** Array of hexadecimal digits. */
    public static final char[] HEX_CHARACTERS = "0123456789abcdef".toCharArray();


    /**
     * Retrieves short description of a problem that caused the exception.
     * @param throwable Instance of the exception.
     * @return Short description of a problem.
     */
    public static String getShortExceptionMessage(Throwable throwable)
    {
        if (throwable instanceof FileNotFoundException)
            return String.format("File not found: %s", throwable.getMessage());
        if (throwable instanceof ClassNotFoundException)
            return String.format("Class not found: %s", throwable.getMessage());

        String message = throwable.getMessage();
        if (message == null || message.length() == 0)
            message = throwable.getClass().getName();

        return message;
    }

    /**
     * Retrieves detailed description of a problem that caused the exception.
     * @param throwable Instance of the exception.
     * @return Detailed description of a problem.
     */
    public static String getDetailedExceptionMessage(Throwable throwable)
    {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }


    private StringUtils() {}
    static { new StringUtils(); }
}
