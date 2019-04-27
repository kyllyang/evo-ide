package com.intellij.evo.framework.initializr.maven;

import com.intellij.evo.framework.initializr.EvoInitializrRunConfigUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenConsole;
import org.jetbrains.idea.maven.project.MavenEmbeddersManager;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.utils.MavenProgressIndicator;

public class EvoMavenPostProcessorTask implements MavenProjectsProcessorTask {
    private final Module myModule;

    EvoMavenPostProcessorTask(Module module) {
        this.myModule = module;
    }

    public void perform(Project project, MavenEmbeddersManager embeddersManager, MavenConsole console, MavenProgressIndicator indicator) {
        EvoInitializrRunConfigUtil runConfigUtil = new EvoInitializrRunConfigUtil(this.myModule);
        runConfigUtil.createRunConfig();
    }
}
