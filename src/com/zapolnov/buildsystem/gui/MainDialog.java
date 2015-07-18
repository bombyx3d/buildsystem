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

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.gui.widgets.ButtonPanel;
import com.zapolnov.buildsystem.gui.widgets.FileDialog;
import com.zapolnov.buildsystem.gui.widgets.InvisibleFrame;
import com.zapolnov.buildsystem.gui.widgets.ProjectSettingsPanel;
import com.zapolnov.buildsystem.project.Project;
import com.zapolnov.buildsystem.project.ProjectReader;
import com.zapolnov.buildsystem.utility.Colors;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.Log;
import com.zapolnov.buildsystem.utility.StringUtils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Main dialog of the GUI application. */
public class MainDialog extends JDialog
{
    public static final String TITLE = "Bombyx3D build system";
    public static final String ERROR_DETAILS_TITLE = "Error details";
    public static final String BROWSE_DIALOG_TITLE = "Open project";
    public static final String PROJECT_PATH_LABEL = "Project path:";
    public static final String BROWSE_BUTTON_TITLE = "...";
    public static final String GENERATE_BUTTON_TITLE = "Generate";
    public static final String CLOSE_BUTTON_TITLE = "Exit";
    public static final String REBUILD_CHECKBOX_TITLE = "Perform a full (non-incremental) run";
    public static final String OPEN_PROJECT_CHECKBOX_TITLE = "Open project after successful completion";
    public static final String EXIT_ON_SUCCESS_CHECKBOX_TITLE = "Exit after successful completion";
    public static final String DIRECTORY_DOES_NOT_EXIST_MESSAGE = "Entered path does not exist.";
    public static final String NOT_A_DIRECTORY_MESSAGE = "Entered path does not represent a directory.";
    public static final String PROJECT_NOT_FOUND_MESSAGE = "Entered directory does not contain a project file.";
    public static final String UNABLE_TO_LOAD_PROJECT_MESSAGE = "Unable to load project file.";
    public static final String RELOAD_BUTTON_TITLE = "Reload";
    public static final String DETAILS_BUTTON_TITLE = "Details...";

    private static final String PREF_PROJECT_DIRECTORY = "ProjectDirectory";

    public static final int PREFERRED_WIDTH = 600;
    public static final int PREFERRED_HEIGHT = 600;

    private final Preferences preferences;
    private final JTextField projectPathEdit;
    private final JPanel projectSettingsContainer;
    private final JCheckBox rebuildCheckBox;
    private final JCheckBox openProjectCheckBox;
    private final JCheckBox exitOnSuccessCheckBox;
    private final JButton generateButton;
    private File projectDirectory;
    private Project project;
    private ProjectBuilder projectBuilder;
    private ProjectSettingsPanel projectSettingsPanel;

