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
package com.zapolnov.buildsystem.build.cmake;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.project.Project;
import com.zapolnov.buildsystem.project.ProjectVisitor;
import com.zapolnov.buildsystem.project.directives.DefineDirective;
import com.zapolnov.buildsystem.project.directives.HeaderPathsDirective;
import com.zapolnov.buildsystem.project.directives.ImportDirective;
import com.zapolnov.buildsystem.project.directives.SourceDirectoriesDirective;
import com.zapolnov.buildsystem.project.directives.SourceFilesDirective;
import com.zapolnov.buildsystem.project.directives.TargetNameDirective;
import com.zapolnov.buildsystem.utility.FileBuilder;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.SystemUtils;
import com.zapolnov.buildsystem.utility.Template;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Utilities for CMake-based generators. */
public class CMakeGeneratorUtilities
{
    /** Template for root CMakeLists.txt. */
    private static final Template rootTemplate;
    /** Template for CMakeLists.txt in subdirectory 'src'. */
    private static final Template srcTemplate;

    /*

    private String targetName;
    private File outputDirectory;
    private Project project;
    private Map<String, String> defines;
    private List<File> sourceFiles;
    private List<File> headerFiles;
    private List<File> thirdPartySourceFiles;
    private List<File> thirdPartyHeaderFiles;
    private List<File> headerPaths;
    private List<File> thirdPartyHeaderPaths;
    private List<File> projectFiles;
    private boolean useQt5;
    private boolean useOpenGL;
    private JComboBox<String> buildToolCombo;
    private JComboBox<String> buildTypeCombo;
    private BuildTool selectedBuildTool;
    private String selectedBuildType;
    private String qt5Path;
    private String cmakeExecutable;

    public CMakeGeneratorUtilities() throws IOException
    {
    }


    @Override public void generate(final Project project, CommandInvoker.Printer printer, boolean build)
        throws Throwable
    {
        try {
            this.project = project;
            targetName = "App";
            defines = new LinkedHashMap<>();
            sourceFiles = new ArrayList<>();
            headerFiles = new ArrayList<>();
            thirdPartySourceFiles = new ArrayList<>();
            thirdPartyHeaderFiles = new ArrayList<>();
            headerPaths = new ArrayList<>();
            thirdPartyHeaderPaths = new ArrayList<>();
            projectFiles = new ArrayList<>();
            useQt5 = false;
            useOpenGL = false;

            // Create output directory

            String outputDirectoryName;
            if (selectedBuildTool == null) {
                outputDirectoryName = String.format("%s/%s", ID, "default");
            } else {
                outputDirectoryName = String.format("%s/%s", ID, selectedBuildTool.directoryName);
                if (selectedBuildType != null)
                    outputDirectoryName = String.format("%s/%s", outputDirectoryName, selectedBuildType);
            }

            outputDirectory = new File(project.outputDirectory(), outputDirectoryName);
            Utility.ensureDirectoryExists(outputDirectory);

            // Collect information from the project file

            project.directives().visitDirectives(new ProjectDirectiveVisitor(project, outputDirectory, this) {
                @Override protected void visitDefine(String name, String value) {
                    defines.put(name, value != null ? value : "1");
                }
                @Override protected void visitSourceFile(File file) {
                    if (isSourceFile(file))
                        sourceFiles.add(Utility.getCanonicalFile(file));
                    else if (isHeaderFile(file))
                        headerFiles.add(Utility.getCanonicalFile(file));
                }
                @Override protected void visitThirdPartySourceFile(File file) {
                    if (isSourceFile(file))
                        thirdPartySourceFiles.add(Utility.getCanonicalFile(file));
                    else if (isHeaderFile(file))
                        thirdPartyHeaderFiles.add(Utility.getCanonicalFile(file));
                }
                @Override protected void visitHeaderPath(File directory) {
                    headerPaths.add(Utility.getCanonicalFile(directory));
                }
                @Override protected void visitThirdPartyHeaderPath(File directory) {
                    thirdPartyHeaderPaths.add(Utility.getCanonicalFile(directory));
                }
                @Override public void visitTargetName(TargetNameDirective directive) {
                    targetName = directive.name();
                }
                @Override public void visitProjectFile(File file) {
                    projectFiles.add(Utility.getCanonicalFile(file));
                }
                @Override public void visitCMakeUseQt5Directive(CMakeUseQt5Directive directive) {
                    useQt5 = directive.value();
                }
                @Override public void visitCMakeUseOpenGLDirective(CMakeUseOpenGLDirective directive) {
                    useOpenGL = directive.value();
                }
            });

            // Try to find Qt5 installation directory

            if (qt5Path == null && selectedBuildTool != null && useQt5)
                qt5Path = findQt5InstallationDirectory(selectedBuildTool);

            // Generate files

            writeCMakeLists(selectedBuildTool, selectedBuildType, qt5Path);

            // Run CMake

            if (cmakeExecutable != null && selectedBuildTool != null) {

                // Run CMake to generate project files

                List<String> cmakeCommand = new ArrayList<>();
                cmakeCommand.add(cmakeExecutable);
                cmakeCommand.add("-G");
                cmakeCommand.add(selectedBuildTool.cmakeGenerator);
                if (selectedBuildTool.acceptsBuildType)
                    cmakeCommand.add(String.format("-DCMAKE_BUILD_TYPE=%s", selectedBuildType));
                if (qt5Path != null)
                    cmakeCommand.add(String.format("-DCMAKE_PREFIX_PATH=%s", qt5Path));
                cmakeCommand.add("-DCMAKE_INSTALL_PREFIX=_INSTALL_");
                for (String define : selectedBuildTool.defines)
                    cmakeCommand.add(String.format("-D%s", define));
                cmakeCommand.add(".");

                CommandInvoker commandInvoker = new CommandInvoker(outputDirectory, printer);
                commandInvoker.invoke(cmakeCommand.toArray(new String[cmakeCommand.size()]));

                // Run CMake to build project

                if (build) {
                    cmakeCommand = new ArrayList<>();
                    cmakeCommand.add(cmakeExecutable);
                    cmakeCommand.add("--build");
                    cmakeCommand.add(".");

                    commandInvoker = new CommandInvoker(outputDirectory, printer);
                    commandInvoker.invoke(cmakeCommand.toArray(new String[cmakeCommand.size()]));
                }

                // Open the generated project file

                if (!Main.batchMode()) {
                    File fileToOpen = outputDirectory;

                    if (!build) {
                        if (Utility.IS_WINDOWS) {
                            File file = new File(outputDirectory, String.format("%s.sln", targetName));
                            if (file.exists() && !file.isDirectory())
                                fileToOpen = file;
                        }
                    }

                    try {
                        Desktop.getDesktop().open(fileToOpen);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                        if (fileToOpen != outputDirectory) {
                            try {
                                Desktop.getDesktop().open(fileToOpen);
                            } catch (IOException ee) {
                                ee.printStackTrace(System.err);
                            }
                        }
                    }
                }
            }
        } finally {
            targetName = null;
            outputDirectory = null;
            defines = null;
            sourceFiles = null;
            headerFiles = null;
            thirdPartySourceFiles = null;
            thirdPartyHeaderFiles = null;
            headerPaths = null;
            thirdPartyHeaderPaths = null;
            projectFiles = null;
            this.project = null;
        }
    }
    */

