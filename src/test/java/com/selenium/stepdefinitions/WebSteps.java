package com.selenium.stepdefinitions;

import com.selenium.driver.DriverManager;
import com.selenium.utils.WebActionUtils;

import io.cucumber.java.en.When;

/** Generic browser/window steps that aren't tied to a specific page. */
public class WebSteps {

    private WebActionUtils actions;

    private WebActionUtils actions() {
        if (actions == null) {
            actions = new WebActionUtils(DriverManager.getDriver());
        }
        return actions;
    }

    @When("the user opens a new tab with url {string}")
    public void openNewTab(String url) {
        actions().identifyParentWindow();
        actions().openNewTab();
        actions().navigateTo(url);
    }

    @When("the user closes the current tab")
    public void closeCurrentTab() {
        actions().closeCurrentWindow();
    }

    @When("the user switches back to the parent window")
    public void switchToParentWindow() {
        actions().switchToParentWindow();
    }
}
