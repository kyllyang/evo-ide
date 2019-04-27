package com.intellij.evo.framework.initializr;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.impl.PsiNameHelperImpl;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

public class EvoInitializrApplicationInfoStep extends ModuleWizardStep implements Disposable {
    private static final String NAME = "Na&me";
    private static final String GROUP = "&Group";
    private static final String ARTIFACT = "&Artifact";
    private static final String VERSION = "&Version";
    private static final String PACKAGE = "Pac&kage";
    private final EvoInitializrModuleBuilder myBuilder;
    private final WizardContext myWizardContext;
    private final JBPanel myPanel = new JBPanel(new BorderLayout());
    private JBTextField myGroupField;
    private JBTextField myArtifactField;
    private JBTextField myVersionField;
    private JBTextField myNameField;
    private JBTextField myDescriptionField;
    private JBTextField myPackageNameField;

    public EvoInitializrApplicationInfoStep(EvoInitializrModuleBuilder builder, WizardContext wizardContext) {
        this.myBuilder = builder;
        this.myWizardContext = wizardContext;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (this.myBuilder.getOptions() == null) {
            return false;
        } else {
            validateRequiredField(this.myNameField, "Na&me");
            validateRequiredField(this.myGroupField, "&Group");
            validateRequiredField(this.myArtifactField, "&Artifact");
            validateRequiredField(this.myVersionField, "&Version");
            validateRequiredField(this.myPackageNameField, "Pac&kage");
            validateSanitizedField(this.myArtifactField, "&Artifact");
            String packageNameValue = this.myPackageNameField.getText();
            if (!PsiNameHelperImpl.getInstance().isQualifiedName(packageNameValue)) {
                throw new ConfigurationException(packageNameValue + " is not a valid package name");
            } else {
                Sdk sdk = this.myWizardContext.isCreatingNewProject() ? this.myWizardContext.getProjectJdk() : (Sdk) ObjectUtils.chooseNotNull(this.myBuilder.getModuleJdk(), this.myWizardContext.getProjectJdk());
                JavaSdkVersion wizardVersion = sdk == null ? null : JavaSdk.getInstance().getVersion(sdk);
                if (wizardVersion != null) {
                    JavaSdkVersion selectedVersion = JavaSdkVersion.fromVersionString(EvoInitializrConstants.JAVA_SDK_VERSION);
                    if (selectedVersion != null && !wizardVersion.isAtLeast(selectedVersion)) {
                        throw new ConfigurationException("Selected Java version " + selectedVersion.getDescription() + " is not supported by SDK (maximum " + wizardVersion.getDescription() + ")");
                    }
                }

                return true;
            }
        }
    }

    private static void validateRequiredField(JBTextField field, String name) throws ConfigurationException {
        if (field.getText().isEmpty()) {
            throw new ConfigurationException(UIUtil.removeMnemonic(name) + " must be set");
        }
    }

    private static void validateSanitizedField(JBTextField field, String name) throws ConfigurationException {
        String text = field.getText();
        if (!text.equals(sanitize(text))) {
            throw new ConfigurationException(UIUtil.removeMnemonic(name) + " contains illegal characters");
        }
    }

    private static String sanitize(String input) {
        String fileName = FileUtil.sanitizeFileName(input, false);
        return StringUtil.toLowerCase(fileName.replace(' ', '-'));
    }

    @Override
    public void _init() {
        EvoInitializrOptions options = this.myBuilder.getOptions();
        if (options == null) {
            this.loadSpringInitializrOptions();
        }
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.myGroupField;
    }

    private void loadSpringInitializrOptions() {
        EvoInitializrOptionsLoader loader = new EvoInitializrOptionsLoader();
        EvoInitializrOptions options = loader.loadOptions();
        this.myBuilder.setOptions(options);
        this.createSpringBootPanel(options);
    }

    private void createSpringBootPanel(EvoInitializrOptions options) {
        if (options != null) {
            EvoInitializrFormBuilder builder = new EvoInitializrFormBuilder();
            JLabel label = new JLabel("Project Metadata");
            label.setFont(UIUtil.getLabelFont().deriveFont(1));
            builder.addVerticalSpacing();
            builder.addComponent(label);
            builder.addVerticalSpacing();
            this.myGroupField = builder.addTextField("&Group", EvoInitializrConstants.APPLICATION_GROUP);
            DocumentAdapter updatePackageNameAdapter = new DocumentAdapter() {
                protected void textChanged(@NotNull DocumentEvent e) {
                    String packageName = StringUtil.toLowerCase(EvoInitializrApplicationInfoStep.this.myGroupField.getText()) + "." + EvoInitializrApplicationInfoStep.sanitize(StringUtil.toLowerCase(EvoInitializrApplicationInfoStep.this.myArtifactField.getText()));
                    EvoInitializrApplicationInfoStep.this.myPackageNameField.setText(StringUtil.replace(packageName, "-", ""));
                }
            };
            this.myGroupField.getDocument().addDocumentListener(updatePackageNameAdapter);
            this.myArtifactField = builder.addTextField("&Artifact", EvoInitializrConstants.APPLICATION_ARTIFACT);
            this.myArtifactField.getDocument().addDocumentListener(new DocumentAdapter() {
                protected void textChanged(@NotNull DocumentEvent e) {
                    EvoInitializrApplicationInfoStep.this.myNameField.setText(EvoInitializrApplicationInfoStep.this.myArtifactField.getText());
                }
            });
            this.myArtifactField.getDocument().addDocumentListener(updatePackageNameAdapter);
            this.myVersionField = builder.addTextField("&Version", EvoInitializrConstants.APPLICATION_VERSION);
            this.myNameField = builder.addTextField("Na&me", EvoInitializrConstants.APPLICATION_NAME);
            this.myDescriptionField = builder.addTextField("&Description", EvoInitializrConstants.APPLICATION_DESCRIPTION);
            this.myPackageNameField = builder.addTextField("Pac&kage", EvoInitializrConstants.APPLICATION_PACKAGE);
            this.myPanel.add(ScrollPaneFactory.createScrollPane(builder.getPanel(), true), "North");
            this.myGroupField.selectAll();
            IdeFocusManager.findInstanceByComponent(this.myPanel).requestFocus(this.myGroupField, true);
        }
    }

    @Override
    public JComponent getComponent() {
        return this.myPanel;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void updateDataModel() {
        EvoInitializrOptions.group = this.myGroupField.getText();
        EvoInitializrOptions.artifact = this.myArtifactField.getText();
        EvoInitializrOptions.version = this.myVersionField.getText();
        EvoInitializrOptions.name = this.myNameField.getText();
        EvoInitializrOptions.description = this.myDescriptionField.getText();
        EvoInitializrOptions.packageName = this.myPackageNameField.getText();
    }

    @Override
    public String getHelpId() {
        return EvoInitializrConstants.HELP_ID_REFERENCE;
    }
}
