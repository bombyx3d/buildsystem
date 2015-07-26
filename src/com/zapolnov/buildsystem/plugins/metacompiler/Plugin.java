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
import java.util.LinkedHashMap;
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

        final Map<CxxClass, String> singletons = new LinkedHashMap<>();
        final Map<CxxClass, String> interfaces = new LinkedHashMap<>();
        final Map<CxxClass, String> customInterfaces = new LinkedHashMap<>();
        final Set<String> includes = new TreeSet<>();

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
                    String className = scopeStack.peek().mergeWith(cxxClass.name).text;
                    switch (cxxClass.type)
                    {
                    case DEFAULT:
                        break;

                    case INTERFACE:
                    case IMPLEMENTATION:
                        includes.add(FileUtils.getCanonicalPath(cxxClass.translationUnit.file));
                        interfaces.put(cxxClass, className);
                        break;

                    case SINGLETON_IMPLEMENTATION:
                        includes.add(FileUtils.getCanonicalPath(cxxClass.translationUnit.file));
                        singletons.put(cxxClass, className);
                        break;

                    case CUSTOM_IMPLEMENTATION:
                        includes.add(FileUtils.getCanonicalPath(cxxClass.translationUnit.file));
                        customInterfaces.put(cxxClass, className);
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

        // Generate code for queryInterface() methods
        final StringBuilder queryInterfaceMethods = new StringBuilder();
        final Map<String, String> typeIDs = new TreeMap<>();
        for (Map.Entry<CxxClass, String> it : interfaces.entrySet())
            generateQueryInterfaceMethod(queryInterfaceMethods, it.getValue(), it.getKey(), typeIDs, false);
        for (Map.Entry<CxxClass, String> it : singletons.entrySet())
            generateQueryInterfaceMethod(queryInterfaceMethods, it.getValue(), it.getKey(), typeIDs, false);
        for (Map.Entry<CxxClass, String> it : customInterfaces.entrySet())
            generateQueryInterfaceMethod(queryInterfaceMethods, it.getValue(), it.getKey(), typeIDs, true);

        // Write type identifiers
        for (Map.Entry<String, String> it : typeIDs.entrySet()) {
            cxxBuilder.append(String.format("static const Engine::TypeID %s = Engine::typeOf<%s>();\n",
                it.getValue(), it.getKey()));
        }

        // Write queryInterface() methods
        cxxBuilder.append(queryInterfaceMethods.toString());

        // Write initializer
        cxxBuilder.append(
            "\n" +
            "void Engine::Core::Initializer::init(Core& core)\n" +
            "{\n" +
            "    (void)core;        // Prevent compiler warnings\n"
        );
        for (Map.Entry<CxxClass, String> it : singletons.entrySet()) {
            cxxBuilder.append(String.format(
                "\n" +
                "    core.addSingleton(new %s);\n",
                it.getValue()
            ));
        }
        cxxBuilder.append(
            "}\n"
        );

        cxxBuilder.commit(projectBuilder.database);
    }

    private void generateQueryInterfaceMethod(StringBuilder output, String className, CxxClass cxxClass,
        Map<String, String> typeIDs, boolean custom)
    {
        String identifier = typeIDs.get(className);
        if (identifier == null) {
            identifier = "g_tid_" + StringUtils.makeIdentifier(StringUtils.makeIdentifier(className));
            typeIDs.put(className, identifier);
        }

        output.append(String.format(
            "\n" +
            "void* %s::queryInterface(Engine::TypeID typeID)\n" +
            "{\n" +
            "    if (typeID == %s)\n" +
            "        return this;\n",
            className, identifier
        ));

        if (!cxxClass.parentClasses().isEmpty()) {
            output.append("    void* p;\n");
            for (CxxParentClass parent : cxxClass.parentClasses()) {
                output.append(String.format(
                    "    p = %s::queryInterface(typeID);\n" +
                    "    if (p != nullptr)\n" +
                    "        return p;\n",
                    parent.name.text
                ));
            }
        }

        if (custom) {
            output.append(String.format(
                "    return %s::_queryCustomInterface(typeID);\n" +
                "}\n",
                className));
        } else {
            output.append(
                "    return nullptr;\n" +
                "}\n");
        }

    }

    @Override public void preGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }

    @Override public void postGenerate(ProjectBuilder projectBuilder) throws Throwable
    {
    }
}
