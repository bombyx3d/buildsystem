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
import java.net.URISyntaxException;

/** Operating system utilities. */
public final class SystemUtils
{
    /** A name of the operating system with are running under. */
    public static final String OS_NAME = System.getProperty("os.name");
    /** Set to `true` if we are running under Linux-based operating system. */
    public static final boolean IS_LINUX =
        StringUtils.startsWith(OS_NAME, "Linux") || StringUtils.startsWith(OS_NAME, "LINUX");
    /** Set to `true` if we are running under Apple OS X. */
    public static final boolean IS_OSX = StringUtils.startsWith(OS_NAME, "Mac OS");
    /** Set to `true` if we are running under Microsoft Windows. */
    public static final boolean IS_WINDOWS = StringUtils.startsWith(OS_NAME, "Windows");

    /**
     * Determines path to the `java` executable.
     * @return Path to the `java` executable.
     */
    public static String getJavaExecutable()
    {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            File executableFile;
            if (!IS_WINDOWS)
                executableFile = new File(new File(javaHome), "bin/java");
            else
                executableFile = new File(new File(javaHome), "bin/java.exe");

            if (executableFile.exists())
                return FileUtils.getCanonicalPath(executableFile);
        }
        return "java";
    }

    /**
     * Determines path to the jar file of the running application.
     * @return Path to the jar file.
     */
    public static File getApplicationJarFile()
    {
        try {
            File jar = new File(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            return FileUtils.getCanonicalFile(jar);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to determine path to the application JAR file.", e);
        }
    }

    private SystemUtils() {}
    static { new SystemUtils(); }
}
