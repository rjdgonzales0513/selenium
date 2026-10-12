package com.selenium.stepdefinitions;

import com.common.utils.LoggerUtils;
import com.selenium.driver.DriverManager;
import com.selenium.utils.ElementActionUtils;

import io.cucumber.java.en.When;

/** Generic browser/window steps that aren't tied to a specific page. */
public class WebSteps {

    private ElementActionUtils actions;

    private ElementActionUtils actions() {
        if (actions == null) {
            actions = new ElementActionUtils(DriverManager.getDriver());
        }
        return actions;
    }

    @When("the user opens a new tab with url {string}")
    public void openNewTab(String url) {
        actions().identifyParentWindow();
        actions().openNewTab();
        DriverManager.getDriver().navigate().to(url);
        LoggerUtils.info("Navigated new tab to " + url);
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
