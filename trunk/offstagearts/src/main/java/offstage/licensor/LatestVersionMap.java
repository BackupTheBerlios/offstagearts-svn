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
	if (mavenName.contains("offstagearts") || mavenName.contains("holyoke")) {
System.out.println(mavenName);
		int dash = mavenName.indexOf('-');
		int ldot = mavenName.lastIndexOf('.');
		destName = mavenName.substring(0, dash+1) + "LATEST" + mavenName.substring(ldot);
	}
	
	return destName;
}
	
}
