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

import com.zapolnov.buildsystem.build.FileParser;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxLexer;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxParser;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** An analyzer for C++ files. */
public class CxxAnalyzer implements FileParser
{
    /** Abstract syntax tree for the analyzed file. */
    private CxxTranslationUnit syntaxTree;

    /**
     * Retrieves an abstract syntax tree for the analyzed file.
     * @return Root of the AST.
     */
    public CxxTranslationUnit syntaxTree()
    {
        return syntaxTree;
    }

    @Override public void parse(File file) throws Exception
    {
        syntaxTree = new CxxTranslationUnit();
        try {
            CxxLexer lexer = new CxxLexer(new FileReader(file), file.toString());
            CxxParser parser = new CxxParser(lexer, CxxLexer.SymbolFactory.instance);
            syntaxTree = (CxxTranslationUnit)parser.parse().value;
        } catch (CxxParser.ParseError ignored) {
        }
    }

    @Override public void save(ObjectOutputStream stream) throws IOException
    {
        stream.writeObject(syntaxTree);
    }

    @Override public void load(ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        syntaxTree = (CxxTranslationUnit)stream.readObject();
        if (syntaxTree == null)
            syntaxTree = new CxxTranslationUnit();
    }
}
