package com.esko.Service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esko.Dao.AttendanceFeedDao;
import com.esko.Model.Attendancefeed;
import com.esko.Utils.CSVFileReader.AttendanceFeedReader;
import com.esko.Utils.Exception.UnsupportedFileException;

@Service
@Transactional
public class AttendanceFeedServiceImpl implements AttendanceFeedService {
	@Autowired
	private AttendanceFeedDao dao;
	@Inject
	private AttendanceFeedReader attendanceFeedReader;
	final static Logger LOGGER = Logger.getLogger(AttendanceFeedService.class.getName());

	@Override
	public JSONObject read() {
		LOGGER.info("Processing attendance feed in service");
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			parsedCSVJSON = attendanceFeedReader.read();
			parsedCSVJSON.put("parsed", "Successful");
			LOGGER.info("in rest api " + parsedCSVJSON.toString());
		} catch (UnsupportedFileException e) {
			parsedCSVJSON.put("parsed", "Unsuccessful");
			e.printStackTrace();
		}
		return parsedCSVJSON;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Attendancefeed> listAttendanceFeed(Date startDate, Date endDate) {
		LOGGER.info("Getting attendance list between date range selected Service");
		List<Attendancefeed> l1 = dao.listAttendanceFeed(startDate, endDate);
		return l1;
	}

}
