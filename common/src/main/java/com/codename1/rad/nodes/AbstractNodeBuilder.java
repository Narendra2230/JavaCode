/*
 * The MIT License
 *
 * Copyright 2021 shannah.
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
package com.codename1.rad.nodes;

import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.NodeBuilder;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;

import java.util.Map;

/**
 *
 * @author shannah
 */
public abstract class AbstractNodeBuilder<T extends Node> implements NodeBuilder<T> {
    private final ViewContext context;
    private T node;
    private String tagName;
    private Map<String,String> attributes;
    protected AbstractNodeBuilder(ViewContext context, String tagName, Map<String,String> attributes) {
        this.tagName = tagName;
        this.context = context;
        this.attributes = attributes;
    }
    
    public ViewContext getContext() {
        return context;
    }
    
    public T getNode() {
        if (node == null) {
            node = build();
        }
        return node;
    }



    public String getTagName() {
        return tagName;
    }

    public Map<String,String> getAttributes() {
        return attributes;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

}
