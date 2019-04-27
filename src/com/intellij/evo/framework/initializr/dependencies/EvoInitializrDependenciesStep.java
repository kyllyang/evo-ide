package com.intellij.evo.framework.initializr.dependencies;

import com.intellij.evo.framework.initializr.EvoInitializrConstants;
import com.intellij.evo.framework.initializr.EvoInitializrModuleBuilder;
import com.intellij.evo.framework.initializr.EvoInitializrOptions;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class EvoInitializrDependenciesStep extends ModuleWizardStep {
    private final EvoInitializrModuleBuilder myBuilder;
    private DependenciesStepPanel myPanel;

    public EvoInitializrDependenciesStep(EvoInitializrModuleBuilder builder) {
        this.myBuilder = builder;
    }

    @Override
    public JComponent getComponent() {
        return this.myPanel;
    }

    public void _init() {
        if (this.myPanel == null) {
            this.createPanel();
        }

    }

    public JComponent getPreferredFocusedComponent() {
        return this.myPanel.getPreferredFocusedComponent();
    }

    private void createPanel() {
        EvoInitializrOptions options = this.myBuilder.getOptions();
        if (options != null) {
            this.myPanel = new DependenciesStepPanel(options);
        }
    }

    @Override
    public void updateDataModel() {
    }

    public String getHelpId() {
        return EvoInitializrConstants.HELP_ID_REFERENCE;
    }
}
