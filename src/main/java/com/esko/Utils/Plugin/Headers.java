package com.esko.Utils.Plugin;

public enum Headers {

	EMPLOYEE_ID("EmployeeId"), ATTENDANCE_DATE("AttendanceDate"), CHECK_IN(
			"CheckIn"), CHECK_OUT("CheckOut");

	private String text;

	private Headers(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