    public static void writeCMakeLists(ProjectBuilder projectBuilder) throws NoSuchAlgorithmException, IOException
    {
        final File srcDir = new File(projectBuilder.outputDirectory, "src");
        final String[] targetName = new String[]{ "Project" };
        final List<String> defines = new ArrayList<>();
        final List<File> projectIncludeDirectories = new ArrayList<>();
        final List<File> systemIncludeDirectories = new ArrayList<>();
        final List<File> projectDirectories = new ArrayList<>();
        final List<File> headerFiles = new ArrayList<>();
        final List<File> sourceFiles = new ArrayList<>();
        final List<File> thirdPartyHeaderFiles = new ArrayList<>();
        final List<File> thirdPartySourceFiles = new ArrayList<>();

        projectBuilder.project.scope.visit(new ProjectVisitor() {
            @Override public void visitImport(ImportDirective directive) {
                projectDirectories.add(directive.scope.directory);
                directive.scope.visit(this);
            }
            @Override public void visitTargetName(TargetNameDirective directive) {
                targetName[0] = directive.name;
            }
            @Override public void visitDefine(DefineDirective directive) {
                defines.addAll(directive.defines());
            }
            @Override public void visitHeaderPaths(HeaderPathsDirective directive) {
                if (!directive.thirdparty)
                    projectIncludeDirectories.addAll(directive.headerPaths());
                else
                    systemIncludeDirectories.addAll(directive.headerPaths());
            }
            @Override public void visitSourceDirectories(SourceDirectoriesDirective directive) {
                directive.visitFiles(this);
            }
            @Override public void visitSourceFiles(SourceFilesDirective directive) {
                for (File file : directive.sourceFiles()) {
                    if (FileUtils.isCSourceFile(file) || FileUtils.isCxxSourceFile(file))
                        (directive.thirdparty ? thirdPartySourceFiles : sourceFiles).add(file);
                    else if (FileUtils.isHeaderFile(file))
                        (directive.thirdparty ? thirdPartyHeaderFiles : headerFiles).add(file);
                }
            }
        });

        // Build list of ADD_DEFINITIONS() commands

        StringBuilder definitions = new StringBuilder();
        if (!defines.isEmpty()) {
            definitions.append("add_definitions(\n");
            for (String define : defines) {
                String value = define.replace("\\", "\\\\").replace("\"", "\\\"");
                definitions.append(String.format("    \"-D%s\"\n", cmakeEscape(value)));
            }
            definitions.append(")");
        }

        // Build list of INCLUDE_DIRECTORIES() commands

        StringBuilder includeDirectories = new StringBuilder();
        if (!projectIncludeDirectories.isEmpty()) {
            includeDirectories.append("include_directories(\n");
            for (File directory : projectIncludeDirectories) {
                String relativePath = FileUtils.getRelativePath(srcDir, directory);
                includeDirectories.append(String.format("    \"%s\"\n", cmakeEscapePath(relativePath)));
            }
            includeDirectories.append(")\n");
        }
        if (!systemIncludeDirectories.isEmpty()) {
            includeDirectories.append("include_directories(SYSTEM\n");
            for (File directory : systemIncludeDirectories) {
                String relativePath = FileUtils.getRelativePath(srcDir, directory);
                includeDirectories.append(String.format("    \"%s\"\n", cmakeEscapePath(relativePath)));
            }
            includeDirectories.append(")\n");
        }

        // Build list of project files

        Map<String, List<String>> projectFileGroups = new LinkedHashMap<>();
        StringBuilder projectFileList = new StringBuilder();
        projectFileList.append(String.format("\"%s\"\n", cmakeEscapePath(
            FileUtils.getCanonicalPath(new File(projectBuilder.project.directory, Project.PROJECT_FILE_NAME)))));
        for (File projectDirectory : projectDirectories) {
            List<String> items = new ArrayList<>();
            File projectFile = new File(projectDirectory, Project.PROJECT_FILE_NAME);
            extractSourceFileRelativePaths(projectBuilder.project, projectBuilder.outputDirectory, projectFile,
                items, projectFileGroups);
            projectFileList.append(String.format("        \"%s\"\n", cmakeEscapePath(items.get(0))));
        }

        // Build list of SOURCE_GROUP() commands for source files

        StringBuilder projectFileGroupsString = new StringBuilder();
        for (Map.Entry<String, List<String>> sourceGroup : projectFileGroups.entrySet()) {
            if (!sourceGroup.getValue().isEmpty()) {
                String groupName = sourceGroup.getKey().replace("/", "\\");
                while (groupName.startsWith("..\\"))
                    groupName = groupName.substring(3);
                projectFileGroupsString.append(String.format("source_group(\"%s\" FILES\n", cmakeEscape(groupName)));
                for (String file : sourceGroup.getValue())
                    projectFileGroupsString.append(String.format("    \"%s\"\n", cmakeEscapePath(file)));
                projectFileGroupsString.append(")\n\n");
            }
        }

        // Build list of source files and SOURCE_GROUPs for them

        Map<String, List<String>> sourceGroups = new LinkedHashMap<>();
        List<String> sourcePaths = new ArrayList<>();
        List<String> headerPaths = new ArrayList<>();
        List<String> thirdPartySourcePaths = new ArrayList<>();
        List<String> thirdPartyHeaderPaths = new ArrayList<>();

        enumerateSourceFiles(projectBuilder.project, srcDir, sourceFiles, sourcePaths, sourceGroups);
        enumerateSourceFiles(projectBuilder.project, srcDir, headerFiles, headerPaths, sourceGroups);
        enumerateSourceFiles(projectBuilder.project, srcDir, thirdPartySourceFiles, thirdPartySourcePaths, sourceGroups);
        enumerateSourceFiles(projectBuilder.project, srcDir, thirdPartyHeaderFiles, thirdPartyHeaderPaths, sourceGroups);

        // Write root CMakeLists.txt

        FileBuilder builder = new FileBuilder(projectBuilder.outputDirectory, "CMakeLists.txt");
        writeAutoGeneratedHeader(builder);

        Map<String, String> options = new LinkedHashMap<>();
        options.put("target_name", cmakeEscape(targetName[0]));
        options.put("java_executable", cmakeEscapePath(SystemUtils.getJavaExecutable()));
        options.put("jar", cmakeEscapePath(FileUtils.getRelativePath(projectBuilder.project.directory,
            SystemUtils.getApplicationJarFile())));
        options.put("generator", cmakeEscape(projectBuilder.generator().getClass().getName()));
        options.put("project_directory", cmakeEscapePath(FileUtils.getCanonicalPath(projectBuilder.project.directory)));
        options.put("project_files", projectFileList.toString());
        options.put("source_groups", projectFileGroupsString.toString());
        rootTemplate.emit(builder, options);

        builder.commit(projectBuilder.database);

        // Write src/CMakeLists.txt

        builder = new FileBuilder(srcDir, "CMakeLists.txt");
        writeAutoGeneratedHeader(builder);

        options = new HashMap<>();
        options.put("target_name", cmakeEscape(targetName[0]));
        options.put("defines", definitions.toString());
        options.put("include_directories", includeDirectories.toString());
        srcTemplate.emit(builder, options);

        builder.commit(projectBuilder.database);

        // Write SourceFiles.cmake

        builder = new FileBuilder(srcDir, "SourceFiles.cmake");
        writeAutoGeneratedHeader(builder);
        writeSourcePaths(builder, "source_files", sourcePaths);
        writeSourcePaths(builder, "header_files", headerPaths);
        writeSourcePaths(builder, "third_party_source_files", thirdPartySourcePaths);
        writeSourcePaths(builder, "third_party_header_files", thirdPartyHeaderPaths);
        builder.commit(projectBuilder.database);

        // Write SourceGroups.cmake

        builder = new FileBuilder(srcDir, "SourceGroups.cmake");
        writeAutoGeneratedHeader(builder);

        for (Map.Entry<String, List<String>> sourceGroup : sourceGroups.entrySet()) {
            if (!sourceGroup.getValue().isEmpty()) {
                String groupName = sourceGroup.getKey().replace("/", "\\");
                while (groupName.startsWith("..\\"))
                    groupName = groupName.substring(3);
                builder.append(String.format("source_group(\"%s\" FILES\n", cmakeEscape(groupName)));
                for (String file : sourceGroup.getValue())
                    builder.append(String.format("    \"%s\"\n", cmakeEscapePath(file)));
                builder.append(")\n\n");
            }
        }

        builder.commit(projectBuilder.database);
    }

