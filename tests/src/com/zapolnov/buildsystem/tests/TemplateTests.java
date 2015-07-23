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
import com.zapolnov.buildsystem.utility.Template;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class TemplateTests extends Assert
{
    @Test public void testTemplate() throws IOException, NoSuchAlgorithmException
    {
        Path temporaryDirectoryPath = Files.createTempDirectory("TemplateTest");
        File temporaryDirectory = temporaryDirectoryPath.toFile();
        temporaryDirectory.deleteOnExit();

        Database database = new Database(temporaryDirectory);
        try {
            File file = new File(temporaryDirectory, "TestTemplateOutput");
            assertTrue(!file.exists());
            file.deleteOnExit();

            generate(file, "", database, new HashMap<>());
            validate(file, "");

            generate(file, "Hello, world!", database, new HashMap<>());
            validate(file, "Hello, world!");

            generate(file, "@", database, new HashMap<>());
            validate(file, "@");

            generate(file, "@{Test", database, new HashMap<>());
            validate(file, "@{Test");

            boolean thrown = false;
            try {
                generate(file, "@{Test}", database, new HashMap<>());
            } catch (RuntimeException e) {
                thrown = true;
            }
            assertTrue(thrown);

            Map<String, String> options = new HashMap<>();
            options.put("Test", "Test String");
            generate(file, "@{Test}", database, options);
            validate(file, "Test String");

            options = new HashMap<>();
            options.put("Test", "Test String");
            generate(file, "A@{Test}B", database, options);
            validate(file, "ATest StringB");
        } finally {
            database.close();
            new File(temporaryDirectory, Database.FILE_NAME).deleteOnExit();
        }
    }

    private void generate(File file, String templateText, Database database, Map<String, String> options)
        throws IOException, NoSuchAlgorithmException
    {
        FileBuilder builder = new FileBuilder(file);

        ByteArrayInputStream templateData = new ByteArrayInputStream(templateText.getBytes("UTF-8"));
        Template template = new Template(templateData);
        template.emit(builder, options);

        boolean written = builder.commit(database);
        assertTrue(written);
    }

    private void validate(File file, String expectedContents) throws IOException
    {
        assertTrue(file.exists());
        assertFalse(file.isDirectory());

        if (file.length() == 0) {
            assertEquals("", expectedContents);
            return;
        }

        String gotString;
        try (FileReader reader = new FileReader(file)) {
            char buffer[] = new char[16384];
            int length = reader.read(buffer);
            gotString = new String(buffer, 0, length);
        }

        assertEquals(expectedContents, gotString);
    }
}
