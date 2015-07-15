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

import java.awt.Component;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/** A wrapper over the JFileChooser dialog. */
public class FileDialog extends JFileChooser
{
    /**
     * Displays the file selection dialog.
     * @param parent Parent.
     * @param title Dialog title.
     * @param initialDirectory Initial directory.
     * @param filters List of file filters.
     * @return Selected file or `null` if dialog has been cancelled.
     */
    public static File chooseOpenFile(Component parent, String title,
        File initialDirectory, List<FileFilter> filters)
    {
        FileDialog chooser = new FileDialog();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (filters == null)
            chooser.setAcceptAllFileFilterUsed(true);
        else {
            filters.forEach(chooser::addChoosableFileFilter);
            chooser.setAcceptAllFileFilterUsed(false);
        }

        if (initialDirectory != null)
            chooser.setCurrentDirectory(initialDirectory);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();

        return null;
    }

    /**
     * Displays the directory selection dialog.
     * @param parent Parent.
     * @param title Dialog title.
     * @param initialDirectory Directory to be selected in the dialog immediately after it will be shown.
     * @return Selected directory or `null` if dialog has been cancelled.
     */
    public static File chooseDirectory(Component parent, String title, File initialDirectory)
    {
        FileDialog chooser = new FileDialog();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (initialDirectory != null)
            chooser.setCurrentDirectory(initialDirectory);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
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
