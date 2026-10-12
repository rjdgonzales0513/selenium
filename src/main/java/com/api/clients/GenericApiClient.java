package com.api.clients;

import static io.restassured.RestAssured.given;

import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Generic, resource-agnostic HTTP client. send() covers every verb; the
 * per-verb methods are shortcuts for the common cases.
 */
public class GenericApiClient {

    public Response send(String method, RequestSpecification spec, String endpoint,
            Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers, Object body) {
        RequestSpecification request = given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers);
        if (body != null) {
            request.body(body);
        }

        return switch (method.toUpperCase()) {
            case "GET" -> request.when().get(endpoint);
            case "POST" -> request.when().post(endpoint);
            case "PUT" -> request.when().put(endpoint);
            case "PATCH" -> request.when().patch(endpoint);
            case "DELETE" -> request.when().delete(endpoint);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    public Response get(RequestSpecification spec, String endpoint) {
        return send("GET", spec, endpoint, Map.of(), Map.of(), Map.of(), null);
    }

    public Response post(RequestSpecification spec, String endpoint, Object body) {
        return send("POST", spec, endpoint, Map.of(), Map.of(), Map.of(), body);
    }

    public Response put(RequestSpecification spec, String endpoint, Map<String, ?> pathParams, Object body) {
        return send("PUT", spec, endpoint, pathParams, Map.of(), Map.of(), body);
    }

    public Response patch(RequestSpecification spec, String endpoint, Map<String, ?> pathParams, Object body) {
        return send("PATCH", spec, endpoint, pathParams, Map.of(), Map.of(), body);
    }

    public Response delete(RequestSpecification spec, String endpoint, Map<String, ?> pathParams) {
        return send("DELETE", spec, endpoint, pathParams, Map.of(), Map.of(), null);
    }
}
