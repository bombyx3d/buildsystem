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

%%

%unicode
%class CxxLexer
%public
%final
%type CxxToken
%char
%line
%column

%eofclose
%eofval{
    return symbol(CxxToken.EOF);
%eofval}

%{
    private CxxToken symbol(int type)
    {
        return new CxxToken(type, yyline + 1, yycolumn + 1, yytext());
    }
%}

LineTerminator          = \r|\n|\r\n
ExceptLineTerminator    = [^\r\n] | "\\\n"
Whitespace              = [ \t\f] | {LineTerminator}

Identifier              = [a-zA-Z$_] [a-zA-Z0-9$_]*

%%

<YYINITIAL> {

    "/*" ~"*/"                                      {}
    "//" {ExceptLineTerminator}* {LineTerminator}?  {}

    "#" {ExceptLineTerminator}* {LineTerminator}?   {}

    "<"                                             { return symbol(CxxToken.LESS); }
    ">"                                             { return symbol(CxxToken.GREATER); }
    "("                                             { return symbol(CxxToken.LPAREN); }
    ")"                                             { return symbol(CxxToken.RPAREN); }
    "{"                                             { return symbol(CxxToken.LCURLY); }
    "}"                                             { return symbol(CxxToken.RCURLY); }
    ","                                             { return symbol(CxxToken.COMMA); }
    "="                                             { return symbol(CxxToken.EQUAL); }
    ":"                                             { return symbol(CxxToken.COLON); }
    "::"                                            { return symbol(CxxToken.SCOPE); }
    ";"                                             { return symbol(CxxToken.SEMICOLON); }

    "class"                                         { return symbol(CxxToken.CLASS); }
    "namespace"                                     { return symbol(CxxToken.NAMESPACE); }
    "private"                                       { return symbol(CxxToken.PRIVATE); }
    "protected"                                     { return symbol(CxxToken.PROTECTED); }
    "public"                                        { return symbol(CxxToken.PUBLIC); }
    "struct"                                        { return symbol(CxxToken.STRUCT); }
    "template"                                      { return symbol(CxxToken.TEMPLATE); }
    "typename"                                      { return symbol(CxxToken.TYPENAME); }
    "virtual"                                       { return symbol(CxxToken.VIRTUAL); }

    "Z_INTERFACE"                                   { return symbol(CxxToken.Z_INTERFACE); }
    "Z_IMPLEMENTATION"                              { return symbol(CxxToken.Z_IMPLEMENTATION); }

    {Identifier}                                    { return symbol(CxxToken.IDENTIFIER); }

    {Whitespace}+                                   {}
}

[^]                                                 { return symbol(CxxToken.UNRECOGNIZED); }
