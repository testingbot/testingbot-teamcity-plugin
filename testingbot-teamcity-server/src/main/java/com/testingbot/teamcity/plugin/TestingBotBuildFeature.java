package com.testingbot.teamcity.plugin;

import java.util.Map;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public class TestingBotBuildFeature extends BuildFeature {

    private final PluginDescriptor pluginDescriptor;
    
    public TestingBotBuildFeature(@NotNull final SBuildServer server, @NotNull final PluginDescriptor pluginDescriptor) {
        super();
        this.pluginDescriptor = pluginDescriptor;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        boolean success = params.containsKey(Constants.TB_KEY) &&
                params.containsKey(Constants.TB_SECRET);
        if (!success) {
            return "Please fill in your TestingBot Key and Secret";
        }
        
        return "";
    }

    @Override
    public String getType() {
        return Constants.BUILD_FEATURE_TYPE;
    }

    @Override
    public String getDisplayName() {
        return "TestingBot";
    }

    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("settings.jsp");
    }
    
    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return false;
    }
}
