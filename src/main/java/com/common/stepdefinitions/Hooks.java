package com.common.stepdefinitions;

import com.selenium.driver.DriverManager;
import com.selenium.report.ExtentManager;
import com.selenium.utils.ScreenshotUtils;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    @Before(order = 0)
    public void setUp(Scenario scenario) {
        ExtentManager.initScenarioTestAndLog(scenario.getName());
        ExtentManager.getLogger().info("Scenario started: " + scenario.getName());

    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ScreenshotUtils.takeScreenshotAsBytes(DriverManager.getDriver());
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
                ExtentManager.getLogger().fail("Scenario failed")
                        .addScreenCaptureFromBase64String(
                                java.util.Base64.getEncoder().encodeToString(screenshot));
            } else {
                ExtentManager.getLogger().pass("Scenario passed");
            }
        } catch (Exception e) {
            ExtentManager.getLogger().warning("Failed to capture/log failure screenshot: " + e.getMessage());
        } finally {
            ExtentManager.removeTest();
            DriverManager.quitDriver();
        }

    }

    @AfterAll
    public static void afterAll() {
        ExtentManager.flush();
    }
}
