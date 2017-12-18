package com.testingbot.teamcity.plugin;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.STestRun;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class TestResultsLink extends SimplePageExtension {
    
    private static final String TB_SESSION_ID = "TestingBotSessionID";
    public TestResultsLink(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor pluginDescriptor) {
        super(pagePlaces);
        setIncludeUrl(pluginDescriptor.getPluginResourcesPath("testResultsLink.jsp"));
        setPlaceId(PlaceId.TEST_DETAILS_BLOCK);
        setPosition(PositionConstraint.first());
        setPluginName("testingbot-results");
        register();
    }
    
    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        Object testAttr = request.getAttribute("test");
        Object testRunsAttr = request.getAttribute("testRuns");
        if (testAttr != null && testRunsAttr != null) {
            STest test = (STest) testAttr;
            List<STestRun> testRuns = (List<STestRun>) testRunsAttr;
            STestRun testRun = null;

            // Get the STestRun for the current STest
            for (STestRun tr : testRuns) {
                if (tr.getTest().getTestNameId() == test.getTestNameId()) {
                    testRun = tr;
                    break;
                }
            }
            if (testRun != null) {
                if (StringUtils.containsIgnoreCase(testRun.getFullText(), TB_SESSION_ID)) {
                    //extract session id
                    String sessionId = StringUtils.substringBetween(testRun.getFullText(), TB_SESSION_ID + "=", " ");
                    if (sessionId == null) {
                        sessionId = StringUtils.substringAfter(testRun.getFullText(), TB_SESSION_ID + "=");
                    }
                    if (sessionId != null && !sessionId.equalsIgnoreCase("null")) {
                        sessionId = StringUtils.trim(StringUtils.chomp(sessionId));
                        model.put("sessionId", sessionId);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isAvailable(@NotNull final HttpServletRequest request) {
        return true;
    }
}
