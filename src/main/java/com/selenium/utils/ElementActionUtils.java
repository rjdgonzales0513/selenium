package com.selenium.utils;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import com.common.utils.LoggerUtils;

public class ElementActionUtils {
    private String parentWindow;
    private WebDriver driver;

    public ElementActionUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void type(By element, String value, String elementName) {
        WaitUtils.visible(driver, element).sendKeys(value);
        LoggerUtils.info("Typed '" + value + "' into " + elementName);
    }

    /** Same as type() but never logs the value - use for passwords and other secrets. */
    public void typeSecret(By element, String value, String elementName) {
        WaitUtils.visible(driver, element).sendKeys(value);
        LoggerUtils.info("Typed **** into " + elementName);
    }

    public void click(By element, String elementName) {
        WaitUtils.visible(driver, element).click();
        LoggerUtils.info("Clicked " + elementName);
    }

    public void switchFrame(By element, String elementName) {

        driver.switchTo().frame(WaitUtils.visible(driver, element));
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
