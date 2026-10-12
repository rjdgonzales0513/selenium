package com.selenium.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.common.report.ExtentManager;
import com.common.utils.LoggerUtils;
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

    /**
     * Takes a screenshot and embeds it in the current step of the Extent report.
     */
    public static void attachToReport(WebDriver driver, String title) {
        attachToReport(takeScreenshotAsBytes(driver), title);
    }

    public static void attachToReport(byte[] screenshot, String title) {
        LoggerUtils.debug("Screenshot attached to report: " + title);
        if (ExtentManager.hasTest()) {
            ExtentManager.getLogger().info(title, MediaEntityBuilder
                    .createScreenCaptureFromBase64String(Base64.getEncoder().encodeToString(screenshot)).build());
        }
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
