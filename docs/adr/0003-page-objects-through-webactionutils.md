# 0003. Page objects do all browser interaction through WebActionUtils

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

UI test failures are often caused by timing (an element isn't ready yet) and are hard to diagnose without a record of what the test did. When each page calls `findElement`, `click()` and `sendKeys` directly, waits are applied unevenly, logging is missing or inconsistent, and the same Selenium boilerplate is copied into every page.

## Decision

We will use the Page Object Model, with one class between pages and WebDriver:

- Every page extends `BasePage`, takes a `WebDriver` in its constructor, and gets a `WebActionUtils` instance as `actions`.
- Pages do **all** browser interaction through `actions`. They never call `findElement`, `click()`, `sendKeys`, `Select` or `driver.get` themselves.
- Every `WebActionUtils` method waits (through `WaitUtils`, up to `explicitWait` seconds) and logs the action using a readable element name passed in by the page. `typeSecret` logs `****` instead of the value.
- `isDisplayed` returns `false` on timeout instead of throwing, so steps can turn it into a clear assertion failure.
- Pages return state (`isDisplayed()`, `getErrorMessage()`, `getItemNames()`). **Assertions belong in step definitions.**
- Elements on every page after login (cart icon, badge, logout) live in `HeaderComponent`.
- Locators prefer `[data-test=...]` attributes.

A new kind of interaction (hover, JavaScript click, and so on) is added to `WebActionUtils`, not written inline in a page.

## Consequences

- Waits and logging are consistent everywhere. The log and report read as a list of user actions ("Clicked Login button").
- Pages are short and declarative, and changing how waits work is a change to one class.
- Pages can be reused by any step, because they don't decide what counts as a failure.
- `WebActionUtils` is a bottleneck: it has to grow whenever a new interaction is needed, and contributors have to resist "just this once" direct Selenium calls. Code review has to enforce this.
- A shared fixed wait doesn't suit every element. Unusual cases need a dedicated method.
