package com.selenium.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.selenium.constants.FrameworkConstants;

public class ScreenshotUtils {

    public static byte[] takeScreenshotAsBytes(WebDriver driver) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver cannot be null");
        }

        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static String takeScreenshotAsBase64(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    public static String takeScreenshot(
            WebDriver driver,
            String screenshotName) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver cannot be null");
        }

        try {

            Path screenshotDirectory = Paths.get(FrameworkConstants.SCREENSHOT_PATH);

            Files.createDirectories(screenshotDirectory);

            File source = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);

            String fileName = screenshotName + "_" + System.currentTimeMillis() + ".png";

            Path destination = screenshotDirectory.resolve(fileName);

            Files.copy(source.toPath(), destination);

            return destination.toString();

        } catch (IOException e) {

            throw new RuntimeException("Unable to save screenshot: " + screenshotName, e);
        }
    }
}
