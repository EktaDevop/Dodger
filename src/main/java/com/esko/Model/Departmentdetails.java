package com.esko.Model;
// Generated May 17, 2017 11:18:41 AM by Hibernate Tools 5.1.0.Alpha1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Departmentdetails generated by hbm2java
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "departmentdetails", catalog = "spandana", uniqueConstraints = @UniqueConstraint(columnNames = "departmentName"))
public class Departmentdetails implements java.io.Serializable {

	private int id;
	private String departmentName;
	@JsonBackReference
	private Set<Holidaycalendar> holidaycalendars = new HashSet<Holidaycalendar>(0);
	@JsonBackReference
	private Set<Employeedetails> employeedetailses = new HashSet<Employeedetails>(0);

	public Departmentdetails() {
	}

	public Departmentdetails(String departmentName) {
		this.departmentName = departmentName;
	}

	public Departmentdetails(String departmentName, Set<Holidaycalendar> holidaycalendars,
			Set<Employeedetails> employeedetailses) {
		this.departmentName = departmentName;
		this.holidaycalendars = holidaycalendars;
		this.employeedetailses = employeedetailses;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "departmentName", unique = true, nullable = false, length = 45)
	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "departmentdetails")
	public Set<Holidaycalendar> getHolidaycalendars() {
		return this.holidaycalendars;
	}

	public void setHolidaycalendars(Set<Holidaycalendar> holidaycalendars) {
		this.holidaycalendars = holidaycalendars;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "departmentdetails")
	public Set<Employeedetails> getEmployeedetailses() {
		return this.employeedetailses;
	}

	public void setEmployeedetailses(Set<Employeedetails> employeedetailses) {
		this.employeedetailses = employeedetailses;
	}

}
