package com.selenium.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Checkout step two: order summary with prices, tax and total. */
public class CheckoutOverviewPage extends BasePage {
    private final By itemNames = By.cssSelector("div[data-test='inventory-item-name']");
    private final By itemPrices = By.cssSelector("div[data-test='inventory-item-price']");
    private final By subtotal = By.cssSelector("div[data-test='subtotal-label']");
    private final By tax = By.cssSelector("div[data-test='tax-label']");
    private final By total = By.cssSelector("div[data-test='total-label']");
    private final By finishBtn = By.cssSelector("button[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getItemNames() {
        return actions.getVisibleTexts(itemNames);
    }

    public List<BigDecimal> getItemPrices() {
        return actions.getVisibleTexts(itemPrices).stream().map(CheckoutOverviewPage::money).toList();
    }

    public BigDecimal getItemTotal() {
        return money(actions.getText(subtotal));
    }

    public BigDecimal getTax() {
        return money(actions.getText(tax));
    }

    public BigDecimal getTotal() {
        return money(actions.getText(total));
    }

    public void finish() {
        actions.click(finishBtn, "Finish button");
    }

    /** "Item total: $39.98" -> 39.98 */
    private static BigDecimal money(String text) {
        return new BigDecimal(text.substring(text.indexOf('$') + 1).trim());
    }
}
