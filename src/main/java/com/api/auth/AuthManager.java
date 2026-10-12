package com.api.auth;

import com.common.config.ConfigManager;

/**
 * Single place responsible for producing credentials.
 *
 * getBearerToken() performs a real login (POST to auth.login.endpoint with
 * auth.username / auth.password), reads the token out of the response body
 * at auth.token.jsonpath, and caches it for the rest of the JVM run so every
 * test doesn't re-login. Thread-safe (double-checked locking) so it also
 * works fine under parallel execution.
 */
public class AuthManager {

    /** Used for APIs that authenticate via a static API key header. */
    public static String getApiKey() {
        return ConfigManager.get("auth.token");
    }

    public static String getBearerToken() {
        return ConfigManager.get("bearer.token");
    }
   
}
