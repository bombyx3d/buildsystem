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
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Writer for text files.
 * Before writing file contents to disk, this class checks whether it differs from the previous contents and, if not,
 * it does not overwrite the file.
 */
public final class FileBuilder
{
    private final StringBuilder stringBuilder;
    private final File file;

    /**
     * Constructor.
     * @param directory Target directory.
     * @param fileName Name of the file.
     */
    public FileBuilder(File directory, String fileName)
    {
        this(new File(directory, fileName));
    }

    /**
     * Constructor.
     * @param file Path to the file.
     */
    public FileBuilder(File file)
    {
        this.file = FileUtils.getCanonicalFile(file);
        this.stringBuilder = new StringBuilder();
        FileUtils.ensureDirectoryExists(this.file.getParentFile());
    }

    /**
     * Appends character to the file.
     * @param ch Character to append.
     */
    public void append(char ch)
    {
        stringBuilder.append(ch);
    }

    /**
     * Appends string to the file.
     * @param string String to append.
     */
    public void append(String string)
    {
        stringBuilder.append(string);
    }

    /**
     * Appends hexadecimal representation of the given byte to the file.
     * @param value Byte.
     */
    public void appendHex(byte value)
    {
        stringBuilder.append(StringUtils.HEX_CHARACTERS[(value >> 4) & 0xF]);
        stringBuilder.append(StringUtils.HEX_CHARACTERS[value & 0xF]);
    }

    /**
     * Writes file data to disk.
     * @param database Database instance to check for file modifications.
     * @return `true` if file has been overwritten, or `false` if file contents did not change.
     */
    public boolean commit(Database database) throws NoSuchAlgorithmException, IOException
    {
        String text = stringBuilder.toString();
        byte[] bytes = text.getBytes(StringUtils.UTF8_CHARSET);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(bytes);
        if (!database.didOutputFileChange(file, md5)) {
            Log.trace(String.format("Keeping %s", FileUtils.getRelativePath(database.directory, file)));
            return false;
        }

        Log.info(String.format("Writing %s", FileUtils.getRelativePath(database.directory, file)));
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(bytes);
            stream.flush();
        }

        return true;
    }
}
