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
package com.zapolnov.buildsystem.plugins.file2c;

import com.zapolnov.buildsystem.plugins.AbstractPlugin;
import com.zapolnov.buildsystem.project.ProjectReader;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.yaml.YamlError;
import com.zapolnov.buildsystem.utility.yaml.YamlValue;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Plugin implementing the 'file2c' directive. */
@SuppressWarnings("unused") public class Plugin extends AbstractPlugin
{
    @Override public Map<String, ProjectReader.DirectiveParser> customDirectives()
    {
        Map<String, ProjectReader.DirectiveParser> directives = new HashMap<>();
        directives.put("file2c", (r, k, v) -> {
            CompressionMethod compressionMethod = CompressionMethod.NONE;
            File input = null;
            String output = null;
            String identifier = null;
            String namespace = null;

            for (Map.Entry<YamlValue, YamlValue> item : v.toMapping().entrySet()) {
                YamlValue key = item.getKey();
                YamlValue value = item.getValue();

                switch (key.toString())
                {
                case "input":
                    input = new File(r.currentScope().directory, value.toString());
                    if (!input.exists()) {
                        throw new YamlError(value,
                            String.format("File \"%s\" does not exist.", FileUtils.getCanonicalPath(input)));
                    }
                    input = FileUtils.getCanonicalFile(input);
                    break;

                case "output":
                    output = value.toString();
                    if (output.length() == 0)
                        throw new YamlError(value, "Expected file name.");
                    break;

                case "identifier":
                    identifier = value.toString();
                    if (identifier.length() == 0)
                        throw new YamlError(value, "Expected identifier.");
                    break;

                case "namespace":
                    namespace = value.toString();
                    if (namespace.length() == 0)
                        throw new YamlError(value, "Expected identifier.");
                    break;

                case "compress":
                    String name = value.toString();
                    compressionMethod = null;
                    for (CompressionMethod method : CompressionMethod.values()) {
                        if (method.name.equals(name)) {
                            compressionMethod = method;
                            break;
                        }
                    }
                    if (compressionMethod == null) {
                        List<String> validValues = new ArrayList<>();
                        for (CompressionMethod method : CompressionMethod.values())
                            validValues.add(method.name);
                        throw new YamlError(value, String.format("Invalid compression method. Valid values are: \"%s\".",
                            String.join("\", \"", validValues)));
                    }
                    break;

                default:
                    throw new YamlError(key, String.format("Unknown option \"%s\".", key.toString()));
                }
            }

            if (input == null)
                throw new YamlError(k, "Missing input file name.");
            if (output == null)
                throw new YamlError(k, "Missing output file name.");
            if (identifier == null)
                throw new YamlError(k, "Missing identifier name.");

            r.currentScope().addDirective(new FileToCDirective(input, output, identifier, namespace, compressionMethod));
        });
        return directives;
    }
}
