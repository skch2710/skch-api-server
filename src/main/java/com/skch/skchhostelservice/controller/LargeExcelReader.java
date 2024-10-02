package com.skch.skchhostelservice.controller;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LargeExcelReader {
	
	public static void main(String[] args) throws IOException {
		readLargeExcelFile("C:/Users/HP/Downloads/User_Template (4).xlsx");
	}

    public static void readLargeExcelFile(String filePath) throws IOException {
        try (InputStream is = new FileInputStream(filePath);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(10000)    // Number of rows to keep in memory at once
                     .bufferSize(40960)      // Buffer size to use while reading the file
                     .open(is)) {

            // Assuming you are reading from a specific sheet (e.g., "Data Sheet")
            Sheet sheet = workbook.getSheet("Data Sheet");
            System.out.println(sheet.getLastRowNum());
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    switch (cell.getCellType()) {
//                        case STRING:
//                            System.out.print(cell.getStringCellValue() + " | ");
//                            break;
//                        case NUMERIC:
//                            System.out.print(cell.getNumericCellValue() + " | ");
//                            break;
//                        default:
//                            System.out.print("Unknown type | ");
//                    }
//                }
//                System.out.println(); // Newline after each row
//            }
        }
    }
}
