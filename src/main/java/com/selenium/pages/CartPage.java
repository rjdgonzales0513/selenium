package com.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

public class CartPage {
    private final WebDriver driver;
    private final By cartHeader = By.xpath("//span[text()='Your Cart']");
    private final By itemNames = By.cssSelector("div[data-test='inventory-item-name']");
    private final By checkoutBtn = By.cssSelector("button[data-test='checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return WaitUtils.visible(driver, cartHeader).isDisplayed();
    }

    public List<String> getItemNames() {
        WaitUtils.visible(driver, cartHeader);
        return driver.findElements(itemNames).stream().map(WebElement::getText).toList();
    }

    public void remove(String item) {
        WaitUtils.clickable(driver, By.xpath("//div[text()='" + item
                + "']/ancestor::div[@class='cart_item_label']//button")).click();
        LoggerUtils.info("Removed '" + item + "' from cart");
    }

    public void checkout() {
        WaitUtils.clickable(driver, checkoutBtn).click();
        LoggerUtils.info("Clicked checkout");
    }
}
