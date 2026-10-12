package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.selenium.context.PageManager;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {

    private final PageManager pages;

    public CartSteps(PageManager pages) {
        this.pages = pages;
    }

    @Then("the cart page should be displayed")
    public void verifyCartPage() {
        assertTrue(pages.cart().isDisplayed(), "Cart page was not displayed");
    }

    @Then("the cart should contain {string}")
    public void verifyCartContains(String item) {
        List<String> items = pages.cart().getItemNames();
        assertTrue(items.contains(item), "Cart items " + items + " should contain '" + item + "'");
    }

    @Then("the cart should not contain {string}")
    public void verifyCartDoesNotContain(String item) {
        List<String> items = pages.cart().getItemNames();
        assertFalse(items.contains(item), "Cart items " + items + " should not contain '" + item + "'");
    }

    @When("the user removes {string} from the cart")
    public void removeFromCart(String item) {
        pages.cart().remove(item);
    }

    @Then("the cart should contain exactly:")
    public void verifyCartContents(List<String> expected) {
        assertEquals(expected, pages.cart().getItemNames(), "Cart contents");
    }

    @When("the user proceeds to checkout")
    public void checkout() {
        pages.cart().checkout();
    }
}
