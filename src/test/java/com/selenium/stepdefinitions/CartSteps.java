package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.selenium.driver.DriverManager;
import com.selenium.pages.CartPage;

import io.cucumber.java.en.Given;

public class CartSteps {

    private CartPage cartPage;

    @Given("I validate that I am in the Cart page")
    public void validateInventoryPage() {
        cartPage = new CartPage(DriverManager.getDriver());
        assertTrue(cartPage.isCartHeaderDisplayed());
    }

}
