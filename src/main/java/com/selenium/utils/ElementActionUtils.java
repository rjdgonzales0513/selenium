package com.selenium.utils;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import com.aventstack.extentreports.ExtentTest;

public class ElementActionUtils {
    private String parentWindow;
    private WebDriver driver;
    private static ExtentTest logger = LoggerUtils.getLogger();

    public ElementActionUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void type(By element, String value, String elementName) {
        WaitUtils.visible(driver, element).sendKeys(value);
        logger.info(elementName + " has inputted " + value);
    }

    public void click(By element, String elementName) {
        WaitUtils.visible(driver, element).click();
        logger.info(elementName + " has been clicked");
    }

    public void switchFrame(By element, String elementName) {

        driver.switchTo().frame(WaitUtils.visible(driver, element));
        logger.info(elementName + " has been switch to this frame.");
    }

    public void switchToDefaultFrame() {
        driver.switchTo().defaultContent();
        logger.info("Frame has been switch to default frame.");
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        logger.info("Frame has been switch to parent frame.");
    }

    public void switchFrameByIndex(int index) {
        driver.switchTo().frame(index);
        logger.info(String.format("Frame has been switch to %d frame.", index));
    }

    public void indentifyParentWindow() {
        parentWindow = driver.getWindowHandle();
    }

    public void switchToParentWindow() {

        driver.switchTo().window(parentWindow);
    }

    public void switchWindowByTitle(String expectedTitle) {
        Set<String> allWindows = driver.getWindowHandles();

        for (String handle : allWindows) {
            driver.switchTo().window(handle);

            // Check if the current window matches the desired title
            if (driver.getTitle().contains(expectedTitle)) {
                logger.pass("Switched to window: " + driver.getTitle());
                break;
            }
        }
    }

    public void openNewWindow() {
        driver.switchTo().newWindow(WindowType.WINDOW);
    }

    public void openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    public void closeCurrentWindow() {
        driver.close();
    }

}
