# Selenium + RestAssured Cucumber Framework

A test automation framework for UI and API tests, written in Gherkin with Cucumber and run on the JUnit Platform.

- **UI tests:** Selenium WebDriver against [Sauce Demo](https://www.saucedemo.com)
- **API tests:** RestAssured against [reqres.in](https://reqres.in/api)
- **Reporting:** ExtentReports, a Cucumber HTML report, and Log4j2 logs

## Tech stack

| Tool | Version |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Selenium | 4.48.0 |
| Cucumber | 7.34.7 |
| JUnit Platform | 6.1.3 |
| RestAssured | 5.5.0 |
| ExtentReports | 5.1.2 |
| Apache POI (Excel test data) | 5.4.1 |

## Prerequisites

- JDK 21
- Maven
- Chrome or Firefox. Selenium Manager downloads the matching driver automatically.

## Running tests

```bash
mvn test -Denv=qa
```

Options you can pass with `-D`:

| Property | Values | Default |
|---|---|---|
| `env` | `qa`, `staging` | `qa` |
| `browser` | `chrome`, `firefox` | `chrome` |
| `headless` | `true`, `false` | `false` |

Example:

```bash
mvn test -Denv=qa -Dbrowser=firefox -Dheadless=true
```

### Running a subset

The runner (`src/test/java/com/selenium/runners/TestRunner.java`) filters on the `@All` tag, and that setting takes precedence over `-Dcucumber.filter.tags` on the command line. To run one tag, change the `FILTER_TAGS_PROPERTY_NAME` value in the runner, for example to `"@smoke"` or `"@get_user"`. You can also run a single feature file from your IDE.

## Configuration

Config files live in `src/test/resources/config/`:

- `config.properties` is always loaded. It holds the browser, wait times, the UI `baseUrl`, and the API `base.url` and auth settings.
- `<env>.properties` is loaded next, based on `-Denv`.

Any key can be overridden with a JVM system property, for example `-DexplicitWait=20`.

## Project structure

```
src/main/java/com/              Framework code (no step definitions)
├── common/
│   ├── config/       ConfigManager
│   ├── listeners/    ScenarioReportListener (Cucumber plugin that drives logging and the report)
│   ├── report/       ExtentManager
│   └── utils/        LoggerUtils, ExcelUtils
├── selenium/    UI layer: driver factory, page objects (BasePage), WebActionUtils, wait/screenshot utils
└── api/         API layer: request specs, logging filter, generic HTTP client, auth, scenario context
src/test/java/com/              Test code
├── common/stepdefinitions/     Hooks (start browser for @Browser, failure screenshot, quit)
├── selenium/
│   ├── context/                PageManager (shares page objects between step classes)
│   ├── runners/                TestRunner (JUnit Platform suite)
│   └── stepdefinitions/        Login, Inventory, Cart, Checkout and Web (tab/window) steps
└── api/stepdefinitions/        APISteps
src/test/resources/
├── features/         api/, login/, products/, cart/, checkout/, e2e/
├── config/           Environment properties
└── testdata/         JSON request bodies and Excel test data per environment
```

## Test suites

The test plan, test scenarios and detailed test cases (steps, data, expected results, execution status) are in `docs/Test_Plan_and_Test_Cases.xlsx`.

| Feature | Scenarios | What it covers |
|---|---|---|
| `e2e/PurchaseFlow.feature` | `E2E_TC_001` | Full purchase: login → add 2 items → cart → checkout info → overview (item total and tax checked) → finish → confirmation → logout |
| `login/Login.feature` | `LOGIN_TC_001`–`004`, `LOGIN_OL_001`–`003` | Excel-driven login, valid login, valid and invalid credentials outline, new-tab handling, locked-out user |
| `products/Products.feature` | `PROD_TC_001` | Sort products by price (low to high) |
| `cart/Cart.feature` | `CART_TC_001`–`002` | Add an item, cart badge, cart contents, remove an item |
| `checkout/CheckoutValidation.feature` | `CHECKOUT_TC_001`–`004` | Required-field errors on the checkout form, cancelling checkout |
| `api/API.feature` | `API_TC_001`–`004` | Get a user, list users, create a user, unknown user (404) |

## Writing tests

### Conventions

- Every feature must be tagged `@All`, or the runner won't pick it up.
- UI features must be tagged `@Browser`. The browser is only started for `@Browser` scenarios.
- Start each scenario name with an ID, such as `CART_TC_001 - ...`. In a Scenario Outline, put `<id>` in the name and add an `id` column to Examples, so each row gets a unique name in logs and reports.
- UI steps are written as "the user …". API steps are written as "I …".
- Page objects extend `BasePage` and perform every browser action through the inherited `actions` object (`WebActionUtils`), never through raw Selenium calls:

  ```java
  public class LoginPage extends BasePage {
      private final By loginBtn = By.id("login-button");

      public void login(String user, String pass) {
          actions.type(username, user, "Username field");
          actions.typeSecret(password, pass, "Password field");
          actions.click(loginBtn, "Login button");
      }
  }
  ```

  `WebActionUtils` waits for each element and logs each action with the readable name you pass in. It covers `navigateTo`, `click`, `type`, `typeSecret`, `selectByVisibleText`, `getText`, `getVisibleTexts`, `getTexts` (no wait, may be empty), `isDisplayed` (returns false instead of throwing), and the frame and window helpers. If a page needs a new kind of interaction, add it to `WebActionUtils` so every page can reuse it.
- UI step classes receive `PageManager` through their constructor (picocontainer creates one per scenario), and get pages from it, for example `pages.cart()`. Don't keep page objects in static fields.

```gherkin
@Browser @All
Feature: Login

  @smoke
  Scenario: LOGIN_TC_002 - Login with valid credentials
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the products page should be displayed
```

### UI steps

| Area | Steps |
|---|---|
| Login | `the user is on the login page`, `the user logs in with username "…" and password "…"`, `the user logs in using test data "LOGIN_001"`, `the login should be "Passed"/"Failed"`, `the login error "…" should be displayed`, `the login page should be displayed` |
| Products & header | `the products page should be displayed`, `the user sorts products by "Price (low to high)"`, `the product prices should be in ascending/descending order`, `the user adds "…" to the cart`, `the user adds the following items to the cart:` (one-column table), `the cart badge should show N`, `the user opens the cart`, `the user logs out` |
| Cart | `the cart page should be displayed`, `the cart should contain "…"`, `the cart should contain exactly:` (table), `the cart should not contain "…"`, `the user removes "…" from the cart`, `the user proceeds to checkout` |
| Checkout | `the user enters checkout information "First" "Last" with postal code "…"`, `the checkout error "…" should be displayed`, `the user cancels checkout`, `the order overview should list exactly:` (table), `the item total should equal the sum of the item prices`, `the order total should equal the item total plus tax`, `the user finishes the order`, `the order confirmation "…" should be displayed` |
| Windows | `the user opens a new tab with url "…"`, `the user closes the current tab`, `the user switches back to the parent window` |

### Data-driven UI tests

`When the user logs in using test data "LOGIN_001"` reads a row from the `Login` sheet of `src/test/resources/testdata/<env>/TestData.xlsx`. It matches the row on the `TestDataId` column.

### API tests

API steps set up the request first, then send it:

```gherkin
@API @All
Feature: Users API

  Scenario: API_TC_003 - Create a user
    Given I load the request body from file "testdata/api/user.json"
    When I send a POST request to "/users"
    Then the response status code should be 201
    And the response json path "name" should be "morpheus"
```

| Purpose | Steps |
|---|---|
| Build the request | `I set query param "k" to "v"`, `I set header …`, `I set path param …`, data-table versions such as `I set the following query params:`, `I load the request body from file "…"`, `I set the request body to:` (doc string) |
| Send it | `I send a GET/POST/PUT/PATCH/DELETE request to "/path"`, optionally followed by `with authorization` (adds the bearer token) |
| Check the response | `the response status code should be 200`, `the response json path "x" should be "text"` or `should be 2`, `the response json path "x" should exist`, `the response body should be an empty JSON object` |
| Reuse values | `I store the response json path "data.email" as "email"`, `the response json path "x" should equal the stored "email"` |

## Logging

Use `LoggerUtils` (`com.common.utils`) for all logging instead of `System.out`:

```java
LoggerUtils.info("Opened login page: " + url);
LoggerUtils.pass("Added '" + item + "' to cart");
LoggerUtils.warn("No window found with title containing: " + title);
LoggerUtils.fail("Something went wrong", exception);
LoggerUtils.debug("Only written to the log file, not the report");
LoggerUtils.block("Request", jsonText);   // titled block of preformatted text
```

Each call is written to:

- **The console and `logs/automation.log`** through Log4j2, under the name of the class that logged it. Every line includes the current scenario name, for example `[Add to cart]`. The log file rolls over daily or at 10 MB. Formats are set in `src/test/resources/log4j2-test.properties`.
- **The Extent report**, under the step that is currently running.

What gets logged automatically:

- **Scenarios:** a START line with tags and feature file location, and an END line with the result and duration.
- **Steps:** every Gherkin step as it runs, and the error when one fails.
- **API calls:** method, URL, status code, response time, request headers and body, and response body. Response headers are logged at DEBUG level only.
- **Screenshots:** one is captured and attached when a UI scenario fails. To add one yourself, call `ScreenshotUtils.attachToReport(driver, "title")`.

Secrets are masked as `****`. This covers `Authorization`, `x-api-key` and cookie headers, and `password`, `token` and `secret` fields in JSON bodies. Login passwords are never logged. In UI code, use `actions.typeSecret(...)` for sensitive inputs.

## Reports

After a run, these files are generated:

| Report | Location |
|---|---|
| Extent report | `ExtentReport.html` |
| Cucumber HTML report | `target/cucumber-report.html` |
| Surefire XML/TXT | `target/surefire-reports/` |
| Log file | `logs/automation.log` |

The Extent report shows each scenario with its Given/When/Then steps underneath. Each step has a pass, fail or skip status and its duration. Failed steps include the full error, and failed UI scenarios include a screenshot. Scenario tags appear as categories, so you can filter by `@smoke`, `@API` and so on. The dashboard shows the environment, browser, headless mode, base URLs, OS, Java version and user.

## Browser notes

- Chrome's password manager is turned off in `ChromeDriverImpl`. Without this, logging into Sauce Demo opens a "Change your password" breach warning. That popup is part of Chrome itself, so Selenium can't click it.

## Known issues

- With a recent Chrome, Selenium 4.48 prints `Unable to find an exact match for CDP version` warnings. Tests still run; upgrading Selenium removes the warnings.
- If you move or delete a `.feature` file, run `mvn clean test`. Otherwise the old copy in `target/test-classes` keeps running.
