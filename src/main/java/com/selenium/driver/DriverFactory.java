package com.selenium.driver;

import org.openqa.selenium.WebDriver;

import com.common.config.ConfigManager;
import com.selenium.utils.LoggerUtils;

public class DriverFactory {

    private DriverFactory() {

    }

    public static WebDriver createDriver() {
        WebDriver driver;
        String browser = ConfigManager.getBrowser().toUpperCase();

        LoggerUtils.getLogger().info("Creating WebDriver");
        LoggerUtils.getLogger().info(String.format("Browser: %s", browser));

        switch (browser) {
            case "CHROME" -> driver = new ChromeDriverImpl().createDriver();
            case "FIREFOX" -> driver = new FirefoxDriverImpl().createDriver();
            default -> {
                System.out.println("not available");
                throw new IllegalStateException();
            } // throw exception
        }

        LoggerUtils.getLogger().info("WebDriver created successfully");

        return driver;
    }
}
