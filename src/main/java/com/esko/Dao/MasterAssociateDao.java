package com.esko.Dao;

import java.util.List;

import com.esko.FrontEndObjects.EmployeeDetails;
import com.esko.Model.Departmentdetails;
import com.esko.Model.Employeedetails;
import com.esko.Model.Employeemanagerdetails;

public interface MasterAssociateDao {
	/*
	 * To get the list of departments from the database
	 */
	public List<Departmentdetails> getDepartmentDetails();

	/*
	 * To add department in the Department Details Table
	 */
	public void addDepartmentRow(Departmentdetails departmentDetailsRow);

	/*
	 * To delete department from the department table
	 */
	public void deleteDepartmentRow(Departmentdetails departmentDetails) throws Exception;

	/*
	 * get the depatmentdetail by department name
	 */
	public Departmentdetails getDepartmentByName(String departmentName);

	/*
	 * checking if the department exists
	 */
	public String checkDepartment(String departmentName);

	/*
	 * To get the list of employee from employee details table
	 */
	public List<Object[]> getEmployeeDetails();

	/*
	 * Getting the list of all the managers
	 */
	public List<String> getmanagers();

	/*
	 * getting employee detail even if the deleted column was set to YES
	 */
	public Employeedetails getEmployeeDetailsByNameShowingDeleted(String string);

	/*
	 * Getting employee detail by employee name only where deleted is NO
	 */
	public Employeedetails getEmployeeDetailsByNameHidingDeleted(String employeeId);

	/*
	 * Creates employee in employeedetails table
	 */
	public void createEmployeeDetails(Employeedetails employeeDetails);

	/*
	 * creates employee manager relation in employee manager table
	 */
	public void createEmployeeMangerDetails(Employeemanagerdetails employeeMangerDetails);

	/*
	 * Updating the employee details
	 */
	public void updateEmployeeDetails(EmployeeDetails employeeDetails);

	/*
	 * Deleting the employee
	 */
	public void deleteEmployee(EmployeeDetails employeeDetails) throws Exception;

	/*
	 * to delete the temp created employee
	 */
	public void deleteCreatedEmployee(Employeedetails employeedetails);
	
	/*
	 * to check if employee is a manager
	 */
	public boolean isEmployeeManager(String employeeId);

	/*
	 * getting the manager details of the employee 
	 */
	public Employeemanagerdetails getEmployeeMangerDetails(Employeedetails employeedetails);

}
