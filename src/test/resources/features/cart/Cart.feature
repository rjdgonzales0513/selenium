@Browser @Regression @All
Feature: Cart

  @smoke @add_to_cart
  Scenario: CART_TC_001 - Add an item to the cart
    Given the user is on the login page
    When the user logs in using test data "LOGIN_001"
    Then the products page should be displayed
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should show 1
    When the user opens the cart
    Then the cart page should be displayed
    And the cart should contain "Sauce Labs Backpack"

  @remove_from_cart
  Scenario: CART_TC_002 - Remove an item from the cart
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    And the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    Then the cart should contain "Sauce Labs Backpack"
    When the user removes "Sauce Labs Backpack" from the cart
    Then the cart should not contain "Sauce Labs Backpack"
    And the cart badge should show 0
