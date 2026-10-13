# 0008. API tests use request-building steps and a generic client

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

A separate step for every endpoint ("I get user 2", "I create a user") multiplies step definitions and Java code for each new endpoint. API tests also need consistent auth, logging and secret masking, and the demo API (reqres.in) needs a static `x-api-key` header as well as optional bearer auth.

## Decision

- **Generic Gherkin steps** in `APISteps` build the request one piece at a time in `TestContext`: query params, headers, path params, a body from a file (`testdata/api/*.json`) or from a doc string. A single `When I send a {word} request to {string}` step sends it, optionally `with authorization`. Generic `Then` steps check the status code, JSON paths, empty bodies and stored values.
- **`ApiSpec`** builds every RestAssured `RequestSpecification`: no auth, API key (`x-api-key`) or bearer (+ API key, for reqres.in). Every spec includes `ApiLoggingFilter`, which logs method, URL, status, timing, request headers and body, and response body (response headers at DEBUG only), with secrets masked.
- **`GenericApiClient.send(method, …)`** handles every HTTP verb. The body is optional.
- **`AuthManager.getBearerToken()`** logs in once per JVM run, using config `auth.login.endpoint`, `auth.username`, `auth.password` and `auth.token.jsonpath`, and caches the token.

## Consequences

- Most new API tests need only a feature file. No Java code is required for a new endpoint.
- Every request is logged and masked the same way.
- Feature files describe HTTP mechanics rather than business intent. That's fine for contract-style checks, but complex flows may later need a few higher-level steps.
- The cached token is never refreshed during a run. Long runs against an API with short-lived tokens would need expiry handling.
- The `x-api-key` header in the bearer spec exists only for reqres.in, and should be removed when pointing at a real API.
- reqres.in's 40 requests/day/IP limit causes `429` failures that aren't code bugs, especially on shared CI runners.
