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
package com.zapolnov.zbt.gui;

import com.zapolnov.zbt.generators.Generator;
import com.zapolnov.zbt.project.Project;
import com.zapolnov.zbt.utility.Database;
import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class MainDialog extends JDialog
{
    public static final String TITLE = "Generate Project Files";
    public static final String BUTTON_TITLE = "Generate";

    private final Project project;
    private final ProjectConfigurationPanel projectConfigurationPanel;

    public MainDialog(JFrame parent, Project project, Generator generator, Map<String, String> options)
    {
        super(parent, TITLE, true);

        this.project = project;

        //setPreferredSize(new Dimension(400, 400));
        //setResizable(false);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);

        projectConfigurationPanel = new ProjectConfigurationPanel(project, generator, options);
        projectConfigurationPanel.addChangeListener(this::pack);

        JScrollPane scrollArea = new JScrollPane(projectConfigurationPanel);
        scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollArea, BorderLayout.PAGE_START);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        contentPanel.add(buttonPanel, BorderLayout.PAGE_END);

        JButton button = new JButton(BUTTON_TITLE);
        button.addActionListener(e -> generateProject());
        buttonPanel.add(button, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void generateProject()
    {
        try {
            Generator generator = projectConfigurationPanel.selectedGenerator();
            Map<String, String> options = projectConfigurationPanel.selectedOptions();

            if (generator == null) {
                JOptionPane messageBox = new JOptionPane("Please select valid generator.", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = messageBox.createDialog(this, "Error");
                dialog.setVisible(true);
                return;
            }

            project.database().setOption(Database.OPTION_GENERATOR_NAME, generator.name());
            for (Map.Entry<String, String> option : options.entrySet()) {
                String key = String.format(Database.PROJECT_OPTION_FORMAT, option.getKey());
                project.database().setOption(key, option.getValue());
            }

            project.database().commit();
            project.build(generator, options);
        } catch (Throwable t) {
            FatalErrorDialog dialog = new FatalErrorDialog(this, t);
            dialog.setVisible(true);
        }
    }

    public static void run(Project project, Generator generator, Map<String, String> options)
    {
        JFrame dummyFrame = new DummyFrame(TITLE);
        MainDialog mainDialog = new MainDialog(dummyFrame, project, generator, options);
        mainDialog.setVisible(true);
    }
}
