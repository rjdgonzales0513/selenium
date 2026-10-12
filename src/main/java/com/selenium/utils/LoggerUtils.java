package com.selenium.utils;


import com.aventstack.extentreports.ExtentTest;
import com.selenium.report.ExtentManager;

public final class LoggerUtils {
    private LoggerUtils() {
    }

    public static ExtentTest getLogger() {
        return ExtentManager.getLogger();
    }
}
