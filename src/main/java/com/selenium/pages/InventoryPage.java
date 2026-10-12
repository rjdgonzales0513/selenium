package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.selenium.utils.LoggerUtils;
import com.selenium.utils.ScreenshotUtils;
import com.selenium.utils.WaitUtils;

public class InventoryPage {
    private final WebDriver driver;
    private final By productHeader = By.xpath("//span[text()='Products']");
    private final By cartBtn = By.xpath("//a[@data-test='shopping-cart-link']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardDisplayed() {
        return WaitUtils.visible(driver, productHeader).isDisplayed();
    }

    public void addToCartItem(String item) {
        WaitUtils.clickable(driver, getAddToCartButton(item));

        WaitUtils.visible(driver, getAddToCartButton(item)).click();
        LoggerUtils.getLogger().pass("Successfully clicked add to cart button");
    }

    public void goToCart() {
        WaitUtils.visible(driver, cartBtn).click();
        LoggerUtils.getLogger().fail("Successfully clicked cart button",
                MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.takeScreenshotAsBase64(driver))
                        .build());

    }

    private By getAddToCartButton(String itemName) {
        return By.xpath("//div[text()='" + itemName
                + "']/ancestor::div[@class='inventory_item_label']/following-sibling::div//button");
    }
}
