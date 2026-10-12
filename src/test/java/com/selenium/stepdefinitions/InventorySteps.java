package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import com.common.utils.LoggerUtils;
import com.selenium.context.PageManager;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Products page plus the header actions (cart icon, menu) available after login. */
public class InventorySteps {

    private final PageManager pages;

    public InventorySteps(PageManager pages) {
        this.pages = pages;
    }

    @Then("the products page should be displayed")
    public void verifyProductsPage() {
        assertTrue(pages.inventory().isDisplayed(), "Products page was not displayed");
    }

    @When("the user adds {string} to the cart")
    public void addToCart(String item) {
        pages.inventory().addToCart(item);
    }

    @When("the user adds the following items to the cart:")
    public void addItemsToCart(List<String> items) {
        items.forEach(pages.inventory()::addToCart);
    }

    @When("the user sorts products by {string}")
    public void sortProducts(String option) {
        pages.inventory().sortBy(option);
    }

    /** order is "ascending" or "descending". */
    @Then("the product prices should be in {word} order")
    public void verifyPriceOrder(String order) {
        List<BigDecimal> actual = pages.inventory().getItemPrices();
        Comparator<BigDecimal> comparator = switch (order.toLowerCase()) {
            case "ascending" -> Comparator.naturalOrder();
            case "descending" -> Comparator.reverseOrder();
            default -> throw new IllegalArgumentException("Expected 'ascending' or 'descending' but was: " + order);
        };
        List<BigDecimal> expected = actual.stream().sorted(comparator).toList();
        LoggerUtils.info("Product prices shown: " + actual);
        assertEquals(expected, actual, "Product prices should be in " + order + " order");
    }

    @Then("the cart badge should show {int}")
    public void verifyCartBadge(int expected) {
        assertEquals(expected, pages.header().getCartCount(), "Cart badge count");
    }

    @When("the user opens the cart")
    public void openCart() {
        pages.header().openCart();
    }

    @When("the user logs out")
    public void logout() {
        pages.header().logout();
    }
}
