package com.esko.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esko.Dao.MasterAssociateDao;
import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Employeedetails;
import com.esko.Model.Employeemanagerdetails;
import com.esko.Utils.CSVFileReader.MasterAssociateFeedReader;
import com.esko.Utils.Exception.UnsupportedFileException;

@Service

public class MasterAssociateServiceImpl implements MasterAssociateService {
	final static Logger LOGGER = Logger.getLogger(MasterAssociateServiceImpl.class.getName());
	@Inject
	private MasterAssociateFeedReader masterAssociateFeedReader;
	@Inject
	private MasterAssociateDao masterAssociateDao;

	@Override
	public List<Departmentdetails> getDepartmentDetails() {
		LOGGER.info("in get department details Service");
		List<Departmentdetails> departmentList = masterAssociateDao.getDepartmentDetails();
		return departmentList;
	}

	@Override
	public void addDepartmentRow(Departmentdetails departmentDetails) {
		LOGGER.info("In addition department Service ");
		masterAssociateDao.addDepartmentRow(departmentDetails);
	}

	@Override
	public String deleteDepartmentRow(Departmentdetails departmentDetails) {
		LOGGER.info("Department in Delete Service" + departmentDetails.toString());
		String result = "";
		try {
			masterAssociateDao.deleteDepartmentRow(departmentDetails);
			result = "Deleted";
		} catch (Exception e) {
			result = "Exception";
		}
		return result;
	}

	@Override
	public Departmentdetails getDepartmentByName(String departmentName) {
		LOGGER.info("Get Department details from name in service ");
		Departmentdetails departmentdetails = masterAssociateDao.getDepartmentByName(departmentName);
		return departmentdetails;
	}

	@Override
	public String checkDepartment(String departmentName) {
		String departmentExists = masterAssociateDao.checkDepartment(departmentName);
		return departmentExists;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeDetails> getEmployeeDetails() {
		LOGGER.info("In get employee details Service");
		List<EmployeeDetails> employeeDetails = getEmployeeDetailsList();
		return employeeDetails;
	}

	private List<EmployeeDetails> getEmployeeDetailsList() {
		List<Object[]> employeeList = getemployeeDetailsDatabase();
		List<EmployeeDetails> employeeDetailsList = new ArrayList<EmployeeDetails>();
		for (Object[] ed : employeeList) {
			String employeeId = (String) ed[0];
			String employeeName = (String) ed[1];
			String wfhEligibility = (String) ed[2];
			Departmentdetails departmentDetails = (Departmentdetails) ed[4];
			String departmentName = departmentDetails.getDepartmentName();
			Employeedetails eds = (Employeedetails) ed[3];
			String managerId = eds.getEmployeeId();
			EmployeeDetails employeeDetails = new EmployeeDetails(employeeId, employeeName, wfhEligibility,managerId, departmentName);
			employeeDetailsList.add(employeeDetails);
		}
		return employeeDetailsList;
	}

	public List<Object[]> getemployeeDetailsDatabase() {
		List<Object[]> employeeList = (List<Object[]>) masterAssociateDao.getEmployeeDetails();
		return employeeList;
	}

	@Override
	public List<String> getmanagers() {
		LOGGER.info("in get manager details Service");
		List<String> managers = masterAssociateDao.getmanagers();
		return managers;
	}

	@Override
	@Transactional
	public String addEmployee(EmployeeDetails employeeDetails) throws NullPointerException {
		LOGGER.info("Adding employee in service");
		LOGGER.info(employeeDetails.toString());
		String employeeId = employeeDetails.getEmployeeId().toUpperCase();
		String employeeName = employeeDetails.getEmployeeName();
		String managerId = employeeDetails.getManagerId();
		String departmentId = employeeDetails.getDepartmentId();
		String wfhEligibility = employeeDetails.getWfhEligibility();
		String deleted = "No";
		Employeedetails existingEmployee = masterAssociateDao.getEmployeeDetailsByNameShowingDeleted(employeeId);
		if (existingEmployee == null) {
			Departmentdetails departmentDetails = masterAssociateDao.getDepartmentByName(departmentId);
			if (departmentDetails == null) {
				throw new NullPointerException("Department doesnot exist");
			}

			Employeedetails employeedetails = new Employeedetails(employeeId, departmentDetails, employeeName, deleted,wfhEligibility);
			masterAssociateDao.createEmployeeDetails(employeedetails);
			Employeedetails employeeDetail = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
			Employeedetails managerDetail = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(managerId);
			if (managerDetail == null) {
				masterAssociateDao.deleteCreatedEmployee(employeedetails);
				throw new NullPointerException("Manager is not an employee");
			}
			Employeemanagerdetails employeemanagerdetails = new Employeemanagerdetails(managerDetail, employeeDetail);
			masterAssociateDao.createEmployeeMangerDetails(employeemanagerdetails);
			return "Added";
		} else {
			existingEmployee = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
			if (existingEmployee != null) {
				return "EmployeeId already exists";
			} else {
				masterAssociateDao.updateEmployeeDetails(employeeDetails);
				return "";
			}
		}

	}

	@Override
	public void updateEmployeeDetails(EmployeeDetails employeeDetails) {
		LOGGER.info("Updating employee in service");
		masterAssociateDao.updateEmployeeDetails(employeeDetails);
	}

	@Override
	public String deleteEmployee(EmployeeDetails employeeDetails) {
		LOGGER.info("Deleting employee in service");
		String result = "";
		try {
			masterAssociateDao.deleteEmployee(employeeDetails);
			result = "Deleted";
		} catch (Exception e) {
			Employeedetails ed = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeDetails.getEmployeeId());
			if (ed == null) {
				result = "Deleted";
			} else {
				result = "Exception";
			}
		}
		LOGGER.info("employee was " + result);
		return result;
	}

	@Override
	public JSONObject read(InputStream uploadedInputStream) {
		LOGGER.info("Processing master feed in service");
		JSONObject parsedCSVJSON = new JSONObject();
		try {
			parsedCSVJSON = masterAssociateFeedReader.read(uploadedInputStream);
			parsedCSVJSON.put("parsed", "Successful");
		} catch (UnsupportedFileException e) {
			parsedCSVJSON.put("parsed", "Unsuccessful");
		}
		return parsedCSVJSON;
	}

	@Override
	public Employeedetails getEmployeeDetailByName(String employeeId) {
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
		return employeedetails;
	}

	@Override
	public Employeedetails checkEmployee(String uid) {
		Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(uid);
		return employeedetails;
	}

	@Override
	public String getEmployeeMangerDetails(Employeedetails employeedetails) {
		Employeemanagerdetails employeemanagerdetails = masterAssociateDao.getEmployeeMangerDetails(employeedetails);
		Employeedetails employeedetails2 = employeemanagerdetails.getEmployeedetailsByManagerIdEm();
		String managerId = employeedetails2.getEmployeeId();
		return managerId;
	}

}
