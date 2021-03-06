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
package com.zapolnov.buildsystem.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Template file processor. */
public class Template
{
    /** A command in the template. */
    private interface Command
    {
        void emit(FileBuilder builder);
    }

    /** Raw text block in the template. */
    private final static class Text implements Command
    {
        private final String text;

        public Text(String text)
        {
            this.text = text;
        }

        @Override public void emit(FileBuilder builder)
        {
            builder.append(text);
        }
    }

    /** Variable reference in the template. */
    private final class Variable implements Command
    {
        private final String name;

        public Variable(String name)
        {
            this.name = name;
        }

        @Override public void emit(FileBuilder builder)
        {
            String value = variables.get(name);
            if (value == null)
                throw new RuntimeException(String.format("Use of undeclared variable \"%s\" in template.", name));
            builder.append(value);
        }
    }


    /** List of template commands. */
    private final List<Command> commands = new ArrayList<>();
    /** Map of variable values. */
    private Map<String, String> variables;


    /**
     * Constructor.
     * @param stream Input stream with template file contents.
     */
    public Template(InputStream stream) throws IOException
    {
        String text = FileUtils.stringFromInputStream(stream);

        int offset = 0;
        for (;;) {
            int beginOffset = text.indexOf("@{", offset);
            if (beginOffset < 0)
                break;

            int endOffset = text.indexOf('}', beginOffset + 1);
            if (endOffset < 0)
                break;

            if (beginOffset > offset)
                commands.add(new Text(text.substring(offset, beginOffset)));

            String name = text.substring(beginOffset + 2, endOffset);
            commands.add(new Variable(name));

            offset = endOffset + 1;
        }

        if (text.length() > offset)
            commands.add(new Text(text.substring(offset, text.length())));
    }

    /**
     * Generates document from the template with the given values of variables.
     * @param fileBuilder File builder.
     * @param variables Variable values.
     */
    public void emit(FileBuilder fileBuilder, Map<String, String> variables)
    {
        try {
            this.variables = Collections.unmodifiableMap(variables);
            for (Command command : commands)
                command.emit(fileBuilder);
        } finally {
            this.variables = null;
        }
    }
}
