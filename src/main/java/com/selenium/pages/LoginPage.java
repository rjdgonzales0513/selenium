package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.common.config.ConfigManager;
import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

public class LoginPage {
    private final WebDriver driver;
    private final By username = By.xpath("//input[@id='user-name']");
    private final By password = By.xpath("//input[@id='password']");
    private final By loginBtn = By.xpath("//input[@id='login-button']");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        String url = ConfigManager.getBaseUrl();
        driver.get(url);
        LoggerUtils.info("Opened login page: " + url);

        return this;
    }

    public void login(String user, String pass) {
        WaitUtils.visible(driver, username).sendKeys(user);
        LoggerUtils.info("Entered username: " + user);
        driver.findElement(password).sendKeys(pass);
        LoggerUtils.info("Entered password: ****");
        driver.findElement(loginBtn).click();
        LoggerUtils.info("Clicked login button");
    }

    public boolean isDisplayed() {
        return WaitUtils.visible(driver, loginBtn).isDisplayed();
    }

    public String getErrorMessage() {
        return WaitUtils.visible(driver, errorMessage).getText();
    }
}
