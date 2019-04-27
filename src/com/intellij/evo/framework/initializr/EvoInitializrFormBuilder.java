package com.intellij.evo.framework.initializr;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

public class EvoInitializrFormBuilder extends FormBuilder {
    public EvoInitializrFormBuilder() {
        this.setFormLeftIndent(10);
    }

    public JBTextField addTextField(String label, String value) {
        JBTextField field = new JBTextField(value);
        field.setColumns(50);
        this.addLabeledComponent(label + ":", field);
        return field;
    }

    public void addVerticalSpacing() {
        this.addVerticalGap(4);
    }
}
