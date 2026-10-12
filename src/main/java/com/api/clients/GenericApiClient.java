package com.api.clients;

import static io.restassured.RestAssured.given;

import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Generic, resource-agnostic HTTP verb wrapper.
 * Resource-specific clients (e.g. UserApiClient) can either extend this
 * or just call it directly for ad-hoc endpoints that don't warrant their
 * own dedicated client class.
 */
public class GenericApiClient {

     // ================================================================ GET
    public Response get(RequestSpecification spec, String endpoint) {
        return get(spec, endpoint, Map.of(), Map.of(), Map.of());
    }

    public Response get(RequestSpecification spec, String endpoint,
                         Map<String, ?> pathParams, Map<String, ?> queryParams) {
        return get(spec, endpoint, pathParams, queryParams, Map.of());
    }

    public Response get(RequestSpecification spec, String endpoint,
                         Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers) {
        return given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers)
                .when().get(endpoint)
                .then().extract().response();
    }

    // =============================================================== POST
    public Response post(RequestSpecification spec, String endpoint, Object body) {
        return post(spec, endpoint, Map.of(), Map.of(), Map.of(), body);
    }

    public Response post(RequestSpecification spec, String endpoint,
                          Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers, Object body) {
        return given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers)
                .body(body)
                .when().post(endpoint)
                .then().extract().response();
    }

    // ================================================================ PUT
    public Response put(RequestSpecification spec, String endpoint,
                         Map<String, ?> pathParams, Object body) {
        return put(spec, endpoint, pathParams, Map.of(), Map.of(), body);
    }

    public Response put(RequestSpecification spec, String endpoint,
                         Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers, Object body) {
        return given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers)
                .body(body)
                .when().put(endpoint)
                .then().extract().response();
    }

    // ============================================================== PATCH
    public Response patch(RequestSpecification spec, String endpoint,
                           Map<String, ?> pathParams, Object body) {
        return patch(spec, endpoint, pathParams, Map.of(), Map.of(), body);
    }

    public Response patch(RequestSpecification spec, String endpoint,
                           Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers, Object body) {
        return given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers)
                .body(body)
                .when().patch(endpoint)
                .then().extract().response();
    }

    // ============================================================= DELETE
    public Response delete(RequestSpecification spec, String endpoint,
                            Map<String, ?> pathParams) {
        return delete(spec, endpoint, pathParams, Map.of(), Map.of());
    }

    public Response delete(RequestSpecification spec, String endpoint,
                            Map<String, ?> pathParams, Map<String, ?> queryParams, Map<String, ?> headers) {
        return given().spec(spec)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .headers(headers)
                .when().delete(endpoint)
                .then().extract().response();
    }
}
