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
package com.zapolnov.buildsystem.utility.yaml;

import com.zapolnov.buildsystem.utility.StringUtils;

/** An exception thrown for errors in YAML files. */
public class YamlError extends RuntimeException
{
    /**
     * Constructor.
     * @param value Value that caused the error.
     * @param message Error message.
     */
    public YamlError(YamlValue value, String message)
    {
        super(makeMessage(value, message, null));
    }

    /**
     * Constructor.
     * @param value Value that caused the error.
     * @param exception Exception that caused the error.
     */
    public YamlError(YamlValue value, Throwable exception)
    {
        super(makeMessage(value, null, exception), exception);
    }

    /**
     * Constructor.
     * @param value Value that caused the error.
     * @param message Error message.
     * @param exception Exception that caused the error.
     */
    public YamlError(YamlValue value, String message, Throwable exception)
    {
        super(makeMessage(value, message, exception), exception);
    }

    /**
     * Constructs an error message for this exception.
     * @param value Value that caused the error.
     * @param message Error message.
     * @param exception Exception that caused the error.
     */
    private static String makeMessage(YamlValue value, String message, Throwable exception)
    {
        if (message == null && exception != null)
            message = StringUtils.getShortExceptionMessage(exception);

        if (value == null) {
            if (exception instanceof Error)
                throw (Error)exception;
            if (exception instanceof RuntimeException)
                throw (RuntimeException)exception;
            throw new RuntimeException(message, exception);
        }

        return String.format("Problem%s %s", value.node.getStartMark().toString(), message);
    }
}
