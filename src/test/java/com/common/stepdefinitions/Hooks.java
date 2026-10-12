package com.common.stepdefinitions;

import com.common.utils.LoggerUtils;
import com.selenium.driver.DriverFactory;
import com.selenium.driver.DriverManager;
import com.selenium.utils.ScreenshotUtils;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Browser lifecycle. Report/log lifecycle (scenario start/end, steps, flush)
 * lives in ScenarioReportListener.
 */
public class Hooks {

    @Before("@Browser")
    public void startBrowser() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.hasDriver()) {
                byte[] screenshot = ScreenshotUtils.takeScreenshotAsBytes(DriverManager.getDriver());
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
                ScreenshotUtils.attachToReport(screenshot, "Screenshot at failure");
            }
        } catch (Exception e) {
            LoggerUtils.warn("Failed to capture failure screenshot: " + e.getMessage());
        } finally {
            DriverManager.quitDriver();
        }
    }
}
