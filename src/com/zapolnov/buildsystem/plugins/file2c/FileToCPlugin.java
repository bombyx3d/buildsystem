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

import com.zapolnov.buildsystem.plugins.Plugin;

/** Plugin implementing the 'file2c' directive. */
public class FileToCPlugin extends Plugin
{
    /*
    @Override public CustomDirective processDirective(Project project, File basePath, String key,
        YamlValue keyOption, YamlValue valueOption)
    {
        if ("file2c".equals(key)) {
            CompressionMethod compressionMethod = CompressionMethod.NONE;
            File input = null;
            String output = null;
            String identifier = null;
            String namespace = null;

            if (!valueOption.isMapping())
                throw new YamlError(valueOption, "Expected mapping.");

            for (Map.Entry<YamlValue, YamlValue> item : valueOption.toMapping().entrySet()) {
                YamlValue subKeyOption = item.getKey();
                YamlValue subValueOption = item.getValue();
                String subValue;

                String subKey = subKeyOption.toString();
                if (subKey == null)
                    throw new YamlError(subKeyOption, "Mapping key should be a string.");

                switch (subKey)
                {
                case "input":
                    subValue = subValueOption.toString();
                    if (subValue == null)
                        throw new YamlError(subValueOption, "Expected string.");
                    input = new File(basePath, subValue);
                    if (!input.exists()) {
                        throw new YamlError(subValueOption,
                            String.format("File \"%s\" does not exist.", FileUtils.getCanonicalPath(input)));
                    }
                    input = FileUtils.getCanonicalFile(input);
                    break;

                case "output":
                    subValue = subValueOption.toString();
                    if (subValue == null)
                        throw new YamlError(subValueOption, "Expected string.");
                    if (subValue.length() == 0)
                        throw new YamlError(subValueOption, "Expected file name.");
                    output = subValue;
                    break;

                case "identifier":
                    subValue = subValueOption.toString();
                    if (subValue == null)
                        throw new YamlError(subValueOption, "Expected string.");
                    if (subValue.length() == 0)
                        throw new YamlError(subValueOption, "Expected identifier.");
                    identifier = subValue;
                    break;

                case "namespace":
                    subValue = subValueOption.toString();
                    if (subValue == null)
                        throw new YamlError(subValueOption, "Expected string.");
                    if (subValue.length() == 0)
                        throw new YamlError(subValueOption, "Expected identifier.");
                    namespace = subValue;
                    break;

                case "compress":
                    subValue = subValueOption.toString();
                    if (subValue == null)
                        throw new YamlError(subValueOption, "Expected string.");
                    compressionMethod = null;
                    for (CompressionMethod method : CompressionMethod.values()) {
                        if (method.name.equals(subValue)) {
                            compressionMethod = method;
                            break;
                        }
                    }
                    if (compressionMethod == null) {
                        List<String> validValues = new ArrayList<>();
                        for (CompressionMethod method : CompressionMethod.values())
                            validValues.add(method.name);
                        throw new YamlError(subValueOption, String.format(
                            "Invalid compression method. Valid values are: \"%s\".",
                            String.join("\", \"", validValues)));
                    }
                    break;

                default:
                    throw new YamlError(subKeyOption, String.format("Unknown option \"%s\".", subKey));
                }
            }

            if (input == null)
                throw new YamlError(keyOption, "Missing input file name.");
            if (output == null)
                throw new YamlError(keyOption, "Missing output file name.");
            if (identifier == null)
                throw new YamlError(keyOption, "Missing identifier name.");

            return new FileToCDirective(input, output, identifier, namespace, compressionMethod);
        }

        return null;
    }
    */
}
