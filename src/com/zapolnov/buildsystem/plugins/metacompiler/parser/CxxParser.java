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
package com.zapolnov.buildsystem.plugins.metacompiler.parser;

import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxClassType;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxFullyQualifiedName;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxMemberProtection;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxNamespace;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxParentClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxScope;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Parser for C++ files. */
public final class CxxParser
{
    /** Parse error. */
    public final static class Error extends RuntimeException
    {
        /** Token that caused the error. */
        public final CxxToken token;

        /**
         * Constructor.
         * @param token Token that caused the error.
         * @param message Error message.
         */
        public Error(CxxToken token, String message)
        {
            super(String.format("%s: %s", token.location(), message));
            this.token = token;
        }

        /**
         * Constructor.
         * @param token Token that caused the error.
         * @param message Error message.
         * @param exception Exception that caused the error.
         */
        public Error(CxxToken token, String message, Throwable exception)
        {
            super(message, exception);
            this.token = token;
        }
    }


    /** A file being parsed. */
    private final File file;
    /** A lexer. */
    private final CxxLexer lexer;
    /** Current token. */
    private CxxToken token;
    /** Current scope. */
    private CxxScope currentScope;
    /** Stack of scopes. */
    private final Stack<CxxScope> scopeStack = new Stack<>();

    /**
     * Constructor.
     * @param file File to parse.
     */
    public CxxParser(File file) throws IOException
    {
        this.file = file;
        this.lexer = new CxxLexer(new FileReader(file));
    }

    /**
     * Constructor.
     * @param reader File reader.
     * @param file Name Name of the file.
     */
    public CxxParser(Reader reader, File file)
    {
        this.file = file;
        this.lexer = new CxxLexer(reader);
    }

    /**
     * Pushes scope onto the top of the stack.
     * @param scope Scope to push.
     */
    private void pushScope(CxxScope scope)
    {
        scopeStack.push(currentScope);
        currentScope = scope;
    }

    /** Pops scope from the top of the stack. */
    private void popScope()
    {
        currentScope = scopeStack.pop();
    }

    /**
     * Parses the translation unit.
     * @return Translation unit.
     */
    public CxxTranslationUnit parseTranslationUnit() throws IOException
    {
        CxxTranslationUnit translationUnit = new CxxTranslationUnit(file);
        pushScope(translationUnit.globalScope);
        try {
            nextToken();
            while (token.id != CxxToken.EOF)
                parseNamespaceMember();
        } finally {
            this.currentScope = null;
            scopeStack.clear();
        }

        return translationUnit;
    }

    /** Parses namespace declaration. */
    private void parseNamespace() throws IOException
    {
        nextToken();

        CxxFullyQualifiedName name = null;
        if (token.id != CxxToken.LCURLY)
            name = parseFullyQualifiedName();

        parseLeftCurly();

        CxxNamespace namespace = new CxxNamespace(currentScope.translationUnit, name);
        currentScope.addSymbol(namespace);

        pushScope(namespace.scope);
        try {
            while (token.id != CxxToken.RCURLY && token.id != CxxToken.EOF)
                parseNamespaceMember();
        } finally {
            popScope();
        }

        parseRightCurly();
    }

    /** Parses a member of the namespace. */
    private void parseNamespaceMember() throws IOException
    {
        switch (token.id)
        {
        case CxxToken.NAMESPACE:
            parseNamespace();
            break;

        case CxxToken.CLASS:
        case CxxToken.STRUCT:
            parseClass();
            break;

        case CxxToken.TEMPLATE:
            parseTemplate();
            break;

        case CxxToken.LCURLY:
            nextToken();
            skipUntilRCurly();
            break;

        default:
            nextToken();
        }
    }

