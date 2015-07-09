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

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/** A panel with horizontally centered buttons. */
public class ButtonPanel extends JPanel
{
    private final JPanel buttonPanel;

    /**
     * Constructor.
     * @param buttonCount Number of buttons.
     */
    public ButtonPanel(int buttonCount)
    {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        buttonPanel = new JPanel(new GridLayout(1, buttonCount, 5, 0));
        add(buttonPanel);
    }

    /**
     * Adds button to the panel.
     * @param title Title of the button.
     * @param handler Button click handler.
     */
    public JButton addButton(String title, Runnable handler)
    {
        JButton button = new JButton(title);
        button.addActionListener(e -> handler.run());
        buttonPanel.add(button);
        return button;
    }
}
