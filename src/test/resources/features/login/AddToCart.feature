@Browser @Regression @All
Feature: User Login

  @smoke @add_to_cart
  Scenario: Add to cart
    Given the user is on the login page
    When I login using test data "LOGIN_001"
    Then the dashboard should be displayed
    Given I validate that I am in the inventory page
    When I click the add to cart button for item "Sauce Labs Backpack"
    When I click the cart button
    Then I validate that I am in the Cart page
