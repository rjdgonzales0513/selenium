package com.common.utils;

import org.apache.poi.ss.usermodel.*;

import com.common.config.ConfigManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelUtils {

    /**
     * Retrieves a complete row of test data using TestDataId.
     *
     * @param sheetName  name of Excel sheet
     * @param testDataId unique TestDataId
     * @return Map containing column name -> cell value
     */
    public static Map<String, String> getTestData(String sheetName, String testDataId) {
        String filePath = "src/test/resources/testdata/" + ConfigManager.getEnvironment() + "/TestData.xlsx";
        LoggerUtils.info("Reading test data '" + testDataId + "' from sheet '" + sheetName + "' in " + filePath);
        Map<String, String> testData = new LinkedHashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "Excel sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException(
                        "Header row is missing in sheet: " + sheetName);
            }

            int testDataIdColumn = findColumn(
                    headerRow,
                    "TestDataId");

            if (testDataIdColumn == -1) {
                throw new RuntimeException(
                        "TestDataId column not found in sheet: "
                                + sheetName);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String currentId = getCellValue(row.getCell(testDataIdColumn));

                if (testDataId.equalsIgnoreCase(currentId)) {

                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {

                        String columnName = getCellValue(headerRow.getCell(j));

                        String value = getCellValue(row.getCell(j));

                        testData.put(columnName, value);
                    }

                    return testData;
                }
            }

            throw new RuntimeException(
                    "TestDataId not found: "
                            + testDataId
                            + " in sheet: "
                            + sheetName);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read Excel file: " + filePath, e);
        }
    }

    private static int findColumn(
            Row headerRow,
            String columnName) {

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {

            String header = getCellValue(headerRow.getCell(i));

            if (columnName.equalsIgnoreCase(header)) {
                return i;
            }
        }

        return -1;
    }

    private static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter.formatCellValue(cell).trim();
    }
}