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

import com.zapolnov.buildsystem.gui.widgets.InvisibleFrame;
import com.zapolnov.buildsystem.utility.StringUtils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;

/** Dialog displaying fatal error messages. */
public class FatalErrorDialog extends JDialog
{
    public static final String TITLE = "Fatal Error";
    public static final String CLOSE_BUTTON_TITLE = "Close";
    public static final String DETAILS_BUTTON_TITLE_1 = "Show Details >>";
    public static final String DETAILS_BUTTON_TITLE_2 = "<< Hide Details";

    private JPanel contentPanel;
    private JPanel buttonPanel;
    private JPanel buttonContainer;
    private JPanel messagePanel;
    private JScrollPane scrollArea;
    private JButton closeButton;
    private JButton detailsButton;

    /**
     * Constructor.
     * @param parent Parent dialog.
     * @param exception Exception.
     */
    public FatalErrorDialog(JDialog parent, Throwable exception)
    {
        super(parent, TITLE, true);
        init(StringUtils.getShortExceptionMessage(exception), StringUtils.getDetailedExceptionMessage(exception));
    }

    /**
     * Constructor.
     * @param parent Parent frame.
     * @param exception Exception.
     */
    public FatalErrorDialog(Frame parent, Throwable exception)
    {
        super(parent, TITLE, true);
        init(StringUtils.getShortExceptionMessage(exception), StringUtils.getDetailedExceptionMessage(exception));
    }

    /**
     * Initializes the dialog.
     * @param shortMessage Short description of the problem.
     * @param longMessage Detailed description of the problem.
     */
    private void init(String shortMessage, String longMessage)
    {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);

        messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        contentPanel.add(messagePanel, BorderLayout.CENTER);

        JTextPane messageText = new JTextPane();
        messageText.setText(shortMessage);
        messageText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        messageText.setEditable(false);
        messageText.setAlignmentX(Container.CENTER_ALIGNMENT);
        messageText.setBackground(UIManager.getColor("Label.background"));
        messagePanel.add(messageText, BorderLayout.PAGE_START);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);
        textArea.append(longMessage);
        textArea.setBackground(UIManager.getColor("Label.background"));
        textArea.setRows(30);
        textArea.setCaretPosition(0);

        scrollArea = new JScrollPane(textArea);
        scrollArea.setVisible(false);
        scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        buttonContainer = new JPanel(new GridBagLayout());
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        contentPanel.add(buttonContainer, BorderLayout.PAGE_END);

        closeButton = new JButton(CLOSE_BUTTON_TITLE);
        closeButton.addActionListener(e -> dispose());

        detailsButton = new JButton(DETAILS_BUTTON_TITLE_1);
        detailsButton.addActionListener(e -> setDetailsVisible(!scrollArea.isVisible()));

        createButtonPanel(true);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Shows or hides the "Show details" button.
     * @param visible Set to `true` to make the "Show details" button visible or to `false` to make it hidden.
     */
    public void setDetailsButtonVisible(boolean visible)
    {
        createButtonPanel(visible);
        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Shows or hides detailed message.
     * @param visible Set to `true` to show details message or to `false` to hide details message.
     */
    public void setDetailsVisible(boolean visible)
    {
        contentPanel.remove(scrollArea);
        contentPanel.remove(messagePanel);

        if (!visible)
            contentPanel.add(messagePanel, BorderLayout.CENTER);
        else
            contentPanel.add(scrollArea, BorderLayout.CENTER);

        messagePanel.setVisible(!visible);
        scrollArea.setVisible(visible);

        detailsButton.setText(visible ? DETAILS_BUTTON_TITLE_2 : DETAILS_BUTTON_TITLE_1);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Creates the button panel.
     * @param detailsButtonVisible Set to `true` to include the "Show details" button.
     */
    private void createButtonPanel(boolean detailsButtonVisible)
    {
        if (buttonPanel != null)
            buttonPanel.removeAll();

        buttonPanel = new JPanel(new GridLayout(1, detailsButtonVisible ? 2 : 1, 5, 0));
        buttonContainer.add(buttonPanel);

        buttonPanel.add(closeButton);
        if (detailsButtonVisible)
            buttonPanel.add(detailsButton);
    }

    /**
     * Displays this dialog.
     * Exits the application with code 1 after this dialog is closed.
     * @param exception Exception.
     */
    public static void run(Throwable exception)
    {
        JFrame invisibleFrame = new InvisibleFrame(TITLE);
        try {
            FatalErrorDialog mainDialog = new FatalErrorDialog(invisibleFrame, exception);
            mainDialog.setVisible(true);
        } finally {
            invisibleFrame.dispose();
        }
        System.exit(1);
    }
}
