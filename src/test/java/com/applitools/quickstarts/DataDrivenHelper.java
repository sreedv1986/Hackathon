package com.applitools.quickstarts;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class DataDrivenHelper extends ExcelHelper {

	public final static int TestNameColumn = 1;
	public final static int TestDataStartColumn = 2;
	public DataDrivenHelper(String filePath) throws Exception {
		super(filePath);
		// TODO Auto-generated constructor stub
	}

	public Object[][] getTestcaseDataset(String sheetName, String testName) {
		int startrownumber= getRowNumber(sheetName, TestNameColumn, testName);
		int rowcounter=0;
		for(int i=startrownumber;getCellData(sheetName, i, TestNameColumn).equals(testName) ;i++) {
			rowcounter++;
		}
		int columncount= getCellCount(sheetName, startrownumber);
		
		Object[][] dataset = new Object[rowcounter-1][columncount-1];
		for(int row=0; row<rowcounter-1; row++) {
			for(int col=0; col<columncount-1; col++) {
			dataset[row][col]=	getCellData(sheetName, startrownumber+row+1, TestDataStartColumn+col);
			}
		}
		return dataset;
	}
	
	public Map<String, String> getKeyValueDataset(String sheetName, String keyValueName) {
		int startrownumber= getRowNumber(sheetName, TestNameColumn, keyValueName);
		int columncount= getCellCount(sheetName, startrownumber);
		Map<String, String> kvMap = new HashMap<String, String>();
		
		for(int col=0; col<columncount-1; col++) {
			String key = getCellData(sheetName, startrownumber, TestDataStartColumn+col);
			String value = getCellData(sheetName, startrownumber+1, TestDataStartColumn+col);
			kvMap.put(key, value);
		}
		
		return kvMap;
	}
}
