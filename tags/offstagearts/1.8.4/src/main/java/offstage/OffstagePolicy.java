/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage;

import java.awt.AWTPermission;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.sql.SQLPermission;

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
//	none = new Permissions();
//		none.add(new AWTPermission("*"));
//		none.add(new SQLPermission("setLog"));
none = all;		// A mystery permission is needed when running via Java WebStart.
}
	
public PermissionCollection getPermissions(CodeSource src)
{
	URL url = src.getLocation();
	
//return all;		// Need to add the right AWTPermissions to "none" to get this working right...

	// Either equal comparison works; this has been tested.
	// I'm going with the more robust one out of paranoia.
	return (siteCodeURL.equals(url) ? none : all);
//	return (url == siteCodeURL ? none : all);		// This is riskier...
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
