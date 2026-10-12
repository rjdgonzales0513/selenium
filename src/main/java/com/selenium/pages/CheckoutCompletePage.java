package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {
    private final By completeHeader = By.cssSelector("h2[data-test='complete-header']");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public String getConfirmationMessage() {
        return actions.getText(completeHeader);
    }
}
