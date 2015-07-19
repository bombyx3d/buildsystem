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
package com.zapolnov.buildsystem.tests;

import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxLexer;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxParser;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxMemberProtection;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;

public class CxxParserTest extends Assert
{
    private CxxTranslationUnit parse(String source, boolean printErrors) throws Exception
    {
        try {
            CxxLexer lexer = new CxxLexer(new StringReader(source), source);
            CxxParser parser = new CxxParser(lexer, CxxLexer.SymbolFactory.instance);
            parser.printErrors = printErrors;
            return (CxxTranslationUnit)parser.parse().value;
        } catch (CxxParser.ParseError ignored) {
            return new CxxTranslationUnit();
        }
    }

    @Test public void testBasicFunctionality() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("", true);
        assertTrue(unit.classes().isEmpty());

        unit = parse("/* A comment.\nNew line. */", true);
        assertTrue(unit.classes().isEmpty());

        unit = parse("//", true);
        assertTrue(unit.classes().isEmpty());

        unit = parse("class Test;", false);
        assertTrue(unit.classes().isEmpty());

        unit = parse("class Test {}", false);
        assertTrue(unit.classes().isEmpty());

        unit = parse("// class Test {};", true);
        assertTrue(unit.classes().isEmpty());

        unit = parse("class Test {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertTrue(unit.classes().get(0).parentClassList.classList().isEmpty());
    }

    @Test public void testSingleInheritanceParentClassList() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("class Test : Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertNull(unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : public Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : protected Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : private Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : virtual Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertNull(unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : public virtual Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : protected virtual Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : private virtual Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : virtual public Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : virtual protected Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);

        unit = parse("class Test : virtual private Parent {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(1, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(0).virtual);
    }

    @Test public void testMultipleInheritanceParentClassList() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("class Test : Parent1, Parent2 {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(2, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent1", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertNull(unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);
        assertEquals("Parent2", unit.classes().get(0).parentClassList.classList().get(1).name.text);
        assertNull(unit.classes().get(0).parentClassList.classList().get(1).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(1).virtual);

        unit = parse("class Test : Parent1, virtual public Parent2, private Parent3, protected virtual Parent4 {};", true);
        assertEquals(1, unit.classes().size());
        assertEquals("Test", unit.classes().get(0).name.text);
        assertEquals(4, unit.classes().get(0).parentClassList.classList().size());
        assertEquals("Parent1", unit.classes().get(0).parentClassList.classList().get(0).name.text);
        assertNull(unit.classes().get(0).parentClassList.classList().get(0).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(0).virtual);
        assertEquals("Parent2", unit.classes().get(0).parentClassList.classList().get(1).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, unit.classes().get(0).parentClassList.classList().get(1).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(1).virtual);
        assertEquals("Parent3", unit.classes().get(0).parentClassList.classList().get(2).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, unit.classes().get(0).parentClassList.classList().get(2).protectionLevel);
        assertFalse(unit.classes().get(0).parentClassList.classList().get(2).virtual);
        assertEquals("Parent4", unit.classes().get(0).parentClassList.classList().get(3).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, unit.classes().get(0).parentClassList.classList().get(3).protectionLevel);
        assertTrue(unit.classes().get(0).parentClassList.classList().get(3).virtual);
    }
}
