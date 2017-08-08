package com.esko.Security;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;

/*
 * used to assign roles to user
 * */
public class Role implements GrantedAuthority{

	final static Logger LOGGER = Logger.getLogger(Role.class.getName());
	private String name;
	
	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
    public void setName(String name) {
        this.name = name;
    }

}
