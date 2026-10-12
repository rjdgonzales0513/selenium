package com.selenium.driver;

import org.openqa.selenium.WebDriver;

import com.common.config.ConfigManager;
import com.common.utils.LoggerUtils;

public class DriverFactory {

    private DriverFactory() {

    }

    public static WebDriver createDriver() {
        WebDriver driver;
        String browser = ConfigManager.getBrowser().toUpperCase();

        LoggerUtils.info(String.format("Starting %s (headless=%s)", browser, ConfigManager.isHeadless()));

        switch (browser) {
            case "CHROME" -> driver = new ChromeDriverImpl().createDriver();
            case "FIREFOX" -> driver = new FirefoxDriverImpl().createDriver();
            default -> throw new IllegalStateException("Unsupported browser: " + browser);
        }

        LoggerUtils.info("WebDriver created successfully");

        return driver;
    }
}
