package com.esko.Service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esko.Dao.HolidayCalenderDao;
import com.esko.FrontEndObjects.HolidayCalendar;
import com.esko.Model.Holidaycalendar;

@Service
@Transactional
public class HolidayCalendarServiceImpl implements HolidayCalenderService {
	@Autowired
	private HolidayCalenderDao holidayCalendarDao;
	final static Logger LOGGER = Logger.getLogger(HolidayCalendarServiceImpl.class.getName());

	@Override
	public List<Holidaycalendar> getHolidayList() {
		LOGGER.info("In get holiday details Service");
		List<Holidaycalendar> l1 = holidayCalendarDao.getHolidayList();
		LOGGER.info("Holiday List is: " + l1.toString());
		return l1;
	}

	@Override
	@Transactional(readOnly = true)
	public void insertHoliday(Holidaycalendar cal) {
		LOGGER.info("in add Calendar Row Service " + cal.toString());
		holidayCalendarDao.insertHoliday(cal);
	}

	@Override
	@Transactional(readOnly = true)
	public void deleteCalendarRow(Holidaycalendar cal) {
		LOGGER.info("in delete calendar Service");
		holidayCalendarDao.deleteCalendarRow(cal);
	}

	@Override
	@Transactional(readOnly = true)
	public void updateCalendarRow(HolidayCalendar cal) {
		LOGGER.info("in update calendar Service");
		holidayCalendarDao.updateCalendarRow(cal);
	}

	@Override
	public Holidaycalendar getHolidaycalendarById(int id) {
		LOGGER.info("in getting holidaycalendar using id in service");
		Holidaycalendar holidaycalendar = holidayCalendarDao.getHolidaycalendarById(id);
		return holidaycalendar;
	}
}
