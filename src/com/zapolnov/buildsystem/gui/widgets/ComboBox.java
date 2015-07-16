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
package com.zapolnov.buildsystem.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

/** Custom implementation of combo box. */
public class ComboBox<T> extends JComboBox<String>
{
    /** Listener for selection changes. */
    public interface ChangeListener<T>
    {
        /**
         * Called when selection changes in the combo box.
         * @param value Selected value (or `null`).
         */
        void onItemSelected(T value);
    }


    /** List of item values. */
    private final List<T> values = new ArrayList<>();
    /** List of selection change listeners. */
    private final List<ChangeListener<T>> listeners = new ArrayList<>();


    /** Constructor. */
    public ComboBox()
    {
        setEditable(false);
        addActionListener(e -> {
            if (!listeners.isEmpty()) {
                T selectedValue = getSelectedValue();
                for (ChangeListener<T> listener : listeners)
                    listener.onItemSelected(selectedValue);
            }
        });
    }

    /**
     * Adds item to the combo box.
     * @param title Title of the item.
     * @param value Value of the item.
     */
    public void addItem(String title, T value)
    {
        values.add(value);
        addItem(title);
    }

    /**
     * Adds listener to be called when selection changes.
     * @param listener Listener.
     */
    public void addListener(ChangeListener<T> listener)
    {
        listeners.add(listener);
    }

    /** Removes all items from the combo box. */
    @Override public void removeAllItems()
    {
        super.removeAllItems();
        values.clear();
    }

    /**
     * Retrieves title of the currently selected item.
     * @return Title of the currently selected item.
     */
    @Override public String getSelectedItem()
    {
        return (String)super.getSelectedItem();
    }

    /**
     * Selects the specified item.
     * @param item Title of the item to select or `null`.
     */
    @Override public void setSelectedItem(Object item)
    {
        if (item != null) {
            for (int i = 0; i < getItemCount(); i++) {
                if (getItemAt(i).equals(item)) {
                    super.setSelectedItem(item);
                    return;
                }
            }
        }
        super.setSelectedItem(null);
    }

    /**
     * Retrieves value of the currently selected item.
     * @return Value of the currently selected item or `null`.
     */
    public T getSelectedValue()
    {
        int index = getSelectedIndex();
        if (index < 0)
            return null;
        return values.get(index);
    }

    /**
     * Selects the specified item.
     * @param item Value of the item to select or `null`.
     */
    public void setSelectedValue(T item)
    {
        if (item == null) {
            setSelectedIndex(-1);
            return;
        }

        int index = values.indexOf(item);
        if (index >= 0)
            setSelectedIndex(index);
    }
}
