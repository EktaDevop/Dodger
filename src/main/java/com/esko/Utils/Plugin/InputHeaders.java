package com.esko.Utils.Plugin;

public enum InputHeaders {

	EMPLOYEE_ID("Employee Name"), ATTENDANCE_DATE("Attendance Date"), CHECK_IN(
			"In Time"), CHECK_OUT("Out Time");

	private String text;

	private InputHeaders(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
