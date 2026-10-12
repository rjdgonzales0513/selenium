package com.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {
    private final By cartHeader = By.xpath("//span[text()='Your Cart']");
    private final By itemNames = By.cssSelector("div[data-test='inventory-item-name']");
    private final By checkoutBtn = By.cssSelector("button[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return actions.isDisplayed(cartHeader);
    }

    /** Item names in the cart; empty if the cart is empty. */
    public List<String> getItemNames() {
        isDisplayed();
        return actions.getTexts(itemNames);
    }

    public void remove(String item) {
        actions.click(removeButton(item), "'Remove' for " + item);
    }

    public void checkout() {
        actions.click(checkoutBtn, "Checkout button");
    }

    private By removeButton(String itemName) {
        return By.xpath("//div[text()='" + itemName + "']/ancestor::div[@class='cart_item_label']//button");
    }
}
