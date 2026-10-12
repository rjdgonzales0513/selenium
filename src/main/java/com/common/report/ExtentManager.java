package com.common.report;

import java.io.File;
import java.util.Collection;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.common.config.ConfigManager;

/**
 * Owns the Extent report. Each scenario gets one ExtentTest, and each Gherkin
 * step inside it gets a child node (Given/When/Then), so the report reads like
 * the feature file. Both are ThreadLocal so parallel scenarios don't mix.
 *
 * The lifecycle (create scenario, start/end step, flush) is driven by
 * ScenarioReportListener; framework code only ever logs through LoggerUtils.
 */
public final class ExtentManager {

    private static final String REPORT_PATH = System.getProperty("user.dir") + File.separator + "ExtentReport.html";

    private static ExtentReports report;

    private static final ThreadLocal<ExtentTest> SCENARIO = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> STEP = new ThreadLocal<>();

    private ExtentManager() {
    }

    private static synchronized void init() {
        if (report != null) {
            return;
        }
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
        spark.config().setDocumentTitle("Test Automation Report");
        spark.config().setReportName("UI & API Test Results");
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        spark.config().setEncoding("UTF-8");

        report = new ExtentReports();
        report.attachReporter(spark);
        report.setSystemInfo("Environment", configValue("env"));
        report.setSystemInfo("Browser", configValue("browser"));
        report.setSystemInfo("Headless", configValue("headless"));
        report.setSystemInfo("UI Base URL", configValue("baseUrl"));
        report.setSystemInfo("API Base URL", configValue("base.url"));
        report.setSystemInfo("OS", System.getProperty("os.name"));
        report.setSystemInfo("Java", System.getProperty("java.version"));
        report.setSystemInfo("User", System.getProperty("user.name"));
    }

    private static String configValue(String key) {
        try {
            return ConfigManager.get(key);
        } catch (RuntimeException e) {
            return "n/a";
        }
    }

    public static synchronized void createScenario(String name, Collection<String> tags) {
        init();
        ExtentTest test = report.createTest(name);
        tags.forEach(test::assignCategory);
        SCENARIO.set(test);
    }

    public static void startStep(String keyword, String text) {
        ExtentTest scenario = SCENARIO.get();
        if (scenario == null) {
            return;
        }
        ExtentTest node;
        try {
            node = scenario.createNode(new GherkinKeyword(keyword), text);
        } catch (Exception e) {
            node = scenario.createNode(keyword + " " + text);
        }
        STEP.set(node);
    }

    public static void endStep() {
        STEP.remove();
    }

    public static boolean hasTest() {
        return SCENARIO.get() != null;
    }

    /** The current step node if a step is running, otherwise the scenario itself (e.g. inside hooks). */
    public static ExtentTest getLogger() {
        ExtentTest step = STEP.get();
        if (step != null) {
            return step;
        }
        ExtentTest scenario = SCENARIO.get();
        if (scenario == null) {
            throw new IllegalStateException("No ExtentTest bound to this thread. Is ScenarioReportListener registered?");
        }
        return scenario;
    }

    public static void removeTest() {
        STEP.remove();
        SCENARIO.remove();
    }

    public static synchronized void flush() {
        if (report != null) {
            report.flush();
        }
    }
}
