package com.selenium.utils;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.Select;

import com.common.utils.LoggerUtils;

/**
 * The single place for browser interactions. Every action waits for the
 * element first (explicit wait from config) and logs what it did, so page
 * objects only describe locators and business actions.
 *
 * elementName is a human-readable label used in logs and the report,
 * e.g. "Login button".
 */
public class WebActionUtils {
    private final WebDriver driver;
    private String parentWindow;

    public WebActionUtils(WebDriver driver) {
        this.driver = driver;
    }

    // ------------------------------------------------------------ navigation

    public void navigateTo(String url) {
        driver.get(url);
        LoggerUtils.info("Navigated to " + url);
    }

    // --------------------------------------------------------------- actions

    public void click(By locator, String elementName) {
        WaitUtils.clickable(driver, locator).click();
        LoggerUtils.info("Clicked " + elementName);
    }

    public void type(By locator, String value, String elementName) {
        WebElement element = WaitUtils.visible(driver, locator);
        element.clear();
        element.sendKeys(value);
        LoggerUtils.info("Typed '" + value + "' into " + elementName);
    }

    /** Same as type() but never logs the value - use for passwords and other secrets. */
    public void typeSecret(By locator, String value, String elementName) {
        WebElement element = WaitUtils.visible(driver, locator);
        element.clear();
        element.sendKeys(value);
        LoggerUtils.info("Typed **** into " + elementName);
    }

    public void selectByVisibleText(By locator, String text, String elementName) {
        new Select(WaitUtils.visible(driver, locator)).selectByVisibleText(text);
        LoggerUtils.info("Selected '" + text + "' in " + elementName);
    }

    // ----------------------------------------------------------------- reads

    /** Waits for the element to be visible and returns its text. */
    public String getText(By locator) {
        return WaitUtils.visible(driver, locator).getText().trim();
    }

    /** Waits until all matching elements are visible (at least one) and returns their texts. */
    public List<String> getVisibleTexts(By locator) {
        return WaitUtils.allVisible(driver, locator).stream().map(e -> e.getText().trim()).toList();
    }

    /** Texts of the matching elements right now, without waiting - may be empty. */
    public List<String> getTexts(By locator) {
        return driver.findElements(locator).stream().map(e -> e.getText().trim()).toList();
    }

    /** True if the element becomes visible within the explicit wait, false otherwise. */
    public boolean isDisplayed(By locator) {
        try {
            return WaitUtils.visible(driver, locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------------------------------------------------------------- frames

    public void switchFrame(By locator, String elementName) {
        driver.switchTo().frame(WaitUtils.visible(driver, locator));
        LoggerUtils.info("Switched to frame: " + elementName);
    }

    public void switchToDefaultFrame() {
        driver.switchTo().defaultContent();
        LoggerUtils.info("Switched to default content");
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        LoggerUtils.info("Switched to parent frame");
    }

    public void switchFrameByIndex(int index) {
        driver.switchTo().frame(index);
        LoggerUtils.info("Switched to frame at index " + index);
    }

    // --------------------------------------------------------------- windows

    public void identifyParentWindow() {
        parentWindow = driver.getWindowHandle();
        LoggerUtils.debug("Parent window handle: " + parentWindow);
    }

    public void switchToParentWindow() {
        driver.switchTo().window(parentWindow);
        LoggerUtils.info("Switched to parent window: " + driver.getTitle());
    }

    public void switchWindowByTitle(String expectedTitle) {
        Set<String> allWindows = driver.getWindowHandles();

        for (String handle : allWindows) {
            driver.switchTo().window(handle);

            // Check if the current window matches the desired title
            if (driver.getTitle().contains(expectedTitle)) {
                LoggerUtils.pass("Switched to window: " + driver.getTitle());
                return;
            }
        }
        LoggerUtils.warn("No window found with title containing: " + expectedTitle);
    }

    public void openNewWindow() {
        driver.switchTo().newWindow(WindowType.WINDOW);
        LoggerUtils.info("Opened new window");
    }

    public void openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
        LoggerUtils.info("Opened new tab");
    }

    public void closeCurrentWindow() {
        driver.close();
        LoggerUtils.info("Closed current window");
    }
}
