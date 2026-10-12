package com.api.context;

import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds state for the duration of a single Cucumber scenario.
 * Injected into every step-definition class by cucumber-picocontainer,
 * so no static/shared fields are needed (safe for parallel execution).
 *
 * queryParams / pathParams / headers accumulate as Gherkin steps like
 * "I set query param ..." or "I set header ..." run, then get read by
 * the When-step that actually fires the request - no new Java method
 * needed just to add one more param or header combination.
 */
public class TestContext {

    private Response response;
    private Object requestBody;
    private String requestBodyJson;

    private final Map<String, Object> queryParams = new LinkedHashMap<>();
    private final Map<String, Object> pathParams = new LinkedHashMap<>();
    private final Map<String, Object> headers = new LinkedHashMap<>();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBodyJson() {
        return requestBodyJson;
    }

    public void setRequestBodyJson(String requestBodyJson) {
        this.requestBodyJson = requestBodyJson;
    }

    public void addQueryParam(String key, Object value) {
        queryParams.put(key, value);
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public void addPathParam(String key, Object value) {
        pathParams.put(key, value);
    }

    public Map<String, Object> getPathParams() {
        return pathParams;
    }

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
