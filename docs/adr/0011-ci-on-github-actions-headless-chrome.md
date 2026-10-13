# 0011. CI on GitHub Actions with headless Chrome

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

The code is hosted on GitHub. We want every push and pull request to `main` tested automatically, a way to run a chosen subset on demand, and reports anyone can download, all without maintaining build servers.

## Decision

`.github/workflows/tests.yml`:

- Runs on `ubuntu-latest` for pushes and pull requests to `main`, and manually (`workflow_dispatch`) with inputs for the tag expression, environment and browser.
- Runs `mvn -B clean test -Dheadless=true`. `clean` prevents stale feature files in `target/` from running. Headless Chrome uses a 1920×1080 window (set in `ChromeDriverImpl`) so layouts match a desktop browser.
- Cancels an older run of the same branch or PR when a new commit arrives.
- Writes a job summary of scenario results and uploads `ExtentReport.html`, the Cucumber HTML report, Surefire results and `logs/automation.log` as the `test-reports` artifact, kept for 14 days.

## Consequences

- No CI infrastructure to maintain. Results are visible on every PR, and the workflow can be made a required status check.
- CI runs on Linux, so case-sensitivity and path issues hidden by Windows or macOS show up there (see [ADR-0009](0009-excel-test-data-keyed-by-id.md)).
- GitHub-hosted runners share IP addresses, so reqres.in's per-IP rate limit can make API scenarios fail with `429` on CI even when nothing is wrong.
- Only Linux is tested. Windows or macOS browser behaviour isn't covered.
