package com.esko.Dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.esko.Model.Employeedetails;
import com.esko.Model.Notinoffice;
import com.esko.Other.DateConverter;

@Repository
public class NotInOfficeDaoImpl implements NotInOfficeDao {
	@Autowired
	private SessionFactory factory;

	@Inject
	private MasterAssociateDao masterAssociateDao;

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	final static Logger LOGGER = Logger.getLogger(NotInOfficeDaoImpl.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getListLeaveDates(String employeeId) {
		LOGGER.info("In get Leave list Dao");
		List<Date> leaveList = new ArrayList<Date>();
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameShowingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String todayDate = "";
			Date date1 = new Date();
			try {
				todayDate = dateFormat.format(date);
				date1 = new SimpleDateFormat("yyyy-MM-dd").parse(todayDate);
			} catch (ParseException e) {

			}
			leaveList = session
					.createQuery(
							"select l.id.leaveDate FROM com.esko.Model.Leavefeed l where l.employeedetails = :employeedetails and l.id.leaveDate >= :date1 ORDER BY l.id.leaveDate ASC")
					.setParameter("date1", date1).setParameter("employeedetails", employeedetails).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during leaves fetch " + e.getMessage());
			session.getTransaction().rollback();
		}
		return leaveList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> listWFHDates(String employeeId) {
		LOGGER.info("In get WFH list Dao");
		List<Date> wfhList = new ArrayList<Date>();
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameShowingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Date date = new Date();
			Date date1 = DateConverter.getDateInputDate("yyyy-MM-dd", date);
			wfhList = session
					.createQuery(
							"select n.dateOfRequestStart FROM com.esko.Model.Notinoffice n where n.employeedetailsByEmployeeIdNio = :employeedetails and n.dateOfRequestStart >= :date1 and (n.requestStatus = :requestStatus1 or n.requestStatus = :requestStatus2 or n.requestStatus = :requestStatus3 or n.requestStatus = :requestStatus4) ORDER BY n.id.dateOfRequestStart ASC")
					.setParameter("date1", date1).setParameter("employeedetails", employeedetails)
					.setParameter("requestStatus1", "Approved").setParameter("requestStatus2", "Pending")
					.setParameter("requestStatus3", "Cancellation Pending")
					.setParameter("requestStatus4", "Cancellation Declined").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during WFH list fetch " + e.getMessage());
			session.getTransaction().rollback();
		}
		return wfhList;
	}

