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

package offstage.licensor;

import citibob.version.Version;


/**
 *
 * @author citibob
 */
public class LatestVersionMap implements VersionMap
{


/** Returns the version of the system as a whole --- given the version of
 * the main offstagearts.jar file */
public String getFileVersion(String releaseVersion)
{
	return "LATEST";
}

	
/** Given the name of a Maven-created Jar file, returns the name that will be
 * used in the deployed Java Web Start version.
 * @param jarName
 * @return
 */
public String getJawsName(String mavenName)
{
	String destName = mavenName;
	
	// Chop off third part of version number
	if (mavenName.contains("offstagearts")) {
System.out.println(mavenName);
		int dash = mavenName.indexOf('-');
		int ldot = mavenName.lastIndexOf('.');
		destName = mavenName.substring(0, dash+1) + "LATEST" + mavenName.substring(ldot);
	}
	
	return destName;
}
	
}
