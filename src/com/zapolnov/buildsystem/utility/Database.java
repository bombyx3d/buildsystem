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
import java.util.Arrays;
import java.util.concurrent.ConcurrentNavigableMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/** A database. */
public class Database
{
    /** File name for the database. */
    public final static String FILE_NAME = "database";

    public final static String OPTION_TARGET_PLATFORM = "";

    private final static String INPUT_FILES_TABLE = "InputFiles";
    private final static String INPUT_FILES_OPTIONS_HASHES_TABLE = "InputFilesOptionsHashes";
    private final static String OUTPUT_FILES_TABLE = "OutputFiles";
    private final static String OPTIONS_TABLE = "Options";

    /** Directory containing the database file. */
    public final File directory;
    /** Database. */
    protected DB db;

    /**
     * Constructor.
     * @param directory Directory where database should be created.
     */
    public Database(File directory)
    {
        this.directory = directory;
    }

    /** Opens the database if it has not been opened yet. */
    public void open()
    {
        if (db == null) {
            db = DBMaker.newFileDB(new File(directory, FILE_NAME))
                .closeOnJvmShutdown()
                .make();
        }
    }

    /** Saves all uncommitted changes to the file. */
    public void commit()
    {
        if (db != null) {
            db.commit();
            try {
                db.close();
            } finally {
                db = null;
            }
        }
    }

    /**
     * Reverts all uncommitted changes.
     * This method may throw an exception.
     */
    public void rollback()
    {
        if (db != null) {
            db.rollback();
            try {
                db.close();
            } finally {
                db = null;
            }
        }
    }

    /**
     * Reverts all uncommitted changes.
     * This method attempts not to throw an exception in case of failure.
     */
    public void rollbackSafe()
    {
        try {
            rollback();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Closes the database.
     * Database can't be used after this method has been invoked.
     */
    public void close()
    {
        if (db != null) {
            try {
                db.close();
            } finally {
                db = null;
            }
        }
    }

    /**
     * Retrieves value of the specified option.
     * @param key Name of the option.
     */
    public String getOption(String key)
    {
        try {
            open();
            ConcurrentNavigableMap<String, String> table = db.getTreeMap(OPTIONS_TABLE);
            return table.get(key);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * Sets value of the specified option.
     * @param key Name of the option.
     * @param value Option value.
     */
    public void setOption(String key, String value)
    {
        open();
        ConcurrentNavigableMap<String, String> table = db.getTreeMap(OPTIONS_TABLE);
        table.put(key, value);
    }

    /**
     * Checks whether the specified input file has changed from the time of previous check.
     * @param file Input file.
     * @param extraData Additional metadata for the file.
     * File is considered modified if this data differs from the previously specified data.
     * @return `true` if file has been modified or if it has not been processed yet, otherwise returns `false`.
     */
    public boolean didInputFileChange(File file, byte[] extraData)
    {
        boolean result = false;

        if (!file.exists())
            return true;

        try {
            open();
            String path = FileUtils.getCanonicalPath(file);

            ConcurrentNavigableMap<String, byte[]> hashesTable = db.getTreeMap(INPUT_FILES_OPTIONS_HASHES_TABLE);
            byte[] previousHash = hashesTable.get(path);
            if (previousHash == null || !Arrays.equals(extraData, previousHash)) {
                hashesTable.put(path, extraData);
                result = true;
            }

            ConcurrentNavigableMap<String, Long> table = db.getTreeMap(INPUT_FILES_TABLE);
            Long expectedLastModificationTime = table.get(path);
            long actualLastModificationTime = file.lastModified();
            if (expectedLastModificationTime == null || expectedLastModificationTime != actualLastModificationTime) {
                table.put(path, actualLastModificationTime);
                result = true;
            }

            return result;
        } catch (Throwable t) {
            t.printStackTrace();
            return true;
        }
    }

    /**
     * Checks whether new contents for the output file differ from the previously written contents.
     * @param file Ouptut file.
     * @param md5 MD5 hash of the data.
     * @return `true` if the provided MD5 hash differs from the previously written one, or if file has never
     * been written, otherwise returns `false`.
     */
    public boolean didOutputFileChange(File file, byte[] md5)
    {
        try {
            open();
            ConcurrentNavigableMap<String, byte[]> table = db.getTreeMap(OUTPUT_FILES_TABLE);

            String path = FileUtils.getCanonicalPath(file);
            byte[] previousMd5 = table.get(path);
            if (previousMd5 != null && Arrays.equals(md5, previousMd5))
                return false;

            table.put(path, md5);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return true;
    }
}
