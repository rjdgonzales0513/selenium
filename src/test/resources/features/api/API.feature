@API @All
Feature: User Login

  @get_user
  Scenario: Get Single Users
    When I send a GET request to "/users/2" without authorization
    Then I store key "email" from json path "data.email"
    And the response status code should be 200

  @get_listuser
  Scenario: Get list Users
    When I send a GET request to "/users?page=2" without authorization
    Then I store key "email" from json path "data[1].email"
    And the response status code should be 200

  @post_createuser
  Scenario: create user
    Given I load the request body from file "testdata/api/user.json"
    When I POST request to "/users"
    Then the response status code should be 201
    And the response json path "name" should be "morpheus"
