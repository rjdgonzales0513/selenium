package com.selenium;

import java.time.Duration;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {

     private static ChromeDriver driver;
    public static void main(String[] args) {
        WebDriver driver = getDriver();
        driver.get("https://www.selenium.dev/documentation/webdriver/drivers/service/");
    }


    public static ChromeDriver getDriver() {
        if (driver == null) {
            ChromeDriverService service =
                    new ChromeDriverService.Builder().build();

            driver = new ChromeDriver(service);
        }

        return driver;
    }

    public static String getLoadedData(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            // Find and click the load button
            WebElement loadButton = driver.findElement(By.id("load-button"));
            loadButton.click();

            // Wait until the button becomes enabled again
            wait.until(d -> {
                WebElement button = d.findElement(By.id("load-button"));
                return button.isEnabled();
            });

            // Return the content as soon as it is available
            WebElement content = driver.findElement(By.id("content"));
            return content.getText();

        } catch (TimeoutException e) {
            // Button did not become enabled within 2 seconds
            return null;
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}