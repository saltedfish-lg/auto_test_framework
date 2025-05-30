package com.baidu.utils;

import com.baidu.api.enums.AuthType;
import com.baidu.api.model.AuthCaseData;
import com.baidu.api.model.ErrorCaseData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    /**
     * 读取错误用例 Excel 数据，映射为对象列表
     * @param filePath 文件路径（如 src/test/resources/data/user_error_cases.xlsx）
     * @return 错误用例数据列表
     */
    public static List<ErrorCaseData> readErrorCases(String filePath) {
        List<ErrorCaseData> cases = new ArrayList<>();

        try (InputStream is = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return cases; // 空表格
            Row header = rowIterator.next(); // 跳过表头

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowIsEmpty(row)) continue;

                ErrorCaseData data = new ErrorCaseData();
                data.setPath(getCellString(row, 0));
                data.setMethod(getCellString(row, 1));
                data.setToken(normalize(getCellString(row, 2)));
                data.setExpectedStatus((int) getCellNumeric(row, 3));
                data.setExpectedError(getCellString(row, 4));
                data.setSchemaFile(getCellString(row, 5));

                // ✅ 第 7 列（enabled），若为空或为 true 则执行
                String enabledCell = getCellString(row, 6);
                data.setEnabled(enabledCell.isEmpty() || enabledCell.equalsIgnoreCase("true"));

                if (data.isEnabled()) {
                    cases.add(data);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ 读取 Excel 失败: " + filePath, e);
        }

        return cases;
    }

    private static boolean rowIsEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (!getCellString(row, i).isEmpty()) return false;
        }
        return true;
    }

    private static String getCellString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString().trim();
    }

    private static double getCellNumeric(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        try {
            return Double.parseDouble(cell.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String normalize(String value) {
        return value.equalsIgnoreCase("(empty)") ? "" : value;
    }
    /**
     * 读取带多鉴权类型的测试数据 Excel（支持 expectedError + schemaFile）
     * @param filePath Excel 文件路径
     * @return AuthCaseData 列表
     */
    public static List<AuthCaseData> readAuthCases(String filePath) {
        List<AuthCaseData> cases = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || rowIsEmpty(row)) continue;

                AuthCaseData data = new AuthCaseData();
                data.setPath(getCellString(row, 0));             // path
                data.setMethod(getCellString(row, 1));           // method

                try {
                    data.setAuthType(AuthType.valueOf(getCellString(row, 2).toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("⚠️ 第 " + (i + 1) + " 行非法 AuthType: " + getCellString(row, 2));
                }

                data.setExpectedStatus((int) getCellNumeric(row, 3));  // expectedStatus
                data.setExpectedError(getCellString(row, 4));          // expectedError
                data.setSchemaFile(getCellString(row, 5));             // schemaFile

                cases.add(data);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ 读取鉴权 Excel 测试数据失败: " + filePath, e);
        }

        return cases;
    }


}
