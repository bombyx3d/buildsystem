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

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;

%%

%unicode
%class CxxLexer
%public
%implements CxxToken
%cupsym CxxToken
%cup
%char
%line
%column

%eofval{
    return symbol(EOF);
%eofval}

%ctorarg String fileName
%init{
    this.fileName = fileName;
%init}

%{
    public static class SymbolFactory extends ComplexSymbolFactory
    {
        public static final SymbolFactory instance = new SymbolFactory();
    }

    private final String fileName;

    private Symbol symbol(int type)
    {
        ComplexSymbolFactory.Location location = new ComplexSymbolFactory.Location(fileName, yyline, yycolumn, yychar);
        return SymbolFactory.instance.newSymbol(yytext(), type, location, location);
    }

    private Symbol symbol(int type, Object value)
    {
        ComplexSymbolFactory.Location location = new ComplexSymbolFactory.Location(fileName, yyline, yycolumn, yychar);
        return SymbolFactory.instance.newSymbol(yytext(), type, location, location, value);
    }
%}

LineTerminator          = \r|\n|\r\n
ExceptLineTerminator    = [^\r\n]
Whitespace              = [ \t\v\f] | {LineTerminator}

Identifier              = [a-zA-Z$_] [a-zA-Z0-9$_]*

%%

<YYINITIAL> {

    "/*" ~"*/"                                      {}
    "//" {ExceptLineTerminator}* {LineTerminator}?  {}

    ","                                             { return symbol(COMMA); }
    ":"                                             { return symbol(COLON); }
    ";"                                             { return symbol(SEMICOLON); }
    "{"                                             { return symbol(LCURLY); }
    "}"                                             { return symbol(RCURLY); }

    "class"                                         { return symbol(CLASS); }
    "struct"                                        { return symbol(STRUCT); }
    "virtual"                                       { return symbol(VIRTUAL); }
    "public"                                        { return symbol(PUBLIC); }
    "protected"                                     { return symbol(PROTECTED); }
    "private"                                       { return symbol(PRIVATE); }

    {Identifier}                                    { return symbol(IDENTIFIER, yytext()); }

    {Whitespace}                                    {}
}

[^]                                                 { return symbol(UNKNOWN); }
