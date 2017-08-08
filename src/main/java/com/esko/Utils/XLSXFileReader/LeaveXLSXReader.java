package com.esko.Utils.XLSXFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class LeaveXLSXReader {
	
		public static final String CLASS_NAME = "ReadDataFromExcelSheet: ";
	    /** 
	     * Reads given excel sheet data and returns excel sheet data in HashMap
	     *
	     * @param p_strFilePath		Excelsheet file name along with folder path
	     * @return 
	     * @return HashMap			Returns data from excelsheet into HashMap         		
	     * @exception Exception		Throws exception in case file is not found or some other error conditions
	     * @see Exception
	     */	
		public static HashMap readExcelSheetData(String p_strFilePath)throws Exception{
			String METHOD_NAME = "readExcelSheetData(): ";
			HashMap m_objHmAllRowData = new HashMap();
			ArrayList m_objAlCellData;
			String m_strKey="ROW";
			FileInputStream m_objFiFileName = null;
			int m_iRowValue=0;
			try
	        {
				m_objFiFileName = new FileInputStream(new File(p_strFilePath));
				XSSFWorkbook m_objWorkBook = new XSSFWorkbook(m_objFiFileName);
	            //Get first or desired sheet from the Workbook
				XSSFSheet  m_objSheet = m_objWorkBook.getSheetAt(0);
	             //Iterate through each row one by one
	            Iterator m_objRowIterator = m_objSheet.iterator();
	            while (m_objRowIterator.hasNext())
	            {
	            	XSSFRow  m_objHssfRow = (XSSFRow )m_objRowIterator.next();
	                //For each row, iterate through all the columns
	                Iterator m_objCellIterator = m_objHssfRow.cellIterator();
	                m_objAlCellData = new ArrayList();
	                m_iRowValue++;
	                while (m_objCellIterator.hasNext())
	                {
	                	XSSFCell   m_objCellData = (XSSFCell )m_objCellIterator.next();
	                    //Check the cell type and format accordingly
	                    switch (m_objCellData.getCellType())
	                    {
	                        case XSSFCell .CELL_TYPE_NUMERIC:
	                            m_objAlCellData.add(m_objCellData.getNumericCellValue());
	                            break;
	                        case XSSFCell .CELL_TYPE_STRING:
	                            m_objAlCellData.add(m_objCellData.getStringCellValue());
	                            break;
	                       case XSSFCell .CELL_TYPE_BLANK:
	                    	   m_objAlCellData.add("Blank");
	                    }
	                }
	                m_objHmAllRowData.put(m_strKey+m_iRowValue,m_objAlCellData);
	            }
	            
	        }
			finally
			{
				if(m_objFiFileName!=null){
					m_objFiFileName.close();
					m_objFiFileName=null;
				}
			}

			return m_objHmAllRowData;
		}//End of method
		
}
