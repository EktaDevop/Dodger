package com.esko.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esko.Dao.HolidayCalenderDao;
import com.esko.Dao.NotInOfficeDao;
import com.esko.Model.Employeedetails;
import com.esko.Model.Holidaycalendar;
import com.esko.Model.Notinoffice;
import com.esko.Other.DateConverter;

@Service
@Transactional
public class NotInOfficeServiceImpl implements NotInOfficeService {
	final static Logger LOGGER = Logger.getLogger(NotInOfficeService.class.getName());
	@Autowired
	private NotInOfficeDao notInOfficeDao;

	@Autowired
	private HolidayCalenderDao holidayCalendarDao;

	@Override
	public List<Date> listLeaveDates(String employeeId) {
		List<Date> list = notInOfficeDao.getListLeaveDates(employeeId);
		return list;
	}

	@Override
	public List<Date> listWFHDates(String employeeId) {
		List<Date> list = notInOfficeDao.listWFHDates(employeeId);
		return list;
	}

	@Override
	public String addNotInOffice(Notinoffice notInOffice) {
		LOGGER.info("Adding in not in office Service");
		Employeedetails employeedetails = notInOffice.getEmployeedetailsByEmployeeIdNio();
		int wfhEligibleDays = employeedetails.getWfhDays();
		List<Date> listLeaveDates = listLeaveDates(notInOffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId());
		List<Date> listWFHDates = listWFHDates(notInOffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId());
		List<Holidaycalendar> List = holidayCalendarDao.getHolidayList();
		List<Date> holidayDates = new ArrayList<Date>();
		for (int i = 0; i < List.size(); i++) {
			holidayDates.add(List.get(i).getHolidayDate());
		}
		List<Date> listFinal1 = new ArrayList<Date>();
		listFinal1.addAll(listLeaveDates);
		listFinal1.addAll(listWFHDates);
		listFinal1.addAll(holidayDates);
		List<Date> listFinal = new ArrayList<Date>();
		listFinal.addAll(listFinal1);
		for (int i = 0; i < listFinal1.size(); i++) {
			Calendar c = Calendar.getInstance();
			Date date = DateConverter.getDateInputDate("yyyy-MM-dd", listFinal.get(i));
			c.setTime(date);
			int day = c.get(Calendar.DAY_OF_WEEK);
			c.add(Calendar.DAY_OF_MONTH, -1);
			Date prevDate = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
			c.add(Calendar.DAY_OF_MONTH, 2);
			Date nextDate = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
			if (day == 6) {
				c.add(Calendar.DAY_OF_MONTH, 2);
				Date nextMonday = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
				if (!(listFinal.contains(nextMonday))) {
					listFinal.add(nextMonday);
				}
			}if(day==2){
				c.add(Calendar.DAY_OF_MONTH, -4);
				Date prevFriday = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
				if (!(listFinal.contains(prevFriday))) {
					listFinal.add(prevFriday);
				}
			}
			if (!(listFinal.contains(prevDate))) {
				listFinal.add(prevDate);
			}
			if (!(listFinal.contains(nextDate))) {
				listFinal.add(nextDate);
			}

		}

		if (notInOffice.getRequestType().equalsIgnoreCase("Work From Home")) {
			if (!(listFinal.contains(notInOffice.getDateOfRequestStart()))) {
				boolean check = true;
				check = notInOfficeDao.checkAppliedInterval(notInOffice);
				if(check){
					int wfhDays = getWfhDays(notInOffice.getEmployeedetailsByEmployeeIdNio().getEmployeeId(),
							notInOffice.getDateOfRequestStart());
					String status = "";
					if (wfhDays == wfhEligibleDays) {
						status = "Exception";
						return status;
					} else {
						status = notInOfficeDao.addNotInOffice(notInOffice);
						return status;
					}
				}else{
					return "Exception";
				}
			}else{
				return "Exception";
			}

		} else {
			boolean check = true;
			check = notInOfficeDao.checkAppliedInterval(notInOffice);
			if (check) {
				String status = notInOfficeDao.addNotInOffice(notInOffice);
				return status;
			} else {
				return "Exception";
			}
		}

	}

	@Override
	public List<Notinoffice> getNotInOfficeHistory(String employeeId) {
		LOGGER.info("Getting not in office history Service");
		List<Notinoffice> list = notInOfficeDao.getNotInOfficeHistory(employeeId);
		return list;
	}

	@Override
	public void cancelRequest(int id, String employeeId) {
		LOGGER.info("canceling not in office history Service");
		notInOfficeDao.cancelRequest(id, employeeId);

	}

	@Override
	public List<Notinoffice> pendingRequests(String employeeId) {
		LOGGER.info("Getting pending requests for managers in Service");
		List<Notinoffice> list = notInOfficeDao.pendingRequests(employeeId);
		return list;
	}

	@Override
	public void approveRequest(int id) {
		LOGGER.info("Approving not in office history Service");
		notInOfficeDao.approveRequest(id);

	}

	@Override
	public List<Notinoffice> getNotInOfficeList(Date startDate, Date endDate) {
		LOGGER.info("Getting not in office history in date range Service");
		List<Notinoffice> notinofficeList = notInOfficeDao.getNotInOfficeList(startDate, endDate);
		return notinofficeList;
	}

	@Override
	public int getWfhDays(String employeeId, Date date) {
		int whfDays = notInOfficeDao.getWfhDays(employeeId, date);
		return whfDays;
	}

	@Override
	public void cancelRequestManager(int id, String employeeId) {
		LOGGER.info("canceling not in office history Service");
		notInOfficeDao.cancelRequestManager(id, employeeId);
	}
}
