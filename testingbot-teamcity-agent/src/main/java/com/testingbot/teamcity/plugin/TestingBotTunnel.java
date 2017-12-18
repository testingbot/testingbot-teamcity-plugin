package com.testingbot.teamcity.plugin;

import java.util.Collection;
import jetbrains.buildServer.agent.AgentBuildFeature;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.jetbrains.annotations.NotNull;
import com.testingbot.tunnel.App;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.util.EventDispatcher;

public class TestingBotTunnel extends AgentLifeCycleAdapter {
    private App tunnel = null;
    
    public TestingBotTunnel(@NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher,
                                  @NotNull ArtifactsWatcher watcher) {
        Loggers.AGENT.info("TestingBotTunnel AgentLifeCycleAdapter ready");
        eventDispatcher.addListener(this);
    }
    
    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        BuildProgressLogger buildLogger = runningBuild.getBuildLogger();
        buildLogger.message("Build Started. Setting TestingBot environment variables");
        Collection<AgentBuildFeature> features = runningBuild.getBuildFeaturesOfType(Constants.BUILD_FEATURE_TYPE);
        if (features.isEmpty()) return;
        for (AgentBuildFeature feature : features) {
            addEnvironmentVariables(runningBuild, feature);
            if (shouldStartTunnel(feature)) {
                try {
                    buildLogger.message("Starting TestingBot Tunnel");
                    tunnel = new App();
                    tunnel.setClientKey(feature.getParameters().get(Constants.TB_KEY));
                    tunnel.setClientSecret(feature.getParameters().get(Constants.TB_SECRET));
                    tunnel.init();
                    tunnel.boot();
                } catch (Exception e) {
                    buildLogger.message("TestingBot Tunnel failure: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    public void beforeBuildFinish(@NotNull final AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        super.beforeBuildFinish(build, buildStatus);
        BuildProgressLogger buildLogger = build.getBuildLogger();
        Collection<AgentBuildFeature> features = build.getBuildFeaturesOfType(Constants.BUILD_FEATURE_TYPE);
        if (features.isEmpty()) return;
        for (AgentBuildFeature feature : features) {
            if (shouldStartTunnel(feature) && tunnel != null) {
                buildLogger.message("Stopping TestingBot Tunnel");
                tunnel.stop();
                tunnel = null;
            }
        }
    }
    
    private boolean shouldStartTunnel(AgentBuildFeature feature) {
        String useTunnel = feature.getParameters().get(Constants.TB_TUNNEL);
        return (useTunnel != null && useTunnel.equalsIgnoreCase("true"));
    }
    
    private void addEnvironmentVariables(AgentRunningBuild runningBuild, AgentBuildFeature feature) {
        runningBuild.addSharedEnvironmentVariable(Constants.TB_KEY, feature.getParameters().get(Constants.TB_KEY));
        runningBuild.addSharedEnvironmentVariable(Constants.TB_SECRET, feature.getParameters().get(Constants.TB_SECRET));
        runningBuild.addSharedEnvironmentVariable(Constants.BUILD_ID, runningBuild.getBuildTypeExternalId() + runningBuild.getBuildNumber());
    }
}
