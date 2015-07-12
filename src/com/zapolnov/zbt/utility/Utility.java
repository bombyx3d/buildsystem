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
package com.zapolnov.zbt.utility;

import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class Utility
{
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean IS_LINUX = startsWith(OS_NAME, "Linux") || startsWith(OS_NAME, "LINUX");
    public static final boolean IS_OSX = startsWith(OS_NAME, "Mac OS");
    public static final boolean IS_WINDOWS = startsWith(OS_NAME, "Windows");

    private Utility() {}

    public static byte[] md5ForString(String string)
    {
        try {
            return MessageDigest.getInstance("MD5").digest(string.getBytes(StringUtils.UTF8_CHARSET));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] makeOptionsHash(Object... parameters)
    {
        StringBuilder builder = new StringBuilder();
        for (Object parameter : parameters) {
            if (parameter == null)
                builder.append('\2');
            else {
                builder.append('\3');
                if (parameter instanceof File)
                    builder.append(FileUtils.getCanonicalPath((File) parameter));
                else
                    builder.append(parameter.toString());
            }
            builder.append('\1');
        }
        return md5ForString(builder.toString());
    }

    public static boolean startsWith(String string, String prefix)
    {
        return !(string == null || prefix == null) && string.startsWith(prefix);
    }

    public static boolean fileHasExtension(File file, String extension)
    {
        String fileName = file.getName();
        return (fileName.endsWith(extension) && fileName.length() > extension.length());
    }

    public static boolean fileHasExtension(File file, String[] extensions)
    {
        String fileName = file.getName();
        for (String extension : extensions) {
            if (fileName.endsWith(extension) && fileName.length() > extension.length())
                return true;
        }
        return false;
    }


    public static boolean isFileInsideDirectory(File file, File directory)
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

        try {
            String[] directoryPathStack = directory.getCanonicalPath().replace(File.separator, "/").split("/");
            String[] filePathStack = file.getCanonicalPath().replace(File.separator, "/").split("/");

            if (directoryPathStack.length == 0 || filePathStack.length == 0)
                return false;
            if (!directoryPathStack[0].equals(filePathStack[0]))
                return false;

            int minLength = Math.min(directoryPathStack.length, filePathStack.length);
            int same = 1;
            while (same < minLength && directoryPathStack[same].equals(filePathStack[same]))
                ++same;

            return same == directoryPathStack.length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String resolveExecutable(String program)
    {
        String[] extensions = null;
        if (!Utility.IS_WINDOWS) {
            extensions = new String[]{ "" };
        } else {
            String pathext = System.getenv("PATHEXT");
            if (pathext != null)
                extensions = pathext.split(";");
            if (extensions == null || extensions.length == 0)
                extensions = new String[]{ ".com", ".exe", ".bat", ".cmd" };
        }

        String path = System.getenv("PATH");
        if (path != null) {
            String[] paths = path.split(File.pathSeparator);
            for (String directory : paths) {
                for (String extension : extensions) {
                    File file = new File(new File(directory), program + extension);
                    if (file.exists())
                        return FileUtils.getCanonicalPath(file);
                }
            }
        }

        return null;
    }



    public static byte[] byteArrayFromFile(File file)
    {
        try (FileInputStream stream = new FileInputStream(file)) {
            int fileLength = (int)file.length();
            byte[] buffer = new byte[fileLength];
            int bytesRead = stream.read(buffer, 0, fileLength);
            if (bytesRead != fileLength) {
                throw new RuntimeException(String.format("Incomplete read in file \"%s\".",
                    FileUtils.getCanonicalPath(file)));
            }
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJavaExecutable()
    {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            File executableFile;
            if (!Utility.IS_WINDOWS)
                executableFile = new File(new File(javaHome), "bin/java");
            else
                executableFile = new File(new File(javaHome), "bin/java.exe");

            if (executableFile.exists())
                return FileUtils.getCanonicalPath(executableFile);
        }
        return "java";
    }

    public static File getApplicationJarFile()
    {
        try {
            File jar = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            return FileUtils.getCanonicalFile(jar);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to determine path to the application JAR file.", e);
        }
    }
}
