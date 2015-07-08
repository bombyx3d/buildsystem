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

import com.zapolnov.buildsystem.utility.Log;
import com.zapolnov.buildsystem.utility.LogLevel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;

public class LogTests extends Assert
{
    private final class TestPrinter implements Log.Printer
    {
        public String errorMessage;
        public String warningMessage;
        public String infoMessage;
        public String debugMessage;
        public String traceMessage;

        @Override public void printLogMessage(LogLevel level, String message)
        {
            switch (level)
            {
            case ERROR: errorMessage = message; return;
            case WARNING: warningMessage = message; return;
            case INFO: infoMessage = message; return;
            case DEBUG: debugMessage = message; return;
            case TRACE: traceMessage = message; return;
            }
            throw new RuntimeException("Invalid logging level.");
        }
    }

    private final String TEST_ERROR_MESSAGE = "Test Error Message";
    private final String TEST_WARNING_MESSAGE = "Test Warning Message";
    private final String TEST_INFO_MESSAGE = "Test Informational Message";
    private final String TEST_DEBUG_MESSAGE = "Test Debug Message";
    private final String TEST_TRACE_MESSAGE = "Test Tracing Message";

    @Test public void testConsolePrinter() throws UnsupportedEncodingException
    {
        final PrintStream defaultOutputStream = System.out;
        final PrintStream defaultErrorStream = System.err;

        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        ByteArrayOutputStream testError = new ByteArrayOutputStream();

        try {
            PrintStream testOutputStream = new PrintStream(testOutput);
            PrintStream testErrorStream = new PrintStream(testError);

            System.setOut(testOutputStream);
            System.setErr(testErrorStream);

            Log.setPrinter(null);
            Log.error(TEST_ERROR_MESSAGE);
            Log.warn(TEST_WARNING_MESSAGE);
            Log.info(TEST_INFO_MESSAGE);
            Log.debug(TEST_DEBUG_MESSAGE);
            Log.trace(TEST_TRACE_MESSAGE);

            testOutputStream.flush();
            testErrorStream.flush();
        } finally {
            System.setOut(defaultOutputStream);
            System.setErr(defaultErrorStream);
        }

        ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
        ByteArrayOutputStream expectedError = new ByteArrayOutputStream();

        PrintStream expectedOutputStream = new PrintStream(expectedOutput);
        expectedOutputStream.println(TEST_INFO_MESSAGE);
        expectedOutputStream.println(TEST_DEBUG_MESSAGE);
        expectedOutputStream.println(TEST_TRACE_MESSAGE);
        expectedOutputStream.flush();

        PrintStream expectedErrorStream = new PrintStream(expectedError);
        expectedErrorStream.println(TEST_ERROR_MESSAGE);
        expectedErrorStream.println(TEST_WARNING_MESSAGE);
        expectedErrorStream.flush();

        assertArrayEquals(testOutput.toByteArray(), expectedOutput.toByteArray());
        assertArrayEquals(testError.toByteArray(), expectedError.toByteArray());
    }

    @Test public void testCustomPrinter()
    {
        try {
            TestPrinter printer = new TestPrinter();
            Log.setPrinter(printer);

            Log.error(TEST_ERROR_MESSAGE);
            Log.warn(TEST_WARNING_MESSAGE);
            Log.info(TEST_INFO_MESSAGE);
            Log.debug(TEST_DEBUG_MESSAGE);
            Log.trace(TEST_TRACE_MESSAGE);

            assertEquals(printer.errorMessage, TEST_ERROR_MESSAGE);
            assertEquals(printer.warningMessage, TEST_WARNING_MESSAGE);
            assertEquals(printer.infoMessage, TEST_INFO_MESSAGE);
            assertEquals(printer.debugMessage, TEST_DEBUG_MESSAGE);
            assertEquals(printer.traceMessage, TEST_TRACE_MESSAGE);
        } finally {
            Log.setPrinter(null);
        }
    }
}
