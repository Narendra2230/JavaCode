/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.ui.Component;

/**
 * An interface used for implementing cell editors for the {@link Table} class.  A cell editor should be able to create a component
 * for a particular cell of a table that supports editing the data in that cell.
 * @author shannah
 */
public interface TableCellEditor {
    
    /**
     * Creates a component to edit the content of the given row and column of a table.
     * @param table The table whose cell to edit.
     * @param value The cell value.
     * @param isSelected Whether the cell is selected currently.
     * @param row The row (0-based).
     * @param column The column (0-based).
     * @return A component to edit the content of the cell.
     */
    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column);
}
