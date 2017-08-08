package com.esko.Utils.Plugin;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.esko.Utils.Exception.UnsupportedFileException;
 
public class ConverterImpl extends Converter {
	final static Logger LOGGER = Logger.getLogger(ConverterImpl.class.getName());
 
    /**
     * Convert the input feedFile to nativeFeedFormat
     * @throws UnsupportedFileException 
     */
 
    public void convert(InputStream uploadedInputStream) throws UnsupportedFileException {
 
        try {
        	
            File file = null;
            try {
                String resourcePath = getResourcePath();
                LOGGER.info("resource path is: "+resourcePath);
                file = new File(resourcePath + "/AttendanceFeed.csv");
                if(file.delete()){
        			LOGGER.info(file.getName() + " is deleted!");
        		}else{
        			LOGGER.info("Delete operation is failed.");
        		}
                file = new File(resourcePath + "/AttendanceFeed.csv");
            } catch (Exception e) {
            	LOGGER.info("found exception");
                e.printStackTrace();
            }
            CsvReader csvInput = new CsvReader(new InputStreamReader(uploadedInputStream));
            CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ',');
            
            for (Headers header : Headers.values()) {
                csvOutput.write(header.toString());
            }
            csvOutput.endRecord();
            csvInput.readHeaders();
            String[] inputHeaders = csvInput.getHeaders();
            if(inputHeaders==null){
            	throw new UnsupportedFileException("File format is not correct!");
            }
            List<String> inputHeader = Arrays.asList(inputHeaders);
            for (InputHeaders header : InputHeaders.values()) {
       		 if(!(inputHeader.contains(header.toString()))){
               	throw new UnsupportedFileException("File format is not correct!");
               }
          }
 
            Map<InputHeaders, String> enumMap = new EnumMap<InputHeaders, String>(InputHeaders.class);
            
 
            while (csvInput.readRecord()) {
 
                for (InputHeaders header : InputHeaders.values()) {
                    enumMap.put(header, csvInput.get(header.toString()));
 
                }
                DataTransformUtil.transformInputFeed(enumMap);
 
                for (InputHeaders header : InputHeaders.values()) {
                    csvOutput.write(enumMap.get(header));
                }
 
                csvOutput.endRecord();
 
            }
 
            csvInput.close();
            csvOutput.close();
 
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (ParseException e) {
        }
 
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
        } catch (Exception e) {
            throw e;
        }
    }
}