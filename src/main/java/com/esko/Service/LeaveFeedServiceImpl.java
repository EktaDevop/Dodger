package com.esko.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esko.Dao.LeaveFeedDao;
import com.esko.Model.Leavefeed;
import com.esko.Utils.CSVFileReader.LeaveFeedReader;
import com.esko.Utils.Exception.UnsupportedFileException;

@Service
@Transactional
public class LeaveFeedServiceImpl implements LeaveFeedService {
	final static Logger LOGGER = Logger.getLogger(LeaveFeedService.class.getName());
	@Autowired
	private LeaveFeedDao leaveFeedDao;
	@Inject
	private LeaveFeedReader leaveFeedReader;

	@Override
	public JSONObject read() {
		LOGGER.info("Processing Leave feed in service");
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			parsedCSVJSON = leaveFeedReader.read();
			parsedCSVJSON.put("parsed", "Successful");
			LOGGER.info("In rest api " + parsedCSVJSON.toString());
		} catch (UnsupportedFileException e) {
			parsedCSVJSON.put("parsed", "Unsuccessful");
		} catch (IOException e) {

		}
		return parsedCSVJSON;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Leavefeed> listLeaveFeed(Date startDate, Date endDate) {
		LOGGER.info("Getting leave list between date range selected Service");
		List<Leavefeed> l1 = leaveFeedDao.listLeaveFeed(startDate, endDate);
		return l1;
	}

}
