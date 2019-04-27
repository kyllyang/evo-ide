package com.intellij.evo.framework.initializr;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;

public class EvoInitializrSdkChooserStep extends ModuleWizardStep {
    private final EvoInitializrModuleBuilder myBuilder;

    public EvoInitializrSdkChooserStep(EvoInitializrModuleBuilder builder) {
        this.myBuilder = builder;
    }

    @Override
    public JComponent getComponent() {
        return new JBPanel<>();
    }

    @Override
    public void updateDataModel() {
    }
}
