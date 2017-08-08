package com.esko.Dao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.esko.Model.Employeedetails;
import com.esko.Model.Employeerole;
import com.esko.Model.Rolemaster;

@Repository
public class EmployeeRoleDaoImpl implements EmployeeRoleDao{
	@Autowired
	private SessionFactory factory;

	final static Logger LOGGER = Logger.getLogger(EmployeeRoleDaoImpl.class.getName());

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	@Override
	public void addEmployeeRoles(Employeerole er) {
		LOGGER.info("In addition EmployeeRole Dao ");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(er);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during adding employee role " + e.getMessage());
			session.getTransaction().rollback();
		}
		
	}

	@Override
	public Rolemaster getRoleByName(String roleName) {
		LOGGER.info("Get Role details from name in Dao ");
		Session session = factory.getCurrentSession();
		Rolemaster rolemaster = new Rolemaster();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("FROM com.esko.Model.Rolemaster r WHERE r.roleName =:roleName ");
			query.setParameter("roleName", roleName);
			rolemaster = (Rolemaster) query.uniqueResult();
			LOGGER.info("Role detail in dao : " + rolemaster);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting role detail by the department name  " + e.getMessage());
			session.getTransaction().rollback();
		}
		return rolemaster;
	}

	@Override
	public String getEmployeeRole(Employeedetails employeedetails) {
		Session session = factory.getCurrentSession();
		Employeerole employeerole = new Employeerole();
		String role = "";
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("FROM com.esko.Model.Employeerole er WHERE er.employeedetails =:employeedetails ");
			query.setParameter("employeedetails", employeedetails);
			employeerole = (Employeerole) query.uniqueResult();
			Rolemaster rolemaster = employeerole.getRolemaster();
			role = rolemaster.getRoleName();
			session.getTransaction().commit();
			return role;
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting role detail by the department name  " + e.getMessage());
			session.getTransaction().rollback();
		}
		return role;
	}

}
