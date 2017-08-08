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

import com.esko.Model.Leavefeed;
import com.esko.Model.LeavefeedId;

@Repository
public class LeaveFeedDaoImpl implements LeaveFeedDao {
	@Autowired
	private SessionFactory factory;

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	final static Logger LOGGER = Logger.getLogger(LeaveFeedDaoImpl.class.getName());

	/*
	 * to check if the same row exists in the database
	 * 
	 * @see com.esko.Dao.LeaveFeedDao#checkRow(com.esko.Feeds.LeaveFeed)
	 */
	public boolean checkRow(Leavefeed leaveFeedRow) {
		LOGGER.info("Checking if the Leave feed row exist in the database DAO");
		Session session = factory.getCurrentSession();
		LeavefeedId leaveFeedId = leaveFeedRow.getId();
		long res = 0;
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("SELECT count(*) FROM com.esko.Model.Leavefeed l WHERE l.id =:id ");
			query.setParameter("id", leaveFeedId);
			res = (Long) query.uniqueResult();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during checking leave feed row " + e.getMessage());
			session.getTransaction().rollback();
		}
		return (res == 1) ? true : false;
	}

	/*
	 * creates row in the database for the leave feed
	 * 
	 * @see com.esko.Dao.LeaveFeedDao#createLeaveFeed(com.esko.Feeds.LeaveFeed)
	 */
	public void createLeaveFeed(Leavefeed leaveFeed1) {
		LOGGER.info("Creating row in the leave feed table DAO");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(leaveFeed1);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during creating leave " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	/*
	 * to update the row in the database if the same row is present in the feed
	 * 
	 * @see com.esko.Dao.LeaveFeedDao#updateLeaveFeed(java.lang.String,
	 * java.lang.String, com.esko.Feeds.LeaveFeed)
	 */
	public void updateLeaveFeed(Leavefeed leaveFeedRow) {
		LOGGER.info("Updating row in the leave feed table DAO");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Leavefeed lf = (Leavefeed) session.load(Leavefeed.class, leaveFeedRow.getId());
			lf.setLeaveType(leaveFeedRow.getLeaveType());
			session.update(lf);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during updating leave " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	/*
	 * to show all the row present in the leave feed datadbase
	 * 
	 * @see com.esko.Dao.LeaveFeedDao#getLeaveFeed()
	 */
	@SuppressWarnings("unchecked")
	public List<Leavefeed> listLeaveFeed(Date startDate, Date endDate) {
		LOGGER.info("in get leave dao");
		Session session = factory.getCurrentSession();
		List<Leavefeed> leaveFeeds = new ArrayList<Leavefeed>();
		try {
			session.getTransaction().begin();
			leaveFeeds = session
					.createQuery(
							" SELECT leavfeed FROM com.esko.Model.Leavefeed leavfeed where leavfeed.id.leaveDate BETWEEN :startDate AND :endDate ORDER BY leavfeed.id.leaveDate ASC")
					.setParameter("startDate", startDate).setParameter("endDate", endDate).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while getting the leave list between the date range  " + e.getMessage());
			session.getTransaction().rollback();
		}
		return leaveFeeds;
	}

}
