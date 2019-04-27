package com.intellij.evo.framework.initializr;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiClass;
import com.intellij.spring.boot.application.SpringBootApplicationService;
import com.intellij.spring.boot.options.SpringBootSettings;
import com.intellij.spring.boot.run.SpringBootApplicationConfigurationTypeBase;
import com.intellij.spring.boot.run.SpringBootApplicationRunConfigurationBase;
import com.intellij.util.containers.ContainerUtil;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EvoInitializrRunConfigUtil {
    private static final Logger LOG = Logger.getInstance("#com.intellij.evo.framework.initializr.EvoInitializrRunConfigUtil");
    private final Module myModule;
    private final RunManager myRunManager;
    private final SpringBootApplicationConfigurationTypeBase mySpringBootType;

    public EvoInitializrRunConfigUtil(Module module) {
        this.myModule = module;
        this.myRunManager = RunManager.getInstance(this.myModule.getProject());
        this.mySpringBootType = SpringBootApplicationConfigurationTypeBase.getInstance();
    }

    public void createRunConfig() {
        SpringBootSettings configuration = SpringBootSettings.getInstance(this.myModule.getProject());
        if (configuration.isAutoCreateRunConfiguration()) {
            DumbService.getInstance(this.myModule.getProject()).runReadActionInSmartMode(new Runnable() {
                @Override
                public void run() {
                    createSpringBootRunConfiguration();
                }
            });
        }
    }

    private void createSpringBootRunConfiguration() {
        List<PsiClass> applications = SpringBootApplicationService.getInstance().getSpringApplications(this.myModule);
        if (applications.size() == 1) {
            PsiClass springBootApp = applications.get(0);
            if (SpringBootApplicationService.getInstance().hasMainMethod(springBootApp) && !this.hasSpringBootRunConfiguration(springBootApp)) {
                this.createSpringBootRunConfiguration(springBootApp);
            }
        }
    }

    private void createSpringBootRunConfiguration(PsiClass springBootApp) {
        try {
            RunnerAndConfigurationSettings settings = this.myRunManager.createConfiguration("", this.mySpringBootType.getDefaultConfigurationFactory());
            SpringBootApplicationRunConfigurationBase newRunConfig = (SpringBootApplicationRunConfigurationBase)settings.getConfiguration();
            newRunConfig.setModule(this.myModule);
            newRunConfig.setSpringBootMainClass(springBootApp.getQualifiedName());
            settings.setName(newRunConfig.suggestedName());
            this.myRunManager.setUniqueNameIfNeed(settings);
            this.myRunManager.addConfiguration(settings);
            if (this.myRunManager.getAllSettings().size() == 1) {
                this.myRunManager.setSelectedConfiguration(settings);
            }
        } catch (Throwable var4) {
            LOG.error("Error creating Spring Boot run configuration for " + springBootApp, var4);
        }

    }

    private boolean hasSpringBootRunConfiguration(@NotNull PsiClass applicationClass) {
        List<SpringBootApplicationRunConfigurationBase> allSpringBootRunConfigs = ContainerUtil.findAll(this.myRunManager.getConfigurationsList(this.mySpringBootType), SpringBootApplicationRunConfigurationBase.class);
        Iterator var3 = allSpringBootRunConfigs.iterator();

        SpringBootApplicationRunConfigurationBase config;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            config = (SpringBootApplicationRunConfigurationBase)var3.next();
        } while(!config.getSpringBootMainClass().equals(applicationClass.getQualifiedName()));

        return true;
    }
}
