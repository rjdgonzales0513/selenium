package com.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.common.utils.LoggerUtils;
import com.selenium.utils.WaitUtils;

/** The header shown on every page after login: cart link/badge and the side menu. */
public class HeaderComponent {
    private final WebDriver driver;
    private final By cartLink = By.cssSelector("a[data-test='shopping-cart-link']");
    private final By cartBadge = By.cssSelector("span[data-test='shopping-cart-badge']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.cssSelector("a[data-test='logout-sidebar-link']");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
    }

    /** Number shown on the cart icon; 0 when the badge isn't displayed. */
    public int getCartCount() {
        List<WebElement> badge = driver.findElements(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText().trim());
    }

    public void openCart() {
        WaitUtils.clickable(driver, cartLink).click();
        LoggerUtils.info("Opened cart");
    }

    public void logout() {
        WaitUtils.clickable(driver, menuButton).click();
        WaitUtils.clickable(driver, logoutLink).click();
        LoggerUtils.info("Logged out");
    }
}
