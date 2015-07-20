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

import com.zapolnov.buildsystem.utility.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest extends Assert
{
    @Test public void testStartsWith() throws IOException
    {
        assertTrue(StringUtils.startsWith("Q", "Q"));
        assertTrue(StringUtils.startsWith("QS", "Q"));
        assertTrue(StringUtils.startsWith("QS", ""));
        assertFalse(StringUtils.startsWith("QS", "S"));
        assertFalse(StringUtils.startsWith("S", "Q"));
    }

    private static int unhex(char c)
    {
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'a' && c <= 'f')
            return c - 'a' + 10;
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        throw new RuntimeException(String.format("Invalid hexadecimal digit '%c'.", c));
    }

    private static byte[] toByteArray(String string)
    {
        int count = string.length() / 2;
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            char c1 = string.charAt(i * 2);
            char c2 = string.charAt(i * 2 + 1);
            result[i] = (byte)((unhex(c1) << 4) | unhex(c2));
        }
        return result;
    }

    @Test public void testMd5ForString() throws IOException
    {
        byte[] expected = toByteArray("6cd3556deb0da54bca060b4c39479839");
        byte[] actual = StringUtils.md5ForString("Hello, world!");
        assertArrayEquals(expected, actual);
    }

    @Test public void testMd5ForObjects() throws IOException
    {
        byte[] expected = toByteArray("d41d8cd98f00b204e9800998ecf8427e");
        byte[] actual = StringUtils.md5ForObjects();
        assertArrayEquals(expected, actual);

        expected = toByteArray("52c93399b0e0828f85654938bc699221");
        actual = StringUtils.md5ForObjects("Hello, world!");
        assertArrayEquals(expected, actual);

        expected = toByteArray("050d144172d916d0846f839e0412e929");
        actual = StringUtils.md5ForObjects(new Object[]{null});
        assertArrayEquals(expected, actual);

        expected = toByteArray("47bf4f5cf379cc3543c41db0951ec419");
        actual = StringUtils.md5ForObjects("Hello", "world!");
        assertArrayEquals(expected, actual);
    }

    @Test public void testToHex()
    {
        assertEquals("", StringUtils.toHex(null));
        assertEquals("", StringUtils.toHex(new byte[0]));
        assertEquals("00", StringUtils.toHex(new byte[]{ 0x00 }));
        assertEquals("0a", StringUtils.toHex(new byte[]{ 0x0a }));
        assertEquals("2a", StringUtils.toHex(new byte[]{ 0x2a }));
        assertEquals("032a", StringUtils.toHex(new byte[]{ 0x03, 0x2a }));
        assertEquals("732a", StringUtils.toHex(new byte[]{ 0x73, 0x2a }));
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", StringUtils.toHex(toByteArray("d41d8cd98f00b204e9800998ecf8427e")));
        assertEquals("52c93399b0e0828f85654938bc699221", StringUtils.toHex(toByteArray("52c93399b0e0828f85654938bc699221")));
        assertEquals("050d144172d916d0846f839e0412e929", StringUtils.toHex(toByteArray("050d144172d916d0846f839e0412e929")));
    }

    @Test public void testFileHasExtension() throws IOException
    {
        boolean result = StringUtils.fileHasExtension(new File("Test"), new String[]{});
        assertFalse(result);

        result = StringUtils.fileHasExtension(new File("Test"), new String[]{ ".exe" });
        assertFalse(result);

        result = StringUtils.fileHasExtension(new File("Test.exe"), new String[]{ ".exe" });
        assertTrue(result);

        result = StringUtils.fileHasExtension(new File("Test.exe"), new String[]{ ".exe", ".com" });
        assertTrue(result);

        result = StringUtils.fileHasExtension(new File("Test.exe"), new String[]{ ".com", ".exe" });
        assertTrue(result);

        result = StringUtils.fileHasExtension(new File("Test.com"), new String[]{ ".exe", ".com", ".z" });
        assertTrue(result);

        result = StringUtils.fileHasExtension(new File("Test.tar.gz"), new String[]{ ".com", ".exe", ".gz" });
        assertTrue(result);

        result = StringUtils.fileHasExtension(new File("Test.tar.gz"), new String[]{ ".com", ".exe", ".tar.gz" });
        assertTrue(result);
    }

    @Test public void testShortExceptionMessage() throws IOException
    {
        String message = StringUtils.getShortExceptionMessage(new NullPointerException());
        assertEquals("java.lang.NullPointerException", message);

        message = StringUtils.getShortExceptionMessage(new FileNotFoundException("test.file"));
        assertEquals("File not found: test.file", message);

        message = StringUtils.getShortExceptionMessage(new ClassNotFoundException("TestClass"));
        assertEquals("Class not found: TestClass", message);

        message = StringUtils.getShortExceptionMessage(new RuntimeException("Test message"));
        assertEquals("Test message", message);
    }

    @Test public void testDetailedExceptionMessage() throws IOException
    {
        String message = StringUtils.getDetailedExceptionMessage(new NullPointerException());
        assertNotNull(message);
        assertTrue(message.length() > 0);
    }
}
