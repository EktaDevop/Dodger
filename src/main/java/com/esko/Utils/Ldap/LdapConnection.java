package com.esko.Utils.Ldap;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.esko.Utils.Pdf.PropertiesFile;

import sun.misc.BASE64Decoder;

public class LdapConnection {
	

	
	final static Logger LOGGER = Logger.getLogger(LdapConnection.class.getName());
	Properties prop=PropertiesFile.readProperties("ldap.properties");
	Hashtable<String, String> env = new Hashtable<String, String>();
	
	//private constructor to avoid client applications to use constructor
    private LdapConnection()
    {
    	
    }
    
    private static final LdapConnection connection = new LdapConnection();
    
    public static LdapConnection getInstance(){
    	
        return connection;
    }
    
    public DirContext getLdapCoonection() throws NamingException
    {
    	LOGGER.info("setting ldap parameters");
    	DirContext ctx=null;
    	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL,prop.getProperty("ldapURL") );
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, prop.getProperty("userDn"));
		env.put(Context.SECURITY_CREDENTIALS, prop.getProperty("password"));
		LOGGER.info("connecting to ldap server....");
		ctx = new InitialDirContext(env);
		LOGGER.info("connected to ldap server");
		return ctx;
    	
    }
    
	public void authenticateUser(String uid,String pwd) throws NamingException, IOException
	{
    	 		
    	 		DirContext ctx= getLdapCoonection();
		        String domain=prop.getProperty("domain");
		        SearchControls ctls = new SearchControls();
       	 		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
       	 		String filter = "(&(cn=*)(" + "sAMAccountName" + "=" + uid + "))";
   				NamingEnumeration<SearchResult> answer = ctx.search(prop.getProperty("domain"), filter, ctls);		
   				SearchResult sr = (SearchResult) answer.next();
   				String cnameresult = sr.getName();
   				if (cnameresult != null)
   				{
   					LOGGER.info("user details:"+ cnameresult);
   					if (pwd != null && pwd.length() > 0) 
   					{	
   						/* set user authentication parameters */
   						env.put(javax.naming.Context.SECURITY_PRINCIPAL, cnameresult+","+domain);
   						env.put(javax.naming.Context.SECURITY_CREDENTIALS, pwd);
   						ctx = new InitialDirContext(env);
   						LOGGER.info("user is authenticated");	
   						
   					}
   					
   				}
   				
   				else
   				{
   					LOGGER.info("not a valid user");	
   				}
   		}
	
}

    

