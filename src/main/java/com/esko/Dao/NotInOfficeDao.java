package com.esko.Dao;

import java.util.Date;
import java.util.List;

import com.esko.Model.Notinoffice;

public interface NotInOfficeDao {
	/*
	 * Getting the list of the applied leaves by the employee
	 */
	public List<Date> getListLeaveDates(String employeeId);

	/*
	 * Getting the list of the applied WFH by the employee
	 */
	public List<Date> listWFHDates(String employeeId);

	/*
	 * Inserting row in not in office table
	 */
	public String addNotInOffice(Notinoffice notInOffice);

	/*
	 * Getting the history of not in office of an employee
	 */
	public List<Notinoffice> getNotInOfficeHistory(String employeeId);

	/*
	 * To cancel the applied not in office request
	 */
	public void cancelRequest(int id, String employeeId);

	/*
	 * To get the pending request list to the manager
	 */
	public List<Notinoffice> pendingRequests(String employeeId);

	/*
	 * To approve the applied not in office request
	 */
	public void approveRequest(int id);

	/*
	 * Checking if the not in office can be applied for the selected interval
	 */
	public boolean checkAppliedInterval(Notinoffice notInOffice);

	/*
	 * Getting the list for not in office present in the not in office table
	 * between the selected date range
	 */
	public List<Notinoffice> getNotInOfficeList(Date startDate, Date endDate);

	public int getWfhDays(String employeeId, Date date);

	public void cancelRequestManager(int id, String employeeId);

}
