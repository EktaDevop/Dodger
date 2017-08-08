package com.esko.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.esko.Model.Employeedetails;
import com.esko.Service.EmployeeRoleService;
import com.esko.Service.MasterAssociateService;
import com.esko.Utils.Exception.UnsupportedFileException;
import com.esko.Utils.Ldap.LdapConnection;

/*
 * basic ldap authentication connects to ldap server,verifies credentials
 * and authenticates user
 * 
 */

@Service
public class LdapAuthServiceImpl implements AuthenticationProvider {
	
	final static Logger LOGGER = Logger.getLogger(LdapAuthServiceImpl.class.getName());
	
	@Inject
	private MasterAssociateService masterAssociateService;
	@Inject
	private EmployeeRoleService employeeRoleService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException 
	{	
		LOGGER.info("in authenticate ");
		String username=(String) authentication.getPrincipal();
		String password=(String) authentication.getCredentials();
		 
		//Login authentication code
		
		Employeedetails employeedetails = masterAssociateService.checkEmployee(username);
		
		
		if (employeedetails != null) {	
			
			LOGGER.info("User exists in Spandana Database ");
				LdapConnection connection = LdapConnection.getInstance();
		
				try 
				{
					
					connection.authenticateUser(username, password);
					LOGGER.info("User is authenticated ");
					String employeeId = employeedetails.getEmployeeId();
					String role = employeeRoleService.getEmployeeRole(employeedetails);
					LOGGER.info("Assigning permissions to access Resources based on role ");
					Role r=new Role();
					List<Role> roles = new ArrayList<Role>();
					if(role.equals("Admin"))
					{
						LOGGER.info("User is Admin ");
						r.setName("ROLE_ADMIN");
						roles.add(r);
					}
					else if(role.equals("Manager"))
					{
						LOGGER.info("User is Manager ");
						r.setName("ROLE_MANAGER");
						roles.add(r);
					}
					else
					{
						LOGGER.info("User is Associate ");
						r.setName("ROLE_ASSOCIATE");
						roles.add(r);
					}	
					
					Collection<? extends GrantedAuthority> authorities= roles;
					SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, password));		
					return new UsernamePasswordAuthenticationToken(username, password,authorities);
				} 
		catch (NamingException e) 
		{
			LOGGER.info(e);
			e.printStackTrace();
			
		} 
		catch (IOException e) {
			
			LOGGER.info(e);
			e.printStackTrace();
		}
	}
		LOGGER.info("Authentication failed");
		return null;
					
	}
	
	@Override
	public boolean supports(Class<?> authentication) 
	{
		LOGGER.info( "Authentication Manager invoked");
	    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
