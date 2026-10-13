# 0012. Run sequentially, but keep the code ready for parallel runs

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

The suite is small (19 scenarios), and running sequentially keeps logs easy to read and avoids load on shared demo sites (Sauce Demo, and reqres.in with its rate limit). The suite will grow, though, and making a framework thread-safe after the fact is expensive.

## Decision

- Run scenarios **sequentially** for now.
- Keep all per-scenario state thread-safe so parallel runs can be turned on later: the WebDriver (`DriverManager`) and the Extent test and step (`ExtentManager`) are `ThreadLocal`, the scenario name is in Log4j's `ThreadContext`, and step state is injected per scenario by picocontainer ([ADR-0004](0004-dependency-injection-with-picocontainer.md)). `ScenarioReportListener` is a `ConcurrentEventListener`, so its events arrive on the thread that runs the scenario.
- Shared state is limited to read-only config and the cached bearer token.

## Consequences

- Turning on parallel runs should need only configuration, no code changes. Note that `src/test/resources/junit-platform.properties` currently uses `junit.jupiter.execution.parallel.*` keys. Those apply to the JUnit Jupiter engine, not to Cucumber. Parallel Cucumber needs `cucumber.execution.parallel.enabled=true` and `cucumber.execution.parallel.config.*` (for example `strategy=fixed`, `fixed.parallelism=3`).
- `AuthManager`'s lazy token cache should be checked for thread safety before running API scenarios in parallel.
- New code must keep to these rules: no static mutable state in pages, steps or utilities.