    /** Parses class declaration. */
    private void parseClass() throws IOException
    {
        nextToken();
        CxxFullyQualifiedName name = parseFullyQualifiedName();

        boolean isTemplateSpecialization = false;
        if (token.id == CxxToken.LESS) {
            nextToken();
            skipUntilRightAngleBracket();
            isTemplateSpecialization = true;
        }

        if (token.id == CxxToken.SEMICOLON) {
            // Forward declaration of a class
            nextToken();
            return;
        }

        List<CxxParentClass> parentClasses = new ArrayList<>();
        if (token.id == CxxToken.COLON) {
            do {
                nextToken();

                boolean virtualInheritance = false;
                if (token.id == CxxToken.VIRTUAL) {
                    nextToken();
                    virtualInheritance = true;
                }

                CxxMemberProtection protection = parseClassMemberProtection();

                if (!virtualInheritance && token.id == CxxToken.VIRTUAL) {
                    nextToken();
                    virtualInheritance = true;
                }

                CxxFullyQualifiedName parentName = parseFullyQualifiedName();
                parentClasses.add(new CxxParentClass(parentName, protection, virtualInheritance));
            } while (token.id == CxxToken.COMMA);
        }

        parseLeftCurly();

        CxxClass cxxClass = new CxxClass(currentScope.translationUnit, name, parentClasses, isTemplateSpecialization);
        currentScope.addSymbol(cxxClass);

        pushScope(cxxClass.scope);
        try {
            while (token.id != CxxToken.RCURLY && token.id != CxxToken.EOF)
                parseClassMember(cxxClass);
        } finally {
            popScope();
        }

        parseRightCurly();
        parseSemicolon();
    }

    /**
     * Parses a member of the class.
     * @param cxxClass Class being parsed.
     */
    private void parseClassMember(CxxClass cxxClass) throws IOException
    {
        CxxToken firstToken = token;
        CxxFullyQualifiedName name;

        switch (token.id)
        {
        case CxxToken.CLASS:
        case CxxToken.STRUCT:
            parseClass();
            break;

        case CxxToken.TEMPLATE:
            parseTemplate();
            break;

        case CxxToken.Z_INTERFACE:
            name = parseZInterface();
            if (cxxClass.type != CxxClassType.DEFAULT)
                throw new Error(firstToken, "Unexpected Z_INTERFACE.");
            else if (cxxClass.isTemplateSpecialization)
                throw new Error(name.firstToken, "Z_INTERFACE() is not supported in template specializations.");
            else if (!name.text.equals(cxxClass.name.lastComponent()))
                throw new Error(name.firstToken, "Name in Z_INTERFACE() does not match the class name.");
            else
                cxxClass.type = CxxClassType.INTERFACE;
            break;

        case CxxToken.Z_CUSTOM_IMPLEMENTATION:
            name = parseZCustomImplementation();
            if (cxxClass.type != CxxClassType.DEFAULT)
                throw new Error(firstToken, "Unexpected Z_CUSTOM_IMPLEMENTATION.");
            else if (cxxClass.isTemplateSpecialization)
                throw new Error(name.firstToken, "Z_CUSTOM_IMPLEMENTATION() is not supported in template specializations.");
            else if (!name.text.equals(cxxClass.name.lastComponent()))
                throw new Error(name.firstToken, "Name in Z_CUSTOM_IMPLEMENTATION() does not match the class name.");
            else
                cxxClass.type = CxxClassType.CUSTOM_IMPLEMENTATION;
            break;

        case CxxToken.Z_IMPLEMENTATION:
            name = parseZImplementation();
            if (cxxClass.type != CxxClassType.DEFAULT)
                throw new Error(firstToken, "Unexpected Z_IMPLEMENTATION.");
            else if (cxxClass.isTemplateSpecialization)
                throw new Error(name.firstToken, "Z_IMPLEMENTATION() is not supported in template specializations.");
            else if (!name.text.equals(cxxClass.name.lastComponent()))
                throw new Error(name.firstToken, "Name in Z_IMPLEMENTATION() does not match the class name.");
            else
                cxxClass.type = CxxClassType.IMPLEMENTATION;
            break;

        case CxxToken.LCURLY:
            nextToken();
            skipUntilRCurly();
            break;

        default:
            nextToken();
        }
    }

    /**
     * Parses class member protection level.
     * @return Class member protection level.
     */
    private CxxMemberProtection parseClassMemberProtection() throws IOException
    {
        switch (token.id)
        {
        case CxxToken.PRIVATE:
            nextToken();
            return CxxMemberProtection.PRIVATE;
        case CxxToken.PROTECTED:
            nextToken();
            return CxxMemberProtection.PROTECTED;
        case CxxToken.PUBLIC:
            nextToken();
            return CxxMemberProtection.PUBLIC;
        }
        return null;
    }

    /** Parses template declaration. */
    private void parseTemplate() throws IOException
    {
        nextToken();

        parseLeftAngleBracket();
        while (token.id != CxxToken.GREATER) {
            nextToken();
        }
        parseRightAngleBracket();
    }

