package org.icar.h.core.matlab;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author pothoven
 *
 */
public class SimpleServer 
{

	public static void server(Registry registry)
    {
		ResourceBundle properties = PropertyResourceBundle.getBundle("org.icar.h.sps_management.Boot");
		int port = 1099;
        try
        {
    		boolean useSecurityManager = false;
    		try {
    			Boolean.valueOf(properties.getString("useSecurityManager"));
    		} catch (Exception e) {
    			// default to false
    		}

        	if (useSecurityManager && System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
        	
            SimpleImpl obj = new SimpleImpl();
            /* Bind this object instance to the name "SimpleServer" */
            // Naming.rebind("SimpleServer", obj);
            registry.bind("SimpleServer", obj);
            
            System.out.println("SimpleServer started on port " + port);
        }
        catch (Exception e)
        {
            System.out.println("SimpleServer err: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
