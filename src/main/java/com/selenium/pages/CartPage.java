package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.selenium.utils.WaitUtils;

public class CartPage {
    private final WebDriver driver;
    private final By cartHeader = By.xpath("//span[text()='Your Cart']");
    
    public CartPage(WebDriver driver) { this.driver = driver; }

    public boolean isCartHeaderDisplayed() {
        return WaitUtils.visible(driver, cartHeader).isDisplayed();
    }

}
