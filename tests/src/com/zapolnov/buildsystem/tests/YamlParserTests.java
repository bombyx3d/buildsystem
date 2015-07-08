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

import com.zapolnov.buildsystem.utility.yaml.YamlError;
import com.zapolnov.buildsystem.utility.yaml.YamlParser;
import com.zapolnov.buildsystem.utility.yaml.YamlValue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.Assert;
import org.junit.Test;

public class YamlParserTests extends Assert
{
    @Test public void testYamlParserNonExistent() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestNonExistent", ".yml");
        if (!yamlFile.delete())
            throw new IOException("Unable to delete temporary file.");

        boolean thrown = false;
        try { YamlParser.readFile(yamlFile); } catch (RuntimeException e) { thrown = true; }
        assertTrue(thrown);
    }

    @Test public void testYamlParserSyntaxError() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestSyntaxError", ".yml");
        yamlFile.deleteOnExit();

        try (FileOutputStream stream = new FileOutputStream(yamlFile)) {
            try (PrintWriter writer = new PrintWriter(stream)) {
                writer.println(":");
                writer.flush();
            }
        }

        boolean thrown = false;
        try { YamlParser.readFile(yamlFile); } catch (RuntimeException e) { thrown = true; }
        assertTrue(thrown);
    }

    @Test public void testYamlParserOne() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestOne", ".yml");
        yamlFile.deleteOnExit();

        YamlValue value = YamlParser.readFile(yamlFile);
        assertNull(value);
    }

    @Test public void testYamlParserTwo() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestTwo", ".yml");
        yamlFile.deleteOnExit();

        try (FileOutputStream stream = new FileOutputStream(yamlFile)) {
            try (PrintWriter writer = new PrintWriter(stream)) {
                writer.println("item1");
                writer.flush();
            }
        }

        YamlValue value = YamlParser.readFile(yamlFile);

        assertNotNull(value);
        assertFalse(value.isMapping());
        assertFalse(value.isSequence());
        assertTrue(value.isString());

        assertEquals(value.toString(), "item1");
        assertEquals(value.toSequence().get(0).toString(), "item1");

        boolean thrown = false;
        try { value.toMapping(); } catch(YamlError e) { thrown = true; }
        assertTrue(thrown);
    }

    @Test public void testYamlParserThree() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestThree", ".yml");
        yamlFile.deleteOnExit();

        try (FileOutputStream stream = new FileOutputStream(yamlFile)) {
            try (PrintWriter writer = new PrintWriter(stream)) {
                writer.println("[ item1, item2 ]");
                writer.flush();
            }
        }

        YamlValue value = YamlParser.readFile(yamlFile);
        assertNotNull(value);
        assertFalse(value.isMapping());
        assertTrue(value.isSequence());
        assertFalse(value.isString());

        assertEquals(value.toSequence().size(), 2);
        assertEquals(value.toSequence().get(0).toString(), "item1");
        assertEquals(value.toSequence().get(1).toString(), "item2");

        boolean thrown = false;
        try { value.toMapping(); } catch(YamlError e) { thrown = true; }
        assertTrue(thrown);

        thrown = false;
        try { value.toString(); } catch(YamlError e) { thrown = true; }
        assertTrue(thrown);
    }

    @Test public void testYamlParserFour() throws IOException
    {
        File yamlFile = File.createTempFile("YamlParserTestFour", ".yml");
        yamlFile.deleteOnExit();

        try (FileOutputStream stream = new FileOutputStream(yamlFile)) {
            try (PrintWriter writer = new PrintWriter(stream)) {
                writer.println("item1: value1\nitem2: value2");
                writer.flush();
            }
        }

        YamlValue value = YamlParser.readFile(yamlFile);
        assertNotNull(value);
        assertTrue(value.isMapping());
        assertFalse(value.isSequence());
        assertFalse(value.isString());

        YamlValue values[] = value.toMapping().values().toArray(new YamlValue[2]);
        assertEquals(values.length, 2);
        assertEquals(values[0].toString(), "value1");
        assertEquals(values[1].toString(), "value2");

        boolean thrown = false;
        try { value.toSequence(); } catch(YamlError e) { thrown = true; }
        assertTrue(thrown);

        thrown = false;
        try { value.toString(); } catch(YamlError e) { thrown = true; }
        assertTrue(thrown);
    }
}
