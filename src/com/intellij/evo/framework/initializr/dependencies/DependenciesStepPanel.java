package com.intellij.evo.framework.initializr.dependencies;

import com.intellij.evo.framework.initializr.EvoInitializrConstants;
import com.intellij.evo.framework.initializr.EvoInitializrOptions;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DependenciesStepPanel extends JBSplitter {
    private final EvoInitializrOptions myOptions;
    private final JBList<String> myCategoriesList;
    private final DependenciesTable myDependenciesTable;

    public DependenciesStepPanel(EvoInitializrOptions options) {
        super(false, 0.4F);
        this.myOptions = options;

        this.myCategoriesList = new JBList<>(EvoInitializrConstants.DEPENDENCIES_MAP.keySet());
        this.myCategoriesList.setFixedCellHeight(JBUI.scale(22));
        ColoredListCellRenderer<String> categoryRenderer = new ColoredListCellRenderer<String>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends String> list, String value, int index, boolean selected, boolean hasFocus) {
                this.append(value);
                SpeedSearchUtil.applySpeedSearchHighlighting(DependenciesStepPanel.this.myCategoriesList, this, true, selected);
            }
        };
        this.myCategoriesList.setCellRenderer(categoryRenderer);
        this.myCategoriesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedValue = DependenciesStepPanel.this.myCategoriesList.getSelectedValue();
                    if (selectedValue != null) {
                        DependenciesStepPanel.this.myDependenciesTable.setDependencies(selectedValue);
                    }
                }
            }
        });

        this.myDependenciesTable = new DependenciesTable();

        this.setShowDividerIcon(false);
        this.setShowDividerControls(false);
        this.setDividerWidth(JBUI.scale(36));
        this.setFirstComponent(ScrollPaneFactory.createScrollPane(this.myCategoriesList, true));
        this.setSecondComponent(ScrollPaneFactory.createScrollPane(this.myDependenciesTable, true));

        this.myCategoriesList.setSelectedIndex(0);
    }

    public JComponent getPreferredFocusedComponent() {
        return this.myCategoriesList;
    }
}
