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
package com.zapolnov.buildsystem;

import com.bulenkov.darcula.DarculaLaf;
import com.zapolnov.buildsystem.gui.FatalErrorDialog;
import com.zapolnov.buildsystem.gui.MainDialog;
import com.zapolnov.buildsystem.utility.StringUtils;
import javax.swing.UIManager;

/** Main class of the application. */
public class Main
{
    /**
     * Application entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        boolean gui = true;

        // Handle '--batch' command-line argument early
        for (int i = 0; i < args.length; i++) {
            if ("--batch".equals(args[i]) || "-b".equals(args[i])) {
                gui = false;
            }
        }

        if (gui) {
            try {
                try { UIManager.setLookAndFeel(new DarculaLaf()); } catch (Throwable ignored) {}
                MainDialog.run();
            } catch (Throwable t) {
                System.err.println(StringUtils.getDetailedExceptionMessage(t));
                FatalErrorDialog.run(t);
            }
        }
    }
}
