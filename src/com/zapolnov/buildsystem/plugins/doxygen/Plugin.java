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
package com.zapolnov.buildsystem.plugins.doxygen;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.plugins.AbstractPlugin;
import com.zapolnov.buildsystem.project.ProjectVisitor;
import com.zapolnov.buildsystem.project.directives.SourceDirectoriesDirective;
import com.zapolnov.buildsystem.project.directives.SourceFilesDirective;
import com.zapolnov.buildsystem.project.directives.TargetPlatformSelectorDirective;
import com.zapolnov.buildsystem.utility.FileBuilder;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.StringUtils;
import com.zapolnov.buildsystem.utility.SystemUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Plugin that generates the Doxyfile.inc file. */
@SuppressWarnings("unused") public class Plugin extends AbstractPlugin
{
    public static final String[] EXTENSIONS = new String[]{ ".dox" };
    public static final String DOXYFILE_INC = "Doxyfile.inc";

    @Override public void postGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
        List<File> headerFiles = new ArrayList<>();

        projectBuilder.project.scope.visit(new ProjectVisitor() {
            @Override public void visitSourceDirectories(SourceDirectoriesDirective directive) {
                directive.visitFiles(this);
            }
            @Override public void visitSourceFiles(SourceFilesDirective directive) {
                if (!directive.thirdparty) {
                    for (File file : directive.sourceFiles()) {
                        if (FileUtils.isHeaderFile(file) || StringUtils.fileHasExtension(file, EXTENSIONS))
                            headerFiles.add(file);
                    }
                }
            }
            @Override public boolean visitTargetPlatformSelector(TargetPlatformSelectorDirective directive) {
                return directive.targetPlatform == projectBuilder.generator().targetPlatform();
            }
        });

        FileBuilder builder = new FileBuilder(projectBuilder.outputDirectory(), DOXYFILE_INC);
        for (File file : headerFiles) {
            String path = FileUtils.getRelativePath(projectBuilder.project.directory, file);
            if (SystemUtils.IS_WINDOWS)
                path = path.replace('\\', '/');
            builder.append(String.format("INPUT += %s\n", path));
        }
        builder.commit(projectBuilder.database);
    }
}
