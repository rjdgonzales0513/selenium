package com.selenium.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.common.config.ConfigManager;

public class FirefoxDriverImpl implements IDriver {
    
    @Override
    public WebDriver createDriver() {

        var options = new FirefoxOptions();

        if(ConfigManager.isHeadless()) {
            options.addArguments("-headless");
        }

        return new FirefoxDriver(options);
    }
}
