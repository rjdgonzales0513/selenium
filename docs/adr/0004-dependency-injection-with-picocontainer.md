# 0004. Share per-scenario state with picocontainer instead of statics

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

Cucumber creates a new instance of each step-definition class for every scenario, but a scenario's steps are spread over several classes (`LoginSteps`, `CartSteps`, `CheckoutSteps`, …), and they need the same page objects or the same API request and response. The usual shortcut, `static` fields, leaks state between scenarios and breaks under parallel execution.

## Decision

We will use `cucumber-picocontainer` for constructor injection of per-scenario context objects:

- **UI:** `com.selenium.context.PageManager` is injected into every UI step class. It creates page objects lazily from `DriverManager.getDriver()`, because the driver only exists after the `@Browser` hook has run. New pages are added to `PageManager`, not constructed in steps.
- **API:** `com.api.context.TestContext` is injected into `APISteps`. It holds the request being built (query, path params, headers, body), the last response, and values saved with `I store the response json path … as …`.

Picocontainer creates one instance of each per scenario and throws it away afterwards.

## Consequences

- No static mutable state in the glue, so scenarios are isolated and ready for parallel runs.
- Step classes can be split by area without sharing state through globals.
- Every new page needs a small, repetitive addition to `PageManager`.
- Lazy creation means a page is built against the driver of the current scenario. Holding on to a page outside a scenario isn't possible, and isn't intended.
