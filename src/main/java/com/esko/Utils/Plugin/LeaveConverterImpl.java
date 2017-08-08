package com.esko.Utils.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.Pdf.PropertiesFile;
import com.esko.Utils.XLSXFileReader.LeaveXLSXReader;

public class LeaveConverterImpl extends LeaveConverter {
	
	final static Logger LOGGER = Logger.getLogger(LeaveConverterImpl.class.getName());
	
	/* just login converter plugin
	 * converts the just login format to spanda native format 
	 * */

	Properties prop=PropertiesFile.readProperties("spandana.properties");
	
	@Override
	public void convert(InputStream uploadedInputStream, String fileName) throws UnsupportedFileException{
		  File file = null;
		  
          try {
              String resourcePath = getResourcePath();
              file = new File(resourcePath + "/LeaveFeed.csv");
              if(file.delete()){
      			LOGGER.info(file.getName() + " is deleted!");
      		}else{
      			LOGGER.info("Delete operation is failed.");
      		}
              file = new File(resourcePath + "/LeaveFeed.csv");
          } catch (Exception e) {
              e.printStackTrace();
          }
		String xLSXFileName = null;
		try {
			
			xLSXFileName = writeToFile(uploadedInputStream, fileName);
			
		} 
		catch (IOException e1) {
			
			e1.printStackTrace();
		}	
		final String COMMA_DELIMITER = ",";
		final String NEW_LINE_SEPARATOR = "\n";
		final String FILE_HEADER=prop.getProperty("LeaveFeedColumns");
		
		//final String FILE_HEADER = "EmployeeID,LeaveDate,LeaveType,Duration";
		
		/* it will have all rows present in xlsx file */
		HashMap rows = new HashMap();
		
		/* value will have assoicate details */
		ArrayList value = new ArrayList();
		FileWriter fileWriter = null;
		LeaveXLSXReader lsr = new LeaveXLSXReader();
		
		try {
			
			LOGGER.info("parsing xlsx file");
			rows = LeaveXLSXReader.readExcelSheetData(xLSXFileName);
			ArrayList<String> keys = new ArrayList<String>(rows.keySet());
			LOGGER.info("no of rows in file:"+" "+keys.size());
			if(keys.size()==0)
			{
				LOGGER.info("file format is not correct");
				throw new UnsupportedFileException("file format is not correct");
				
			}
			int rowcount = 0;
			for(String key: keys)
			{
				value=(ArrayList) rows.get(key);
				if((value.get(0).toString()).equals("Emp No."))
				{
					rowcount++;
					if(value.size()>=8 && (value.get(4).equals("Leave Type")) 
							&& (value.get(5).equals("From(DD/MM/YYYY)")) 
							&& (value.get(6).equals("To(DD/MM/YYYY)"))
							&& (value.get(7).equals("No.of days"))
							&& (value.get(8).equals("Leave Status")))
					{
						break;
					}
					else
					{
						LOGGER.info("file format is not correct");
						throw new UnsupportedFileException("file format is not correct");
					}
					
				}
			}
			if(rowcount==0)
			{
				LOGGER.info("file format is not correct");
				throw new UnsupportedFileException("file format is not correct");
			}
			
			LOGGER.info("started...creating native spandana leave feed format");
			fileWriter = new FileWriter(file);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			for (String key : keys)
			{
				value = (ArrayList)rows.get(key);

				if (value.get(0) != "Blank" && value.size()>7) {
					if (value.get(0) != null && !(value.get(0).equals("Emp No.")) && value.get(4) != null
							&& value.get(5) != null && value.get(6) != null && value.get(7) != null
							&& value.get(8) != null && (value.get(8).toString().equals("Approved") || value.get(8).toString().equals("Pending"))) 
					{
						Float days = Float.parseFloat(value.get(7).toString());
						if (days == 0.5) 
						{
							fileWriter.append(String.valueOf(value.get(0)));
							fileWriter.append(COMMA_DELIMITER);
							fileWriter.append(value.get(5).toString().split(" ")[0]);
							fileWriter.append(COMMA_DELIMITER);
							fileWriter.append(value.get(4).toString().split(" ")[0]);
							fileWriter.append(COMMA_DELIMITER);
								if (value.get(5).toString().contains("AM") && value.get(5).toString().contains("AM")) 
								{
									fileWriter.append("First_Half");
								} 
								else 
								{
									fileWriter.append("Second_Half");
								}
							

							fileWriter.append(NEW_LINE_SEPARATOR);
						}
						/*
						 * for more than a day leave, here we have to exclude
						 * both weekends and Holidays
						 */
						else {
							String d1 = value.get(5).toString().split(" ")[0];
							String d2 = value.get(6).toString().split(" ")[0];
							Date startDate = new SimpleDateFormat("dd/M/yyyy").parse(d1);
							Date endDate = new SimpleDateFormat("dd/M/yyyy").parse(d2);
							int noOfDays = getNoOfDays(startDate, endDate);
							for (int i = 0; i <= noOfDays; i++) {
								Date fromDate = new SimpleDateFormat("dd/M/yyyy")
										.parse(value.get(5).toString().split(" ")[0]);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(fromDate);
								calendar.add(Calendar.DATE, i);
								SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
								sdf.setCalendar(calendar);
								sdf.format(calendar.getTime());
								String dateFormatted = sdf.format(calendar.getTime());
								fileWriter.append(String.valueOf(value.get(0)));
								fileWriter.append(COMMA_DELIMITER);
								fileWriter.append(dateFormatted);
								fileWriter.append(COMMA_DELIMITER);
								fileWriter.append(value.get(4).toString().split(" ")[0]);
								fileWriter.append(COMMA_DELIMITER);
								if (value.get(5).toString().contains("AM") && value.get(6).toString().contains("AM")) {
									if (i == noOfDays) {
										fileWriter.append("First_Half");
									} else {
										fileWriter.append("Full");
									}
									fileWriter.append(NEW_LINE_SEPARATOR);
								} else if (value.get(5).toString().contains("AM")
										&& value.get(6).toString().contains("PM")) {
									fileWriter.append("Full");
									fileWriter.append(NEW_LINE_SEPARATOR);
								} else if (value.get(5).toString().contains("PM")
										&& value.get(6).toString().contains("AM")) {
									if (i == 0) {
										fileWriter.append("Second_Half");
									} else if ((float) i == noOfDays) {
										fileWriter.append("First_Half");
									} else {
										fileWriter.append("Full");
									}
									fileWriter.append(NEW_LINE_SEPARATOR);
								} else if (value.get(5).toString().contains("PM")
										&& value.get(6).toString().contains("PM")) {

									if (i == 0) {
										fileWriter.append("Second_Half");
									} else {
										fileWriter.append("Full");

									}
									fileWriter.append(NEW_LINE_SEPARATOR);
								}
							}
						}
					}
				}

			}
		} 
		catch (Exception e) {
			
			LOGGER.info(e);
			e.printStackTrace();
		}

		finally {
			
			try {
				fileWriter.flush();
				fileWriter.close();
			} 
			catch (IOException e) 
			{
				LOGGER.info(e);
				e.printStackTrace();
				
			}
		}
		try {
			
			getResourcePath();
		} 
		catch (Exception e) {
			
			LOGGER.info(e);
			e.printStackTrace();
		}
	}

