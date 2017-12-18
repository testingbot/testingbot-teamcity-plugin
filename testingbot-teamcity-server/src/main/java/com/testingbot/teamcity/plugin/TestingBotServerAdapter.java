package com.testingbot.teamcity.plugin;

import com.testingbot.models.TestingbotTest;
import com.testingbot.teamcity.plugin.Constants;
import com.testingbot.testingbotrest.TestingbotREST;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.buildLog.LogMessage;
import org.apache.commons.lang.StringUtils;

public class TestingBotServerAdapter extends BuildServerAdapter {

    private static final String TB_SESSION_ID = "TestingBotSessionID";
    private final SBuildServer sBuildServer;
    private static final Logger logger = Logger.getLogger(TestingBotServerAdapter.class);

    public TestingBotServerAdapter(SBuildServer sBuildServer) {
        this.sBuildServer = sBuildServer;
    }

    public void register() {
        sBuildServer.addListener(this);
    }

    @Override
    public void buildFinished(SRunningBuild build) {
        super.buildFinished(build);
        ArrayList<String> sessionIds = new ArrayList();
        Iterator<LogMessage> iterator = build.getBuildLog().getMessagesIterator();
        logger.info("Build finished");
        while (iterator.hasNext()) {
            LogMessage logMessage = iterator.next();
            String line = logMessage.getText();
            logger.info("Scanning line " + line);
            if (StringUtils.containsIgnoreCase(line, TB_SESSION_ID)) {
                //extract session id
                String sessionId = StringUtils.substringBetween(line, TB_SESSION_ID + "=", " ");
                if (sessionId == null) {
                    sessionId = StringUtils.substringAfter(line, TB_SESSION_ID + "=");
                }
                if (sessionId != null && !sessionId.equalsIgnoreCase("null")) {
                    sessionId = StringUtils.trim(StringUtils.chomp(sessionId));
                    sessionIds.add(sessionId);
                    sendBuildNumber(build, sessionId);
                }
            }
        }

        build.setTags(sessionIds);
    }

    private void sendBuildNumber(SRunningBuild build, String sessionId) {
        if (build.getBuildType() == null) {
            return;
        }
        Collection<SBuildFeatureDescriptor> features = build.getBuildType().getBuildFeatures();
        if (features.isEmpty()) {
            return;
        }
        for (SBuildFeatureDescriptor feature : features) {
            if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE)) {
                TestingbotREST testingbotREST = new TestingbotREST(getKey(feature), getSecret(feature));
                TestingbotTest test = testingbotREST.getTest(sessionId);
                String buildNumber = build.getBuildTypeExternalId() + build.getBuildNumber();
                logger.info("Setting build number " + buildNumber + " for test " + sessionId + " user: " + getKey(feature));
                test.setBuild(buildNumber);
                if (test.isSuccess() == false) {
                    test.setSuccess(build.getStatusDescriptor().getStatus().isSuccessful());
                }
                testingbotREST.updateTest(test);
            }
        }
    }

    private String getKey(SBuildFeatureDescriptor feature) {
        return feature.getParameters().get(Constants.TB_KEY);
    }

    private String getSecret(SBuildFeatureDescriptor feature) {
        return feature.getParameters().get(Constants.TB_SECRET);
    }

}
