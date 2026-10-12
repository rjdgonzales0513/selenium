package com.common.config;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        load();
    }

    private ConfigManager() {
    }

    private static void load() {
        String env = System.getProperty("env", "qa");
        loadFile("config/config.properties");
        loadFile("config/" + env + ".properties");
    }

    private static void loadFile(String path) {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null)
                throw new IllegalStateException("Config not found: " + path);
            PROPERTIES.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load " + path, e);
        }
    }

    public static String get(String key) {

        String systemValue = System.getProperty(key);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String value = PROPERTIES.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("Missing config: " + key);
        }
        return value.trim();
    }

    public static String getBrowser() {
        return get("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static String getEnvironment() {
        return get("env");
    }

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(get("pageLoadTimeout"));
    }

    public static long getTimeout() {
        return Long.parseLong(get("explicitWait"));
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}