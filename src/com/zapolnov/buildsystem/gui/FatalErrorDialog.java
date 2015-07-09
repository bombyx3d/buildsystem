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
import com.zapolnov.buildsystem.utility.Log;
import com.zapolnov.zbt.utility.Utility;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/** Dialog displaying fatal error messages. */
public class FatalErrorDialog extends JDialog
{
    public static final String TITLE = "Fatal Error";
    public static final String OK_BUTTON_TITLE = "Close";
    public static final String DETAILS_BUTTON_TITLE_1 = "Show Details >>";
    public static final String DETAILS_BUTTON_TITLE_2 = "<< Hide Details";

    /**
     * Constructor.
     * @param parent Parent dialog.
     * @param exception Exception.
     */
    public FatalErrorDialog(JDialog parent, Throwable exception)
    {
        super(parent, TITLE, true);
        init(Utility.getExceptionMessage(exception), makeMessageForException(exception));
    }

    /**
     * Constructor.
     * @param parent Parent frame.
     * @param exception Exception.
     */
    public FatalErrorDialog(Frame parent, Throwable exception)
    {
        super(parent, TITLE, true);
        init(Utility.getExceptionMessage(exception), makeMessageForException(exception));
    }

    /**
     * Initializes the dialog.
     * @param shortMessage Short description of the problem.
     * @param longMessage Detailed description of the problem.
     */
    private void init(String shortMessage, String longMessage)
    {
        try { Log.error(longMessage); } catch (Throwable ignored) {}

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        contentPanel.add(messagePanel, BorderLayout.CENTER);

        JLabel messageLabel = new JLabel(shortMessage);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);
        textArea.setRows(20);
        textArea.append(longMessage);
        textArea.setCaretPosition(0);

        final JScrollPane scrollArea = new JScrollPane(textArea);
        scrollArea.setVisible(false);
        scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel buttonContainer = new JPanel(new GridBagLayout());
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        contentPanel.add(buttonContainer, BorderLayout.PAGE_END);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonContainer.add(buttonPanel);

        JButton button = new JButton(OK_BUTTON_TITLE);
        button.addActionListener(e ->
            dispose()
        );
        buttonPanel.add(button, BorderLayout.CENTER);

        final JButton detailsButton = new JButton(DETAILS_BUTTON_TITLE_1);
        detailsButton.addActionListener(e -> {
            boolean visible = !scrollArea.isVisible();
            messagePanel.remove(scrollArea);
            contentPanel.remove(messagePanel);
            if (!visible)
                contentPanel.add(messagePanel, BorderLayout.CENTER);
            else {
                contentPanel.add(messagePanel, BorderLayout.PAGE_START);
                contentPanel.add(scrollArea, BorderLayout.CENTER);
            }
            scrollArea.setVisible(visible);
            detailsButton.setText(visible ? DETAILS_BUTTON_TITLE_2 : DETAILS_BUTTON_TITLE_1);
            pack();
            setLocationRelativeTo(getParent());
        });
        buttonPanel.add(detailsButton);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Creates detailed description message for exception.
     * @param exception Exception.
     * @return Detailed error message.
     */
    private static String makeMessageForException(Throwable exception)
    {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
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
