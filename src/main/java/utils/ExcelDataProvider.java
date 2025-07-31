package utils;

import constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDataProvider {
    private static final Logger logger = LogManager.getLogger(ExcelDataProvider.class);

    private ExcelDataProvider() {
        // Private constructor to prevent instantiation
    }

    public static Object[][] getTestData(String fileName, String sheetName) {
        Object[][] data = null;
        try (FileInputStream fis = new FileInputStream(FrameworkConstants.TEST_DATA_PATH + fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();
            
            // Get header row for map keys
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < colCount; i++) {
                headers.add(headerRow.getCell(i).getStringCellValue());
            }

            data = new Object[rowCount][1];
            // Start from row 1 as row 0 is header
            for (int i = 1; i <= rowCount; i++) {
                Map<String, String> rowData = new HashMap<>();
                Row row = sheet.getRow(i);
                
                if (row != null) {
                    for (int j = 0; j < colCount; j++) {
                        Cell cell = row.getCell(j);
                        rowData.put(headers.get(j), getCellValueAsString(cell));
                    }
                    data[i-1][0] = rowData;
                }
            }

            logger.info("Successfully read test data from excel: " + fileName + " - " + sheetName);
        } catch (IOException e) {
            logger.error("Error reading excel file: " + e.getMessage(), e);
            throw new constants.FrameworkException("Failed to read test data from excel", e);
        }
        return data;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
