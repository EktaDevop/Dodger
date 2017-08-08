package com.esko.Model;
// Generated May 17, 2017 11:18:41 AM by Hibernate Tools 5.1.0.Alpha1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Holidaycalendar generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "holidaycalendar", catalog = "spandana")
public class Holidaycalendar implements java.io.Serializable {

	private int id;
	@JsonManagedReference
	private Departmentdetails departmentdetails;
	private Date holidayDate;
	private String holiday;

	public Holidaycalendar() {
	}

	public Holidaycalendar(Departmentdetails departmentdetails, Date holidayDate, String holiday) {
		this.departmentdetails = departmentdetails;
		this.holidayDate = holidayDate;
		this.holiday = holiday;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "departmentIdHC", nullable = false)
	public Departmentdetails getDepartmentdetails() {
		return this.departmentdetails;
	}

	public void setDepartmentdetails(Departmentdetails departmentdetails) {
		this.departmentdetails = departmentdetails;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "holidayDate", nullable = false, length = 10)
	public Date getHolidayDate() {
		return this.holidayDate;
	}

	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}

	@Column(name = "holiday", nullable = false, length = 45)
	public String getHoliday() {
		return this.holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

}
