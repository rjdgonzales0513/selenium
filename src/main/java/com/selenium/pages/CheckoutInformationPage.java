package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Checkout step one: customer name and postal code. */
public class CheckoutInformationPage extends BasePage {
    private final By firstName = By.cssSelector("input[data-test='firstName']");
    private final By lastName = By.cssSelector("input[data-test='lastName']");
    private final By postalCode = By.cssSelector("input[data-test='postalCode']");
    private final By continueBtn = By.cssSelector("input[data-test='continue']");
    private final By cancelBtn = By.cssSelector("button[data-test='cancel']");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    /** Fills the form and clicks Continue. Empty values leave the field blank (for validation tests). */
    public void enterInformation(String first, String last, String zip) {
        typeIfPresent(firstName, first, "First name field");
        typeIfPresent(lastName, last, "Last name field");
        typeIfPresent(postalCode, zip, "Postal code field");
        actions.click(continueBtn, "Continue button");
    }

    public void cancel() {
        actions.click(cancelBtn, "Cancel button");
    }

    public String getErrorMessage() {
        return actions.getText(errorMessage);
    }

    private void typeIfPresent(By field, String value, String fieldName) {
        if (value != null && !value.isEmpty()) {
            actions.type(field, value, fieldName);
        }
    }
}
