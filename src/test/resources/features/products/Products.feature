@Browser @Regression @All
Feature: Products

  @sorting
  Scenario: PROD_TC_001 - Sort products by price (low to high)
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page should be displayed
    When the user sorts products by "Price (low to high)"
    Then the product prices should be in ascending order
