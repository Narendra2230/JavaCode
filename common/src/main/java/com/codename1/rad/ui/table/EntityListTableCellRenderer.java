/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.table;

import com.codename1.rad.nodes.FieldNode;
import ca.weblite.shared.components.table.AbstractTableCellRenderer;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.TableCellRenderer;

import com.codename1.rad.models.Property.Editable;
import com.codename1.ui.Component;
import com.codename1.rad.ui.PropertyViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.rad.models.Entity;

/**
 * A renderer for rendering cells of {@link Table}s that use {@link EntityListTableModel} as a model.
 * @author shannah
 */
public class EntityListTableCellRenderer extends  AbstractTableCellRenderer {
    
    private PropertyViewFactory viewFactory;
    private TableCellRenderer parent;
    
    public EntityListTableCellRenderer(TableCellRenderer parent, PropertyViewFactory viewFactory) {
        this.parent = parent;
        this.viewFactory = viewFactory;
    }
    
    public EntityListTableCellRenderer(TableCellRenderer parent) {
        this(parent, UI.getDefaultPropertyViewFactory());
    }
    
    public EntityListTableCellRenderer(PropertyViewFactory viewFactory) {
        this(UI.getDefaultTableCellRenderer(), viewFactory);
    }
    
    public EntityListTableCellRenderer() {
        this(UI.getDefaultTableCellRenderer(), UI.getDefaultPropertyViewFactory());
    }

    @Override
    public Component getTableCellRendererComponent(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        EntityListTableModel model = (EntityListTableModel)table.getModel();
        FieldNode field = model.getColumnField(column);
        FieldNode fieldCopy = field.copy();
        fieldCopy.setAttributes(new Editable(false));
        Entity entity = model.getEntity(row);
        if (entity == null) {
            if (parent != null) {
                return parent.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                return new com.codename1.ui.Label();
            }
        }
        return viewFactory.createPropertyView(entity, fieldCopy);
        
    }

    
}
