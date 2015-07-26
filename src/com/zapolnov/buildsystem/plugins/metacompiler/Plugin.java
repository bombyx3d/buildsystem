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
import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxParser;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxFullyQualifiedName;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxNamespace;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxParentClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
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
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/** Plugin that preprocesses source files and automatically generates some code. */
@SuppressWarnings("unused") public class Plugin extends AbstractPlugin
{
    /** Name of the generated file. */
    public final String GENERATED_FILE_NAME = "src/meta.cpp";

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
                    if (FileUtils.isHeaderFile(file)) {
                        try {
                            scanResults.add(projectBuilder.parseFile(file, new CxxAnalyzer()).syntaxTree());
                        } catch (CxxParser.Error e) {
                            throw e;
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

        Set<String> includes = new TreeSet<>();
        Map<String, String> typeIDs = new TreeMap<>();
        StringBuilder queryInterfaceMethods = new StringBuilder();

        for (CxxTranslationUnit translationUnit : scanResults) {
            translationUnit.visit(new CxxAstVisitor() {
                final Stack<CxxFullyQualifiedName> scopeStack = new Stack<>();

                {
                    scopeStack.push(new CxxFullyQualifiedName(null, ""));
                }

                @Override public void enterNamespace(CxxNamespace namespace) {
                    if (namespace.name == null)
                        scopeStack.push(scopeStack.peek());
                    else
                        scopeStack.push(scopeStack.peek().mergeWith(namespace.name));
                }
                @Override public void leaveNamespace(CxxNamespace namespace) {
                    scopeStack.pop();
                }
                @Override public void enterClass(CxxClass cxxClass) {
                    boolean custom = false;
                    String className = scopeStack.peek().mergeWith(cxxClass.name).text;
                    switch (cxxClass.type)
                    {
                    case DEFAULT:
                        break;

                    case CUSTOM_IMPLEMENTATION:
                        custom = true;
                    case INTERFACE:
                    case IMPLEMENTATION:
                        includes.add(FileUtils.getCanonicalPath(cxxClass.translationUnit.file));
                        String identifier = typeIDs.get(className);
                        if (identifier == null) {
                            identifier = "g_tid_" + StringUtils.makeIdentifier(StringUtils.makeIdentifier(className));
                            typeIDs.put(className, identifier);
                        }
                        queryInterfaceMethods.append(String.format(
                            "\n" +
                            "void* %s::queryInterface(Engine::TypeID typeID)\n" +
                            "{\n" +
                            "    if (typeID == %s)\n" +
                            "        return this;\n",
                            className, identifier
                        ));
                        if (!cxxClass.parentClasses().isEmpty()) {
                            queryInterfaceMethods.append("    void* p;\n");
                            for (CxxParentClass parent : cxxClass.parentClasses()) {
                                queryInterfaceMethods.append(String.format(
                                    "    p = %s::queryInterface(typeID);\n" +
                                    "    if (p != nullptr)\n" +
                                    "        return p;\n",
                                    parent.name.text
                                ));
                            }
                        }
                        if (custom) {
                            queryInterfaceMethods.append(String.format(
                                "    return %s::_queryCustomInterface(typeID);\n" +
                                "}\n",
                                className));
                        } else {
                            queryInterfaceMethods.append(
                                "    return nullptr;\n" +
                                "}\n");
                        }
                        break;
                    }
                    scopeStack.push(scopeStack.peek().mergeWith(cxxClass.name));
                }
                @Override public void leaveClass(CxxClass cxxClass) {
                    scopeStack.pop();
                }
            });
        }

        FileBuilder cxxBuilder = new FileBuilder(projectBuilder.generatorOutputDirectory(), GENERATED_FILE_NAME);
        cxxBuilder.appendCxxAutogeneratedHeader();
        directive.addFile(cxxBuilder.file);

        // Write includes
        for (String include : includes) {
            String path = FileUtils.getRelativePath(cxxBuilder.file.getParentFile(), new File(include));
            if (SystemUtils.IS_WINDOWS)
                path = path.replace('\\', '/');
            path = path.replace("\"", "\\\"").replace("\\", "\\\\");
            cxxBuilder.append(String.format("#include \"%s\"\n", path));
        }
        cxxBuilder.append('\n');

        // Write type identifiers
        for (Map.Entry<String, String> it : typeIDs.entrySet()) {
            cxxBuilder.append(String.format("static const Engine::TypeID %s = Engine::typeOf<%s>();\n",
                it.getValue(), it.getKey()));
        }

        // Write queryInterface methods
        cxxBuilder.append(queryInterfaceMethods.toString());

        cxxBuilder.commit(projectBuilder.database);
    }

    @Override public void preGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    @Override public void postGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }
}
