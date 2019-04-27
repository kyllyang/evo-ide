package com.intellij.evo.framework.initializr;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.spring.boot.initializr.SpringInitializrOptions.Option;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Url;
import com.intellij.util.Urls;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.ZipUtil;
import com.intellij.util.io.HttpRequests.Request;
import com.intellij.util.io.HttpRequests.RequestProcessor;

import java.io.*;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvoInitializrDownloader {
    private final EvoInitializrModuleBuilder myBuilder;
    private static final String FILENAME = "filename=";

    public EvoInitializrDownloader(EvoInitializrModuleBuilder builder) {
        this.myBuilder = builder;
    }

    static String userAgent() {
        return ApplicationNamesInfo.getInstance().getFullProductName() + "/" + ApplicationInfo.getInstance().getFullVersion();
    }

    void execute(final ProgressIndicator indicator) throws IOException {
        indicator.setText("Preparing Template...");
        String path = this.myBuilder.getContentEntryPath();
        File contentEntryDir = new File(path);

        createStructure(path);

        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_GROUPID, EvoInitializrConstants.POM_PARENT_GROUPID);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_ARTIFACTID, EvoInitializrConstants.POM_PARENT_ARTIFACTID);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_VERSION, EvoInitializrConstants.POM_PARENT_VERSION);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_GROUPID, EvoInitializrOptions.group);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_ARTIFACTID, EvoInitializrOptions.artifact);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_VERSION, EvoInitializrOptions.version);
        createPom(createPath(path, "pom.xml"), "fileTemplates/pom.xml.ft", replaceMap);
        replaceMap.clear();
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_GROUPID, EvoInitializrOptions.group);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_ARTIFACTID, EvoInitializrOptions.artifact);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_VERSION, EvoInitializrOptions.version);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_ARTIFACTID, EvoInitializrOptions.artifact);
        createPom(createPath(path, EvoInitializrConstants.APPLICATION_ARTIFACT + "-api", "pom.xml"), "fileTemplates/api/pom.xml.ft", replaceMap);
        replaceMap.clear();
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_GROUPID, EvoInitializrOptions.group);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_ARTIFACTID, EvoInitializrOptions.artifact);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_PARENT_VERSION, EvoInitializrOptions.version);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_ARTIFACTID, EvoInitializrOptions.artifact);
        replaceMap.put(EvoInitializrConstants.POM_PLACEHOLDER_DEPENDENCIES, EvoInitializrOptions.selectedDependenciesIds.toString());
        createPom(createPath(path, EvoInitializrConstants.APPLICATION_ARTIFACT + "-core", "pom.xml"), "fileTemplates/core/pom.xml.ft", replaceMap);

        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentEntryDir);
        RefreshQueue.getInstance().refresh(false, true, (Runnable)null, new VirtualFile[]{vf});
    }

    private void createStructure(String path) {
        String pathApi = createPath(path, EvoInitializrConstants.APPLICATION_ARTIFACT + "-api");
        String pathApiJava = createPath(ArrayUtil.mergeArrays(new String[]{pathApi, "src", "main", "java"}, EvoInitializrOptions.packageName.split("\\.")));
        String pathCore = createPath(path, EvoInitializrConstants.APPLICATION_ARTIFACT + "-core");
        String pathCoreDocker = createPath(pathCore, "src", "main", "docker");
        String pathCoreJava = createPath(ArrayUtil.mergeArrays(new String[]{pathCore, "src", "main", "java"}, EvoInitializrOptions.packageName.split("\\.")));
        String pathCoreResources = createPath(pathCore, "src", "main", "resources");

        createDirectory(pathApiJava, pathCoreDocker, pathCoreJava, pathCoreResources);
    }

    private void createPom(String path, String fileTemplate, Map<String, String> replaceMap) {
        String pomText;
        try {
            pomText = StreamUtil.readText(this.getClass().getClassLoader().getResourceAsStream(fileTemplate), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("read pom.xml.tf failure");
        }

        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            pomText = pomText.replaceAll(entry.getKey(), entry.getValue());
        }

        try {
            FileUtil.writeToFile(new File(path), pomText);
        } catch (IOException e) {
            throw new RuntimeException("write pom.xml failure");
        }
    }

    private String createPath(String... dirs) {
    //    return StringUtil.join(dirs, File.separator);
        return StringUtil.join(dirs, "/");
    }

    private void createDirectory(String... paths) {
        for (String path : paths) {
            if (new File(path).mkdirs()) {
            //    throw new RuntimeException("create " + path + " failure");
            }
        }
    }
}
