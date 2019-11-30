
package com.applitools.quickstarts;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelHelper {

	String filePath;
	FileInputStream fis;
	Workbook wb;
	
	public ExcelHelper(String filePath) throws Exception {
		this.filePath= filePath; 
		fis= new FileInputStream(filePath);
		wb = WorkbookFactory.create(fis);
		}
	
	public String getFilePath() {
		return filePath;
	}
	
	public int getRowCount(String sheetName) {
		Sheet sheet= wb.getSheet(sheetName);
		return sheet.getPhysicalNumberOfRows();
	}
	
	public int getCellCount(String sheetName, int rownumber) {
		Sheet sheet= wb.getSheet(sheetName);
		return sheet.getRow(rownumber-1).getPhysicalNumberOfCells();
		
	}
	
	public String getCellData(String sheetName, int rownumber, int cellnumber) {
		try {
		Sheet sheet= wb.getSheet(sheetName);
		Cell cell = sheet.getRow(rownumber-1).getCell(cellnumber-1);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell.toString();
		}catch(Exception e) {
			return "";
		}
	
	//need to check getstringcellvalue as he used tostring method
	}
	
	public void setCellData(String sheetName , int rownumber, int cellnumber, String cellvalue) {
		Sheet sheet= wb.getSheet(sheetName);
		sheet.getRow(rownumber-1).getCell(cellnumber-1).setCellValue(cellvalue);
	}
	
	public int getRowNumber(String sheetName, int columnnumber, String cellvalue) {
		
		for(int row=1;row<=getRowCount(sheetName); row++) {
			if(getCellData(sheetName,row,columnnumber).equals(cellvalue))
				return row;
		} return -1;
	}
	
	public int getColumnNumber(String sheetName, int rownumber, String cellvalue) {
		
		for(int column=1; column<=getCellCount(sheetName,rownumber); column++) {
			if(getCellData(sheetName,rownumber,column).equals(cellvalue))
				return column;
		} return -1;
	}
}
