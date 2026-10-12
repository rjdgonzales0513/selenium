package com.api.spec;

import org.hamcrest.Matchers;

import com.api.auth.AuthManager;
import com.common.config.ConfigManager;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Central place for building RestAssured Request/Response specifications.
 * Keeps every client and step definition free of boilerplate.
 */
public class ApiSpec {

    private static RequestSpecBuilder baseBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url"))
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);
    }

    /**
     * Default "authorized" request spec: logs in (once, cached) and adds
     * "Authorization: Bearer &lt;token&gt;". This is what UserApiClient's
     * *WithAuth methods use.
     *
     * NOTE: reqres.in (the demo API this project ships pointed at) also
     * gates every endpoint behind a static "x-api-key" header regardless of
     * the bearer token, so that header is included here too just so the demo
     * scenarios run out of the box. Against your real API, drop the
     * x-api-key line below - Bearer alone is all you need.
     */
    public static RequestSpecification requestSpecWithAuth() {
        return baseBuilder()
                .addHeader("Authorization", "Bearer " + AuthManager.getBearerToken())
                .addHeader("x-api-key", AuthManager.getApiKey()) // demo-only, see note above
                .build();
    }

    /**
     * Request spec that adds a static "x-api-key" header instead of a bearer token.
     */
    public static RequestSpecification requestSpecWithApiKey() {
        return baseBuilder()
                .addHeader("x-api-key", AuthManager.getApiKey())
                .build();
    }

    /** Request spec with NO authorization headers at all — for negative tests. */
    public static RequestSpecification requestSpecNoAuth() {
        return baseBuilder().build();
    }

    public static ResponseSpecification responseSpecOk() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThan(
                        (long) ConfigManager.getInt("request.timeout", 10000)))
                .build();
    }
}
