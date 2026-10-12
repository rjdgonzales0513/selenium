package com.selenium.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.common.config.ConfigManager;

public final class WaitUtils {

    private WaitUtils() {
    }

    private static WebDriverWait explicitWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getTimeout()));
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return explicitWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> allVisible(WebDriver driver, By locator) {
        return explicitWait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return explicitWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void fluentWait(WebDriver driver, By locator) {
        try {
            Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                    .withTimeout(Duration.ofSeconds(10L))
                    .pollingEvery(Duration.ofSeconds(5L))
                    .ignoring(NoSuchElementException.class);

            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (NoSuchElementException e) {

        } catch (StaleElementReferenceException ee) {
            fluentWait(driver, locator);
        }

    }
}