    /**
     * Parses the Z_INTERFACE macro in class declaration.
     * @return Name of the interface.
     */
    private CxxFullyQualifiedName parseZInterface() throws IOException
    {
        nextToken();

        parseLeftParenthesis();
        CxxFullyQualifiedName name = parseFullyQualifiedName();
        parseRightParenthesis();

        return name;
    }

    /**
     * Parses the Z_CUSTOM_IMPLEMENTATION macro in class declaration.
     * @return Name of the implementation.
     */
    private CxxFullyQualifiedName parseZCustomImplementation() throws IOException
    {
        nextToken();

        parseLeftParenthesis();
        CxxFullyQualifiedName name = parseFullyQualifiedName();
        parseRightParenthesis();

        return name;
    }

    /**
     * Parses the Z_IMPLEMENTATION macro in class declaration.
     * @return Name of the class.
     */
    private CxxFullyQualifiedName parseZImplementation() throws IOException
    {
        nextToken();

        parseLeftParenthesis();
        CxxFullyQualifiedName name = parseFullyQualifiedName();
        parseRightParenthesis();

        return name;
    }

    /**
     * Parses a fully qualified name.
     * @return AST node for the fully qualified name.
     */
    private CxxFullyQualifiedName parseFullyQualifiedName() throws IOException
    {
        StringBuilder builder = new StringBuilder();
        CxxToken firstToken = token;

        if (token.id == CxxToken.SCOPE) {
            builder.append("::");
            nextToken();
        }

        builder.append(parseIdentifier().text);
        while (token.id == CxxToken.SCOPE) {
            builder.append("::");
            nextToken();
            builder.append(parseIdentifier().text);
        }

        return new CxxFullyQualifiedName(firstToken, builder.toString());
    }

    /**
     * Parses an identifier.
     * @return CxxToken for the identifier.
     */
    private CxxToken parseIdentifier() throws IOException
    {
        if (token.id != CxxToken.IDENTIFIER)
            throw new Error(token, "Expected identifier.");

        CxxToken identifierToken = token;
        nextToken();

        return identifierToken;
    }

    /** Skips all tokens until the right angle bracket ('>'). */
    private void skipUntilRightAngleBracket() throws IOException
    {
        while (token.id != CxxToken.GREATER && token.id != CxxToken.EOF) {
            if (token.id != CxxToken.LESS)
                nextToken();
            else {
                nextToken();
                skipUntilRightAngleBracket();
            }
        }
        parseRightAngleBracket();
    }

    /** Skips all tokens until the right curly bracket ('{'). */
    private void skipUntilRCurly() throws IOException
    {
        while (token.id != CxxToken.RCURLY && token.id != CxxToken.EOF) {
            if (token.id != CxxToken.LCURLY)
                nextToken();
            else {
                nextToken();
                skipUntilRCurly();
            }
        }
        parseRightCurly();
    }

    /** Parses the left angle bracket (`<`). */
    private void parseLeftAngleBracket() throws IOException
    {
        if (token.id != CxxToken.LESS)
            throw new Error(token, "Expected '<'.");
        nextToken();
    }

    /** Parses the right angle bracket (`>`). */
    private void parseRightAngleBracket() throws IOException
    {
        if (token.id != CxxToken.GREATER)
            throw new Error(token, "Expected '>'.");
        nextToken();
    }

    /** Parses the left parenthesis (`(`). */
    private void parseLeftParenthesis() throws IOException
    {
        if (token.id != CxxToken.LPAREN)
            throw new Error(token, "Expected '('.");
        nextToken();
    }

    /** Parses the right parenthesis (`)`). */
    private void parseRightParenthesis() throws IOException
    {
        if (token.id != CxxToken.RPAREN)
            throw new Error(token, "Expected ')'.");
        nextToken();
    }

    /** Parses the left curly bracket (`{`). */
    private void parseLeftCurly() throws IOException
    {
        if (token.id != CxxToken.LCURLY)
            throw new Error(token, "Expected '{'.");
        nextToken();
    }

    /** Parses the right curly bracket (`}`). */
    private void parseRightCurly() throws IOException
    {
        if (token.id != CxxToken.RCURLY)
            throw new Error(token, "Expected '}'.");
        nextToken();
    }

    /** Parses the semicolon (`;`). */
    private void parseSemicolon() throws IOException
    {
        if (token.id != CxxToken.SEMICOLON)
            throw new Error(token, "Expected ';'.");
        nextToken();
    }

    /**
     * Reads next token from the input file.
     * @return Token ID.
     */
    private int nextToken() throws IOException
    {
        token = lexer.yylex();
        return token.id;
    }
}
