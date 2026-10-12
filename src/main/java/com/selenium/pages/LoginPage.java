package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.common.config.ConfigManager;

public class LoginPage extends BasePage {
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        actions.navigateTo(ConfigManager.getBaseUrl());
        return this;
    }

    public void login(String user, String pass) {
        actions.type(username, user, "Username field");
        actions.typeSecret(password, pass, "Password field");
        actions.click(loginBtn, "Login button");
    }

    public boolean isDisplayed() {
        return actions.isDisplayed(loginBtn);
    }

    public String getErrorMessage() {
        return actions.getText(errorMessage);
    }
}
