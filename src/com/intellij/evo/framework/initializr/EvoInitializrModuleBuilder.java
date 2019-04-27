package com.intellij.evo.framework.initializr;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.evo.framework.initializr.dependencies.EvoInitializrDependenciesStep;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.module.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spring.boot.options.SpringBootSettings;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

import javax.swing.*;
import java.io.IOException;

public class EvoInitializrModuleBuilder extends MavenModuleBuilder implements ModuleBuilderListener {
    private EvoInitializrOptions myOptions;

    public EvoInitializrModuleBuilder() {
    }

    public EvoInitializrOptions getOptions() {
        return this.myOptions;
    }

    void setOptions(EvoInitializrOptions options) {
        this.myOptions = options;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        ModuleNameLocationSettings moduleNameLocationSettings = settingsStep.getModuleNameLocationSettings();
        if (moduleNameLocationSettings != null) {
            moduleNameLocationSettings.setModuleName(this.myOptions.artifact);
        }

        return super.modifySettingsStep(settingsStep);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{new EvoInitializrApplicationInfoStep(this, wizardContext), new EvoInitializrDependenciesStep(this)};
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new EvoInitializrSdkChooserStep(this);
    }

    @Nullable
    public String getBuilderId() {
        return EvoInitializrConstants.BUILDER_ID;
    }

    public String getDescription() {
        return EvoInitializrConstants.DESCRIPTION;
    }

    public String getPresentableName() {
        return EvoInitializrConstants.PRESENTABLE_NAME;
    }

    public String getParentGroup() {
        return EvoInitializrConstants.PARENT_GROUP;
    }

    public int getWeight() {
        return EvoInitializrConstants.WEIGHT;
    }

    public Icon getNodeIcon() {
        return AllIcons.General.Information;
    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) {
        Sdk sdk = this.getModuleJdk() != null ? this.getModuleJdk() : ProjectRootManager.getInstance(modifiableRootModel.getProject()).getProjectSdk();
        if (sdk != null) {
            modifiableRootModel.setSdk(sdk);
        }

        LanguageLevelModuleExtension moduleExt = modifiableRootModel.getModuleExtension(LanguageLevelModuleExtension.class);
        if (moduleExt != null && sdk != null) {
            JavaSdkVersion selectedVersion = JavaSdkVersion.fromVersionString(EvoInitializrConstants.JAVA_SDK_VERSION);
            JavaSdkVersion sdkVersion = JavaSdk.getInstance().getVersion(sdk);
            if (selectedVersion != null && sdkVersion != null && sdkVersion.isAtLeast(selectedVersion)) {
                moduleExt.setLanguageLevel(selectedVersion.getMaxLanguageLevel());
            }
        }

        this.doAddContentEntry(modifiableRootModel);
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module module = super.createModule(moduleModel);
        ApplicationManager.getApplication().invokeLater(() -> {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                try {
                    EvoInitializrDownloader downloader = new EvoInitializrDownloader(this);
                    downloader.execute(ProgressManager.getInstance().getProgressIndicator());
                } catch (IOException var2) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("Error: " + var2.getMessage(), "Spring Initializr");
                    });
                }

            }, "Downloading Spring Initializr Template...", true, (Project)null);

            Project project = module.getProject();
            if (SpringBootSettings.getInstance(project).isReformatAfterCreation()) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        (new ReformatCodeProcessor(project, module, false)).run();
                    });
                }, project.getDisposed());
            }

            EvoInitializrModuleBuilderPostTask[] var3 = EvoInitializrModuleBuilderPostTask.EXTENSION_POINT_NAME.getExtensions();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                EvoInitializrModuleBuilderPostTask task = var3[var5];
                if (module.isDisposed()) {
                    return;
                }

                if (!task.runAfterSetup(module)) {
                    break;
                }
            }
        }, ModalityState.current());

        return module;
    }

    @Override
    public void moduleCreated(@NotNull Module module) {

        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();


    }
}
