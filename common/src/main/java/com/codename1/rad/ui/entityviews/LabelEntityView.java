/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.ui.entityviews;

import ca.weblite.shared.components.ComponentImage;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.image.ImageContainer;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage.ImageAdapter;
import com.codename1.ui.layouts.BorderLayout;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 * A view which displays a label representing a given entity.  This will display the entity name {@link Thing.name} 
 * as the text, and the entity's icon (one of {@link Thing.thumbnailUrl}, {@link Thing.image}).
 * 
 * <p>The full-arg constructor accepts the actual Label which will be wrapped, along with a placeholder
 * icon and an image adapter, so that the icon can be loaded as a URLImage.
 * 
 * @author shannah
 */
public class LabelEntityView extends AbstractEntityView {
    private Label label;
    
    private Property iconProperty, nameProperty;
    private int iconWidth, iconHeight;
    
    public LabelEntityView(@Inject ViewContext context, @Inject Label label, int iconWidth, int iconHeight) {
        super(context);
        this.label = label;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;

        setLayout(new BorderLayout());
        getStyle().stripMarginAndPadding();
        add(BorderLayout.CENTER, label);
        iconProperty = findProperty(Thing.thumbnailUrl, Thing.image);
        nameProperty = findProperty(Thing.name);

        update();
    }

    /**
     * 
     * @param entity
     * @param node
     * @param label
     * @param iconWidth
     * @param iconHeight
     * @deprecated Use {@link #LabelEntityView(ViewContext, Label, int, int)}
     */
    public LabelEntityView(Entity entity, ViewNode node, Label label, int iconWidth, int iconHeight) {
        super(entity, node);
        
        this.label = label;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        
        setLayout(new BorderLayout());
        getStyle().stripMarginAndPadding();
        add(BorderLayout.CENTER, label);
        iconProperty = findProperty(Thing.thumbnailUrl, Thing.image);
        nameProperty = findProperty(Thing.name);
        
        update();
        
    }
    
    private String lastIconVal = null;
    
    @Override
    public void update() {
        boolean changed = false;
        String text = "";
        if (!getEntity().getEntity().isEmpty(nameProperty)) {
            text = getEntity().getEntity().getText(nameProperty);
        }
        if (!Objects.equals(text, label.getText())) {
            label.setText(text);
            changed = true;
        }
        
        String iconVal = getEntity().getEntity().getText(iconProperty);
        if (!Objects.equals(iconVal, lastIconVal)) {
            lastIconVal = iconVal;
            changed = true;
            if (iconVal == null || iconWidth == 0 || iconHeight == 0) {
                
                
                label.setIcon(null);
                
            } else {
                ImageContainer imgCnt = ImageContainer.createToFileSystem(getEntity(), iconProperty);
                imgCnt.setUIID(label.getIconUIID());
                imgCnt.setWidth(iconWidth);
                imgCnt.setHeight(iconHeight);
                imgCnt.setAspectRatio(iconWidth / (double)iconHeight);
                imgCnt.layoutContainer();
                ComponentImage cimg = new ComponentImage(imgCnt, iconWidth, iconHeight);
                cimg.setAnimation(true);
                label.setIcon(cimg);
            }
        }
        
        if (changed) {
            Form f = getComponentForm();
            if (f != null) {
                revalidateLater();
            }
        }
        
    }

    @Override
    public void commit() {
        
    }

    
    public Label getLabel() {
        return label;
    }
}
