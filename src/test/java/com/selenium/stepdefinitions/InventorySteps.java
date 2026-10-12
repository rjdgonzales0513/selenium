package com.selenium.stepdefinitions;

import com.selenium.driver.DriverManager;
import com.selenium.pages.InventoryPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class InventorySteps {
    
    private InventoryPage inventoryPage;

    @Given("I validate that I am in the inventory page")
    public void validateInventoryPage() {
        inventoryPage = new InventoryPage(DriverManager.getDriver());
    }

    @When("I click the add to cart button for item {string}")
    public void addItemToCart(String item) {
        inventoryPage.addToCartItem(item);
    }

    @When("I click the cart button") 
    public void goToCart() {
        inventoryPage.goToCart();
    }

    
}
