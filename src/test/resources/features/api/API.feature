@API @All
Feature: Users API

  @get_user
  Scenario: API_TC_001 - Get a single user
    When I send a GET request to "/users/2"
    Then the response status code should be 200
    And the response json path "data.id" should be 2
    And the response json path "data.email" should exist
    And I store the response json path "data.email" as "email"

  @get_listuser
  Scenario: API_TC_002 - List users on page 2
    Given I set query param "page" to "2"
    When I send a GET request to "/users"
    Then the response status code should be 200
    And the response json path "page" should be 2
    And the response json path "data[1].email" should exist

  @post_createuser
  Scenario: API_TC_003 - Create a user
    Given I load the request body from file "testdata/api/user.json"
    When I send a POST request to "/users"
    Then the response status code should be 201
    And the response json path "name" should be "morpheus"
    And the response json path "id" should exist

  @get_unknown_user
  Scenario: API_TC_004 - Get a user that does not exist
    When I send a GET request to "/users/23"
    Then the response status code should be 404
    And the response body should be an empty JSON object
