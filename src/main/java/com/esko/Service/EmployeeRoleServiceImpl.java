package com.esko.Service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.esko.Dao.EmployeeRoleDao;
import com.esko.Dao.MasterAssociateDao;
import com.esko.Model.Employeedetails;
import com.esko.Model.Employeerole;
import com.esko.Model.Rolemaster;

@Service
public class EmployeeRoleServiceImpl implements EmployeeRoleService{
	
	@Inject
	private MasterAssociateDao masterAssociateDao; 
	@Inject
	private EmployeeRoleDao employeeRoleDao;
	@Override
	public void addEmployeeRoles() {
		List<Object[]> employeeList = masterAssociateDao.getEmployeeDetails();
		for (Object[] ed : employeeList) {
			String employeeId = (String) ed[0];
			boolean checkManager = masterAssociateDao.isEmployeeManager(employeeId);
			Employeedetails employeedetails = masterAssociateDao.getEmployeeDetailsByNameHidingDeleted(employeeId);
			if(checkManager){
				Rolemaster rolemaster = employeeRoleDao.getRoleByName("Manager");
				Employeerole employeerole = new Employeerole(employeedetails, rolemaster);
				employeeRoleDao.addEmployeeRoles(employeerole);
			}else{
				Rolemaster rolemaster = employeeRoleDao.getRoleByName("Associate");
				Employeerole employeerole = new Employeerole(employeedetails, rolemaster);
				employeeRoleDao.addEmployeeRoles(employeerole);
			}
		}
	}
	@Override
	public String getEmployeeRole(Employeedetails employeedetails) {
		String role = employeeRoleDao.getEmployeeRole(employeedetails);
		return role;
	}

}
