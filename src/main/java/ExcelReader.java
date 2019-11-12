import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ExcelReader {
    private Sheet sheet;

    private ExcelReader() {

    }

    public ExcelReader(File source, int sheetNumber) {
        sheet = getSheet(source, sheetNumber);
    }

    public List<Row> getAllRows(int startFrom) {
        List<Row> rows = new ArrayList<Row>();
        for(int i = startFrom;i<=sheet.getLastRowNum();i++) {
            rows.add(sheet.getRow(i));
        }
        return rows;
    }

    public Row getRowByIndex(int index) {
        return sheet.getRow(index);
    }

    public static String getCellValueByIndex(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        switch(cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return checkNumberValueFromCell(cell.getNumericCellValue());
        }
        return "";
    }

    public String getCellValue(int rowIndex, int cellIndex) {
        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        switch(cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return "" + cell.getNumericCellValue();
        }
        return "";
    }

    private Sheet getSheet(File source, int sheetNumber) {
        Sheet sheet = null;
        try {
            sheet = new XSSFWorkbook(new FileInputStream(source)).getSheetAt(sheetNumber);
        } catch(FileNotFoundException e) {
            System.err.println("Файл не найден");
            e.printStackTrace();
            Logs.print(e.getMessage());
        } catch(IOException e) {
            System.err.println("Невозможно прочитать файл");
            e.printStackTrace();
            Logs.print(e.getMessage());
        }
        return sheet;
    }

    private static String checkNumberValueFromCell(double numberValueFromCell) {
        System.out.println(numberValueFromCell);
        if(("" + numberValueFromCell).contains(".")) {
            if (("" + numberValueFromCell).split("\\.")[1].equals("0"))
                return "" + (long) numberValueFromCell;
            else
                return "" + numberValueFromCell;
        } else if(("" + numberValueFromCell).split(",")[1].equals("0")) {
            return "" + (long) numberValueFromCell;
        } else
            return "" + numberValueFromCell;
    }
}