	public int getNoOfDays(Date startDate, Date endDate) {

		DateTime dt1 = new DateTime(startDate);
		DateTime dt2 = new DateTime(endDate);	
		int noOfDays=Days.daysBetween(dt1, dt2).getDays();
		return noOfDays;
	}

	public String writeToFile(InputStream uploadedInputStream, String uploadedFileName) throws IOException {

		LOGGER.info("started...converting uploaded stream into file");
        OutputStream out = null;
        int read = 0;

        byte[] bytes = new byte[1024];

        String fileName = prop.getProperty("LeaveFeedPath").replace("//", "////") + uploadedFileName;

        out = new FileOutputStream(new File(fileName));

        while ((read = uploadedInputStream.read(bytes)) != -1) {

                out.write(bytes, 0, read);

        }

        out.flush();

        out.close();

        LOGGER.info("uploaded stream converted into file successfully and fileName is:"+" "+fileName);
        return fileName;

}	
	 private String getResourcePath() throws Exception {
	       
	        try {
	        	
	        	ClassLoader classLoader = getClass().getClassLoader();
				URI resourcePathFile = classLoader.getResource("RESOURCE_PATH").toURI();
	            String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
	            URI rootURI = new File("").toURI();
	            URI resourceURI = new File(resourcePath).toURI();
	            URI relativeResourceURI = rootURI.relativize(resourceURI);
	            return relativeResourceURI.getPath();
	        } 
	        catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            throw e;
	        }
	    }

}
