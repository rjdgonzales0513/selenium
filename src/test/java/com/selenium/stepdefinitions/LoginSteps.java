package com.selenium.stepdefinitions;

import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.common.utils.ExcelUtils;
import com.selenium.driver.DriverManager;
import com.selenium.pages.InventoryPage;
import com.selenium.pages.LoginPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {
    private LoginPage loginPage;
    private InventoryPage homePage;

    @Given("the user is on the login page")
    public void userIsOnLoginPage() {
        loginPage = new LoginPage(DriverManager.getDriver()).open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void userLogsIn(String username, String password) {
        loginPage.login(username, password);
        homePage = new InventoryPage(DriverManager.getDriver());
    }

    @When("I login using test data {string}")
        public void loginUsingTestData(String testDataId) {

        Map<String, String> data =
                ExcelUtils.getTestData("Login", testDataId);

        String username = data.get("Username");
        String password = data.get("Password");

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        loginPage.login(username, password);
        homePage = new InventoryPage(DriverManager.getDriver());
    }

    @Then("the dashboard should be displayed")
    public void dashboardShouldBeDisplayed() {
        Assertions.assertTrue(homePage.isDashboardDisplayed(),
                "Dashboard was not displayed");
    }
}
