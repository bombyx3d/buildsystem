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

import java.awt.Color;

/** A set of useful colors. */
public final class Colors
{
    public static final Color BLACK = Color.decode("#1d1f21");
    public static final Color BLUE = Color.decode("#81a2be");
    public static final Color GREEN = Color.decode("#b5bd68");
    public static final Color CYAN = Color.decode("#8abeb7");
    public static final Color RED = Color.decode("#ee6666");
    public static final Color YELLOW = Color.decode("#eec35f");
    public static final Color MAGENTA = Color.decode("#b294bb");
    public static final Color GRAY = Color.decode("#c5c8c6");
    public static final Color DARK_GRAY = Color.decode("#969896");
    public static final Color WHITE = Color.decode("#ffffff");

    private Colors() {}
    static { new Colors(); }
}
