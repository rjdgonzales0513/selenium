package com.selenium.driver;

import org.openqa.selenium.WebDriver;

import com.common.utils.LoggerUtils;

public class DriverManager {
    private DriverManager() {

    }

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();

        if (driver == null) {
            throw new IllegalStateException(
                    "No WebDriver for this thread. UI scenarios must be tagged @Browser so WebSteps starts the browser.");
        }

        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            LoggerUtils.info("WebDriver closed");
        }

    }
}
