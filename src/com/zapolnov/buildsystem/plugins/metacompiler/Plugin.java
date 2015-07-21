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
package com.zapolnov.buildsystem.plugins.metacompiler;

import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.plugins.AbstractPlugin;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxAstVisitor;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxNamespace;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
import com.zapolnov.buildsystem.project.ProjectVisitor;
import com.zapolnov.buildsystem.project.directives.SourceDirectoriesDirective;
import com.zapolnov.buildsystem.project.directives.SourceFilesDirective;
import com.zapolnov.buildsystem.project.directives.TargetPlatformSelectorDirective;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Plugin that preprocesses source files and automatically generates some code. */
@SuppressWarnings("unused") public class Plugin extends AbstractPlugin
{
    /** Our "virtual" directive injected into the project file. */
    private final MetaCompilerSourceFilesDirective directive = new MetaCompilerSourceFilesDirective();

    @Override public void preBuild(ProjectBuilder projectBuilder) throws Throwable
    {
        projectBuilder.project.scope.addDirective(directive);

        final List<CxxTranslationUnit> scanResults = new ArrayList<>();
        projectBuilder.project.scope.visit(new ProjectVisitor() {
            @Override public void visitSourceDirectories(SourceDirectoriesDirective directive) {
                directive.visitFiles(this);
            }
            @Override public void visitSourceFiles(SourceFilesDirective directive) {
                if (directive.thirdparty)
                    return;
                for (File file : directive.sourceFiles()) {
                    if (FileUtils.isHeaderFile(file) || FileUtils.isCSourceFile(file) || FileUtils.isCxxSourceFile(file)) {
                        try {
                            scanResults.add(projectBuilder.parseFile(file, new CxxAnalyzer()).syntaxTree());
                        } catch (Throwable t) {
                            throw new RuntimeException(String.format("Unable to parse file \"%s\".",
                                FileUtils.getCanonicalPath(file)), t);
                        }
                    }
                }
            }
            @Override public boolean visitTargetPlatformSelector(TargetPlatformSelectorDirective directive) {
                return directive.targetPlatform == projectBuilder.generator().targetPlatform();
            }
        });

        for (CxxTranslationUnit translationUnit : scanResults) {
            translationUnit.visit(new CxxAstVisitor() {
                final Stack<String> scopeStack = new Stack<>();

                {
                    scopeStack.push("");
                }

                @Override public void enterNamespace(CxxNamespace namespace) {
                    if (namespace.name == null)
                        scopeStack.push(scopeStack.peek());
                    else {
                        // FIXME
                        Log.trace(String.format("**************** namespace %s", scopeStack.peek() + namespace.name.text));
                        scopeStack.push(scopeStack.peek() + namespace.name.text + "::");
                    }
                }
                @Override public void leaveNamespace(CxxNamespace namespace) {
                    scopeStack.pop();
                }
                @Override public void enterClass(CxxClass cxxClass) {
                    // FIXME
                    Log.trace(String.format("**************** class %s", scopeStack.peek() + cxxClass.name.text));
                    scopeStack.push(scopeStack.peek() + cxxClass.name.text + "::");
                }
                @Override public void leaveClass(CxxClass cxxClass) {
                    scopeStack.pop();
                }
            });
        }
    }

    @Override public void preGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    @Override public void postGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }
}
