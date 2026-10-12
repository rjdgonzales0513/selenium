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
| `env` | `qa` (`staging` isn't set up yet, see Known issues) | `qa` |
| `browser` | `chrome`, `firefox` | `chrome` |
| `headless` | `true`, `false` | `false` |

Example:

```bash
mvn test -Denv=qa -Dbrowser=firefox -Dheadless=true
```

### Running a subset

Use `cucumber.filter.tags` to choose which scenarios run. It defaults to `@All`, which runs everything.

```bash
mvn test -Denv=qa "-Dcucumber.filter.tags=@API"
mvn test -Denv=qa "-Dcucumber.filter.tags=@smoke"
mvn test -Denv=qa "-Dcucumber.filter.tags=@All and not @E2E"
```

In PowerShell, keep the quotes around the whole `-D...` argument. Without them, PowerShell splits it at the dot and Maven fails with `Unknown lifecycle phase ".filter.tags=..."`.

## Configuration

Config files live in `src/test/resources/config/`:

- `config.properties` is always loaded. It holds `browser`, `headless`, `explicitWait` (seconds), the default `env`, and the API settings: `base.url`, `auth.*` and `request.timeout`.
- `<env>.properties` is loaded next, based on `-Denv`, and can override any key. `qa.properties` holds the UI `baseUrl` (`https://www.saucedemo.com/`).

Any key can be overridden with a JVM system property, for example `-DexplicitWait=20`.

## Project structure

```
.github/workflows/tests.yml     GitHub Actions pipeline
docs/Test_Plan_and_Test_Cases.xlsx   Test plan, scenarios and test cases
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

  See [Web actions](#web-actions-webactionutils) for the full list of methods.
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

### Web actions (WebActionUtils)

`com.selenium.utils.WebActionUtils` is the only place that talks to the browser. Every page gets an instance as `actions` from `BasePage`. Each action waits for its element (up to `explicitWait` seconds from config) and logs what it did to the console, `logs/automation.log` and the Extent report, using the `elementName` you pass, for example "Clicked Login button".

| Method | What it does | Waits for |
|---|---|---|
| `navigateTo(url)` | Opens the URL in the current tab | – |
| `click(locator, elementName)` | Clicks the element | Clickable |
| `type(locator, value, elementName)` | Clears the field and types the value; the value is logged | Visible |
| `typeSecret(locator, value, elementName)` | Same as `type`, but logs `****` instead of the value. Use for passwords and tokens | Visible |
| `selectByVisibleText(locator, text, elementName)` | Picks an option in a `<select>` drop-down by its visible text | Visible |
| `getText(locator)` | Returns the element's text, trimmed | Visible |
| `getVisibleTexts(locator)` | Returns the texts of all matching elements. Fails if none appear in time | At least one visible |
| `getTexts(locator)` | Returns the texts of whatever matches right now. May be empty, e.g. an empty cart or a missing badge | No wait |
| `isDisplayed(locator)` | `true` if the element becomes visible, `false` if it doesn't appear in time. Never throws | Visible |
| `switchFrame(locator, elementName)` | Switches into an iframe | Visible |
| `switchFrameByIndex(index)` | Switches into an iframe by its position | – |
| `switchToParentFrame()` | Moves up one frame level | – |
| `switchToDefaultFrame()` | Goes back to the main page | – |
| `identifyParentWindow()` | Remembers the current window so you can return to it | – |
| `openNewTab()` / `openNewWindow()` | Opens and switches to a new tab or window | – |
| `switchWindowByTitle(title)` | Switches to the first window whose title contains `title`; logs a warning if none match | – |
| `switchToParentWindow()` | Returns to the window saved by `identifyParentWindow()` | – |
| `closeCurrentWindow()` | Closes the current tab or window | – |

Choosing a read method:
- **The element must be there:** use `getText` or `getVisibleTexts`, which wait and fail if it never appears.
- **It may be absent:** use `getTexts`, which returns an empty list straight away instead of waiting out the timeout.
- **For assertions:** use `isDisplayed`, so a missing element gives a clear assertion failure rather than a timeout error.

Adding a new action: if a page needs an interaction that isn't listed, such as hover, drag-and-drop or JavaScript click, add it to `WebActionUtils` with a wait and a `LoggerUtils` call, following the existing methods. Then every page can use it.

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

## Continuous integration (GitHub Actions)

`.github/workflows/tests.yml` runs the suite on GitHub's Ubuntu runners with headless Chrome.

| Trigger | What runs |
|---|---|
| Push to `main` | All scenarios (`@All`), qa, Chrome |
| Pull request into `main` | All scenarios (`@All`), qa, Chrome |
| Manual (**Actions → Tests → Run workflow**) | Your choice of tag expression, environment and browser |

A new commit on the same branch or PR cancels the run already in progress.

After each run:
- **Job summary:** the run page shows each scenario with PASSED or FAILED, plus the totals.
- **`test-reports` artifact:** includes `ExtentReport.html`, the Cucumber HTML report, the Surefire results and `logs/automation.log`. It's kept for 14 days. Download it from the run page and open `ExtentReport.html` in a browser.

To make passing tests a merge requirement, add **Tests** as a required status check under **Settings → Branches → Branch protection rules** for `main`.

API tests on CI: reqres.in's 40 requests/day limit applies per IP address, and GitHub-hosted runners share IP addresses with other users. API scenarios can therefore fail with `429` on CI even when nothing is wrong. If that happens often, run UI and API separately with a manual run using `@Browser` or `@API`, or use a reqres.in account and API key.

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

- **The console and `logs/automation.log`** through Log4j2, under the name of the class that logged it. Every line includes the current scenario name, for example `[CART_TC_001 - Add an item to the cart]`. The log file rolls over daily or at 10 MB. Formats are set in `src/test/resources/log4j2-test.properties`.
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

- The `staging` environment isn't set up. `src/test/resources/config/staging.properties` has no `baseUrl` (so UI tests fail with `Missing config: baseUrl`), it points `base.url` at the placeholder `https://staging.example.com`, and there's no `testdata/staging/TestData.xlsx`. Fill these in before using `-Denv=staging` or the pipeline's staging option.
- The files in `src/main/resources/config/` are never used, because the copies in `src/test/resources/config/` take priority on the test classpath.
- reqres.in allows 40 API requests per day per IP address without an account. Once that's used up, API tests fail with status `429` and the message "You've hit the anonymous demo limit"; the limit resets at midnight UTC. Avoid rerunning the API suite over and over, or sign up at reqres.in for a higher limit.
- With a recent Chrome, Selenium 4.48 prints `Unable to find an exact match for CDP version` warnings. Tests still run; upgrading Selenium removes the warnings.
- If you move or delete a `.feature` file, run `mvn clean test`. Otherwise the old copy in `target/test-classes` keeps running.
- On Windows, git ignores case-only renames (`core.ignorecase=true`). Renaming `Testdata.xlsx` to `TestData.xlsx` in Explorer or an IDE changes nothing in git, so Linux CI keeps the old name and fails. Use `git mv -f old New`, or go through a temporary name: `git mv -f Old tmp` then `git mv -f tmp New`.
