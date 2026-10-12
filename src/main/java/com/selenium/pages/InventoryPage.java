package com.selenium.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

public class InventoryPage {
    private final WebDriver driver;
    private final By productHeader = By.xpath("//span[text()='Products']");
    private final By sortDropdown = By.cssSelector("select[data-test='product-sort-container']");
    private final By itemPrices = By.cssSelector("div[data-test='inventory-item-price']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return WaitUtils.visible(driver, productHeader).isDisplayed();
    }

    public void addToCart(String item) {
        WaitUtils.clickable(driver, getAddToCartButton(item)).click();
        LoggerUtils.info("Added '" + item + "' to cart");
    }

    /** Selects a sort option by its visible text, e.g. "Price (low to high)". */
    public void sortBy(String option) {
        new Select(WaitUtils.visible(driver, sortDropdown)).selectByVisibleText(option);
        LoggerUtils.info("Sorted products by '" + option + "'");
    }

    public List<BigDecimal> getItemPrices() {
        return WaitUtils.allVisible(driver, itemPrices).stream()
                .map(e -> new BigDecimal(e.getText().replace("$", "").trim()))
                .toList();
    }

    private By getAddToCartButton(String itemName) {
        return By.xpath("//div[text()='" + itemName
                + "']/ancestor::div[@class='inventory_item_label']/following-sibling::div//button");
    }
}
