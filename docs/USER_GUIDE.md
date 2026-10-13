# User Guide

This guide walks through the common tasks in this framework: running tests, adding UI and API scenarios, adding pages and actions, and working out why a run failed.

The [README](../README.md) has the reference tables: run options, the full list of steps, every `WebActionUtils` method, and the reports. The reasons behind the design are in the [Architecture Decision Records](adr/README.md).

## Contents

1. [Setup](#1-setup)
2. [Running tests](#2-running-tests)
3. [How a run works](#3-how-a-run-works)
4. [Writing a UI scenario](#4-writing-a-ui-scenario)
5. [Adding a page object](#5-adding-a-page-object)
6. [Adding step definitions](#6-adding-step-definitions)
7. [Adding a browser action](#7-adding-a-browser-action)
8. [Data-driven tests with Excel](#8-data-driven-tests-with-excel)
9. [Writing an API scenario](#9-writing-an-api-scenario)
10. [Configuration and environments](#10-configuration-and-environments)
11. [Logging, reports and screenshots](#11-logging-reports-and-screenshots)
12. [Troubleshooting](#12-troubleshooting)
13. [Checklist for a new scenario](#13-checklist-for-a-new-scenario)

---

## 1. Setup

You need JDK 21, Maven 3.9 or later, and Chrome or Firefox. You don't need to download a browser driver: Selenium Manager fetches the matching one on the first run.

```bash
git clone https://github.com/rjdgonzales0513/selenium.git
cd selenium
mvn test-compile          # downloads dependencies and checks that everything compiles
```

**IDE:** import the project as a Maven project. To run a single feature from the IDE, run `TestRunner` (`src/test/java/com/selenium/runners/TestRunner.java`) with a tag filter, as shown in the next section. If you run a `.feature` file directly with an IDE Cucumber plugin, you skip `TestRunner` and its plugin list. The scenario still runs and still logs to the console and `logs/automation.log`, but no Extent report is produced.

## 2. Running tests

```bash
mvn test -Denv=qa                                        # everything tagged @All
mvn test -Denv=qa -Dheadless=true                        # no visible browser
mvn test -Denv=qa -Dbrowser=firefox
mvn test -Denv=qa "-Dcucumber.filter.tags=@smoke"        # one tag
mvn test -Denv=qa "-Dcucumber.filter.tags=@Browser and not @E2E"
mvn clean test -Denv=qa                                  # after moving or deleting a .feature file
```

In PowerShell, keep the whole `-D…` argument inside quotes. Otherwise PowerShell splits it at the dot.

**Running one scenario:** give the scenario its own tag, such as `@wip`, and run with `"-Dcucumber.filter.tags=@wip"`. Remove the tag before you commit.

**API runs:** reqres.in allows only 40 requests per day per IP address. Run the API suite deliberately rather than over and over. Once the limit is used up, every API scenario fails with `429` until midnight UTC.

## 3. How a run works

```
mvn test
 └─ Surefire → JUnit Platform → TestRunner (@Suite, Cucumber engine)
     ├─ finds features in src/test/resources/features (classpath package "features")
     ├─ keeps scenarios that match cucumber.filter.tags (default @All, set in pom.xml)
     └─ for each scenario:
         ├─ ScenarioReportListener   creates the Extent test and sets the scenario name in the log context
         ├─ Hooks @Before("@Browser") DriverFactory → DriverManager (ThreadLocal)
         ├─ picocontainer            creates PageManager / TestContext and the step classes
         ├─ steps                    step class → page object → WebActionUtils → WebDriver
         │                           or step class → TestContext → GenericApiClient → RestAssured
         ├─ Hooks @After             screenshot on failure, then quit the driver
         └─ ScenarioReportListener   records the result; flushes ExtentReport.html when the run ends
```

Each layer has its own job:

| Layer | Location | Job |
|---|---|---|
| Feature files | `src/test/resources/features/` | Describe behaviour in Gherkin |
| Step definitions | `src/test/java/.../stepdefinitions/` | Turn Gherkin into calls, and **make assertions** |
| Page objects | `src/main/java/com/selenium/pages/` | Locators and business actions. **Return state, don't assert** |
| `WebActionUtils` | `src/main/java/com/selenium/utils/` | **The only code that talks to WebDriver**: waits and logs every action |
| API layer | `src/main/java/com/api/` | Request specs, auth, the HTTP client, logging with secrets masked |
| Common | `src/main/java/com/common/` | Config, logging, the report, Excel test data |

## 4. Writing a UI scenario

1. Pick the folder under `src/test/resources/features/` (`login/`, `cart/`, …), or create a new one.
2. Tag the feature `@Browser @All`. Without `@Browser`, no browser is started and the first step fails with `IllegalStateException` from `DriverManager.getDriver()`. Without `@All`, the default run skips the feature.
3. Start the scenario name with a test-case ID, and add the same case to `docs/Test_Plan_and_Test_Cases.xlsx`.
4. Write the steps as "the user …". Reuse the existing steps (listed in the README) before writing new ones.

```gherkin
@Browser @All
Feature: Cart

  @smoke
  Scenario: CART_TC_003 - Badge clears after removing the only item
    Given the user is on the login page
    When the user logs in with username "standard_user" and password "secret_sauce"
    And the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should show 1
    When the user opens the cart
    And the user removes "Sauce Labs Backpack" from the cart
    Then the cart badge should show 0
```

For a Scenario Outline, put `<id>` in the title and add an `id` column to Examples, so every row gets a unique name in logs and reports:

```gherkin
  Scenario Outline: <id> - Sort products by <order>
    ...
    Examples:
      | id          | order               |
      | PROD_TC_002 | Price (high to low) |
      | PROD_TC_003 | Name (Z to A)       |
```

## 5. Adding a page object

Example: a product detail page.

**1. Create the page** in `src/main/java/com/selenium/pages/`:

```java
package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {
    private final By name = By.cssSelector("[data-test='inventory-item-name']");
    private final By price = By.cssSelector("[data-test='inventory-item-price']");
    private final By addToCart = By.cssSelector("[data-test='add-to-cart']");
    private final By backButton = By.cssSelector("[data-test='back-to-products']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return actions.isDisplayed(backButton);
    }

    public String getName() {
        return actions.getText(name);
    }

    public String getPrice() {
        return actions.getText(price);
    }

    public void addToCart() {
        actions.click(addToCart, "Add to cart button");
    }
}
```

Rules:
- Extend `BasePage` and do **everything** through `actions`. Don't call `driver.findElement`, `click()`, `sendKeys`, `new Select(...)` or `driver.get`. See [ADR-0003](adr/0003-page-objects-through-webactionutils.md).
- Prefer `[data-test=...]` locators, then `id`. Avoid XPath that depends on layout.
- Give each action a readable element name, such as `"Add to cart button"`. It appears in the log and the report.
- Return state (`boolean`, `String`, `List<String>`) and leave assertions to the steps.
- Use `actions.typeSecret` for passwords and tokens.
- Elements shared by every page after login (cart icon, badge, menu) belong in `HeaderComponent`, not in each page.

**2. Register it in `PageManager`** (`src/test/java/com/selenium/context/PageManager.java`), using the same lazy pattern as the other pages:

```java
private ProductDetailPage productDetailPage;

public ProductDetailPage productDetail() {
    if (productDetailPage == null) {
        productDetailPage = new ProductDetailPage(driver());
    }
    return productDetailPage;
}
```

Pages are created lazily because the driver doesn't exist until the `@Browser` hook has run. See [ADR-0004](adr/0004-dependency-injection-with-picocontainer.md).

## 6. Adding step definitions

Put UI steps in a class under `src/test/java/com/selenium/stepdefinitions/`, either an existing class for that area or a new one. That package is already on the glue path. Take `PageManager` in the constructor:

```java
package com.selenium.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.selenium.context.PageManager;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ProductDetailSteps {

    private final PageManager pages;

    public ProductDetailSteps(PageManager pages) {
        this.pages = pages;
    }

    @When("the user opens the product {string}")
    public void openProduct(String productName) {
        pages.inventory().openProduct(productName);   // add this method to InventoryPage
    }

    @Then("the product detail page should show {string} priced {string}")
    public void verifyDetail(String expectedName, String expectedPrice) {
        assertTrue(pages.productDetail().isDisplayed(), "Product detail page was not displayed");
        assertEquals(expectedName, pages.productDetail().getName(), "Product name");
        assertEquals(expectedPrice, pages.productDetail().getPrice(), "Product price");
    }
}
```

Rules:
- Assertions (JUnit `Assertions`) go here, with a message that says what was checked.
- Don't keep pages or state in `static` fields. Picocontainer gives every scenario a fresh `PageManager`.
- Use `LoggerUtils` for anything extra you want in the log or report. Never use `System.out`.
- Before adding a step, search the existing ones. Two step definitions with the same text cause an `AmbiguousStepDefinitionsException`.

## 7. Adding a browser action

If a page needs something `WebActionUtils` doesn't offer yet (hover, JavaScript click, drag-and-drop, scroll), add a method to `src/main/java/com/selenium/utils/WebActionUtils.java` instead of calling Selenium from the page. Follow the existing methods: wait for the element through `WaitUtils`, do the action, then log it with `LoggerUtils` using the element name.

```java
public void hover(By locator, String elementName) {
    WebElement element = WaitUtils.visible(driver, locator);
    new Actions(driver).moveToElement(element).perform();   // org.openqa.selenium.interactions.Actions
    LoggerUtils.info("Hovered over " + elementName);
}
```

Then add a row to the Web actions table in the README.

## 8. Data-driven tests with Excel

Test data lives in `src/test/resources/testdata/<env>/TestData.xlsx`. It has one sheet per area, such as `Login`. The first row holds the headers, and the `TestDataId` column is the key.

| TestDataId | Username | Password |
|---|---|---|
| LOGIN_001 | standard_user | secret_sauce |

Read a row in a step:

```java
Map<String, String> data = ExcelUtils.getTestData("Login", testDataId);
pages.login().login(data.get("Username"), data.get("Password"));
```

Notes:
- The file is loaded from the classpath, and the file name must be exactly `TestData.xlsx`, including letter case, or Linux CI can't find it. On Windows, rename files only with `git mv` (see [Troubleshooting](#12-troubleshooting)).
- Values come back as the text Excel would show, formatted with `Locale.US`. Formulas are evaluated.
- Each environment needs its own copy (`testdata/qa/`, `testdata/staging/`, …).
- Don't log passwords from test data. Log the username only, as `LoginSteps` does.

## 9. Writing an API scenario

API steps follow a build, send, check pattern. The `Given` steps add query params, headers, path params and a body to the scenario's `TestContext`. A `When` step sends the request. The `Then` steps check the response.

```gherkin
@API @All
Feature: Users API

  Scenario: API_TC_005 - Update a user
    Given I set path param "id" to "2"
    And I set the request body to:
      """
      { "name": "morpheus", "job": "zion resident" }
      """
    When I send a PUT request to "/users/{id}"
    Then the response status code should be 200
    And the response json path "job" should be "zion resident"
```

- Bodies can be inline (`I set the request body to:` with a doc string) or loaded from a file (`I load the request body from file "testdata/api/user.json"`). Files go under `src/test/resources/testdata/api/`.
- `… request to "/path" with authorization` logs in once through `AuthManager` and adds the bearer token. Without `with authorization`, the request uses the static API key (`x-api-key`, which reqres.in requires).
- To chain requests, store a value with `I store the response json path "data.email" as "email"`, then compare against it with `the response json path "…" should equal the stored "email"`.
- Every request and response is logged automatically by `ApiLoggingFilter`, with `Authorization`, `x-api-key`, cookies and `password`/`token`/`secret` fields masked.
- API features don't carry `@Browser`, so no browser is started.

**A new kind of check:** add a step to `src/test/java/com/api/stepdefinitions/APISteps.java`, and read the response from the injected `TestContext`. **A new auth style:** add a builder to `ApiSpec`, so that it still includes `ApiLoggingFilter`.

## 10. Configuration and environments

`ConfigManager` takes values from three places, in this order of priority:

1. A JVM system property: `-DexplicitWait=20`
2. `src/test/resources/config/<env>.properties`, where `env` comes from `-Denv` and defaults to `qa`
3. `src/test/resources/config/config.properties`

| Key | Used by | Where it's set |
|---|---|---|
| `browser`, `headless`, `explicitWait` | UI | `config.properties` |
| `baseUrl` | UI (Sauce Demo) | `qa.properties` |
| `base.url`, `auth.*`, `request.timeout` | API | `config.properties` |

Read values with `ConfigManager.get("key")` or one of its typed helpers. A missing key fails fast with `Missing config: <key>`.

**Adding an environment** such as `uat`:
1. Create `src/test/resources/config/uat.properties` with at least `baseUrl`, and `base.url` if the API host is different.
2. Create `src/test/resources/testdata/uat/TestData.xlsx` with the same sheets and IDs.
3. Add `uat` to the `env` choice list in `.github/workflows/tests.yml` if CI should offer it.
4. Run with `mvn test -Denv=uat`.

`staging` isn't usable yet. It's missing `baseUrl` and test data, and it uses `explicit.wait` instead of `explicitWait`. Also, the files in `src/main/resources/config/` are never read. Edit the copies under `src/test/resources/config/`.

## 11. Logging, reports and screenshots

```java
LoggerUtils.info("Opened product page for " + name);
LoggerUtils.pass("Order total verified: " + total);
LoggerUtils.warn("Badge not shown, treating count as 0");
LoggerUtils.debug("Raw response: " + body);           // log file only
LoggerUtils.block("Response body", json);             // preformatted block in the report
ScreenshotUtils.attachToReport(DriverManager.getDriver(), "After checkout");
```

Each call goes to the console, to `logs/automation.log` (tagged with the scenario name), and to the step that's currently running in the Extent report. Steps, scenario start and end, API traffic, and screenshots on failure are all logged without any code from you.

| Output | Open it to… |
|---|---|
| `ExtentReport.html` | See each scenario with its Given/When/Then steps, timings, errors and screenshots. Filter by tag |
| `target/cucumber-report.html` | Use Cucumber's own view of the run |
| `logs/automation.log` | Search the full log, including DEBUG lines and API headers |
| `target/surefire-reports/` | Get JUnit XML, for tools that read it |

On CI, all of these are in the `test-reports` artifact on the run page, kept for 14 days.

## 12. Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `IllegalStateException` from `DriverManager.getDriver()` | The feature or scenario isn't tagged `@Browser` | Add `@Browser` to the feature |
| The scenario doesn't run at all | The feature isn't tagged `@All`, or your tag filter doesn't match | Add `@All`, or check `-Dcucumber.filter.tags` |
| A deleted or renamed scenario still runs | A stale copy is left in `target/test-classes` | `mvn clean test` |
| `Unknown lifecycle phase ".filter.tags=…"` | PowerShell split the argument | Quote it: `"-Dcucumber.filter.tags=@x"` |
| API scenarios fail with `429 rate_limit_exceeded` | reqres.in's 40 requests/day limit | Wait until midnight UTC, or use an account or API key. This isn't a code bug |
| `Missing config: baseUrl` | Running with `-Denv=staging`, or another env without `baseUrl` | Add `baseUrl` to `<env>.properties` |
| `TestData.xlsx` not found on CI but fine locally | The file name differs in letter case in git | `git mv -f Old tmp && git mv -f tmp TestData.xlsx`, then check with `git ls-files` |
| `AmbiguousStepDefinitionsException` | Two step definitions match the same text | Remove or reword one |
| `Unable to find an exact match for CDP version` warning | Chrome is newer than Selenium 4.48 | Harmless. Upgrading Selenium removes it |
| A "password found in a data breach" dialog | Chrome's password manager | Already turned off in `ChromeDriverImpl`. Keep those options if you change the driver setup |
| A timeout on an element that is on the page | Wrong locator, or the element is inside an iframe or another window | Check the locator in DevTools. Use `actions.switchFrame` / `switchWindowByTitle` |

## 13. Checklist for a new scenario

- [ ] The feature is tagged `@All`, plus `@Browser` (UI) or `@API` (API)
- [ ] The scenario name starts with a unique ID. Outlines use `<id>` and an `id` column
- [ ] The ID and test case are added to `docs/Test_Plan_and_Test_Cases.xlsx`
- [ ] Existing steps are reused where they fit. New steps follow "the user …" (UI) or "I …" (API)
- [ ] Page code goes through `actions` only. New pages are registered in `PageManager`
- [ ] Assertions are in steps, with clear messages. Pages return state
- [ ] No `System.out`, and no secrets in logs
- [ ] `mvn test -Denv=qa "-Dcucumber.filter.tags=@yourTag"` passes locally, headed and with `-Dheadless=true`
- [ ] Any new files were added with the exact letter case, and appear in `git ls-files`
