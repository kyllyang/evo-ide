package com.intellij.evo.framework.initializr;

import java.util.*;

public class EvoInitializrConstants {
    public static final String BUILDER_ID = "EvoInitializr";
    public static final String DESCRIPTION = "Create <b>Evo Framework</b> applications.<br>Every aspect of <b>Evo Framework</b> is specifically designed to maximize developer productivity.";
    public static final String PRESENTABLE_NAME = "Evo Framework";
    public static final String PARENT_GROUP = "Build Tools";
    public static final int WEIGHT = 80;
    public static final String JAVA_SDK_VERSION = "1.8";
    public static final String HELP_ID_REFERENCE = "reference.evo.framework";

    public static final String APPLICATION_GROUP = "com.example";
    public static final String APPLICATION_ARTIFACT = "demo";
    public static final String APPLICATION_VERSION = "1.0.0-SNAPSHOT";
    public static final String APPLICATION_NAME = "demo";
    public static final String APPLICATION_DESCRIPTION = "Demo project for Evo Framework";
    public static final String APPLICATION_PACKAGE = "com.example.demo";

    public static final Map<String, List<String>> DEPENDENCIES_MAP = new LinkedHashMap<>();

    static {
        DEPENDENCIES_MAP.put("EVO 3.0", Arrays.asList(
                "evo-base-spring-boot-starter",
                "evo-base-data-hibernate-spring-boot-starter",
                "evo-base-data-jpa-spring-boot-starter",
                "evo-base-data-mybatis-mapper-spring-boot-starter",
                "evo-base-data-mybatis-plus-spring-boot-starter",
                "evo-base-data-redis-spring-boot-starter"));
    }

    public static final String POM_PLACEHOLDER_PARENT_ARTIFACTID = "\\$\\{parent.artifactid\\}";
    public static final String POM_PLACEHOLDER_PARENT_GROUPID = "\\$\\{parent.groupid\\}";
    public static final String POM_PLACEHOLDER_PARENT_VERSION = "\\$\\{parent.version\\}";
    public static final String POM_PLACEHOLDER_ARTIFACTID = "\\$\\{artifactid\\}";
    public static final String POM_PLACEHOLDER_GROUPID = "\\$\\{groupid\\}";
    public static final String POM_PLACEHOLDER_VERSION = "\\$\\{version\\}";
    public static final String POM_PLACEHOLDER_DEPENDENCIES = "\\$\\{dependencies\\}";
    public static final String POM_PARENT_ARTIFACTID = "evo-parent";
    public static final String POM_PARENT_GROUPID = "com.github.framework";
    public static final String POM_PARENT_VERSION = "1.0.0-SNAPSHOT";
}