    /**
     * Constructor.
     * @param parent Parent frame.
     */
    public MainDialog(Frame parent)
    {
        super(parent, TITLE, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        preferences = Preferences.userNodeForPackage(MainDialog.class);

        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                Rectangle bounds = e.getComponent().getBounds();
                setPreferredSize(new Dimension(bounds.width, bounds.height));
            }
        });

        projectDirectory = FileUtils.getCanonicalFile(new File("."));
        if (!ProjectReader.isValidProjectDirectory(projectDirectory)) {
            String directory = preferences.get(PREF_PROJECT_DIRECTORY, null);
            if (directory != null) {
                File path = new File(directory);
                if (ProjectReader.isValidProjectDirectory(path))
                    projectDirectory = path;
            }
        }

        Box contentBox = Box.createVerticalBox();
        contentBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentBox, BorderLayout.CENTER);

        JLabel projectPathLabel = new JLabel(PROJECT_PATH_LABEL);
        projectPathLabel.setAlignmentX(Container.LEFT_ALIGNMENT);
        contentBox.add(projectPathLabel);

        JPanel projectPathPanel = new JPanel();
        projectPathPanel.setLayout(new BoxLayout(projectPathPanel, BoxLayout.LINE_AXIS));
        projectPathPanel.setAlignmentX(Container.LEFT_ALIGNMENT);
        contentBox.add(projectPathPanel);

        projectPathEdit = new JTextField();
        projectPathEdit.setMaximumSize(new Dimension(Integer.MAX_VALUE, projectPathEdit.getPreferredSize().height));
        projectPathEdit.setText(projectDirectory.getPath());
        projectPathEdit.selectAll();
        projectPathEdit.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                projectDirectory = FileUtils.getCanonicalFile(new File(projectPathEdit.getText()));
                reloadProject();
            }
            @Override public void removeUpdate(DocumentEvent e) {
                projectDirectory = FileUtils.getCanonicalFile(new File(projectPathEdit.getText()));
                reloadProject();
            }
            @Override public void changedUpdate(DocumentEvent e) {
                projectDirectory = FileUtils.getCanonicalFile(new File(projectPathEdit.getText()));
                reloadProject();
            }
        });
        projectPathPanel.add(projectPathEdit);

        JButton projectBrowseButton = new JButton(BROWSE_BUTTON_TITLE);
        projectBrowseButton.addActionListener(e -> browseProject());
        projectPathPanel.add(projectBrowseButton);

        projectSettingsContainer = new JPanel();
        projectSettingsContainer.setLayout(new BorderLayout());

        JScrollPane scrollArea = new JScrollPane(projectSettingsContainer);
        scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollArea.setAlignmentX(Container.LEFT_ALIGNMENT);
        contentBox.add(scrollArea);

        rebuildCheckBox = new JCheckBox(REBUILD_CHECKBOX_TITLE);
        rebuildCheckBox.setEnabled(false);  // FIXME
        contentBox.add(rebuildCheckBox);

        openProjectCheckBox = new JCheckBox(OPEN_PROJECT_CHECKBOX_TITLE);
        openProjectCheckBox.setEnabled(false);  // FIXME
        contentBox.add(openProjectCheckBox);

        exitOnSuccessCheckBox = new JCheckBox(EXIT_ON_SUCCESS_CHECKBOX_TITLE);
        exitOnSuccessCheckBox.setEnabled(false);  // FIXME
        contentBox.add(exitOnSuccessCheckBox);

        ButtonPanel buttonPanel = new ButtonPanel(3);
        generateButton = buttonPanel.addButton(GENERATE_BUTTON_TITLE, () -> generate());
        buttonPanel.addButton(CLOSE_BUTTON_TITLE, this::dispose);
        add(buttonPanel, BorderLayout.PAGE_END);

        reloadProject();

        pack();
        setLocationRelativeTo(null);
    }

    /** Displays the "Browse project" dialog. */
    private void browseProject()
    {
        List<FileFilter> filters = new ArrayList<>();
        filters.add(new FileNameExtensionFilter("Bombyx3D project file", "yml"));

        File directory = new File(projectPathEdit.getText());
        File selectedFile = FileDialog.chooseOpenFile(this, BROWSE_DIALOG_TITLE, directory, filters);
        if (selectedFile != null) {
            projectDirectory = selectedFile.getParentFile();
            projectPathEdit.setText(projectDirectory.getPath());
            projectPathEdit.selectAll();
        }
    }

    /** Generates the project. */
    private void generate()
    {
        BuildDialog buildDialog = new BuildDialog(this);

        try {
            projectSettingsPanel.validateAndSaveSettings();

            new Thread(() -> {
                try {
                    projectBuilder.run();

                    Log.info("**** SUCCESSFUL COMPLETION ***");
                    SwingUtilities.invokeAndWait(() -> buildDialog.setCloseButtonEnabled(true));
                } catch (Throwable t) {
                    Log.error(StringUtils.getDetailedExceptionMessage(t));
                    SwingUtilities.invokeLater(() -> {
                        buildDialog.setCloseButtonEnabled(true);
                        FatalErrorDialog errorDialog = new FatalErrorDialog(buildDialog, t);
                        errorDialog.setVisible(true);
                        buildDialog.setVisible(false);
                    });
                }
            }).start();

            buildDialog.setVisible(true);
        } catch (Throwable t) {
            Log.error(StringUtils.getDetailedExceptionMessage(t));
            buildDialog.setCloseButtonEnabled(true);
            FatalErrorDialog errorDialog = new FatalErrorDialog(buildDialog, t);
            errorDialog.setVisible(true);
            buildDialog.setVisible(false);
        }
    }

    /** Loads project file from disk and re-initializes the GUI. */
    private void reloadProject()
    {
        unloadProject();

        if (!projectDirectory.exists()) {
            displayError(DIRECTORY_DOES_NOT_EXIST_MESSAGE, null);
            return;
        }

        if (!projectDirectory.isDirectory()) {
            displayError(NOT_A_DIRECTORY_MESSAGE, null);
            return;
        }

        if (!ProjectReader.isValidProjectDirectory(projectDirectory)) {
            displayError(PROJECT_NOT_FOUND_MESSAGE, null);
            return;
        }

        try {
            project = ProjectReader.read(projectDirectory);
            projectBuilder = new ProjectBuilder(project);
            projectSettingsPanel = new ProjectSettingsPanel(projectBuilder);
            projectSettingsContainer.add(projectSettingsPanel, BorderLayout.PAGE_START);
            projectSettingsContainer.add(new JPanel(), BorderLayout.CENTER);
        } catch (Throwable t) {
            displayError(UNABLE_TO_LOAD_PROJECT_MESSAGE, t);
            return;
        }

        preferences.put(PREF_PROJECT_DIRECTORY, projectDirectory.toString());
        try { preferences.sync(); } catch (BackingStoreException e) { e.printStackTrace(); }

        generateButton.setEnabled(true);

        pack();
    }

    /** Unloads the currently loaded project. */
    private void unloadProject()
    {
        if (projectBuilder != null) {
            projectBuilder.database.close();
            projectBuilder = null;
        }

        if (projectSettingsPanel != null) {
            Container container = projectSettingsPanel.getParent();
            if (container != null)
                container.remove(projectSettingsPanel);
            projectSettingsPanel = null;
        }

        projectSettingsContainer.removeAll();
        generateButton.setEnabled(false);

        project = null;
    }

    /**
     * Displays error message in the body of the dialog.
     * @param message Error message.
     * @param exception Exception that caused error (could be null).
     */
    private void displayError(String message, final Throwable exception)
    {
        unloadProject();

        JLabel errorLabel = new JLabel(message);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setForeground(Colors.RED);

        if (exception == null)
            projectSettingsContainer.add(errorLabel, BorderLayout.CENTER);
        else {
            System.err.println(StringUtils.getDetailedExceptionMessage(exception));

            JPanel errorPanel = new JPanel();
            errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.PAGE_AXIS));

            errorLabel.setAlignmentX(Container.CENTER_ALIGNMENT);
            errorPanel.add(errorLabel);

            JTextPane messageText = new JTextPane();
            messageText.setText(StringUtils.getShortExceptionMessage(exception));
            messageText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            messageText.setEditable(false);
            messageText.setAlignmentX(Container.CENTER_ALIGNMENT);
            messageText.setBackground(UIManager.getColor("Label.background"));
            messageText.setForeground(Colors.RED);

            JPanel messageTextPanel = new JPanel();
            messageTextPanel.add(messageText);
            errorPanel.add(messageTextPanel);

            errorPanel.add(Box.createVerticalStrut(10));

            JPanel buttonsPanel = new JPanel(new GridBagLayout());
            buttonsPanel.setAlignmentX(Container.CENTER_ALIGNMENT);
            errorPanel.add(buttonsPanel);

            JButton reloadButton = new JButton(RELOAD_BUTTON_TITLE);
            reloadButton.addActionListener(e -> {
                reloadProject();
            });
            buttonsPanel.add(reloadButton);

            JButton detailsButton = new JButton(DETAILS_BUTTON_TITLE);
            detailsButton.addActionListener(e -> {
                FatalErrorDialog dialog = new FatalErrorDialog(this, exception);
                dialog.setTitle(ERROR_DETAILS_TITLE);
                dialog.setDetailsVisible(true);
                dialog.setDetailsButtonVisible(false);
                dialog.setVisible(true);
            });
            buttonsPanel.add(detailsButton);

            JPanel errorContainer = new JPanel(new GridBagLayout());
            errorContainer.add(errorPanel);

            projectSettingsContainer.add(errorContainer, BorderLayout.CENTER);
        }

        pack();
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
