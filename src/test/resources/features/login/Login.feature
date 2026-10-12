@Browser @All
Feature: Login

  @smoke @dataDriven
  Scenario: LOGIN_TC_001 - Login with Excel test data
    Given the user is on the login page
    When the user logs in using test data "LOGIN_001"
    Then the products page should be displayed

  @smoke
  Scenario: LOGIN_TC_002 - Login with valid credentials
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page should be displayed

  @parallel
  Scenario Outline: <id> - Login as <username> (<expected>)
    Given the user is on the login page
    When the user logs in with username "<username>" and password "<password>"
    Then the login should be "<expected>"

    Examples:
      | id           | username      | password     | expected |
      | LOGIN_OL_001 | standard_user | secret_sauce | Passed   |
      | LOGIN_OL_002 | manager       | manager456   | Failed   |
      | LOGIN_OL_003 | employee      | employee789  | Failed   |

  @switchwindowtest
  Scenario: LOGIN_TC_003 - Open and close a new tab after login
    Given the user is on the login page
    When the user logs in using test data "LOGIN_001"
    Then the products page should be displayed
    When the user opens a new tab with url "https://www.google.com/"
    And the user closes the current tab
    And the user switches back to the parent window
    Then the products page should be displayed

  @locked_out
  Scenario: LOGIN_TC_004 - Login as locked-out user
    Given the user is on the login page
    When the user logs in with username "locked_out_user" and password "secret_sauce"
    Then the login error "Epic sadface: Sorry, this user has been locked out." should be displayed
    And the login page should be displayed
