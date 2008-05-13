/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

/**
 *
 * @author citibob
 */
public interface VersionMap {

	
/** Returns the version of the system as a whole --- given the version of
 * the main offstagearts.jar file */
public String getFileVersion(String releaseVersion);

	
/** Given the name of a Maven-created Jar file, returns the name that will be
 * used in the deployed Java Web Start version.
 * @param jarName
 * @return
 */
public String getJawsName(String mavenName);
	
}
