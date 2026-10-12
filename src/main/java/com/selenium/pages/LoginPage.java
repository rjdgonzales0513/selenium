package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.common.config.ConfigManager;
import com.selenium.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

public class LoginPage {
    private final WebDriver driver;
    private final By username = By.xpath("//input[@id='user-name']");
    private final By password = By.xpath("//input[@id='password']");
    private final By loginBtn = By.xpath("//input[@id='login-button']");

    // private static final ExtentTest log = LoggerUtils.getLogger();

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        LoggerUtils.getLogger().info("Loading Login Page");
        driver.get(ConfigManager.getBaseUrl());

        return this;
    }

    public void login(String user, String pass) {
        WaitUtils.visible(driver, username).sendKeys(user);
        LoggerUtils.getLogger().pass("username has been entered " + user);
        driver.findElement(password).sendKeys(pass);
        LoggerUtils.getLogger().pass("Password has been entered " + pass);
        driver.findElement(loginBtn).click();
    }
}
