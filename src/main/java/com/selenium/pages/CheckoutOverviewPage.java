package com.selenium.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

/** Checkout step two: order summary with prices, tax and total. */
public class CheckoutOverviewPage {
    private final WebDriver driver;
    private final By itemNames = By.cssSelector("div[data-test='inventory-item-name']");
    private final By itemPrices = By.cssSelector("div[data-test='inventory-item-price']");
    private final By subtotal = By.cssSelector("div[data-test='subtotal-label']");
    private final By tax = By.cssSelector("div[data-test='tax-label']");
    private final By total = By.cssSelector("div[data-test='total-label']");
    private final By finishBtn = By.cssSelector("button[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
    }

    public List<String> getItemNames() {
        return WaitUtils.allVisible(driver, itemNames).stream().map(WebElement::getText).toList();
    }

    public List<BigDecimal> getItemPrices() {
        return WaitUtils.allVisible(driver, itemPrices).stream().map(e -> money(e.getText())).toList();
    }

    public BigDecimal getItemTotal() {
        return money(WaitUtils.visible(driver, subtotal).getText());
    }

    public BigDecimal getTax() {
        return money(WaitUtils.visible(driver, tax).getText());
    }

    public BigDecimal getTotal() {
        return money(WaitUtils.visible(driver, total).getText());
    }

    public void finish() {
        WaitUtils.clickable(driver, finishBtn).click();
        LoggerUtils.info("Clicked finish");
    }

    /** "Item total: $39.98" -> 39.98 */
    private static BigDecimal money(String text) {
        return new BigDecimal(text.substring(text.indexOf('$') + 1).trim());
    }
}
