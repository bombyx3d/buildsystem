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
package com.zapolnov.buildsystem.gui.widgets;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** A wrapper over the JFileChooser dialog. */
public class FileDialog extends JFileChooser
{
    /**
     * Displays the directory selection dialog.
     * @param title Dialog title.
     * @param initialDirectory Directory to be selected in the dialog immediately after it will be shown.
     */
    public static File chooseDirectory(String title, File initialDirectory)
    {
        FileDialog chooser = new FileDialog();
        chooser.setCurrentDirectory(initialDirectory);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();

        return null;
    }

    @Override public void updateUI()
    {
        LookAndFeel previousLookAndFeel = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ignored) {
            previousLookAndFeel = null;
        }

        super.updateUI();

        if (previousLookAndFeel != null) {
            setBackground(UIManager.getColor("Label.background"));
            setOpaque(true);

            try {
                UIManager.setLookAndFeel(previousLookAndFeel);
            }  catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
