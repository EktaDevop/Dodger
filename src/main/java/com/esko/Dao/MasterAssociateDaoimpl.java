package com.esko.Dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Employeedetails;
import com.esko.Model.Employeemanagerdetails;

@Repository
public class MasterAssociateDaoimpl implements MasterAssociateDao {
	@Autowired
	private SessionFactory factory;

	final static Logger LOGGER = Logger.getLogger(MasterAssociateDaoimpl.class.getName());

	@PostConstruct
	public void init() {
		LOGGER.info("init of dao " + factory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Departmentdetails> getDepartmentDetails() {
		LOGGER.info("in get Department Details Dao");
		Session session = factory.getCurrentSession();
		List<Departmentdetails> departmentDetails = new ArrayList<Departmentdetails>();
		try {
			session.getTransaction().begin();
			departmentDetails = session.createQuery("FROM Departmentdetails").list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting deaprtment list " + e.getMessage());
			session.getTransaction().rollback();
		}
		for (Departmentdetails departmentDetail1 : departmentDetails) {
			LOGGER.info(departmentDetail1.toString());
		}
		return departmentDetails;
	}

	@Override
	public void addDepartmentRow(Departmentdetails departmentDetailsRow) {
		LOGGER.info("In addition Department Dao ");
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(departmentDetailsRow);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during adding department " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@Override
	public void deleteDepartmentRow(Departmentdetails departmentDetails) throws Exception {
		Session session = factory.getCurrentSession();
		LOGGER.info("In Deletion Department Dao");
		try {
			session.getTransaction().begin();
			session.delete(departmentDetails);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
				LOGGER.error("Hibernate exception during deleting department " + e.getMessage());
				// LOGGER.error("exception during department delete",e);
				throw e;
			}
		}
	}

	@Override
	public Departmentdetails getDepartmentByName(String departmentName) {
		LOGGER.info("Get Department details from name in Dao ");
		Session session = factory.getCurrentSession();
		Departmentdetails departmentdetails = new Departmentdetails();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery("FROM com.esko.Model.Departmentdetails d WHERE d.departmentName =:name ");
			query.setParameter("name", departmentName);
			departmentdetails = (Departmentdetails) query.uniqueResult();
			LOGGER.info("department detail in dao : " + departmentdetails);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error(
					"Hibernate exception during getting department detail by the department name  " + e.getMessage());
			session.getTransaction().rollback();
		}
		return departmentdetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String checkDepartment(String departmentName) {
		Session session = factory.getCurrentSession();

		List<Departmentdetails> list = new ArrayList<Departmentdetails>();
		try {
			session.getTransaction().begin();
			list = session
					.createQuery("FROM com.esko.Model.Departmentdetails d WHERE d.departmentName = :departmentName ")
					.setParameter("departmentName", departmentName).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during checking if the department exists " + e.getMessage());
			session.getTransaction().rollback();
		}
		if (list.size() > 0) {
			return "Yes";
		} else {
			return "No";
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getEmployeeDetails() {
		LOGGER.info("in get employee details Dao");
		Session session = factory.getCurrentSession();
		List<Object[]> employeeDetails = new ArrayList<Object[]>();
		try {
			session.getTransaction().begin();
			Query query = session
					.createQuery(
							"SELECT A.employeeId,A.fullName,A.wfhEligibility,B.employeedetailsByManagerIdEm, A.departmentdetails FROM Employeedetails A, Employeemanagerdetails B where A.employeeId=B.employeedetailsByEmployeeIdEm and A.deleted = :status ORDER BY A.employeeId ASC")
					.setParameter("status", "No");
			employeeDetails = query.list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting employee details " + e.getMessage());
			session.getTransaction().rollback();
		}
		for (Object employeeDetails1 : employeeDetails) {
			LOGGER.info(employeeDetails1.toString());
		}
		return employeeDetails;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getmanagers() {
		LOGGER.info("in get manager details Controller");
		Session session = factory.getCurrentSession();
		List<Employeedetails> list = new ArrayList<Employeedetails>();
		List<String> managers = new ArrayList<String>();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery(
					"select distinct employeedetailsByManagerIdEm from com.esko.Model.Employeemanagerdetails ");
			list = (List<Employeedetails>) query.list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during fetching all the managers " + e.getMessage());
			session.getTransaction().rollback();
		}
		for (Employeedetails employeedetails : list) {
			String employeeId = employeedetails.getEmployeeId();
			if (employeedetails.getDeleted().equalsIgnoreCase("No")) {
				managers.add(employeeId);
			}
		}
		return managers;
	}

	@Override
	public void createEmployeeDetails(Employeedetails employeeDetails) {
		LOGGER.info("creating new employee in employee detail table");
		LOGGER.info(employeeDetails.toString());
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(employeeDetails);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during creating employeedetails " + e.getMessage());
			session.getTransaction().rollback();
		}
		LOGGER.info("employee created");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Employeedetails getEmployeeDetailsByNameShowingDeleted(String employeeId) {
		Session session = factory.getCurrentSession();
		LOGGER.info("getting employee details from employee name Dao showing deleted");
		List<Employeedetails> employeedetails = new ArrayList<Employeedetails>();
		try {
			session.getTransaction().begin();
			employeedetails = session.createQuery("FROM Employeedetails e WHERE e.employeeId =:id")
					.setParameter("id", employeeId).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error(
					"Hibernate exception during fetching employeedetails even if it is soft deleted " + e.getMessage());
			session.getTransaction().rollback();
		}
		LOGGER.info("Employee fetched");
		if (employeedetails.size() > 0) {
			return employeedetails.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Employeedetails getEmployeeDetailsByNameHidingDeleted(String employeeId) {
		Session session = factory.getCurrentSession();
		LOGGER.info("getting employee details from employee name Dao hiding deleted");
		List<Employeedetails> employeedetails = new ArrayList<Employeedetails>();
		int i =0;
		while(i<=1){
			try {
				i++;
				session.getTransaction().begin();
				employeedetails = session
						.createQuery("FROM Employeedetails e WHERE e.employeeId =:id and e.deleted = :deleted")
						.setParameter("id", employeeId).setParameter("deleted", "No").list();
				session.getTransaction().commit();
				break;
			} catch (HibernateException e) {
				if(i==1){
					session= factory.openSession();
					LOGGER.error("Tring to recreate the session object");
					continue;
				}
				LOGGER.error(
						"Hibernate exception during fetching employeedetails of only existing employee " + e.getMessage());
				LOGGER.error("exception during hibernate begin", e);
				session.getTransaction().rollback();
			}
		}
		LOGGER.info("Employee fetched");
		if (employeedetails.size() > 0) {
			return employeedetails.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void createEmployeeMangerDetails(Employeemanagerdetails employeeMangerDetails) {
		LOGGER.info("creating new employee manager relation in employeenamanger detail table");
		LOGGER.info(employeeMangerDetails.toString());
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			session.save(employeeMangerDetails);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during creating employeemanagerdetail table " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@Override
	public void updateEmployeeDetails(EmployeeDetails employeeDetails) {
		LOGGER.info("Updating employee in Dao");
		Departmentdetails departmentDetails = getDepartmentByName(employeeDetails.getDepartmentId());
		Employeedetails employeedetails = getEmployeeDetailsByNameShowingDeleted(employeeDetails.getEmployeeId());
		Employeedetails managerdetails = getEmployeeDetailsByNameShowingDeleted(employeeDetails.getManagerId());
		Employeemanagerdetails emf = getEmployeeMangerDetails(employeedetails);
		String wfhEligibility = employeeDetails.getWfhEligibility();
		int id = emf.getId();
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Employeedetails employeedetails2 = (Employeedetails) session.get(Employeedetails.class,
					employeeDetails.getEmployeeId());
			employeedetails2.setFullName(employeeDetails.getEmployeeName());
			employeedetails2.setDepartmentdetails(departmentDetails);
			employeedetails2.setWfhEligibility(wfhEligibility);
			employeedetails2.setDeleted("No");
			session.update(employeedetails2);
			Employeemanagerdetails employeemanagerdetails = (Employeemanagerdetails) session
					.get(Employeemanagerdetails.class, id);
			employeemanagerdetails.setEmployeedetailsByEmployeeIdEm(employeedetails2);
			employeemanagerdetails.setEmployeedetailsByManagerIdEm(managerdetails);
			session.update(employeemanagerdetails);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during updating existing employee " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

	@Override
	public Employeemanagerdetails getEmployeeMangerDetails(Employeedetails employeedetails) {
		Session session = factory.getCurrentSession();
		Employeemanagerdetails emd = new Employeemanagerdetails();
		try {
			session.getTransaction().begin();
			Query query = session.createQuery(
					"FROM com.esko.Model.Employeemanagerdetails m WHERE m.employeedetailsByEmployeeIdEm =:employeeDetails ");
			query.setParameter("employeeDetails", employeedetails);
			emd = (Employeemanagerdetails) query.uniqueResult();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during getting the employee manager detail row " + e.getMessage());
			session.getTransaction().rollback();
		}
		return emd;
	}

	@Override
	public void deleteEmployee(EmployeeDetails employeeDetails) throws Exception {
		LOGGER.info("In employee delete dao");
		Employeedetails employeedetails = getEmployeeDetailsByNameShowingDeleted(employeeDetails.getEmployeeId());
		Employeemanagerdetails emf1 = getEmployeeMangerDetails(employeedetails);
		boolean isManager = false;
		isManager = isEmployeeManager(employeeDetails.getEmployeeId());
		int id = emf1.getId();
		Session session = factory.getCurrentSession();
		Employeedetails edf = new Employeedetails();
		Employeemanagerdetails emf = new Employeemanagerdetails();
		try {
			session.getTransaction().begin();
			edf = (Employeedetails) session.get(Employeedetails.class, employeeDetails.getEmployeeId());
			emf = (Employeemanagerdetails) session.get(Employeemanagerdetails.class, id);
			if (!isManager) {
				edf.setDeleted("Yes");
				session.update(edf);
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while setting deleted employee to yes " + e.getMessage());
			session.getTransaction().rollback();
		}

		Session session1 = factory.getCurrentSession();
		try {
			session1.getTransaction().begin();
			session1.delete(emf);
			session1.delete(edf);
			session1.getTransaction().commit();

		} catch (Exception e) {
			if (session1.getTransaction() != null) {
				session1.getTransaction().rollback();
				LOGGER.error("Hibernate exception while deleting employee " + e.getMessage());
				throw e;
			}
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isEmployeeManager(String employeeId) {
		LOGGER.info("in checking if employee is manager");
		Employeedetails ed = getEmployeeDetailsByNameHidingDeleted(employeeId);
		Session session = factory.getCurrentSession();
		List<Employeemanagerdetails> list = new ArrayList<Employeemanagerdetails>();
		try {
			session.getTransaction().begin();
			list = session
					.createQuery(
							"FROM com.esko.Model.Employeemanagerdetails emd WHERE emd.employeedetailsByManagerIdEm = :ed ")
					.setParameter("ed", ed).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception while checking if the employee is a manager " + e.getMessage());
			session.getTransaction().rollback();
		}
		if (list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void deleteCreatedEmployee(Employeedetails employeedetails) {
		Session session = factory.getCurrentSession();
		try {
			session.getTransaction().begin();
			Employeedetails edf = (Employeedetails) session.get(Employeedetails.class, employeedetails.getEmployeeId());
			session.delete(edf);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Hibernate exception during delting the temporary created employee " + e.getMessage());
			session.getTransaction().rollback();
		}
	}

}