	@SuppressWarnings("finally")
	@Override
	public String addNotInOffice(Notinoffice notInOffice) {
		LOGGER.info("In Adding not in office Dao");
		Session session = factory.getCurrentSession();
		String status = "";
		try {
			session.getTransaction().begin();
			session.save(notInOffice);
			session.getTransaction().commit();
			status = "Added";
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while inserting not in office  " + e.getMessage());
			status = "Exception";
			session.getTransaction().rollback();
		} finally {
			return status;
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	@Override
	public List<Notinoffice> getNotInOfficeHistory(String employeeId) {
		LOGGER.info("In fetching not in office list Dao");
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice = session
					.createQuery(
							"FROM com.esko.Model.Notinoffice n WHERE n.employeedetailsByEmployeeIdNio =:employeedetails ")
					.setParameter("employeedetails", employeedetails).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while fetching not in office history " + e.getMessage());
			session.getTransaction().rollback();
		} finally {
			return notInOffice;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void cancelRequest(int id, String employeeId) {
		LOGGER.info("In canceling not in office list Dao");
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice = session.createQuery("FROM com.esko.Model.Notinoffice n WHERE n.id =:id")
					.setParameter("id", id).list();
			Notinoffice notinoffice2 = notInOffice.get(0);
			String status = notinoffice2.getRequestStatus();
			if (status.equalsIgnoreCase("pending")) {
				notinoffice2.setRequestStatus("Cancelled");
			} else {
				notinoffice2.setRequestStatus("Cancellation Pending");
			}

			session.update(notinoffice2);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while canceling not in office  " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void approveRequest(int id) {
		LOGGER.info("In approving not in office list Dao");
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice = session.createQuery("FROM com.esko.Model.Notinoffice n WHERE n.id =:id")
					.setParameter("id", id).list();
			Notinoffice notinoffice2 = notInOffice.get(0);
			String status = notinoffice2.getRequestStatus();
			if (status.equalsIgnoreCase("pending")) {
				notinoffice2.setRequestStatus("Approved");
			} else {
				notinoffice2.setRequestStatus("Cancel after approve");
			}
			session.update(notinoffice2);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while approving not in office  " + e.getMessage());
			session.getTransaction().rollback();
		}

	}

	@SuppressWarnings({ "unchecked", "finally" })
	@Override
	public List<Notinoffice> pendingRequests(String employeeId) {
		LOGGER.info("In fetching not in office list Dao");
		Employeedetails managerdetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice = session
					.createQuery(
							"FROM com.esko.Model.Notinoffice n WHERE n.employeedetailsByManagerIdNio =:managerdetails and (n.requestStatus = :requestStatus1 or n.requestStatus = :requestStatus2)")
					.setParameter("managerdetails", managerdetails).setParameter("requestStatus1", "Pending")
					.setParameter("requestStatus2", "Cancellation Pending").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while fetching pending requests  " + e.getMessage());
			session.getTransaction().rollback();
		} finally {
			return notInOffice;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkAppliedInterval(Notinoffice notInOffice) {
		LOGGER.info("In checking interval in not in office list Dao");
		Employeedetails employeedetails = notInOffice.getEmployeedetailsByEmployeeIdNio();
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice1 = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice1 = session
					.createQuery(
							"FROM com.esko.Model.Notinoffice n WHERE n.employeedetailsByEmployeeIdNio =:employeedetails and (n.requestStatus = :requestStatus1 or n.requestStatus = :requestStatus2 or n.requestStatus = :requestStatus3 or n.requestStatus = :requestStatus4)")
					.setParameter("employeedetails", employeedetails).setParameter("requestStatus1", "Approved")
					.setParameter("requestStatus2", "Pending").setParameter("requestStatus3", "Cancellation Pending")
					.setParameter("requestStatus4", "Cancellation Declined").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while checking applied interval  " + e.getMessage());
			session.getTransaction().rollback();
		} finally {
			List<Date> dateList = new ArrayList<Date>();
			for (int i = 0; i < notInOffice1.size(); i++) {
				Date startDate = DateConverter.getDateInputDate("yyyy-MM-dd",
						notInOffice1.get(i).getDateOfRequestStart());
				Date endDate = DateConverter.getDateInputDate("yyyy-MM-dd", notInOffice1.get(i).getDateOfRequestEnd());
				if (startDate.equals(endDate)) {
					dateList.add(startDate);
				} else {
					int dayDifference = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000) + 1);
					List<Date> dateRangeList = DateConverter.getDateRange(startDate, dayDifference);
					for (Date dateitr : dateRangeList) {
						dateList.add(dateitr);
					}
				}
			}
			Date startAppliedDate = notInOffice.getDateOfRequestStart();
			Date endAppliedDate = notInOffice.getDateOfRequestEnd();
			if (startAppliedDate.equals(endAppliedDate)) {
				if (dateList.contains(startAppliedDate)) {
					return false;
				}
			} else {
				int dayDifference = (int) ((endAppliedDate.getTime() - startAppliedDate.getTime())
						/ (24 * 60 * 60 * 1000) + 1);
				List<Date> dateRangeList = DateConverter.getDateRange(startAppliedDate, dayDifference);
				for (Date dateitr : dateRangeList) {
					if (dateList.contains(dateitr)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Create a method to get java.util.Calendar object from Date
	 *
	 * @param java
	 *            .util.Date inputDate:Date object which is to be converted
	 * @returns java.util.Calendar cal : java.util.Calendar Object
	 */
	public Calendar getDate(Date inputDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		return cal;
	}

	@SuppressWarnings({ "unchecked", "finally" })
	@Override
	public List<Notinoffice> getNotInOfficeList(Date startDate, Date endDate) {
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOfficeList = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOfficeList = session
					.createQuery(
							"FROM com.esko.Model.Notinoffice nio where (nio.dateOfRequestStart BETWEEN :startDate AND :endDate or nio.dateOfRequestEnd BETWEEN :startDate AND :endDate ) and (nio.requestStatus =:status1 or nio.requestStatus =:status2 ) ORDER BY nio.id.dateOfRequestStart ASC")
					.setParameter("startDate", startDate).setParameter("endDate", endDate)
					.setParameter("status1", "Approved").setParameter("status2", "Cancellation Declined").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while fetching list of work from home  " + e.getMessage());
			session.getTransaction().rollback();
		} finally {
			return notInOfficeList;
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	@Override
	public int getWfhDays(String employeeId, Date date) {
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOfficeList = new ArrayList<Notinoffice>();
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = DateConverter.getDateInputDate("yyyy-MM-dd", c.getTime());
			System.out.println("date is end" + c.getTime());
			session.getTransaction().begin();
			notInOfficeList = session
					.createQuery(
							"FROM com.esko.Model.Notinoffice nio where nio.employeedetailsByEmployeeIdNio =:employeedetails and nio.requestType =:requestType and (nio.requestStatus =:status1 or nio.requestStatus =:status2 or nio.requestStatus =:status3 or nio.requestStatus =:status4) and nio.dateOfRequestStart BETWEEN :startDate AND :endDate ")
					.setParameter("requestType", "Work From Home").setParameter("employeedetails", employeedetails)
					.setParameter("status1", "Approved").setParameter("status2", "Pending")
					.setParameter("status3", "Cancellation Pending").setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("status4", "Cancellation Declined").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while fetching list of work from home  " + e.getMessage());
			session.getTransaction().rollback();
		} finally {
			return notInOfficeList.size();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void cancelRequestManager(int id, String employeeId) {
		LOGGER.info("In canceling not in office list Dao");
		Session session = factory.getCurrentSession();
		List<Notinoffice> notInOffice = new ArrayList<Notinoffice>();
		try {
			session.getTransaction().begin();
			notInOffice = session.createQuery("FROM com.esko.Model.Notinoffice n WHERE n.id =:id")
					.setParameter("id", id).list();
			Notinoffice notinoffice2 = notInOffice.get(0);
			String status = notinoffice2.getRequestStatus();
			if (status.equalsIgnoreCase("pending")) {
				notinoffice2.setRequestStatus("Declined");
			} else {
				notinoffice2.setRequestStatus("Cancellation Declined");
			}
			session.update(notinoffice2);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while canceling not in office  " + e.getMessage());
			session.getTransaction().rollback();
		}

	}

}
