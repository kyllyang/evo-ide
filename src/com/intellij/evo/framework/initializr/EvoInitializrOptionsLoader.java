package com.intellij.evo.framework.initializr;

import org.jetbrains.annotations.Nullable;

public class EvoInitializrOptionsLoader {
    @Nullable
    public EvoInitializrOptions loadOptions() {
        return EvoInitializrOptionsLoader.parseJson();
    }

    public static EvoInitializrOptions parseJson() {
        EvoInitializrOptions options = new EvoInitializrOptions();
        return options;
    }
}
