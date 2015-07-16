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

import com.zapolnov.buildsystem.build.Generator;
import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.build.TargetPlatform;
import com.zapolnov.buildsystem.utility.Database;
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** An UI panel with project settings. */
public final class ProjectSettingsPanel extends JPanel
{
    public final String TARGET_PLATFORM_LABEL = "Target platform:";
    public final String PROJECT_FILE_FORMAT_LABEL = "Project file format:";

    /** Project builder. */
    private final ProjectBuilder projectBuilder;
    /** Target selection combo. */
    private final ComboBox<TargetPlatform> targetPlatformCombo;
    /** Label of the generator selection combo. */
    private final JLabel generatorLabel;
    /** Generator selection combo. */
    private final ComboBox<Generator> generatorCombo;

    /**
     * Constructor.
     * @param projectBuilder Project builder.
     */
    public ProjectSettingsPanel(ProjectBuilder projectBuilder)
    {
        this.projectBuilder = projectBuilder;
        setLayout(new BorderLayout());

        Box contentBox = Box.createVerticalBox();
        contentBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(contentBox, BorderLayout.CENTER);

        JLabel targetPlatformLabel = new JLabel(TARGET_PLATFORM_LABEL);
        targetPlatformLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        targetPlatformLabel.setAlignmentX(Container.LEFT_ALIGNMENT);
        contentBox.add(targetPlatformLabel);

        targetPlatformCombo = new ComboBox<>();
        targetPlatformCombo.setAlignmentX(Container.LEFT_ALIGNMENT);
        for (TargetPlatform platform : TargetPlatform.values())
            targetPlatformCombo.addItem(platform.name, platform);
        targetPlatformCombo.addListener(this::onTargetPlatformChanged);
        contentBox.add(targetPlatformCombo);

        generatorLabel = new JLabel(PROJECT_FILE_FORMAT_LABEL);
        generatorLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        generatorLabel.setAlignmentX(Container.LEFT_ALIGNMENT);
        contentBox.add(generatorLabel);

        generatorCombo = new ComboBox<>();
        generatorCombo.setAlignmentX(Container.LEFT_ALIGNMENT);
        generatorCombo.addListener(this::onGeneratorChanged);
        contentBox.add(generatorCombo);

        String value = projectBuilder.database.getOption(Database.OPTION_TARGET_PLATFORM);
        if (value == null) {
            targetPlatformCombo.setSelectedValue(null);
        } else {
            try {
                targetPlatformCombo.setSelectedValue(TargetPlatform.valueOf(value));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                targetPlatformCombo.setSelectedValue(null);
            }
        }

        String generatorName = projectBuilder.database.getOption(Database.OPTION_GENERATOR_NAME);
        if (generatorName == null) {
            generatorCombo.setSelectedIndex(-1);
        } else {
            try {
                generatorCombo.setSelectedItem(generatorName);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                generatorCombo.setSelectedIndex(-1);
            }
        }
    }

    /**
     * Sets target platform.
     * @param platform Target platform.
     */
    public void onTargetPlatformChanged(TargetPlatform platform)
    {
        generatorLabel.setVisible(platform != null);
        generatorCombo.setVisible(platform != null);

        generatorCombo.removeAllItems();
        if (platform != null) {
            for (Map.Entry<String, Generator> item : platform.generatorFactory.generators().entrySet())
                generatorCombo.addItem(item.getKey(), item.getValue());
        }
    }

    /**
     * Sets project file format.
     * @param format Project file format.
     */
    public void onGeneratorChanged(Generator format)
    {
    }

    /** Validates options selected by the user and saves them to the database. */
    public void validateAndSaveSettings() throws Throwable
    {
        try {
            TargetPlatform targetPlatform = targetPlatformCombo.getSelectedValue();
            if (targetPlatform == null) {
                targetPlatformCombo.requestFocusInWindow();
                throw new RuntimeException("Please select target platform.");
            }

            Generator generator = generatorCombo.getSelectedValue();
            if (generator == null) {
                generatorCombo.requestFocusInWindow();
                throw new RuntimeException("Please select project file format.");
            }

            projectBuilder.database.setOption(Database.OPTION_TARGET_PLATFORM, targetPlatform.name());
            projectBuilder.database.setOption(Database.OPTION_GENERATOR_NAME, generatorCombo.getSelectedItem());

            projectBuilder.setGenerator(generator);
            projectBuilder.database.commit();
        } catch (Throwable t) {
            projectBuilder.database.rollbackSafe();
            throw t;
        }
    }
}
