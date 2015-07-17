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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Utility functions for strings. */
public class StringUtils
{
    /** Instance of the UTF-8 character set. */
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    /** Array of hexadecimal digits. */
    public static final char[] HEX_CHARACTERS = "0123456789abcdef".toCharArray();


    /**
     * Checks whether given string starts with the specified prefix.
     * @param string String to check.
     * @param prefix Expected prefix.
     * @return `true` if string starts with the specified prefix, otherwise returns `false`.
     */
    public static boolean startsWith(String string, String prefix)
    {
        return !(string == null || prefix == null) && string.startsWith(prefix);
    }

    /**
     * Calculates an MD5 hash for the given string.
     * @param string Input string.
     * @return MD5 hash.
     */
    public static byte[] md5ForString(String string)
    {
        try {
            return MessageDigest.getInstance("MD5").digest(string.getBytes(StringUtils.UTF8_CHARSET));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to calculate MD5 hash.", e);
        }
    }

    /**
     * Calculates an MD5 hash for an array of objects.
     * @param parameters Array of objects. Method `toString()` is invoked for each object
     * to retrieve its string representation.
     * @return MD5 hash.
     */
    public static byte[] md5ForObjects(Object... parameters)
    {
        StringBuilder builder = new StringBuilder();
        for (Object parameter : parameters) {
            if (parameter == null)
                builder.append('\2');
            else {
                builder.append('\3');
                if (parameter instanceof File)
                    builder.append(FileUtils.getCanonicalPath((File)parameter));
                else
                    builder.append(parameter.toString());
            }
            builder.append('\1');
        }
        return md5ForString(builder.toString());
    }

    /**
     * Checks whether given file has one of the provided extensions.
     * @param file File.
     * @param extensions Array of extensions.
     * @return `true` if file has one of the provided extensions, otherwise returns `false`.
     */
    public static boolean fileHasExtension(File file, String[] extensions)
    {
        String fileName = file.getName();
        for (String extension : extensions) {
            if (fileName.endsWith(extension) && fileName.length() > extension.length())
                return true;
        }
        return false;
    }

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
