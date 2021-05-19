/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.TextFormatterAttribute;
import com.codename1.rad.ui.UI;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 * A view for binding to {@link SpanLabel} components.
 * @author shannah
 */
public class SpanLabelPropertyView extends PropertyView<com.codename1.components.SpanLabel> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    
    
    
    public SpanLabelPropertyView(@Inject com.codename1.components.SpanLabel component, @Inject Entity entity, @Inject PropertySelector property) {
        this(component, entity, new FieldNode(UI.property(property)));
    }
    
    public SpanLabelPropertyView(@Inject com.codename1.components.SpanLabel component, @Inject Entity entity, @Inject FieldNode field) {
        super(component, entity, field);
    }

    @Override
    protected void bindImpl() {
        getEntity().getEntity().addPropertyChangeListener(getProperty(), pcl);
        
    }

    @Override
    protected void unbindImpl() {
        
        getEntity().getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    @Override
    public void update() {
        super.update();
        String oldVal = getComponent().getText();
        String newVal = getPropertySelector().getText("");
        TextFormatterAttribute formatter = (TextFormatterAttribute)getField().findAttribute(TextFormatterAttribute.class);
        if (formatter != null) {
            newVal = formatter.getValue().format(newVal);
        }
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setText(newVal);
        }
    }
    
    @Override
    public void commit() {
        
    }
    
}
