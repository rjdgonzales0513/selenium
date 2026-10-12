@Browser @Regression @All
Feature: Checkout information step

  Background:
    Given the user is on the login page
    And the user logs in with username "standard_user" and password "secret_sauce"
    And the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    And the user proceeds to checkout

  @checkout_validation
  Scenario Outline: <id> - Checkout requires <missing>
    When the user enters checkout information "<first>" "<last>" with postal code "<zip>"
    Then the checkout error "<error>" should be displayed

    Examples:
      | id              | missing     | first | last | zip   | error                          |
      | CHECKOUT_TC_001 | first name  |       | Doe  | 12345 | Error: First Name is required  |
      | CHECKOUT_TC_002 | last name   | John  |      | 12345 | Error: Last Name is required   |
      | CHECKOUT_TC_003 | postal code | John  | Doe  |       | Error: Postal Code is required |

  @cancel_checkout
  Scenario: CHECKOUT_TC_004 - Cancel checkout from information step
    When the user cancels checkout
    Then the cart page should be displayed
    And the cart should contain "Sauce Labs Backpack"
