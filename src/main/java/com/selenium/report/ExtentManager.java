package com.selenium.report;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentManager {

    private static ExtentReports report;
    private static ExtentSparkReporter extentReporter;

    // one ExtentTest per thread instead of one shared field
    private static final ThreadLocal<ExtentTest> logger = new ThreadLocal<>();

    private static final String reportPath = System.getProperty("user.dir") + File.separator + "ExtentReport.html";

    private ExtentManager() {
    }

    private static synchronized void init() {
        if (report == null) {
            extentReporter = new ExtentSparkReporter(reportPath);
            report = new ExtentReports();
            report.attachReporter(extentReporter);
        }
    }

    public static ExtentTest getLogger() {
        ExtentTest test = logger.get();
        if (test == null) {
            throw new IllegalStateException(
                    "No ExtentTest bound to this thread. Call initScenarioTestAndLog() first.");
        }
        return test;
    }

    public static synchronized void initScenarioTestAndLog(String scenarioName) {
        init();
        ExtentTest test = report.createTest(scenarioName);
        logger.set(test);
    }

    // call in @AfterMethod to avoid thread-pool reuse leaking stale tests
    public static void removeTest() {
        logger.remove();
    }

    public static synchronized void flush() {
        if (report != null) {
            report.flush();
        }
    }
}