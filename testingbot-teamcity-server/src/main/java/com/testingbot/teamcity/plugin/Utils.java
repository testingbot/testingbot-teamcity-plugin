package com.testingbot.teamcity.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.serverSide.STestRun;

public class Utils {
    public static Map<String, STestRun> processTestResults(final List<STestRun> allTests) {
        Map<String, Long> testCaseMap = new HashMap<String, Long>();
        Map<String, STestRun> testStatusMap = new HashMap<String, STestRun>();

        for (STestRun testRun : allTests) {
            String testCaseName = getTestName(testRun);
            Long testIndex = testCaseMap.containsKey(testCaseName) ? testCaseMap.get(testCaseName) : -1L;
            testCaseMap.put(testCaseName, ++testIndex);

            String testId = String.format("%s{%d}", testCaseName, testIndex);
            if (!testStatusMap.containsKey(testId)) {
                testStatusMap.put(testId, testRun);
            }
        }

        testCaseMap.clear();
        return testStatusMap;
    }
    
    public static String getTestName(final STestRun testRun) {
        return String.format("%s.%s", testRun.getTest().getName().getClassName(), testRun.getTest().getName().getTestMethodName());
    }
}
