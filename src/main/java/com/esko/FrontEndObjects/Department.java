package com.esko.FrontEndObjects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Object to send and receive the department
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Department {
	
	@JsonManagedReference
	String departmentName;
}
