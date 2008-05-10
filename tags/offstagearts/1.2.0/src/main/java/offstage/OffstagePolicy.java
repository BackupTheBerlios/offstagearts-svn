/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage;

import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;

/**
 * Policy to allow all Offstage code to run with full system access, but to
 * provide no permissions at all to sitecode.jar file (loaded to help out
 * with tuition, etc).  sitecode.jar needs no access, and giving it access
 * would just pose a security risk to users' desktops.
 * @author citibob
 */
public class OffstagePolicy extends Policy
{
	
URL siteCodeURL;		// Deny permissions to this code source
	
PermissionCollection all, none;

public OffstagePolicy(URL siteCodeURL)
{
	this.siteCodeURL = siteCodeURL;
	all = new Permissions();
		all.add(new AllPermission());
	none = new Permissions();
}
	
public PermissionCollection getPermissions(CodeSource src)
{
	URL url = src.getLocation();
	
	// Either equal comparison works; this has been tested.
	// I'm going with the more robust one out of paranoia.
	return (siteCodeURL.equals(url) ? none : all);
//	return (url == siteCodeURL ? none : all);
}

public boolean implies(ProtectionDomain domain, Permission permission)
{
//	System.out.println("implies: " + permission);
	PermissionCollection pc = getPermissions(domain);
//	System.out.println("pc = " + pc);
	return pc.implies(permission);
}

public PermissionCollection getPermissions(ProtectionDomain domain)
{
//	return new Permissions();
	return getPermissions(domain.getCodeSource());
}

public void refresh() {}

//boolean implies(ProtectionDomain domain, Permission permission)
//{
//	
//}
//          
//Evaluates the global policy for the permissions granted to the ProtectionDomain and tests whether the permission is granted.
//abstract  void	refresh() 
//          Refreshes/reloads the policy configuration.
//static void	setPolicy(Policy policy) 
//          Sets the system-wide Policy object.
}
