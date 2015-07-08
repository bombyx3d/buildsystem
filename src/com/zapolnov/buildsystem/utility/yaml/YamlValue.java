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
package com.zapolnov.buildsystem.utility.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.nodes.Node;

/** A value from a YAML file. */
public class YamlValue
{
    /** Node in the YAML file. */
    public final Node node;
    /** Actual value. */
    public final Object value;

    /**
     * Constructor.
     * @param node Node in the YAML file.
     * @param value Actual value.
     */
    public YamlValue(Node node, Object value)
    {
        this.node = node;
        this.value = value;
    }

    /**
     * Checks whether this value is a string.
     * @return `true` if this value is a string.
     */
    public boolean isString()
    {
        return this.value != null && !isSequence() && !isMapping();
    }

    /**
     * Checks whether this value is a sequence.
     * @return `true` if this value is a sequence.
     */
    public boolean isSequence()
    {
        return this.value != null && this.value instanceof List;
    }

    /**
     * Checks whether this value is a mapping.
     * @return `true` if this value is a mapping.
     */
    public boolean isMapping()
    {
        return this.value != null && this.value instanceof Map;
    }

    /**
     * Converts this value to a string.
     * @return String.
     */
    @Override public String toString()
    {
        if (isString())
            return value.toString();
        throw new YamlError(this, "Expected a string.");
    }

    /**
     * Converts this value to a sequence.
     * @return List of values.
     */
    @SuppressWarnings("unchecked") public List<YamlValue> toSequence()
    {
        if (isSequence())
            return (List<YamlValue>)this.value;
        else if (isString()) {
            List<YamlValue> sequence = new ArrayList<>(1);
            sequence.add(this);
            return sequence;
        }
        throw new YamlError(this, "Expected a sequence.");
    }

    /**
     * Converts this value to a mapping.
     * @return Map of values.
     */
    @SuppressWarnings("unchecked") public Map<YamlValue, YamlValue> toMapping()
    {
        if (isMapping())
            return (Map<YamlValue, YamlValue>)this.value;
        throw new YamlError(this, "Expected a mapping.");
    }
}
