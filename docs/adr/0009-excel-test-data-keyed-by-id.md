# 0009. Excel test data keyed by `TestDataId`, loaded from the classpath

- **Status:** Accepted
- **Date:** 2026-10-13

## Context

Some test data (credentials, customer details) is maintained by testers who prefer a spreadsheet, and it differs per environment. An early version read the workbook from a file-system path. That broke when the working directory changed, and broke on Linux CI when the file name on disk differed only in letter case from the name in the code.

## Decision

- Test data lives in `src/test/resources/testdata/<env>/TestData.xlsx`, with one sheet per area. Row 1 holds the headers, and the `TestDataId` column is the key.
- `ExcelUtils.getTestData(sheet, testDataId)` returns one row as `Map<column, value>`.
- The workbook is loaded **from the classpath** (`testdata/<env>/TestData.xlsx`), and the lookup **fails if the name differs only in letter case**, so a mismatch shows up on Windows and macOS too, not just on Linux CI.
- Cells are formatted with `DataFormatter(Locale.US)`, and formulas are evaluated, so values don't depend on the machine's regional settings.
- Steps reference data by ID (`the user logs in using test data "LOGIN_001"`). Simple cases may still put literal values or Examples tables in the feature file.

## Consequences

- Testers can edit data without touching code, and each environment has its own data set.
- Behaviour is the same on every OS and from any working directory.
- Excel files are binary: diffs aren't readable in review, and merge conflicts can't be resolved line by line.
- Each environment needs a full copy of the workbook (`testdata/staging/` doesn't exist yet).
- On Windows, case-only renames have to be done with `git mv` through a temporary name, because git has `core.ignorecase=true`.
- Secrets in the workbook end up in the repo. That's acceptable for public demo credentials, but not for real ones.
