package com.selenium.pages;

import org.openqa.selenium.WebDriver;

import com.selenium.utils.WebActionUtils;

/**
 * Parent of every page object. Pages define locators and business actions
 * and perform all browser interactions through {@code actions}.
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebActionUtils actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.actions = new WebActionUtils(driver);
    }
}
