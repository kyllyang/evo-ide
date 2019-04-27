package com.intellij.evo.framework.initializr;

import java.util.HashSet;
import java.util.Set;

public class EvoInitializrOptions {
    public static String group;
    public static String artifact;
    public static String version;
    public static String name;
    public static String description;
    public static String packageName;

    public static final Set<String> selectedDependenciesIds = new HashSet<>();
}
