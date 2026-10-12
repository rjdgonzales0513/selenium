package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.common.utils.ExcelUtils;
import com.common.utils.LoggerUtils;
import com.selenium.context.PageManager;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    private final PageManager pages;

    public LoginSteps(PageManager pages) {
        this.pages = pages;
    }

    @Given("the user is on the login page")
    public void openLoginPage() {
        pages.login().open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void login(String username, String password) {
        pages.login().login(username, password);
    }

    @When("the user logs in using test data {string}")
    public void loginUsingTestData(String testDataId) {
        Map<String, String> data = ExcelUtils.getTestData("Login", testDataId);
        LoggerUtils.info("Using test data '" + testDataId + "' (username: " + data.get("Username") + ")");
        pages.login().login(data.get("Username"), data.get("Password"));
    }

    /** expected is "Passed" (lands on the products page) or "Failed" (login error shown). */
    @Then("the login should be {string}")
    public void verifyLoginResult(String expected) {
        switch (expected.toLowerCase()) {
            case "passed" -> assertTrue(pages.inventory().isDisplayed(), "Products page was not displayed");
            case "failed" -> {
                String error = pages.login().getErrorMessage();
                LoggerUtils.info("Login error shown: " + error);
                assertFalse(error.isBlank(), "Expected a login error message");
            }
            default -> throw new IllegalArgumentException("Expected 'Passed' or 'Failed' but was: " + expected);
        }
    }

    @Then("the login error {string} should be displayed")
    public void verifyLoginError(String expected) {
        assertEquals(expected, pages.login().getErrorMessage(), "Login error message");
    }

    @Then("the login page should be displayed")
    public void verifyLoginPage() {
        assertTrue(pages.login().isDisplayed(), "Login page was not displayed");
    }
}
