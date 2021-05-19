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
package com.codename1.rad.propertyviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.ui.image.ImageContainer;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionListener;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class ImageContainerPropertyView extends PropertyView<ImageContainer> {
    private String lastImagePath;
    
    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    public ImageContainerPropertyView(@Inject ImageContainer imgCnt, @Inject Entity entity, @Inject FieldNode node) {
        super(imgCnt, entity, node);
    }
    
    @Override
    protected void bindImpl() {
        getPropertySelector().addPropertyChangeListener(pcl);
    }

    @Override
    protected void unbindImpl() {
        getPropertySelector().removePropertyChangeListener(pcl);
    }

    @Override
    public void update() {
        super.update();
        String newImagePath = getPropertySelector().getText(null);
        if (getPropertySelector().isEmpty()) {
            if (isVisible()) {
                setHidden(true);
                setVisible(false);
            }
        } else {
            if (!isVisible()) {
                setHidden(false);
                setVisible(true);
            }
        }
        if (!Objects.equals(newImagePath, lastImagePath)) {
            lastImagePath = newImagePath;
            getComponent().invalidateImage();
        }
        Form f = getComponentForm();
        if (f != null) {
            f.revalidateLater();
        }
        
        
    }

    @Override
    public void commit() {
        
    }
    
}
