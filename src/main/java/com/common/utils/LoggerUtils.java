package com.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.common.report.ExtentManager;

/**
 * Single logging entry point for the framework. Every call goes to Log4j
 * (console + logs/automation.log, under the calling class's logger name) and,
 * when a scenario is running, to the current step in the Extent report.
 */
public final class LoggerUtils {

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private LoggerUtils() {
    }

    public static void debug(String message) {
        log4j().debug(message);
    }

    public static void info(String message) {
        log4j().info(message);
        extent(Status.INFO, message);
    }

    public static void pass(String message) {
        log4j().info(message);
        extent(Status.PASS, message);
    }

    public static void warn(String message) {
        log4j().warn(message);
        extent(Status.WARNING, message);
    }

    public static void fail(String message) {
        log4j().error(message);
        extent(Status.FAIL, message);
    }

    public static void fail(String message, Throwable error) {
        log4j().error(message, error);
        if (ExtentManager.hasTest()) {
            ExtentManager.getLogger().fail(message).fail(error);
        }
    }

    /** Logs a titled block of preformatted text (JSON bodies, headers, etc.). */
    public static void block(String title, String content) {
        log4j().info("{}:{}{}", title, System.lineSeparator(), content);
        if (ExtentManager.hasTest()) {
            ExtentManager.getLogger().info(title).info(MarkupHelper.createCodeBlock(content));
        }
    }

    private static void extent(Status status, String message) {
        if (ExtentManager.hasTest()) {
            ExtentManager.getLogger().log(status, message);
        }
    }

    private static Logger log4j() {
        // Explicit type witness: Eclipse's compiler (VS Code Java) otherwise infers Object here
        Class<?> caller = WALKER.<Class<?>>walk(frames -> frames
                .<Class<?>>map(StackWalker.StackFrame::getDeclaringClass)
                .filter(c -> c != LoggerUtils.class)
                .findFirst()
                .orElse(LoggerUtils.class));
        return LogManager.getLogger(caller);
    }
}
