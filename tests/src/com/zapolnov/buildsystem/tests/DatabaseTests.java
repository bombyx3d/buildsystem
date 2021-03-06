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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseTests extends Assert
{
    @Test public void testOptions() throws IOException
    {
        Path temporaryDirectoryPath = Files.createTempDirectory("DatabaseTest");
        File temporaryDirectory = temporaryDirectoryPath.toFile();
        temporaryDirectory.deleteOnExit();

        Database database = new Database(temporaryDirectory);
        try {
            String option = database.getOption("TEST_OPTION");
            assertNull(option);

            database.setOption("TEST_OPTION", "Hello, world!");
            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world!", option);

            database.commit();

            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world!", option);

            database.setOption("TEST_OPTION", "Hello, world 2!");
            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world 2!", option);

            database.rollback();

            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world!", option);

            database.setOption("TEST_OPTION", "Hello, world 2!");
            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world 2!", option);

            database.commit();

            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world 2!", option);

            database.rollbackSafe();

            option = database.getOption("TEST_OPTION");
            assertEquals("Hello, world 2!", option);
        } finally {
            database.close();
            new File(temporaryDirectory, Database.FILE_NAME).deleteOnExit();
        }
    }
}
