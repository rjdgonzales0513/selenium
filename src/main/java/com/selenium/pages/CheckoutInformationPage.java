package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

/** Checkout step one: customer name and postal code. */
public class CheckoutInformationPage {
    private final WebDriver driver;
    private final By firstName = By.cssSelector("input[data-test='firstName']");
    private final By lastName = By.cssSelector("input[data-test='lastName']");
    private final By postalCode = By.cssSelector("input[data-test='postalCode']");
    private final By continueBtn = By.cssSelector("input[data-test='continue']");
    private final By cancelBtn = By.cssSelector("button[data-test='cancel']");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterInformation(String first, String last, String zip) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        LoggerUtils.info(String.format("Entered checkout information: '%s' '%s' '%s'", first, last, zip));
        driver.findElement(continueBtn).click();
    }

    public void cancel() {
        WaitUtils.clickable(driver, cancelBtn).click();
        LoggerUtils.info("Cancelled checkout");
    }

    public String getErrorMessage() {
        return WaitUtils.visible(driver, errorMessage).getText();
    }

    private void type(By field, String value) {
        if (value != null && !value.isEmpty()) {
            WaitUtils.visible(driver, field).sendKeys(value);
        }
    }
}
