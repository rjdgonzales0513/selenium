# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Java 21 / Maven test-automation framework combining **Selenium WebDriver UI tests** (against https://www.saucedemo.com) and **RestAssured API tests** (against https://reqres.in/api), both driven by **Cucumber** feature files on the JUnit Platform. Not a git repository.

## Commands

```bash
mvn test -Denv=qa                        # run the suite (all features tagged @All)
mvn test -Denv=qa -Dbrowser=firefox -Dheadless=true
mvn test-compile                          # compile only
```

- **Tag filtering:** `TestRunner` (`src/test/java/com/selenium/runners/TestRunner.java`) hard-codes `FILTER_TAGS_PROPERTY_NAME = "@All"`, and that wins over `-Dcucumber.filter.tags=...` on the command line (verified: passing `@get_user` still ran every scenario). To run a single scenario or tag, temporarily change the runner's filter value (e.g. `"@get_user"`), or run the feature from the IDE.
- Reports: `ExtentReport.html` (project root, written by `ExtentManager`), `target/cucumber-report.html`, and log4j2 output in `logs/automation.log`.
- All 19 scenarios should pass. The invalid-login rows in `Login.feature` assert that an error is shown, through `the login should be "Failed"`.
- After moving or deleting a `.feature` file, use `mvn clean test`. Otherwise stale copies in `target/test-classes` keep running.
- `CdpVersionFinder` warnings on the console come from Chrome being newer than Selenium 4.48. They're harmless.

## Architecture

Framework code lives in `src/main/java`. All Cucumber glue (step definitions, `Hooks`, `PageManager`) lives in `src/test/java`. There are three top-level packages:

- `com.common`: shared across UI and API.
  - `ConfigManager` loads `config/config.properties`, then `config/<env>.properties`, from the classpath. A `-D` system property overrides any key. Test resources come before main resources on the classpath, so `src/test/resources/config/` is what's actually used. The keys aren't consistent: UI code reads `baseUrl` / `explicitWait`, API code reads `base.url` / `auth.*`, and the env files also define `explicit.wait`, which nothing uses.
  - **Logging/reporting:** log through `com.common.utils.LoggerUtils` (`info`, `pass`, `warn`, `fail`, `debug`, `block`), never `System.out`. Each call goes to Log4j under the caller's class name (console plus `logs/automation.log`, configured in `src/test/resources/log4j2-test.properties`). When a scenario is running, it also goes to the current step in the Extent report.
  - `listeners/ScenarioReportListener` is a Cucumber plugin registered in `TestRunner`'s plugin list. It owns the report lifecycle: it creates one ExtentTest per scenario (tags become categories) and a Gherkin child node per step, records step status and duration, puts the scenario name in the Log4j `ThreadContext` (`%X{scenario}`), and flushes the report on `TestRunFinished`. `report/ExtentManager` holds those ThreadLocals. The listener is only registered in `TestRunner`. A run that bypasses the runner still writes to Log4j but produces no Extent report, because `LoggerUtils` skips Extent when no scenario is bound.
  - `Hooks` (in `src/test`) owns the browser lifecycle. `@Before("@Browser")` creates the driver. `@After` takes a failure screenshot (if a driver exists) and quits the driver.
  - Don't log secrets. `ApiLoggingFilter` masks auth headers and `password`/`token`/`secret` JSON fields. In UI code, use `ElementActionUtils.typeSecret` or log `****`.
  - `ExcelUtils.getTestData(sheet, testDataId)` reads one row, keyed by the `TestDataId` column, from `src/test/resources/testdata/<env>/TestData.xlsx`. It uses a file path relative to the project root, not the classpath, so tests must run from the project root.
- `com.selenium`: the UI layer.
  - `DriverFactory` builds a driver through `IDriver` implementations (`ChromeDriverImpl`, `FirefoxDriverImpl`) based on the `browser` config. `ChromeDriverImpl` turns off Chrome's password manager and leak detection through prefs and `--disable-features=PasswordLeakDetection`. Otherwise logging into Sauce Demo opens a native "password found in a data breach" dialog that Selenium can't dismiss.
  - `DriverManager` keeps the driver in a `ThreadLocal`.
  - Page objects live in `pages/` and take a `WebDriver` in their constructor. Assertions belong in steps; pages return state, such as `isDisplayed()`, `getItemNames()` or `getErrorMessage()`. `HeaderComponent` covers the cart icon, the badge and logout, which appear on every page after login. The Sauce Demo locators prefer `[data-test=...]` attributes. `WaitUtils`, `ElementActionUtils` and `ScreenshotUtils` are helpers. `ScreenshotUtils.attachToReport(driver, title)` embeds a screenshot in the current report step.
  - `Main.java` and `Practice.java` are standalone scratch programs and aren't part of the framework.
- `com.api`: the API layer.
  - `ApiSpec` builds RestAssured request specs: no auth, API key (`x-api-key`), or bearer token. Every spec includes `filters/ApiLoggingFilter`, which logs the method, URL, status, timing, request headers and body, and the response body. Response headers are logged at DEBUG level only.
  - `GenericApiClient.send(method, …)` handles every HTTP verb. The body is optional.
  - `TestContext` holds per-scenario request and response state, including values saved with `I store the response json path … as …`. Cucumber-picocontainer injects it into `APISteps` through the constructor.
  - `AuthManager.getBearerToken()` logs in once (POST to `auth.login.endpoint` with `auth.username` / `auth.password`), reads the token at `auth.token.jsonpath`, and caches it for the JVM run.

### Step definitions and glue

- The glue packages are `com.selenium.stepdefinitions`, `com.common.stepdefinitions` and `com.api.stepdefinitions`, all in `src/test/java`.
- UI step classes (`LoginSteps`, `InventorySteps`, `CartSteps`, `CheckoutSteps`, `WebSteps`) take `com.selenium.context.PageManager` in their constructor. Picocontainer creates one per scenario, and it builds pages lazily from `DriverManager`. Add new pages to `PageManager` rather than constructing them in steps.
- Features live in `src/test/resources/features/` (`api/`, `login/`, `products/`, `cart/`, `checkout/`, `e2e/`) and are discovered with `@SelectPackages("features")`.
- Naming conventions:
  - Every scenario name starts with an ID (`LOGIN_TC_001 - …`, `E2E_TC_001 - …`).
  - Outlines use `<id>` in the title plus an `id` Examples column.
  - UI steps read "the user …". API steps read "I …".
- **The browser is only started for scenarios tagged `@Browser`**. A UI feature without that tag fails with an `IllegalStateException` from `DriverManager.getDriver()`.
- A feature must also carry `@All` to be picked up by the runner.
- Scenario IDs match the test cases in `docs/Test_Plan_and_Test_Cases.xlsx`; keep the two in sync when adding or changing scenarios. `e2e/PurchaseFlow.feature` is the full purchase journey: login, cart, checkout, the total and tax checks, confirmation, and logout.
- API steps use a request-building pattern. Gherkin steps such as "I set query param / header / path param" add values to `TestContext`, and the `When` step sends the request. Bodies are loaded from classpath JSON files (for example `testdata/api/user.json`).
- `junit-platform.properties` has parallel execution disabled. The driver and Extent state are `ThreadLocal`, so the code is set up for parallel runs.
