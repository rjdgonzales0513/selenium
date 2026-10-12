package com.api.utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Thin wrapper around RestAssured's JsonPath so both RESPONSE bodies and
 * REQUEST bodies (captured before they're sent) can be validated with the
 * same JSON-path syntax, e.g. "data.id", "items[0].name", "job".
 */
public class JsonPathValidator {

    public static JsonPath from(Response response) {
        return response.jsonPath();
    }

    public static JsonPath fromJsonString(String json) {
        return new JsonPath(json);
    }

    /** Assert a field in the RESPONSE body equals the expected value. */
    public static void validateResponseField(Response response, String jsonPathExpr, Object expectedValue) {
        Object actual = response.jsonPath().get(jsonPathExpr);
        assertThat("Response JSON path '" + jsonPathExpr + "' mismatch", actual, equalTo(expectedValue));
    }

    /**
     * Assert a NUMERIC field in the RESPONSE body equals the expected int (avoids
     * Integer-vs-String mismatches).
     */
    public static void validateResponseFieldAsInt(Response response, String jsonPathExpr, int expectedValue) {
        Integer actual = response.jsonPath().getInt(jsonPathExpr);
        assertThat("Response JSON path '" + jsonPathExpr + "' mismatch", actual, equalTo(expectedValue));
    }

    /** Assert a field exists (non-null) in the RESPONSE body. */
    public static void assertResponseFieldExists(Response response, String jsonPathExpr) {
        Object actual = response.jsonPath().get(jsonPathExpr);
        assertThat("Response JSON path '" + jsonPathExpr + "' should exist", actual, notNullValue());
    }

    /**
     * Assert a field in the outgoing REQUEST body equals the expected value.
     * Useful for verifying the payload builder produced the JSON you intended
     * before it ever leaves the client (a "contract" check on your own request).
     */
    public static void validateRequestBodyField(String requestBodyJson, String jsonPathExpr, Object expectedValue) {
        Object actual = new JsonPath(requestBodyJson).get(jsonPathExpr);
        assertThat("Request JSON path '" + jsonPathExpr + "' mismatch", actual, equalTo(expectedValue));
    }

    public static boolean hasField(Response response, String jsonPathExpr) {
        return response.jsonPath().get(jsonPathExpr) != null;
    }

    public static String getFieldValue(Response response, String jsonPathExpr) {
        return response.jsonPath().getString(jsonPathExpr);
    }
}
