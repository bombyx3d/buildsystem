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
package com.zapolnov.buildsystem.gui;

import com.zapolnov.buildsystem.gui.widgets.ButtonPanel;
import com.zapolnov.buildsystem.gui.widgets.LogView;
import com.zapolnov.buildsystem.utility.Log;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;

/** Build dialog of the GUI application. */
public class BuildDialog extends JDialog
{
    public static final String TITLE = "Building project";
    public static final String CLOSE_BUTTON_TITLE = "Close";

    public static final int PREFERRED_WIDTH = 780;
    public static final int PREFERRED_HEIGHT = 500;

    public final JButton closeButton;

    /**
     * Constructor.
     * @param parent Parent frame.
     */
    public BuildDialog(JDialog parent)
    {
        super(parent, TITLE, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        final LogView logView = new LogView();
        getContentPane().add(logView, BorderLayout.CENTER);

        ButtonPanel buttonPanel = new ButtonPanel(3);
        closeButton = buttonPanel.addButton(CLOSE_BUTTON_TITLE, this::tryDispose);
        add(buttonPanel, BorderLayout.PAGE_END);

        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                tryDispose();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                Log.setPrinter(logView);
            }
            @Override public void componentHidden(ComponentEvent e) {
                Log.setPrinter(new Log.ConsolePrinter());
            }
            @Override public void componentResized(ComponentEvent e) {
                Rectangle bounds = e.getComponent().getBounds();
                setPreferredSize(new Dimension(bounds.width, bounds.height));
            }
        });

        pack();
        setLocationRelativeTo(parent);
    }

    /** Disposes the dialog if there is no operation in progress. */
    private void tryDispose()
    {
        if (closeButton.isEnabled())
            dispose();
    }
}
