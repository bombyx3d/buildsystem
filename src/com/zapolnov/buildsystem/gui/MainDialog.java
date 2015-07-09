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
import com.zapolnov.buildsystem.gui.widgets.InvisibleFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Main dialog of the GUI application. */
public class MainDialog extends JDialog
{
    public static final String TITLE = "Bombyx3D build system";

    public static final String GENERATE_BUTTON_TITLE = "Generate";
    public static final String BUILD_BUTTON_TITLE = "Build";
    public static final String CLOSE_BUTTON_TITLE = "Exit";

    public static final int PREFERRED_WIDTH = 600;
    public static final int PREFERRED_HEIGHT = 400;

    /**
     * Constructor.
     * @param parent Parent frame.
     */
    public MainDialog(Frame parent)
    {
        super(parent, TITLE, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                Rectangle bounds = e.getComponent().getBounds();
                setPreferredSize(new Dimension(bounds.width, bounds.height));
            }
        });

        LogView logView = new LogView();
        Log.setPrinter(logView);
        getContentPane().add(logView, BorderLayout.CENTER);

        ButtonPanel buttonPanel = new ButtonPanel(3);
        buttonPanel.addButton(GENERATE_BUTTON_TITLE, () -> {
        });
        buttonPanel.addButton(BUILD_BUTTON_TITLE, () -> {
        });
        buttonPanel.addButton(CLOSE_BUTTON_TITLE, this::dispose);
        add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
    }

    /** Displays the main dialog. */
    public static void run()
    {
        JFrame invisibleFrame = new InvisibleFrame(TITLE);
        try {
            MainDialog mainDialog = new MainDialog(invisibleFrame);
            mainDialog.setVisible(true);
        } finally {
            invisibleFrame.dispose();
        }
    }
}
