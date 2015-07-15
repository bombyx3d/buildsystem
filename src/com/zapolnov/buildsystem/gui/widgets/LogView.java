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

import com.zapolnov.buildsystem.utility.Colors;
import com.zapolnov.buildsystem.utility.Log;
import com.zapolnov.buildsystem.utility.LogLevel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/** Logging view. */
public class LogView extends JScrollPane implements Log.Printer
{
    /** Logger pane. */
    private final JTextPane textPane;
    /** Style for error messages. */
    private Style errorStyle;
    /** Style for warning messages. */
    private Style warningStyle;
    /** Style for informational messages. */
    private Style infoStyle;
    /** Style for debug messages. */
    private Style debugStyle;
    /** Style for trace messages. */
    private Style traceStyle;

    /** Constructor. */
    public LogView()
    {
        super(new JTextPane());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        textPane = (JTextPane)getViewport().getView();
        textPane.setBackground(Colors.BLACK);
        textPane.setEditable(false);

        DefaultCaret caret = (DefaultCaret)textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        errorStyle = textPane.addStyle("Error", null);
        StyleConstants.setBackground(errorStyle, Colors.BLACK);
        StyleConstants.setForeground(errorStyle, Colors.RED);

        warningStyle = textPane.addStyle("Warning", null);
        StyleConstants.setBackground(warningStyle, Colors.BLACK);
        StyleConstants.setForeground(warningStyle, Colors.YELLOW);

        infoStyle = textPane.addStyle("Information", null);
        StyleConstants.setBackground(infoStyle, Colors.BLACK);
        StyleConstants.setForeground(infoStyle, Colors.WHITE);

        debugStyle = textPane.addStyle("Debug", null);
        StyleConstants.setBackground(debugStyle, Colors.BLACK);
        StyleConstants.setForeground(debugStyle, Colors.GRAY);

        traceStyle = textPane.addStyle("Trace", null);
        StyleConstants.setBackground(traceStyle, Colors.BLACK);
        StyleConstants.setForeground(traceStyle, Colors.DARK_GRAY);
    }

    @Override public void printLogMessage(LogLevel level, String message)
    {
        final String logMessage = message + '\n';
        SwingUtilities.invokeLater(() -> {
            StyledDocument document = textPane.getStyledDocument();
            try {
                switch (level)
                {
                case ERROR:
                    System.err.println(String.format("[ERROR] %s", message));
                    document.insertString(document.getLength(), logMessage, errorStyle);
                    break;

                case WARNING:
                    System.err.println(String.format("[WARN ] %s", message));
                    document.insertString(document.getLength(), logMessage, warningStyle);
                    break;

                case INFO:
                    System.out.println(String.format("[INFO ] %s", message));
                    document.insertString(document.getLength(), logMessage, infoStyle);
                    break;

                case DEBUG:
                    System.out.println(String.format("[DEBUG] %s", message));
                    document.insertString(document.getLength(), logMessage, debugStyle);
                    break;

                case TRACE:
                    System.out.println(String.format("[TRACE] %s", message));
                    document.insertString(document.getLength(), logMessage, traceStyle);
                    break;
                }
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
