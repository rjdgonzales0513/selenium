package com.api.stepdefinitions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.api.clients.GenericApiClient;
import com.api.context.TestContext;
import com.api.spec.ApiSpec;
import com.api.utils.JsonFileReader;
import com.api.utils.JsonPathValidator;
import com.common.utils.LoggerUtils;

import io.cucumber.datatable.DataTable;
import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.specification.RequestSpecification;

public class APISteps {

    private final GenericApiClient client = new GenericApiClient();
    private final TestContext context;

    public APISteps(TestContext context) {
        this.context = context;
    }

    // ------------------------------------------------------- request setup

    @Given("I set query param {string} to {string}")
    public void setQueryParam(String key, String value) {
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
        context.setRequestBody(JsonFileReader.readAsString(classpathResource));
    }

    @Given("I set the request body to:")
    public void setRequestBody(DocString body) {
        context.setRequestBody(body.getContent());
    }

    // ------------------------------------------------------------- sending

    @When("I send a {word} request to {string}")
    public void sendRequest(String method, String endpoint) {
        send(method, ApiSpec.requestSpecNoAuth(), endpoint);
    }

    @When("I send a {word} request to {string} with authorization")
    public void sendRequestWithAuth(String method, String endpoint) {
        send(method, ApiSpec.requestSpecWithAuth(), endpoint);
    }

    private void send(String method, RequestSpecification spec, String endpoint) {
        context.setResponse(client.send(method, spec, endpoint,
                context.getPathParams(), context.getQueryParams(), context.getHeaders(), context.getRequestBody()));
    }

    // ---------------------------------------------------------- assertions

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int statusCode) {
        assertThat("Response status code", context.getResponse().statusCode(), equalTo(statusCode));
    }

    @Then("the response json path {string} should be {string}")
    public void verifyJsonPathString(String jsonPath, String expected) {
        JsonPathValidator.validateResponseField(context.getResponse(), jsonPath, expected);
    }

    @Then("the response json path {string} should be {int}")
    public void verifyJsonPathInt(String jsonPath, int expected) {
        JsonPathValidator.validateResponseFieldAsInt(context.getResponse(), jsonPath, expected);
    }

    @Then("the response json path {string} should exist")
    public void verifyJsonPathExists(String jsonPath) {
        JsonPathValidator.assertResponseFieldExists(context.getResponse(), jsonPath);
    }

    @Then("the response body should be an empty JSON object")
    public void verifyEmptyJsonBody() {
        assertThat("Response body", context.getResponse().jsonPath().getMap("$").isEmpty(), equalTo(true));
    }

    @Then("I store the response json path {string} as {string}")
    public void storeValue(String jsonPath, String key) {
        String value = JsonPathValidator.getFieldValue(context.getResponse(), jsonPath);
        context.storeValue(key, value);
        LoggerUtils.info("Stored '" + key + "' = " + value + " (from json path '" + jsonPath + "')");
    }

    @Then("the response json path {string} should equal the stored {string}")
    public void verifyJsonPathEqualsStored(String jsonPath, String key) {
        JsonPathValidator.validateResponseField(context.getResponse(), jsonPath, context.getStoredValue(key));
    }
}
