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
package com.zapolnov.buildsystem.build;

import com.zapolnov.buildsystem.build.qt5.Qt5GeneratorFactory;

/** Target platform. */
public enum TargetPlatform
{
    QT5("qt5", "Qt 5", new Qt5GeneratorFactory());

    /** Unique identifier of the platform. */
    public final String id;
    /** Name of the platform. */
    public final String name;
    /** Generator factory for the platform. */
    public final GeneratorFactory generatorFactory;

    /**
     * Constructor.
     * @param id Unique identifier of the platform.
     * @param name Name of the platform.
     * @param generatorFactory Project generator for the platform.
     */
    TargetPlatform(String id, String name, GeneratorFactory generatorFactory)
    {
        this.id = id;
        this.name = name;
        this.generatorFactory = generatorFactory;
    }
}
