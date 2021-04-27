/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.TableCellEditorAttribute;
import com.codename1.rad.attributes.TableCellRendererAttribute;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.propertyviews.TextAreaPropertyView;
import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.OptionsNode;
import com.codename1.rad.nodes.PropertyViewFactoryNode;
import com.codename1.rad.propertyviews.ComboBoxPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.TablePropertyView;
import com.codename1.rad.ui.table.EntityListTableCellEditor;
import com.codename1.rad.ui.table.EntityListTableCellRenderer;
import com.codename1.rad.ui.table.EntityListTableModel;
import ca.weblite.shared.components.table.DefaultTableCellEditor;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.TableCellEditor;
import ca.weblite.shared.components.table.TableCellRenderer;
import com.codename1.components.CheckBoxList;
import com.codename1.components.RadioButtonList;
import com.codename1.components.Switch;
import com.codename1.components.SwitchList;

import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property.Name;
import com.codename1.rad.propertyviews.ButtonListPropertyView;
import com.codename1.rad.propertyviews.CheckBoxPropertyView;
import com.codename1.rad.propertyviews.SwitchPropertyView;
import com.codename1.ui.CheckBox;
import com.codename1.ui.ComboBox;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.list.MultipleSelectionListModel;
import java.util.HashMap;
import java.util.Map;
import com.codename1.rad.ui.PropertyViewDecorator;
import com.codename1.rad.attributes.PropertyViewDecoratorAttribute;
import com.codename1.rad.nodes.PropertyViewDecoratorNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.models.Entity;

/**
 * Default factory used to convert a {@link FieldNode} into a {@link PropertyView}.
 * 
 * @author shannah
 */
public class DefaultPropertyViewFactory implements PropertyViewFactory {

    private Map<WidgetType,PropertyViewFactory> registry = new HashMap<>();
    
    {
        registry.put(WidgetType.TEXT, (entity, field)->{
            boolean editable = field.isEditable();
            if (editable) {
                return new TextFieldPropertyView(new TextField(), entity, field);
                
            } else {
                return new LabelPropertyView(new com.codename1.ui.Label(), entity, field);
            }
            
        });
        
        registry.put(WidgetType.TEXTAREA, (entity, field) ->{
            
            
            TextArea ta = new TextArea();
            ta.setRows(5);
            ta.setColumns(80);
            ta.setMaxSize(1024);
            ta.setEditable(field.isEditable());
            return new TextAreaPropertyView(ta, entity, field);
        });
        
        registry.put(WidgetType.COMBOBOX, (entity, field) -> {
            if (!field.isEditable()) {
                return new LabelPropertyView(new com.codename1.ui.Label(), entity, field);
            }
            ComboBox cb = new ComboBox();
            OptionsNode opts = field.getOptions(entity.getEntity().getEntityType());
            if (opts != null) {
                cb.setModel(opts.getValue());
            }
            return new ComboBoxPropertyView(cb, entity, field);
            
            
            
        });
        
        registry.put(WidgetType.TABLE, (entity, field) -> {
            TableColumns columns = (TableColumns)field.findAttribute(TableColumns.class);
            if (columns == null) {
                throw new IllegalArgumentException("Cannot create a table for field "+field+" because it has not columns defined.  Add a ColumnsNode to the field's attributes.");
            }
            //EntityListTableModel tableModel = new EntityListTableModel
            EntityList entityList = (EntityList)entity.getEntity().get(field.getProperty(entity.getEntity().getEntityType()));
            if (entityList == null) {
                
            }
            EntityListTableModel tableModel = new EntityListTableModel(entityList.getRowType(), entityList, columns);
            
            TableCellEditorAttribute cellEditorAtt = (TableCellEditorAttribute)field.findInheritedAttribute(TableCellEditorAttribute.class);
            TableCellEditor cellEditor = null;
            if (cellEditorAtt == null) {
                PropertyViewFactory viewFactory = null;
                PropertyViewFactoryNode viewFactoryNode = (PropertyViewFactoryNode)field.findInheritedAttribute(PropertyViewFactoryNode.class);
                if (viewFactoryNode == null) {
                    viewFactory = UI.getDefaultPropertyViewFactory();
                } else {
                    viewFactory = viewFactoryNode.getValue();
                }
                cellEditor = new EntityListTableCellEditor(UI.getDefaultTableCellEditor(), viewFactory);
            } else {
                cellEditor = cellEditorAtt.getValue();
            }
            
            TableCellRenderer cellRenderer = null;
            TableCellRendererAttribute cellRendererAtt = (TableCellRendererAttribute)field.findInheritedAttribute(TableCellRendererAttribute.class);
            if (cellRendererAtt == null) {
                PropertyViewFactory viewFactory = null;
                PropertyViewFactoryNode viewFactoryNode = (PropertyViewFactoryNode)field.findInheritedAttribute(PropertyViewFactoryNode.class);
                if (viewFactoryNode == null) {
                    viewFactory = UI.getDefaultPropertyViewFactory();
                } else {
                    viewFactory = viewFactoryNode.getValue();
                }
                cellRenderer = new EntityListTableCellRenderer(UI.getDefaultTableCellRenderer(), viewFactory);
            }
            Table out = new Table(tableModel, cellRenderer, cellEditor);
            out.setEditable(field.isEditable());
            return new TablePropertyView(out, entity, field);  
        });
        
        registry.put(WidgetType.CHECKBOX_LIST, (entity, field) -> {
            OptionsNode options = field.getOptions();
            CheckBoxList list = new CheckBoxList((MultipleSelectionListModel)options.getValue());
            return new ButtonListPropertyView(list, entity, field);
        });
        
        registry.put(WidgetType.SWITCH, (entity, field) -> {
            return new SwitchPropertyView(new Switch(), entity, field);
        });
        
        registry.put(WidgetType.CHECKBOX, (entity, field) -> {
            return new CheckBoxPropertyView(new CheckBox(), entity, field);
        });
        
        
        
        registry.put(WidgetType.SWITCH_LIST, (entity, field) -> {
            OptionsNode options = field.getOptions();
            SwitchList list = new SwitchList((MultipleSelectionListModel)options.getValue());
            return new ButtonListPropertyView(list, entity, field);
        });
        
        registry.put(WidgetType.RADIO_LIST, (entity, field) -> {
            OptionsNode options = field.getOptions();
            RadioButtonList list = new RadioButtonList(options.getValue());
            return new ButtonListPropertyView(list, entity, field);
        });
        
        
    }
    
    @Override
    public PropertyView createPropertyView(Entity entity, FieldNode field) {
        PropertyViewFactory typeFactory = registry.get(field.getWidgetType(entity.getEntity().getEntityType()));
        if (typeFactory == null) {
            throw new IllegalArgumentException("Type "+field.getWidgetType()+" not supported");
        }
        PropertyView out =  typeFactory.createPropertyView(entity, field);
        PropertyViewDecoratorAttribute decoratorAtt = (PropertyViewDecoratorAttribute)field.findAttribute(PropertyViewDecoratorAttribute.class);
        if (decoratorAtt != null) {
            out = decoratorAtt.getValue().decorate(out);
        }
        NodeList decorators = field.getChildNodes(PropertyViewDecoratorNode.class);
        if (decorators != null) {
            for (Node n : decorators) {
                out = ((PropertyViewDecoratorNode)n).getValue().decorate(out);
            }
        }
        return out;
    }
    
}
