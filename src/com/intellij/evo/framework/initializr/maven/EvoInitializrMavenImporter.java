package com.intellij.evo.framework.initializr.maven;

import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.module.Module;
import com.intellij.util.containers.ContainerUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

public class EvoInitializrMavenImporter extends MavenImporter {
    private static final List<String> SUPPORTED_PACKAGINGS = ContainerUtil.immutableList(new String[]{"jar", "war"});

    public EvoInitializrMavenImporter() {
        super("org.springframework.boot", "spring-boot-maven-plugin");
    }

    public void getSupportedPackagings(Collection<String> result) {
        result.addAll(SUPPORTED_PACKAGINGS);
    }

    public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges changes, IdeModifiableModelsProvider modifiableModelsProvider) {
    }

    public void process(IdeModifiableModelsProvider modifiableModelsProvider, Module module, MavenRootModelAdapter rootModel, MavenProjectsTree mavenModel, MavenProject mavenProject, MavenProjectChanges changes, Map<MavenProject, String> mavenProjectToModuleName, List<MavenProjectsProcessorTask> postTasks) {
        String skip = this.findConfigValue(mavenProject, "skip");
        if (!"true".equals(skip)) {
            postTasks.add(new EvoMavenPostProcessorTask(module));
        }
    }
}
