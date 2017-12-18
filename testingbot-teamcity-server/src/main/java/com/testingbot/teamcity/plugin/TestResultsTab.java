package com.testingbot.teamcity.plugin;

import com.testingbot.models.TestingbotTest;
import com.testingbot.models.TestingbotTestBuildCollection;
import com.testingbot.testingbotrest.TestingbotREST;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class TestResultsTab extends ViewLogTab {
    public TestResultsTab(@NotNull PagePlaces pagePlaces, @NotNull final SBuildServer server,
                              @NotNull PluginDescriptor descriptor) {
        super("TestingBot", "testingbot-results", pagePlaces, server);
        setPlaceId(PlaceId.BUILD_RESULTS_TAB);
        setIncludeUrl(descriptor.getPluginResourcesPath("logTab.jsp"));
        register();
    }
    
    @Override
    protected void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request, @NotNull SBuild build) {
        String sessionId = request.getParameter("sessionId");
        if (StringUtils.isNotBlank(sessionId)) {
            // test detail page
            showTestDetails(sessionId, model, request, build);
        } else {
            // test overview
            showTestOverview(model, build);
        }
        
    }
    
    private SBuildFeatureDescriptor getTBBuildFeature(SBuild build) {
        Collection<SBuildFeatureDescriptor> features = build.getBuildType().getBuildFeatures();
        if (features.isEmpty()) return null;
        for (SBuildFeatureDescriptor feature : features) {
            if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE)) {
                return feature;
            }

        }
        return null;
    }
    
    private void showTestDetails(final String sessionId, final Map<String, Object> model,
                                  final HttpServletRequest request, final SBuild build) {
        SBuildFeatureDescriptor tbBuildFeature = getTBBuildFeature(build);
        if (tbBuildFeature == null) {
            return;
        }
        String key = tbBuildFeature.getParameters().get(Constants.TB_KEY);
        String secret = tbBuildFeature.getParameters().get(Constants.TB_SECRET);
        TestingbotREST testingbotREST = new TestingbotREST(key, secret);
        TestingbotTest test = testingbotREST.getTest(sessionId);
        if (test != null) {
            model.put("testUrl", "https://testingbot.com/mini/" + test.getSessionId() + "?ref=teamcity&auth=" + testingbotREST.getAuthenticationHash(test.getSessionId()));
        } else {
            model.put("error", "Could not find test for sessionId " + sessionId);
        }
    }
    
    private void showTestOverview(final Map<String, Object> model, final SBuild build) {
        SBuildFeatureDescriptor tbBuildFeature = getTBBuildFeature(build);
        if (tbBuildFeature == null) {
            return;
        }
        String key = tbBuildFeature.getParameters().get(Constants.TB_KEY);
        String secret = tbBuildFeature.getParameters().get(Constants.TB_SECRET);
        String buildNumber = build.getBuildTypeExternalId() + build.getBuildNumber();
        TestingbotREST testingbotREST = new TestingbotREST(key, secret);
        TestingbotTestBuildCollection collection = testingbotREST.getTestsForBuild(buildNumber);
        model.put("tests", collection.getData());
    }
    
    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest request, @NotNull SBuild build) {
        SBuildFeatureDescriptor tbBuildFeature = getTBBuildFeature(build);
        if (tbBuildFeature == null) {
            return false;
        }
        
        return super.isAvailable(request, build);
    }
}
