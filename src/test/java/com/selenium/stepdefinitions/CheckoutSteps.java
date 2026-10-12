package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import com.common.utils.LoggerUtils;
import com.selenium.context.PageManager;
import com.selenium.pages.CheckoutOverviewPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CheckoutSteps {

    private final PageManager pages;

    public CheckoutSteps(PageManager pages) {
        this.pages = pages;
    }

    @When("the user enters checkout information {string} {string} with postal code {string}")
    public void enterCheckoutInformation(String firstName, String lastName, String postalCode) {
        pages.checkoutInformation().enterInformation(firstName, lastName, postalCode);
    }

    @When("the user cancels checkout")
    public void cancelCheckout() {
        pages.checkoutInformation().cancel();
    }

    @Then("the checkout error {string} should be displayed")
    public void verifyCheckoutError(String expected) {
        assertEquals(expected, pages.checkoutInformation().getErrorMessage(), "Checkout error message");
    }

    @Then("the order overview should list exactly:")
    public void verifyOverviewItems(List<String> expected) {
        assertEquals(expected, pages.checkoutOverview().getItemNames(), "Order overview items");
    }

    @Then("the item total should equal the sum of the item prices")
    public void verifyItemTotal() {
        CheckoutOverviewPage overview = pages.checkoutOverview();
        BigDecimal sum = overview.getItemPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal itemTotal = overview.getItemTotal();
        LoggerUtils.info("Item prices sum: $" + sum + ", item total shown: $" + itemTotal);
        assertEquals(0, sum.compareTo(itemTotal), "Item total $" + itemTotal + " != sum of prices $" + sum);
    }

    @Then("the order total should equal the item total plus tax")
    public void verifyOrderTotal() {
        CheckoutOverviewPage overview = pages.checkoutOverview();
        BigDecimal expected = overview.getItemTotal().add(overview.getTax());
        BigDecimal total = overview.getTotal();
        LoggerUtils.info("Item total + tax: $" + expected + ", total shown: $" + total);
        assertEquals(0, expected.compareTo(total), "Total $" + total + " != item total + tax $" + expected);
    }

    @When("the user finishes the order")
    public void finishOrder() {
        pages.checkoutOverview().finish();
    }

    @Then("the order confirmation {string} should be displayed")
    public void verifyConfirmation(String expected) {
        assertEquals(expected, pages.checkoutComplete().getConfirmationMessage(), "Order confirmation");
    }
}
