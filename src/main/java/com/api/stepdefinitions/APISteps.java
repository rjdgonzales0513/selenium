package com.api.stepdefinitions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.LinkedHashMap;
import java.util.Map;

import com.api.clients.GenericApiClient;
import com.api.context.TestContext;
import com.api.spec.ApiSpec;
import com.api.utils.JsonFileReader;
import com.api.utils.JsonPathValidator;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class APISteps {

    private final GenericApiClient genericApiClient = new GenericApiClient();
    private final TestContext context;
    private Map<String, String> dataMap = new LinkedHashMap<>();

    public APISteps(TestContext context) {
        this.context = context;
    }

    @Given("I set query param {string} to {string}")
    public void setQueryParams(String key, String value) {
        context.addQueryParam(key, value);
    }

    @Given("I set header {string} to {string}")
    public void setHeader(String key, String value) {
        context.addHeader(key, value);
    }

    @Given("I set path param {string} to {string}")
    public void setPathParam(String key, String value) {
        context.addPathParam(key, value);
    }

    @Given("I set the following query params:")
    public void setQueryParams(DataTable table) {
        table.asMap(String.class, String.class).forEach(context::addQueryParam);
    }

    @Given("I set the following headers:")
    public void setHeaders(DataTable table) {
        table.asMap(String.class, String.class).forEach(context::addHeader);
    }

    @Given("I set the following path params:")
    public void setPathParams(DataTable table) {
        table.asMap(String.class, String.class).forEach(context::addPathParam);
    }

    @Given("I load the request body from file {string}")
    public void loadRequestBodyFromFile(String classpathResource) {
        String rawJson = JsonFileReader.readAsString(classpathResource);
        context.setRequestBody(rawJson);
        context.setRequestBodyJson(rawJson);
    }

    @When("I send a GET request to {string} with authorization")
    public void sendGetWithAuth(String endpoint) {
        context.setResponse(genericApiClient.get(
                ApiSpec.requestSpecWithAuth(), endpoint,
                context.getPathParams(), context.getQueryParams(), context.getHeaders()));
    }

    @When("I send a GET request to {string} without authorization")
    public void sendGetWithoutAuth(String endpoint) {
        context.setResponse(genericApiClient.get(
                ApiSpec.requestSpecNoAuth(), endpoint,
                context.getPathParams(), context.getQueryParams(), context.getHeaders()));
    }

    @When("I POST request to {string}")
    public void sendPostWithoutAuth(String endpoint) {
        context.setResponse(genericApiClient.post(ApiSpec.requestSpecNoAuth(), endpoint, context.getRequestBody()));
    }

    @When("I send a {string} request to {string}")
    public void apiCall(String httpMethod, String endpoint) {
        context.setResponse(null);
    }

    @Then("I store key {string} from json path {string}")
    public void storeValue(String key, String jsonPath) {
        String value = JsonPathValidator.getFieldValue(context.getResponse(), jsonPath);
        dataMap.put(key, value);
        System.out.println(value);
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int statusCode) {
        assertThat(context.getResponse().statusCode(), equalTo(statusCode));
    }

    @Then("the response json path {string} should be {string}")
    public void verifyResponseJsonPathString(String jsonPath, String expectedValue) {
        JsonPathValidator.validateResponseField(context.getResponse(), jsonPath, expectedValue);
    }

}
