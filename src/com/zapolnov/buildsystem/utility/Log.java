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

/** Logger for diagnostic messages. */
public final class Log
{
    /** Interface for log message printers. */
    public interface Printer
    {
        /**
         * Prints log message.
         * @param level Level of the message.
         * @param message Message.
         */
        void printLogMessage(LogLevel level, String message);
    }

    /** Printer that writes log messages to standard output and standard error streams. */
    public final static class ConsolePrinter implements Printer
    {
        @Override public void printLogMessage(LogLevel level, String message)
        {
            if (level.integerValue <= LogLevel.WARNING.integerValue)
                System.err.println(message);
            else
                System.out.println(message);
        }
    }


    /** Printer for log messages. */
    private static Printer printer;


    /**
     * Prints an error message.
     * @param message Message to print.
     */
    public static void error(String message)
    {
        printer.printLogMessage(LogLevel.ERROR, message);
    }

    /**
     * Prints a warning message.
     * @param message Message to print.
     */
    public static void warn(String message)
    {
        printer.printLogMessage(LogLevel.WARNING, message);
    }

    /**
     * Prints an informational message.
     * @param message Message to print.
     */
    public static void info(String message)
    {
        printer.printLogMessage(LogLevel.INFO, message);
    }

    /**
     * Prints a debug message.
     * @param message Message to print.
     */
    public static void debug(String message)
    {
        printer.printLogMessage(LogLevel.DEBUG, message);
    }

    /**
     * Prints a trace message.
     * @param message Message to print.
     */
    public static void trace(String message)
    {
        printer.printLogMessage(LogLevel.TRACE, message);
    }

    /**
     * Sets printer for log messages.
     * @param printer Printer for log messages.
     */
    public static void setPrinter(Printer printer)
    {
        Log.printer = (printer != null ? printer : new ConsolePrinter());
    }

    private Log() {}
    static { new Log(); setPrinter(null); }
}
