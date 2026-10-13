# 0001. Record architecture decisions

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

The framework has grown a number of deliberate rules: pages never call WebDriver directly, the browser starts only for `@Browser`, the runner doesn't set a tag filter, and so on. Several of these look odd until you know the reason. Up to now the reasons have lived in commit messages, `CLAUDE.md` and people's heads.

## Decision

We will record significant design decisions as Architecture Decision Records in `docs/adr/`, numbered in sequence and using the template in `docs/adr/template.md`. ADRs 0002–0012 record decisions that were already in place when this practice started. Their date is the date they were written down, not the date the decision was originally made.

## Consequences

- New contributors can find out why the framework works the way it does without reading the git history.
- Changes that go against a recorded decision need a new ADR, which makes the trade-off visible in review.
- ADRs have to be kept current. A superseded ADR stays in place, with its status changed.
