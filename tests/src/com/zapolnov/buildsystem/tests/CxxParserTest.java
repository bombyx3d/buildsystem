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

import com.zapolnov.buildsystem.plugins.metacompiler.parser.CxxParser;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxClass;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxMemberProtection;
import com.zapolnov.buildsystem.plugins.metacompiler.parser.ast.CxxTranslationUnit;
import java.io.File;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;

public class CxxParserTest extends Assert
{
    private CxxTranslationUnit parse(String source) throws Exception
    {
        CxxParser parser = new CxxParser(new StringReader(source), new File(source));
        return parser.parseTranslationUnit();
    }

    @Test public void testBasicFunctionality() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("");
        assertTrue(unit.globalScope.symbols().isEmpty());

        unit = parse("/* A comment.\nNew line. */");
        assertTrue(unit.globalScope.symbols().isEmpty());

        unit = parse("//");
        assertTrue(unit.globalScope.symbols().isEmpty());

        unit = parse("class Test;");
        assertTrue(unit.globalScope.symbols().isEmpty());

        boolean exceptionThrown = false;
        try {
            parse("class Test {}");
        } catch (CxxParser.Error error) {
            exceptionThrown = true;
            assertEquals(1, error.token.line);
            assertEquals(14, error.token.column);
        }
        assertTrue(exceptionThrown);

        unit = parse("// class Test {};");
        assertTrue(unit.globalScope.symbols().isEmpty());

        unit = parse("class Test {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertTrue(((CxxClass) unit.globalScope.symbols().get(0)).parentClasses().isEmpty());
    }

    @Test public void testSingleInheritanceParentClassList() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("class Test : Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertNull(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : public Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : protected Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : private Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : virtual Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertNull(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : public virtual Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : protected virtual Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : private virtual Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : virtual public Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : virtual protected Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);

        unit = parse("class Test : virtual private Parent {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(1, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);
    }

    @Test public void testMultipleInheritanceParentClassList() throws Exception
    {
        CxxTranslationUnit unit;

        unit = parse("class Test : Parent1, Parent2 {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(2, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent1", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertNull(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);
        assertEquals("Parent2", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).name.text);
        assertNull(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).virtual);

        unit = parse("class Test : Parent1, virtual public Parent2, private Parent3, protected virtual Parent4 {};");
        assertEquals(1, unit.globalScope.symbols().size());
        assertEquals("Test", unit.globalScope.symbols().get(0).name.text);
        assertEquals(4, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().size());
        assertEquals("Parent1", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).name.text);
        assertNull(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(0).virtual);
        assertEquals("Parent2", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).name.text);
        assertEquals(CxxMemberProtection.PUBLIC, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(1).virtual);
        assertEquals("Parent3", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(2).name.text);
        assertEquals(CxxMemberProtection.PRIVATE, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(2).protectionLevel);
        assertFalse(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(2).virtual);
        assertEquals("Parent4", ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(3).name.text);
        assertEquals(CxxMemberProtection.PROTECTED, ((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(3).protectionLevel);
        assertTrue(((CxxClass)unit.globalScope.symbols().get(0)).parentClasses().get(3).virtual);
    }
}
