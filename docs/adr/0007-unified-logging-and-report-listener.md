# 0007. One logging facade, with the report driven by a Cucumber plugin

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

We want a step-by-step HTML report (ExtentReports) and a searchable text log (Log4j2), and we want them to agree. When code writes to both on its own, or uses `System.out`, the two drift apart. Putting report setup in `@Before`/`@After` hooks mixes reporting with the browser lifecycle, and hooks can't see individual Gherkin steps.

## Decision

- **`com.common.utils.LoggerUtils` is the only logging API** (`info`, `pass`, `warn`, `fail`, `debug`, `block`). Each call goes to Log4j2 under the caller's class name (console plus `logs/automation.log`, configured in `src/test/resources/log4j2-test.properties`), and to the step currently running in the Extent report, if a scenario is bound. `System.out` isn't used.
- **`com.common.listeners.ScenarioReportListener`**, a Cucumber `ConcurrentEventListener` registered in `TestRunner`'s plugin list, owns the report lifecycle. It creates one Extent test per scenario (tags become categories) and a Gherkin child node per step, records status and duration, puts the scenario name in Log4j's `ThreadContext` (`%X{scenario}`), and flushes the report on `TestRunFinished`.
- `com.common.report.ExtentManager` holds the Extent state in `ThreadLocal`s and writes `ExtentReport.html`.
- `Hooks` only adds a failure screenshot, through `ScreenshotUtils.attachToReport`.
- Secrets are never logged: `ApiLoggingFilter` masks auth headers and `password`/`token`/`secret` JSON fields, and UI code uses `actions.typeSecret`.

## Consequences

- The log file and the report show the same events, and every log line carries the scenario name.
- Step-level structure in the report comes for free, with no code in step definitions.
- Hooks stay focused on the browser.
- Runs that bypass `TestRunner` (for example, running a `.feature` file directly in an IDE) still log to Log4j but produce no Extent report, because the plugin isn't registered. (The listener's Javadoc says it is also registered in `junit-platform.properties`, but that file doesn't register it.)
- The framework depends on ExtentReports 5 and Cucumber's plugin event API.
