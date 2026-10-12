package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.selenium.utils.WaitUtils;

public class CheckoutCompletePage {
    private final WebDriver driver;
    private final By completeHeader = By.cssSelector("h2[data-test='complete-header']");

    public CheckoutCompletePage(WebDriver driver) {
        this.driver = driver;
    }

    public String getConfirmationMessage() {
        return WaitUtils.visible(driver, completeHeader).getText();
    }
}
