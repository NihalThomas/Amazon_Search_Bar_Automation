package utils;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static Object[][] readExcel(String filePath) {
        List<Object[]> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            
            // Start from 1 to skip header row
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<String> rowData = new ArrayList<>();
                    // Read up to 3 columns: TestCase, Input, Expected
                    for (int j = 0; j < 3 && row.getCell(j) != null; j++) {
                        rowData.add(row.getCell(j).getStringCellValue());
                    }
                    // Ensure all rows have 3 columns, pad with empty strings if needed
                    while (rowData.size() < 3) {
                        rowData.add("");
                    }
                    data.add(rowData.toArray());
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toArray(new Object[0][]);
    }
}