package com.selenium.driver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.common.config.ConfigManager;

public class ChromeDriverImpl implements IDriver {

    @Override
    public WebDriver createDriver() {

        ChromeOptions options = new ChromeOptions();

        if (ConfigManager.isHeadless()) {
            options.addArguments("--headless=new");
            // Headless ignores --start-maximized and defaults to a small window; use a desktop size
            options.addArguments("--window-size=1920,1080");
            // Avoids Chrome crashes from the small /dev/shm on Linux CI runners and containers
            options.addArguments("--disable-dev-shm-usage");
        }
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        // Suppress Chrome's password manager prompts, incl. the "password found in a data breach" dialog
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection");

        return new ChromeDriver(options);
    }
}
