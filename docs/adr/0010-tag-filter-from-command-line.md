# 0010. Tag filter comes from a Maven property, not the runner

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

We wanted a default run (everything) and the option to run a subset (`@smoke`, `@API`, `@All and not @E2E`) from the command line and from the CI "Run workflow" form. A `@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, …)` on the JUnit `@Suite` takes priority over a `-Dcucumber.filter.tags` system property, so setting the tag there quietly ignored the command line.

## Decision

- `TestRunner` **does not** set `FILTER_TAGS_PROPERTY_NAME`, and a comment there explains why.
- The default `@All` is a Maven property in `pom.xml` (`<cucumber.filter.tags>@All</cucumber.filter.tags>`), passed to Surefire as a system property, and overridden with `-Dcucumber.filter.tags=...`.
- Every feature carries `@All` so the default run picks it up. Type tags (`@Browser`, `@API`) and purpose tags (`@smoke`, `@E2E`, …) are added as needed.

## Consequences

- Any tag expression works from the command line and from the CI `workflow_dispatch` input.
- A feature without `@All` is silently left out of the default run. Reviewers have to check for it.
- In PowerShell, the `-D` argument has to be quoted, or it is split at the dot and Maven fails with `Unknown lifecycle phase`.
