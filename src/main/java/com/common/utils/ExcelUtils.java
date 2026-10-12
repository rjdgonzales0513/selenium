package com.common.utils;

import org.apache.poi.ss.usermodel.*;

import com.common.config.ConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Reads test data rows from testdata/<env>/TestData.xlsx.
 *
 * The file is loaded from the classpath (src/test/resources is on it), not
 * from a file-system path, so it works on Windows, Linux and macOS, from any
 * working directory, and from an IDE or CI runner alike. The exact upper/lower
 * case of the path is checked too, so a name that would only break on Linux
 * fails on Windows and macOS as well.
 */
public final class ExcelUtils {

    private static final String FILE_NAME = "TestData.xlsx";

    // Fixed locale: cell values must not depend on the machine's regional settings
    // (e.g. 1234.5 would otherwise become "1234,5" on a German/French OS).
    private static final DataFormatter FORMATTER = new DataFormatter(Locale.US);

    private ExcelUtils() {
    }

    /**
     * Retrieves a complete row of test data using TestDataId.
     *
     * @param sheetName  name of Excel sheet
     * @param testDataId unique TestDataId
     * @return Map containing column name -> cell value
     */
    public static Map<String, String> getTestData(String sheetName, String testDataId) {
        String resource = "testdata/" + ConfigManager.getEnvironment() + "/" + FILE_NAME;
        LoggerUtils.info("Reading test data '" + testDataId + "' from sheet '" + sheetName + "' in " + resource);
        Map<String, String> testData = new LinkedHashMap<>();

        try (InputStream in = openResource(resource);
                Workbook workbook = WorkbookFactory.create(in)) {

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "Excel sheet not found: " + sheetName + " in " + resource);
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException(
                        "Header row is missing in sheet: " + sheetName);
            }

            int testDataIdColumn = findColumn(headerRow, "TestDataId", evaluator);

            if (testDataIdColumn == -1) {
                throw new RuntimeException(
                        "TestDataId column not found in sheet: " + sheetName);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String currentId = getCellValue(row.getCell(testDataIdColumn), evaluator);

                if (testDataId.equalsIgnoreCase(currentId)) {

                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {

                        String columnName = getCellValue(headerRow.getCell(j), evaluator);

                        String value = getCellValue(row.getCell(j), evaluator);

                        testData.put(columnName, value);
                    }

                    return testData;
                }
            }

            throw new RuntimeException(
                    "TestDataId not found: " + testDataId + " in sheet: " + sheetName);

        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel file: " + resource, e);
        }
    }

    private static InputStream openResource(String resource) throws IOException {
        URL url = ExcelUtils.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Test data file not found on classpath: " + resource
                    + " (expected at src/test/resources/" + resource + "; the name is case-sensitive)");
        }
        verifyExactCase(url, resource);
        return url.openStream();
    }

    /**
     * Windows and macOS find files regardless of upper/lower case, Linux doesn't.
     * Fail on every OS when the name on disk differs only by case, so the
     * mismatch is caught locally instead of only on Linux CI.
     */
    private static void verifyExactCase(URL url, String resource) throws IOException {
        if (!"file".equals(url.getProtocol())) {
            return; // inside a jar: lookups are already case-sensitive
        }
        try {
            String actual = Paths.get(url.toURI()).toRealPath().toString().replace('\\', '/');
            if (!actual.endsWith(resource)) {
                throw new IllegalStateException("Test data path case mismatch: code expects '" + resource
                        + "' but the file on disk is '" + actual + "'. This would fail on Linux; rename the file or folder.");
            }
        } catch (URISyntaxException e) {
            throw new IOException("Invalid test data URL: " + url, e);
        }
    }

    private static int findColumn(Row headerRow, String columnName, FormulaEvaluator evaluator) {

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {

            String header = getCellValue(headerRow.getCell(i), evaluator);

            if (columnName.equalsIgnoreCase(header)) {
                return i;
            }
        }

        return -1;
    }

    /** Cell value as displayed in Excel; formulas are evaluated to their result. */
    private static String getCellValue(Cell cell, FormulaEvaluator evaluator) {

        if (cell == null) {
            return "";
        }

        return FORMATTER.formatCellValue(cell, evaluator).trim();
    }
}
