@Browser @E2E @All
Feature: End-to-end purchase

  @e2e @smoke
  Scenario: E2E_TC_001 - Standard user buys two items and logs out
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page should be displayed

    When the user adds the following items to the cart:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    Then the cart badge should show 2

    When the user opens the cart
    Then the cart page should be displayed
    And the cart should contain exactly:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |

    When the user proceeds to checkout
    And the user enters checkout information "John" "Doe" with postal code "12345"
    Then the order overview should list exactly:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    And the item total should equal the sum of the item prices
    And the order total should equal the item total plus tax

    When the user finishes the order
    Then the order confirmation "Thank you for your order!" should be displayed
    And the cart badge should show 0

    When the user logs out
    Then the login page should be displayed
