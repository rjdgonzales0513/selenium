package com.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The header shown on every page after login: cart link/badge and the side menu. */
public class HeaderComponent extends BasePage {
    private final By cartLink = By.cssSelector("a[data-test='shopping-cart-link']");
    private final By cartBadge = By.cssSelector("span[data-test='shopping-cart-badge']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.cssSelector("a[data-test='logout-sidebar-link']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    /** Number shown on the cart icon; 0 when the badge isn't displayed. */
    public int getCartCount() {
        List<String> badge = actions.getTexts(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0));
    }

    public void openCart() {
        actions.click(cartLink, "Cart icon");
    }

    public void logout() {
        actions.click(menuButton, "Menu button");
        actions.click(logoutLink, "Logout link");
    }
}
