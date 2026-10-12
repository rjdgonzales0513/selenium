package com.selenium.context;

import org.openqa.selenium.WebDriver;

import com.selenium.driver.DriverManager;
import com.selenium.pages.CartPage;
import com.selenium.pages.CheckoutCompletePage;
import com.selenium.pages.CheckoutInformationPage;
import com.selenium.pages.CheckoutOverviewPage;
import com.selenium.pages.HeaderComponent;
import com.selenium.pages.InventoryPage;
import com.selenium.pages.LoginPage;

/**
 * Per-scenario holder for page objects. cucumber-picocontainer creates one
 * per scenario and injects the same instance into every UI step class, so
 * steps in different classes share pages without static state. Pages are
 * created lazily because the driver only exists once the @Browser hook ran.
 */
public class PageManager {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private HeaderComponent header;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInformationPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;

    private WebDriver driver() {
        return DriverManager.getDriver();
    }

    public LoginPage login() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver());
        }
        return loginPage;
    }

    public InventoryPage inventory() {
        if (inventoryPage == null) {
            inventoryPage = new InventoryPage(driver());
        }
        return inventoryPage;
    }

    public HeaderComponent header() {
        if (header == null) {
            header = new HeaderComponent(driver());
        }
        return header;
    }

    public CartPage cart() {
        if (cartPage == null) {
            cartPage = new CartPage(driver());
        }
        return cartPage;
    }

    public CheckoutInformationPage checkoutInformation() {
        if (checkoutInformationPage == null) {
            checkoutInformationPage = new CheckoutInformationPage(driver());
        }
        return checkoutInformationPage;
    }

    public CheckoutOverviewPage checkoutOverview() {
        if (checkoutOverviewPage == null) {
            checkoutOverviewPage = new CheckoutOverviewPage(driver());
        }
        return checkoutOverviewPage;
    }

    public CheckoutCompletePage checkoutComplete() {
        if (checkoutCompletePage == null) {
            checkoutCompletePage = new CheckoutCompletePage(driver());
        }
        return checkoutCompletePage;
    }
}
