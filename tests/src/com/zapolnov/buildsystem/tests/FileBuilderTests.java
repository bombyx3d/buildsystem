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

import com.zapolnov.buildsystem.utility.Database;
import com.zapolnov.buildsystem.utility.FileBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;

public class FileBuilderTests extends Assert
{
    @Test public void testFileBuilder() throws NoSuchAlgorithmException, IOException
    {
        Path temporaryDirectoryPath = Files.createTempDirectory("FileBuilderTest");
        File temporaryDirectory = temporaryDirectoryPath.toFile();
        temporaryDirectory.deleteOnExit();

        Database database = new Database(temporaryDirectory);
        try {
            File file = new File(temporaryDirectory, "TestFileBuilderOutput");
            assertTrue(!file.exists());
            file.deleteOnExit();

            boolean written = generate(database, new FileBuilder(file), "1");
            assertTrue(written);
            validate(file, "1");

            written = generate(database, new FileBuilder(temporaryDirectory, "TestFileBuilderOutput"), "1");
            assertFalse(written);
            validate(file, "1");

            written = generate(database, new FileBuilder(temporaryDirectory, "TestFileBuilderOutput"), "2");
            assertTrue(written);
            validate(file, "2");

            written = generate(database, new FileBuilder(file), "2");
            assertFalse(written);
            validate(file, "2");
        } finally {
            database.close();
            new File(temporaryDirectory, Database.FILE_NAME).deleteOnExit();
        }
    }

    private boolean generate(Database database, FileBuilder fileBuilder, String prefix)
        throws NoSuchAlgorithmException, IOException
    {
        fileBuilder.append(prefix);
        fileBuilder.append("Hello, world!\n");
        for (int i = 0; i < 0x100; i++) {
            fileBuilder.appendHex((byte)i);
            fileBuilder.append('\n');
        }
        return fileBuilder.commit(database);
    }

    private void validate(File file, String prefix) throws IOException
    {
        assertTrue(file.exists());
        assertFalse(file.isDirectory());

        String gotString;
        try (FileReader reader = new FileReader(file)) {
            char buffer[] = new char[16384];
            int length = reader.read(buffer);
            gotString = new String(buffer, 0, length);
        }

        StringBuilder expectedContents = new StringBuilder();
        expectedContents.append(prefix);
        expectedContents.append("Hello, world!\n");
        for (int i = 0; i < 0x100; i++) {
            expectedContents.append(String.format("%02x", i));
            expectedContents.append('\n');
        }

        String expectedString = expectedContents.toString();
        assertEquals(expectedString, gotString);
    }
}