    /**
     * Writes a header notifying that file has been automatically generated.
     * @param builder File builder.
     */
    private static void writeAutoGeneratedHeader(FileBuilder builder)
    {
        builder.append('\n');
        builder.append("# ------------------------------------------------------\n");
        builder.append("# THIS IS AN AUTOMATICALLY GENERATED FILE. DO NOT EDIT!\n");
        builder.append("# ------------------------------------------------------\n");
        builder.append('\n');
    }

    /**
     * Writes a list of source file paths.
     * @param builder File builder.
     * @param variableName Name of the variable.
     * @param sourcePaths List of source files.
     */
    private static void writeSourcePaths(FileBuilder builder, String variableName, List<String> sourcePaths)
    {
        if (sourcePaths.isEmpty()) {
            builder.append(String.format("set(%s)\n", variableName));
        } else {
            builder.append(String.format("set(%s\n", variableName));
            for (String path : sourcePaths)
                builder.append(String.format("    \"%s\"\n", cmakeEscapePath(path)));
            builder.append(")\n");
        }
    }

    /**
     * Builds a list of relative paths to source files.
     * @param project Project.
     * @param srcDir Directory containing CMakeLists.txt file.
     * @param inFiles List of source files.
     * @param outPaths Output list.
     * @param outGroups Output map of groups.
     */
    private static void enumerateSourceFiles(Project project, File srcDir, List<File> inFiles,
        List<String> outPaths, Map<String, List<String>> outGroups) throws IOException
    {
        for (File source : inFiles)
            extractSourceFileRelativePaths(project, srcDir, FileUtils.getCanonicalFile(source), outPaths, outGroups);
    }

