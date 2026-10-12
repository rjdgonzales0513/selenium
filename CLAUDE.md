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
- The `Fail login` scenario outline fails by design for its invalid-credential rows.

## Architecture

Three top-level packages under `src/main/java` (framework code lives in `main`, not `test`):

- `com.common`: shared across UI and API.
  - `ConfigManager` loads `config/config.properties`, then `config/<env>.properties`, from the classpath. A `-D` system property overrides any key. Test resources come before main resources on the classpath, so `src/test/resources/config/` is what's actually used. The keys aren't consistent: UI code reads `baseUrl` / `explicitWait`, API code reads `base.url` / `auth.*`, and the env files also define `explicit.wait`, which nothing uses.
  - `Hooks` runs for every scenario. It creates a per-scenario ExtentTest, and on failure it takes a screenshot and attaches it. It always calls `DriverManager.quitDriver()` and flushes the Extent report in `@AfterAll`.
  - `ExcelUtils.getTestData(sheet, testDataId)` reads one row, keyed by the `TestDataId` column, from `src/test/resources/testdata/<env>/TestData.xlsx`. It uses a file path, not the classpath. The file on disk is named `Testdata.xlsx`, which only works because Windows paths are case-insensitive.
- `com.selenium`: the UI layer.
  - `DriverFactory` builds a driver through `IDriver` implementations (`ChromeDriverImpl`, `FirefoxDriverImpl`) based on the `browser` config.
  - `DriverManager` keeps the driver in a `ThreadLocal`.
  - Page objects live in `pages/` and take a `WebDriver` in their constructor. `WaitUtils`, `ElementActionUtils` and `ScreenshotUtils` are helpers.
  - `LoggerUtils.getLogger()` returns the current scenario's **ExtentTest**, not a log4j logger.
  - `Main.java` and `Practice.java` are standalone scratch programs and aren't part of the framework.
- `com.api`: the API layer.
  - `ApiSpec` builds RestAssured request specs: no auth, API key (`x-api-key`), or bearer token.
  - `GenericApiClient` wraps the HTTP verbs.
  - `TestContext` holds per-scenario request and response state and is injected into `APISteps` by cucumber-picocontainer through the constructor.
  - `AuthManager.getBearerToken()` currently just reads the `bearer.token` config key. That key isn't defined anywhere, despite the Javadoc describing a login flow.

### Step definitions and glue

- The glue packages are `com.selenium.stepdefinitions`, `com.common.stepdefinitions` and `com.api.stepdefinitions`. Step classes are split between `src/main/java` (`WebSteps`, `APISteps`, `Hooks`) and `src/test/java` (`LoginSteps`, `CartSteps`, `InventorySteps`), all in the same packages.
- Features live in `src/test/resources/features/` and are discovered with `@SelectPackages("features")`.
- **The browser is only started for scenarios tagged `@Browser`**, through `WebSteps`' `@Before(value = "@Browser", order = 1)`. A UI feature without that tag fails with an `IllegalStateException` from `DriverManager.getDriver()`.
- A feature must also carry `@All` to be picked up by the runner.
- API steps use a request-building pattern. Gherkin steps such as "I set query param / header / path param" add values to `TestContext`, and the `When` step sends the request. Bodies are loaded from classpath JSON files (for example `testdata/api/user.json`).
- `junit-platform.properties` has parallel execution disabled. The driver and Extent state are `ThreadLocal`, so the code is set up for parallel runs.
