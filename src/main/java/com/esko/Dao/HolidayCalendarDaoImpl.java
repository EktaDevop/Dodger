package com.esko.Dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.esko.FrontEndObjects.HolidayCalendar;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Holidaycalendar;
import com.esko.Utils.Pdf.PropertiesFile;

@Repository
public class HolidayCalendarDaoImpl implements HolidayCalenderDao {

	final static Logger LOGGER = Logger.getLogger(HolidayCalendarDaoImpl.class.getName());

	@Inject
	private MasterAssociateDao masterAssociateDao;

	@Autowired
	private SessionFactory factory;

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Holidaycalendar> getHolidayList() {
		LOGGER.info("In get holiday details Dao");
		List<Holidaycalendar> holicalendars = new ArrayList<Holidaycalendar>();
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			System.out.println("after open session in holiday ");
			String currentYear = PropertiesFile.readProperties("spandana.properties").getProperty("CurrentYear");
			Date date = new Date();
			try {
				date = new SimpleDateFormat("yyyy-MM-dd").parse(currentYear);
			} catch (ParseException e) {
			}
			holicalendars = session
					.createQuery(
							"FROM com.esko.Model.Holidaycalendar j where j.holidayDate>= :date ORDER BY j.holidayDate ASC")
					.setParameter("date", date).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during holiday fetch " + e.getMessage());
			session.getTransaction().rollback();
		}
		return holicalendars;

	}

	@Override
	public void insertHoliday(Holidaycalendar cal) {
		LOGGER.info("In insert holiday details Dao");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(cal);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during inserting holiday " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@Override
	public void updateCalendarRow(HolidayCalendar cal) {
		LOGGER.info("in update calendar Dao");
		Departmentdetails departmentdetails = masterAssociateDao.getDepartmentByName(cal.getDepartment());
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Holidaycalendar calObject = (Holidaycalendar) session.get(Holidaycalendar.class, cal.getId());
			LOGGER.info("object is :" + calObject.toString());
			calObject.setHolidayDate(cal.getHolidayDate());
			calObject.setHoliday(cal.getHoliday());
			calObject.setDepartmentdetails(departmentdetails);
			session.update(calObject);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during updating holiday " + e.getMessage());
			session.getTransaction().rollback();
		}

	}

	@Override
	public void deleteCalendarRow(Holidaycalendar cal) {
		LOGGER.info("in delete calendar Dao");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Holidaycalendar calObject = (Holidaycalendar) session.get(Holidaycalendar.class, cal.getId());
			session.delete(calObject);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during deleting holiday " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@Override
	public Holidaycalendar getHolidaycalendarById(int id) {
		LOGGER.info("in getting holidaycalendar using id in service");
		Session session = factory.getCurrentSession();
		Holidaycalendar holidaycalendar = new Holidaycalendar();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("FROM com.esko.Model.Holidaycalendar h WHERE h.id = :id");
			query.setParameter("id", id);
			holidaycalendar = (Holidaycalendar) query.uniqueResult();
			LOGGER.info("holiday calendar detail in dao : " + holidaycalendar);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting the holiday row by id " + e.getMessage());
			session.getTransaction().rollback();
		}
		return holidaycalendar;
	}

	@Override
	public boolean checkHoliday(Date in) {
		LOGGER.info("in getting holidaycalendar using id in service");
		Session session = factory.getCurrentSession();
		Holidaycalendar holidaycalendar = new Holidaycalendar();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("FROM com.esko.Model.Holidaycalendar h WHERE h.holidayDate = :in");
			query.setParameter("in", in);
			holidaycalendar = (Holidaycalendar) query.uniqueResult();
			LOGGER.info("holiday calendar detail in dao : " + holidaycalendar);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during checking holiday on a particular date " + e.getMessage());
			session.getTransaction().rollback();
		}
		if (holidaycalendar != null) {
			return true;
		}
		return false;
	}

}
