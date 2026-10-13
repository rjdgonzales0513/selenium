# 0005. ThreadLocal driver, started only for `@Browser` scenarios

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

API scenarios don't need a browser, and starting one for them wastes time and fails on machines without a browser. UI scenarios each need a clean browser. The framework should also allow parallel execution later without a redesign.

## Decision

- `DriverFactory` builds a driver based on the `browser` config, through `IDriver` implementations (`ChromeDriverImpl`, `FirefoxDriverImpl`). Browser-specific options live in those classes. For example, `ChromeDriverImpl` turns off the password manager and leak detection, because Chrome's native breach dialog can't be dismissed by Selenium, and sets `--window-size=1920,1080` when headless.
- `DriverManager` keeps the driver in a `ThreadLocal`.
- `Hooks` owns the lifecycle: `@Before("@Browser")` creates the driver. `@After` attaches a screenshot if the scenario failed and a driver exists, then always quits the driver.
- UI features must be tagged `@Browser`. Driver binaries are resolved by Selenium Manager.

## Consequences

- API runs start no browser, and each UI scenario gets a new browser session with no shared cookies or cart.
- The driver is per thread, so parallel runs are possible (see [ADR-0012](0012-sequential-execution-parallel-ready.md)).
- Forgetting `@Browser` on a UI feature makes `DriverManager.getDriver()` throw `IllegalStateException` on the first UI step. That's loud but easy to fix.
- A new browser per scenario is slower than reusing one, but avoids state leaking between scenarios.
- Adding a browser means a new `IDriver` implementation plus a branch in `DriverFactory`.
