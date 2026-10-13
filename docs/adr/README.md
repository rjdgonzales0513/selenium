# Architecture Decision Records

This folder records the significant design decisions in this framework: the context at the time, what was decided, and the consequences. Read these before changing how the framework is put together, so a change doesn't undo a decision without knowing why it was made.

The format follows [Michael Nygard's ADR template](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions). Copy [`template.md`](template.md) to start a new one.

| ADR | Title | Status |
|---|---|---|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [0002](0002-cucumber-on-junit-platform-for-ui-and-api.md) | One Cucumber project on the JUnit Platform for UI and API tests | Accepted |
| [0003](0003-page-objects-through-webactionutils.md) | Page objects do all browser interaction through WebActionUtils | Accepted |
| [0004](0004-dependency-injection-with-picocontainer.md) | Share per-scenario state with picocontainer instead of statics | Accepted |
| [0005](0005-threadlocal-driver-started-by-browser-tag.md) | ThreadLocal driver, started only for `@Browser` scenarios | Accepted |
| [0006](0006-layered-classpath-configuration.md) | Layered, classpath-based configuration | Accepted |
| [0007](0007-unified-logging-and-report-listener.md) | One logging facade, with the report driven by a Cucumber plugin | Accepted |
| [0008](0008-api-request-building-steps.md) | API tests use request-building steps and a generic client | Accepted |
| [0009](0009-excel-test-data-keyed-by-id.md) | Excel test data keyed by `TestDataId`, loaded from the classpath | Accepted |
| [0010](0010-tag-filter-from-command-line.md) | Tag filter comes from a Maven property, not the runner | Accepted |
| [0011](0011-ci-on-github-actions-headless-chrome.md) | CI on GitHub Actions with headless Chrome | Accepted |
| [0012](0012-sequential-execution-parallel-ready.md) | Run sequentially, but keep the code ready for parallel runs | Accepted |

## When to write an ADR

Write one when a decision:
- changes how layers talk to each other (for example, letting pages call WebDriver directly),
- adds or replaces a core library (test runner, reporting, DI, HTTP client),
- changes a convention every test author has to follow (tags, naming, where assertions go), or
- would surprise a new team member who wasn't there when it was made.

To reverse a decision, don't edit the old ADR. Add a new one and set the old one's status to `Superseded by ADR-NNNN`.
