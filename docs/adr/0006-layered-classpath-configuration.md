# 0006. Layered, classpath-based configuration

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

The same tests have to run against different environments, browsers and modes (headed locally, headless on CI), and individual runs need one-off overrides without editing files. Paths that work on a Windows laptop but not on Linux CI have caused failures before.

## Decision

`com.common.config.ConfigManager` loads, from the **classpath**:

1. `config/config.properties`: shared defaults (`browser`, `headless`, `explicitWait`, `env`, and the API keys `base.url`, `auth.*`, `request.timeout`)
2. `config/<env>.properties`, where `env` comes from `-Denv` (default `qa`): environment-specific values, such as the UI `baseUrl`

`ConfigManager.get(key)` returns a JVM system property with the same name if one is set (`-Dheadless=true`), and otherwise the file value. A missing key throws `Missing config: <key>`. The files that are actually used live in `src/test/resources/config/`, because test resources come before main resources on the test classpath.

## Consequences

- Any setting can be overridden from the command line or CI without touching files.
- Loading from the classpath works the same way on every OS and from any working directory.
- Missing keys fail at the first use, with a clear message.
- `src/main/resources/config/*` is shadowed and never read, which can confuse contributors. It should be removed or documented.
- Key naming is inconsistent (`baseUrl` and `explicitWait` for UI, `base.url` for API). `staging.properties` currently uses `explicit.wait`, which nothing reads, and has no `baseUrl`, so `staging` isn't usable yet. Making the key names consistent would be a separate change.
- Config is loaded once into a static `Properties` object when the class loads. Changing it during a run isn't supported.
