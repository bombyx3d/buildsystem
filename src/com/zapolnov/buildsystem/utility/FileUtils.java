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

import com.zapolnov.zbt.utility.Utility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Utility functions for filesystem operations. */
public class FileUtils
{
    /**
     * Retrieves canonical path for the provided file.
     * @param file File.
     * @return Canonical path to the file.
     */
    public static File getCanonicalFile(File file)
    {
        try {
            return file.getCanonicalFile();
        } catch (Throwable ignored) {
            return file.getAbsoluteFile();
        }
    }

    /**
     * Retrieves canonical path for the provided file.
     * @param file File.
     * @return Canonical path to the file.
     */
    public static String getCanonicalPath(File file)
    {
        try {
            return file.getCanonicalPath();
        } catch (Throwable ignored) {
            return file.getAbsolutePath();
        }
    }

    /**
     * Creates the specified directory and all parent directories if they do not exist yet.
     * @param directory Path to the directory.
     */
    public static void ensureDirectoryExists(File directory)
    {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException(String.format("Unable to create directory \"%s\".",
                    getCanonicalPath(directory)));
            }
        }

        if (!directory.isDirectory()) {
            throw new RuntimeException(String.format("\"%s\" is not a directory.",
                getCanonicalPath(directory)));
        }
    }

    /**
     * Marks the specified file as "hidden" on platforms that support such file attribute.
     * @param directory Path to the directory
     */
    public static void makeDirectoryHidden(File directory)
    {
        if (Utility.IS_WINDOWS) {
            String path = FileUtils.getCanonicalPath(directory);
            try {
                Files.setAttribute(Paths.get(path), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                throw new RuntimeException(
                    String.format("Unable to apply 'hidden' attribute to directory \"%s\".", path), e);
            }
        }
    }

    /**
     * Reads all data from the specified stream into a string.
     * @param stream input stream.
     * @return String with data read from the stream.
     */
    public static String stringFromInputStream(InputStream stream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(stream, StringUtils.UTF8_CHARSET)) {
            char[] buffer = new char[16384];
            int length;
            while ((length = reader.read(buffer)) >= 0)
                stringBuilder.append(buffer, 0, length);
        }
        return stringBuilder.toString();
    }

    /**
     * Calculates a relative path from one file to another.
     * @param fromFile Source file to calculate relative path from.
     * @param toFile Target file.
     * @return Relative path from `fromFile` to `toFile`.
     */
    public static String getRelativePath(File fromFile, File toFile) throws IOException
    {
        // This method is based on code from FileUtils.java of Apache Ant 1.9.5
        // Original code was licensed under the Apache 2.0 license
        //
        // --------------------------------------------------------------------
        // Apache Ant
        // Copyright 1999-2015 The Apache Software Foundation
        //
        // This product includes software developed at
        // The Apache Software Foundation (http://www.apache.org/).
        // --------------------------------------------------------------------

        String fromPath = fromFile.getCanonicalPath();
        String toPath = toFile.getCanonicalPath();

        // build the path stack info to compare
        String[] fromPathStack = fromPath.replace(File.separator, "/").split("/");
        String[] toPathStack = toPath.replace(File.separator, "/").split("/");

        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                // not the same device (would be "" on Linux/Unix)
                return String.join("/", toPathStack);
            }
        } else {
            // no comparison possible
            return String.join("/", toPathStack);
        }

        int minLength = Math.min(fromPathStack.length, toPathStack.length);
        int same = 1; // Used outside the for loop

        // get index of parts which are equal
        while (same < minLength && fromPathStack[same].equals(toPathStack[same]))
            ++same;

        List<String> relativePathStack = new ArrayList<>();

        // if "from" part is longer, fill it up with ".."
        // to reach path which is equal to both paths
        for (int i = same; i < fromPathStack.length; i++)
            relativePathStack.add("..");

        // fill it up path with parts which were not equal
        relativePathStack.addAll(Arrays.asList(toPathStack).subList(same, toPathStack.length));

        return String.join("/", relativePathStack);
    }

    private FileUtils() {}
    static { new FileUtils(); }
}
