package com.esko.Dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.esko.Model.Attendancefeed;
import com.esko.Model.AttendancefeedId;

@Repository
public class AttendanceFeedDaoImpl implements AttendanceFeedDao {
	@Autowired
	private SessionFactory factory;

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	final static Logger LOGGER = Logger.getLogger(AttendanceFeedDaoImpl.class.getName());

	public boolean checkRow(Attendancefeed AttendancefeedRow) {
		LOGGER.info("Checking if the attendance feed row exist in the database DAO");
		Session session = factory.getCurrentSession();
		AttendancefeedId AttendancefeedId = AttendancefeedRow.getId();
		long res = 0;
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("SELECT count(*) FROM com.esko.Model.Attendancefeed a WHERE a.id =:id ");
			query.setParameter("id", AttendancefeedId);
			res = (Long) query.uniqueResult();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during checking attendance row  " + e.getMessage());
			session.getTransaction().rollback();
		}
		return (res == 1) ? true : false;
	}

	public void createAttendancefeed(Attendancefeed Attendancefeed1) {
		LOGGER.info("Creating row in the attendance feed table DAO");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(Attendancefeed1);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during creating attendance row " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	public void updateAttendancefeed(Attendancefeed attendancefeedRow) {
		LOGGER.info("Updating row in the attendance feed table DAO");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Attendancefeed af = (Attendancefeed) session.load(Attendancefeed.class, attendancefeedRow.getId());
			af.setCheckIn(attendancefeedRow.getCheckIn());
			af.setCheckOut(attendancefeedRow.getCheckOut());
			af.setDayDuration(attendancefeedRow.getDayDuration());
			af.setStatus(attendancefeedRow.getStatus());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during updating attendance row " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Attendancefeed> listAttendanceFeed(Date startDate, Date endDate) {
		LOGGER.info("in get Attendance dao");
		Session session = factory.getCurrentSession();
		List<Attendancefeed> Attendancefeeds = new ArrayList<Attendancefeed>();
		try {
			session.getTransaction().begin();
			Attendancefeeds = session
					.createQuery(
							"SELECT attenfeed FROM com.esko.Model.Attendancefeed attenfeed where attenfeed.id.attendanceDate BETWEEN :startDate AND :endDate ORDER BY attenfeed.id.attendanceDate  ASC")
					.setParameter("startDate", startDate).setParameter("endDate", endDate).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during fetching the attendance feed list between the given date range: "
					+ e.getMessage());
			session.getTransaction().rollback();
		}
		return Attendancefeeds;
	}

}
