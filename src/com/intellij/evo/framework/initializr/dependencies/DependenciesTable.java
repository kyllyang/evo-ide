package com.intellij.evo.framework.initializr.dependencies;

import com.intellij.evo.framework.initializr.EvoInitializrConstants;
import com.intellij.evo.framework.initializr.EvoInitializrOptions;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.TableUtil;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Iterator;
import java.util.Set;

public class DependenciesTable extends JBTable {
    private static final ColoredTableCellRenderer DEPENDENCY_RENDERER = new ColoredTableCellRenderer() {
        protected void customizeCellRenderer(JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            boolean tableHasFocus = focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, table);
            this.setPaintFocusBorder(tableHasFocus && table.isRowSelected(row));
            boolean selectable = table.getModel().isCellEditable(row, 0);
            if (selectable) {
                this.append((String) value);
            } else {
                this.append((String) value, SimpleTextAttributes.GRAY_ATTRIBUTES);
            }

            SpeedSearchUtil.applySpeedSearchHighlighting(table, this, true, selected);
        }
    };

    public DependenciesTable() {
        this.setFocusTraversalKeys(0, (Set)null);
        this.setFocusTraversalKeys(1, (Set)null);
        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (DependenciesTable.this.getSelectedRow() == -1) {
                    DependenciesTable.this.setRowSelectionInterval(0, 0);
                } else {
                    DependenciesTable.this.repaint(DependenciesTable.this.getVisibleRect());
                }

            }

            public void focusLost(FocusEvent e) {
            }
        });
        this.setRowMargin(0);
        this.setShowColumns(false);
        this.setShowGrid(false);
        this.setShowVerticalLines(false);
        this.setCellSelectionEnabled(false);
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(0);
        this.getInputMap().put(KeyStroke.getKeyStroke(32, 0), "enable_disable");
        this.getActionMap().put("enable_disable", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!DependenciesTable.this.isEditing()) {
                    int row = DependenciesTable.this.getSelectedRow();
                    if (DependenciesTable.this.getModel().isCellEditable(row, 0)) {
                        SpeedSearchSupply speedSearch = SpeedSearchSupply.getSupply(DependenciesTable.this);
                        if (speedSearch == null) {
                            boolean value = (Boolean) DependenciesTable.this.getModel().getValueAt(row, 0);
                            DependenciesTable.this.getModel().setValueAt(!value, row, 0);
                        }
                    }
                }
            }
        });
    }

    @Nullable
    String getSelectedValue() {
        int row = this.getSelectedRow();
        return row == -1 ? null : (String)this.getModel().getValueAt(row, 1);
    }

    void setDependencies(String deps) {
        DependenciesTable.DependenciesTableModel model = new DependenciesTable.DependenciesTableModel(deps);
        this.setModel(model);
        this.setupColumns();
    }

    private void setupColumns() {
        TableColumnModel columnModel = this.getColumnModel();
        columnModel.setColumnMargin(0);
        TableColumn checkBoxColumn = columnModel.getColumn(0);
        TableUtil.setupCheckboxColumn(this, 0);
        checkBoxColumn.setCellRenderer(new BooleanTableCellRenderer());
        TableColumn dependencyColumn = columnModel.getColumn(1);
        dependencyColumn.setCellRenderer(DEPENDENCY_RENDERER);
    }

    void updateSelection(Set<String> selectedDependenciesIds) {
        for(int row = 0; row < this.getRowCount(); ++row) {
            String id = (String) this.getValueAt(row, 1);
            this.setValueAt(selectedDependenciesIds.contains(id), row, 0);
        }

    }

    private static class DependenciesTableModel extends AbstractTableModel {
        private final String selectedDependenciesId;
        private final java.util.List<String> myDependencies;
        private final boolean[] myChecked;
        private final boolean[] myDisabledCheckBoxes;
        private static final int COL_CHECK = 0;
        private static final int COL_DEP = 1;

        DependenciesTableModel(String selectedDependenciesId) {
            this.selectedDependenciesId = selectedDependenciesId;
            this.myDependencies = EvoInitializrConstants.DEPENDENCIES_MAP.get(selectedDependenciesId);
            this.myChecked = new boolean[this.myDependencies.size()];
            this.myDisabledCheckBoxes = new boolean[this.myDependencies.size()];
            int i = 0;

            for(Iterator<String> var5 = myDependencies.iterator(); var5.hasNext(); ++i) {
                if (selectedDependenciesId.equals(var5.next())) {
                    this.myChecked[i] = true;
                }
            }

        }

        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 && !this.myDisabledCheckBoxes[rowIndex];
        }

        public int getRowCount() {
            return this.myDependencies.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 1 ? this.myDependencies.get(rowIndex) : this.myChecked[rowIndex];
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            assert columnIndex == 0;

            this.myChecked[rowIndex] = (Boolean)aValue;
            boolean selected = this.myChecked[rowIndex];
            String id = this.myDependencies.get(rowIndex);
            if (selected) {
                EvoInitializrOptions.selectedDependenciesIds.add(id);
            } else {
                EvoInitializrOptions.selectedDependenciesIds.remove(id);
            }

            this.fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