    /**
     * Adds file to the list of output files, sorted by groups.
     * @param project Project
     * @param baseDirectory Base directory of the file.
     * @param file File name.
     * @param outPaths Output list.
     * @param outGroups Output map of groups.
     */
    private static void extractSourceFileRelativePaths(Project project, File baseDirectory, File file,
        List<String> outPaths, Map<String, List<String>> outGroups) throws IOException
    {
        String path = FileUtils.getRelativePath(baseDirectory, file);

        if (outPaths != null)
            outPaths.add(path);

        String sourceGroup = FileUtils.getRelativePath(project.directory, file.getParentFile());
        List<String> list = outGroups.get(sourceGroup);
        if (list == null) {
            list = new ArrayList<>();
            outGroups.put(sourceGroup, list);
        }
        list.add(path);
    }

    /**
     * Escapes the specified string for use in CMake scripts.
     * @param string String to escape.
     * @return Escaped string.
     */
    public static String cmakeEscape(String string)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (ch)
            {
            case '\\': builder.append("\\\\"); break;
            case '"': builder.append("\\\""); break;
            default: builder.append(ch); break;
            }
        }
        return builder.toString();
    }

    /**
     * Escapes the specified path string for use in CMake scripts.
     * @param string String to escape.
     * @return Escaped string.
     */
    public static String cmakeEscapePath(String string)
    {
        if (!SystemUtils.IS_WINDOWS)
            return cmakeEscape(string);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (ch)
            {
            case '\\': builder.append("/"); break;
            case '"': builder.append("\\\""); break;
            default: builder.append(ch); break;
            }
        }
        return builder.toString();
    }

    static {
        try {
            rootTemplate = new Template(CMakeGeneratorUtilities.class.getResourceAsStream("root-CMakeLists.template"));
            srcTemplate = new Template(CMakeGeneratorUtilities.class.getResourceAsStream("src-CMakeLists.template"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
