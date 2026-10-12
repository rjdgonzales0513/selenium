@Browser @All
Feature: User Login

  @smoke @dataDriven
  Scenario: Data Driven Login
    Given the user is on the login page
    When I login using test data "LOGIN_001"
    Then the dashboard should be displayed

  @smoke
  Scenario: Successful login
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the dashboard should be displayed

  @parallel
  Scenario Outline: Fail login
    Given the user is on the login page
    When the user logs in with username "<username>" and password "<password>"
    Then the dashboard should be displayed

    Examples:
      | username      | password     | page    |
      | standard_user | secret_sauce | Admin   |
      | manager       | manager456   | Manager |
      | employee      | employee789  | Staff   |

  @switchwindowtest
  Scenario: Data Driven Login
    Given the user is on the login page
    When I login using test data "LOGIN_001"
    Then the dashboard should be displayed
    Then I open new window tab
    Then I close current window
    Then I switch to parent window
