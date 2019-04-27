package com.intellij.evo.framework.initializr;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class EvoInitializrModuleBuilderPostTask {
    static final ExtensionPointName<EvoInitializrModuleBuilderPostTask> EXTENSION_POINT_NAME = ExtensionPointName.create("com.intellij.evo.framework.initializr.moduleBuilderPostTask");

    public EvoInitializrModuleBuilderPostTask() {
    }

    public abstract boolean runAfterSetup(Module var1);
}
