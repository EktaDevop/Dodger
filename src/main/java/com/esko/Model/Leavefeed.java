package com.esko.Model;
// Generated Jul 7, 2017 12:47:02 PM by Hibernate Tools 5.1.0.Alpha1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Leavefeed generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "leavefeed", catalog = "spandana")
public class Leavefeed implements java.io.Serializable {

	private LeavefeedId id;
	private Employeedetails employeedetails;
	private String leaveType;

	public Leavefeed() {
	}

	public Leavefeed(LeavefeedId id, Employeedetails employeedetails, String leaveType) {
		this.id = id;
		this.employeedetails = employeedetails;
		this.leaveType = leaveType;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "employeeIdLf", column = @Column(name = "employeeIdLF", nullable = false, length = 30)),
			@AttributeOverride(name = "leaveDate", column = @Column(name = "leaveDate", nullable = false, length = 10)),
			@AttributeOverride(name = "duration", column = @Column(name = "duration", nullable = false, length = 45)) })
	public LeavefeedId getId() {
		return this.id;
	}

	public void setId(LeavefeedId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employeeIdLF", nullable = false, insertable = false, updatable = false)
	public Employeedetails getEmployeedetails() {
		return this.employeedetails;
	}

	public void setEmployeedetails(Employeedetails employeedetails) {
		this.employeedetails = employeedetails;
	}

	@Column(name = "leaveType", nullable = false, length = 45)
	public String getLeaveType() {
		return this.leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

}