package com.intellij.evo.framework.initializr.maven;

import com.intellij.evo.framework.initializr.EvoInitializrModuleBuilderPostTask;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.Collections;

public class EvoInitializrMavenModuleBuilderPostTask extends EvoInitializrModuleBuilderPostTask {
    public EvoInitializrMavenModuleBuilderPostTask() {
    }

    public boolean runAfterSetup(Module module) {
        Project project = module.getProject();
        VirtualFile pomXml = null;
        VirtualFile[] var4 = ModuleRootManager.getInstance(module).getContentRoots();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            VirtualFile contentRoot = var4[var6];
            VirtualFile child = contentRoot.findChild("pom.xml");
            if (child != null) {
                pomXml = child;
                break;
            }
        }

        if (pomXml == null) {
            return true;
        } else {
            MavenProjectsManager mavenProjectsManager = MavenProjectsManager.getInstance(project);
            mavenProjectsManager.addManagedFiles(Collections.singletonList(pomXml));
            return false;
        }
    }
}
