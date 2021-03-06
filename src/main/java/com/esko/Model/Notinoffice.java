package com.esko.Model;
// Generated Jul 13, 2017 10:45:30 AM by Hibernate Tools 5.1.0.Alpha1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Notinoffice generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "notinoffice", catalog = "spandana")
public class Notinoffice implements java.io.Serializable {

	private Integer id;
	private Employeedetails employeedetailsByEmployeeIdNio;
	private Employeedetails employeedetailsByManagerIdNio;
	private Date dateOfRequestStart;
	private String remarks;
	private String requestStatus;
	private String requestType;
	private Date dateOfRequestEnd;
	private Date dateOfAction;

	public Notinoffice() {
	}

	public Notinoffice(Employeedetails employeedetailsByEmployeeIdNio, Employeedetails employeedetailsByManagerIdNio,
			Date dateOfRequestStart, String remarks, String requestStatus, String requestType, Date dateOfRequestEnd,
			Date dateOfAction) {
		this.employeedetailsByEmployeeIdNio = employeedetailsByEmployeeIdNio;
		this.employeedetailsByManagerIdNio = employeedetailsByManagerIdNio;
		this.dateOfRequestStart = dateOfRequestStart;
		this.remarks = remarks;
		this.requestStatus = requestStatus;
		this.requestType = requestType;
		this.dateOfRequestEnd = dateOfRequestEnd;
		this.dateOfAction = dateOfAction;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employeeIdNIO", nullable = false)
	public Employeedetails getEmployeedetailsByEmployeeIdNio() {
		return this.employeedetailsByEmployeeIdNio;
	}

	public void setEmployeedetailsByEmployeeIdNio(Employeedetails employeedetailsByEmployeeIdNio) {
		this.employeedetailsByEmployeeIdNio = employeedetailsByEmployeeIdNio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "managerIdNIO", nullable = false)
	public Employeedetails getEmployeedetailsByManagerIdNio() {
		return this.employeedetailsByManagerIdNio;
	}

	public void setEmployeedetailsByManagerIdNio(Employeedetails employeedetailsByManagerIdNio) {
		this.employeedetailsByManagerIdNio = employeedetailsByManagerIdNio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfRequestStart", nullable = false, length = 10)
	public Date getDateOfRequestStart() {
		return this.dateOfRequestStart;
	}

	public void setDateOfRequestStart(Date dateOfRequestStart) {
		this.dateOfRequestStart = dateOfRequestStart;
	}

	@Column(name = "remarks", nullable = false)
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "requestStatus", nullable = false)
	public String getRequestStatus() {
		return this.requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	@Column(name = "requestType", nullable = false)
	public String getRequestType() {
		return this.requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfRequestEnd", nullable = false, length = 10)
	public Date getDateOfRequestEnd() {
		return this.dateOfRequestEnd;
	}

	public void setDateOfRequestEnd(Date dateOfRequestEnd) {
		this.dateOfRequestEnd = dateOfRequestEnd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfAction", nullable = false, length = 10)
	public Date getDateOfAction() {
		return this.dateOfAction;
	}

	public void setDateOfAction(Date dateOfAction) {
		this.dateOfAction = dateOfAction;
	}

}
