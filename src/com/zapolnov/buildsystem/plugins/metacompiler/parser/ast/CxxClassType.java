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
package com.zapolnov.buildsystem.plugins.metacompiler.parser.ast;

/** A kind of the C++ class. */
public enum CxxClassType
{
    /** Class is a usual C++ class. */
    DEFAULT,
    /** Class is an interface class (has the Z_INTERFACE macro). */
    INTERFACE,
    /** Class is an implementation class (has the Z_IMPLEMENTATION macro). */
    IMPLEMENTATION,
    /** Class is a singleton implementation (has the Z_SINGLETON_IMPLEMENTATION macro). */
    SINGLETON_IMPLEMENTATION,
    /**
     * Class is an implementation class with a custom variant of the `queryInterface` method
     * (has the Z_CUSTOM_IMPLEMENTATION macro).
     */
    CUSTOM_IMPLEMENTATION,
}
