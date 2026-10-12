package com.selenium.utils;

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

import java.time.Duration;

public final class WaitUtils {

    
    private WaitUtils() {
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getTimeout()))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void clickable(WebDriver driver, By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getTimeout()))
                .until(ExpectedConditions.elementToBeClickable(locator));
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
