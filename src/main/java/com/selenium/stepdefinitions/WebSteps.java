package com.selenium.stepdefinitions;

import com.selenium.driver.DriverFactory;
import com.selenium.driver.DriverManager;
import com.selenium.utils.ElementActionUtils;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WebSteps {
    ElementActionUtils actionUtils;

    @Before(value = "@Browser", order = 1)
    public void init() {
        DriverManager.setDriver(DriverFactory.createDriver());
        actionUtils = new ElementActionUtils(DriverManager.getDriver());
        System.out.println("works in main java");
    }

    @When("I open new window tab")
    public void openNewWindowTab() {

        actionUtils.indentifyParentWindow();
        actionUtils.openNewTab();
        DriverManager.getDriver().navigate().to("https://www.google.com/");
    }

    @Then("I close current window")
    public void closeWindow() {
        actionUtils.closeCurrentWindow();
    }

    @Then("I switch to parent window")
    public void parentWindow() {
        actionUtils.switchToParentWindow();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
