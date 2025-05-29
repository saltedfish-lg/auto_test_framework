package com.baidu.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    public static Iterator<Object[]> readAsIterator(String filePath) throws Exception {
        List<Object[]> data = new ArrayList<>();
        try (var fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<String> headers = new ArrayList<>();

            if (rows.hasNext()) {
                Row headerRow = rows.next();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue().trim());
                }
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    map.put(headers.get(i), cell.getStringCellValue().trim());
                }
                data.add(new Object[]{map});
            }
        }
        return data.iterator();
    }
}
