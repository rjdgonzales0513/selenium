package com.api.auth;

import static io.restassured.RestAssured.given;

import java.util.Map;

import com.api.filters.ApiLoggingFilter;
import com.common.config.ConfigManager;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

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

    private static volatile String bearerToken;

    private AuthManager() {
    }

    /** Used for APIs that authenticate via a static API key header. */
    public static String getApiKey() {
        return ConfigManager.get("auth.token");
    }

    public static String getBearerToken() {
        if (bearerToken == null) {
            synchronized (AuthManager.class) {
                if (bearerToken == null) {
                    bearerToken = login();
                }
            }
        }
        return bearerToken;
    }

    private static String login() {
        Response response = given()
                .baseUri(ConfigManager.get("base.url"))
                .contentType(ContentType.JSON)
                .filter(new ApiLoggingFilter())
                .header("x-api-key", getApiKey()) // demo-only, required by reqres.in
                .body(Map.of(
                        "email", ConfigManager.get("auth.username"),
                        "password", ConfigManager.get("auth.password")))
                .when().post(ConfigManager.get("auth.login.endpoint"))
                .then().extract().response();

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Login failed with status " + response.statusCode()
                    + ": " + response.asString());
        }

        String token = response.jsonPath().getString(ConfigManager.get("auth.token.jsonpath"));
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("No token found at json path '"
                    + ConfigManager.get("auth.token.jsonpath") + "' in login response: " + response.asString());
        }
        return token;
    }
}
