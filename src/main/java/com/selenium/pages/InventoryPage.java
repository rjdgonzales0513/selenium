package com.selenium.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {
    private final By productHeader = By.xpath("//span[text()='Products']");
    private final By sortDropdown = By.cssSelector("select[data-test='product-sort-container']");
    private final By itemPrices = By.cssSelector("div[data-test='inventory-item-price']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return actions.isDisplayed(productHeader);
    }

    public void addToCart(String item) {
        actions.click(addToCartButton(item), "'Add to cart' for " + item);
    }

    /** Selects a sort option by its visible text, e.g. "Price (low to high)". */
    public void sortBy(String option) {
        actions.selectByVisibleText(sortDropdown, option, "Sort dropdown");
    }

    public List<BigDecimal> getItemPrices() {
        return actions.getVisibleTexts(itemPrices).stream()
                .map(text -> new BigDecimal(text.replace("$", "")))
                .toList();
    }

    private By addToCartButton(String itemName) {
        return By.xpath("//div[text()='" + itemName
                + "']/ancestor::div[@class='inventory_item_label']/following-sibling::div//button");
    }
}
