# 0002. One Cucumber project on the JUnit Platform for UI and API tests

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

We test two things: the Sauce Demo web UI and the reqres.in REST API. We wanted test cases that non-developers can read and that match the IDs in `docs/Test_Plan_and_Test_Cases.xlsx`, one way to run and report everything, and a modern test runner that Maven Surefire and IDEs support.

Alternatives considered:
- **Separate UI and API projects:** this would duplicate config, logging, reporting and CI.
- **Plain JUnit or TestNG tests without Gherkin:** less overhead, but the test cases aren't readable as specifications, and they don't line up as clearly with the test plan.
- **Cucumber on the old JUnit 4 runner (`@RunWith(Cucumber.class)`):** deprecated in favour of the JUnit Platform engine.

## Decision

We will keep UI and API tests in one Maven project (Java 21), written as Cucumber feature files and run by `cucumber-junit-platform-engine` through a JUnit Platform `@Suite` (`TestRunner`).

- Features live in `src/test/resources/features/<area>/` and are found with `@SelectPackages("features")`.
- Framework code lives in `src/main/java` under three packages: `com.common` (shared), `com.selenium` (UI) and `com.api` (API). All Cucumber glue lives in `src/test/java`.
- UI uses Selenium WebDriver 4. API uses RestAssured 5.
- Every scenario name starts with a test-case ID from the test plan.

## Consequences

- One command (`mvn test`) runs everything, and one set of reports covers UI and API.
- Shared infrastructure (`ConfigManager`, `LoggerUtils`, the Extent report) is written once.
- Step text becomes a public API: renaming a step means updating every feature that uses it, and two definitions with the same text clash.
- The Gherkin layer adds some indirection compared with plain JUnit tests.
- Test-plan IDs in scenario names have to be kept in sync with the spreadsheet by hand.
