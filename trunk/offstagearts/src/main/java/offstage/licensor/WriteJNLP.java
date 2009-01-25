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

import citibob.reflect.ClassPathUtils;
import citibob.reflect.JarURL;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author citibob
 */
public class WriteJNLP {


static String exec = "executable-netbeans.dir/";

public static String getReleaseVersion3()
{
	URLClassLoader cl = (URLClassLoader)WriteJNLP.class.getClassLoader();
	URL[] urls = cl.getURLs();
	String surl = urls[0].toString();
//System.out.println(surl);
	int slash = surl.lastIndexOf('/');
	int dash = surl.indexOf('-', slash+1);
	String version = surl.substring(dash+1);
	return version;
//	System.out.println(version);
}
/** Two-level version number obtained from CLASSPATH */
public static String getReleaseVersion2()
{
	String ver = getReleaseVersion3();
	int dot = ver.lastIndexOf('.');
	String version = ver.substring(0,dot);
	return version;
//	System.out.println(version);
}


// The three JNLP files we write
static final int JNLP_CHOOSE = 0;
static final int JNLP_LAUNCH = 1;
static final int JNLP_DEMO = 2;

static class JarAndType {
	public String jar;
	public String downloadType;
	public String getJar() { return jar; }
	public String getDownloadType() { return downloadType; }
}
public static void writeJNLP(VersionMap vm, int jnlpType) throws Exception
{
	// Figure out which version number to use for file
	String releaseVersion = getReleaseVersion2();
	
	String fileVersion = vm.getFileVersion(releaseVersion);
	
	// ===========================
	// Write the .jnlp file
// TODO: should really use the resource mechanism here, no need to muck around in the Maven project.
	File projDir = ClassPathUtils.getMavenProjectRoot();
	File prefsFile = new File(projDir, "src/main/resources/offstage/offstagearts.jnlp.st");
	StringTemplate template = new StringTemplate(FileUtils.readFileToString(prefsFile));
		template.setAttribute("releaseVersion", fileVersion);
		template.setAttribute("jarBase", "/jars/");
		
	String outFileName = null;
	switch(jnlpType) {
		case JNLP_CHOOSE :
			template.setAttribute("mainClass", "offstage.launch.Dialog");
			template.setAttribute("launchType", "choose");
		break;
		case JNLP_LAUNCH :
			template.setAttribute("mainClass", "offstage.launch.ConfigsFile");// "offstage.gui.OALaunchLauncher");
			template.setAttribute("launchType", "launch");
		break;
		case JNLP_DEMO :
			template.setAttribute("launchType", "demo");
			template.setAttribute("mainClass", "offstage.launch.Demo");
		break;
	}
		
	outFileName = "offstagearts_" + template.getAttribute("launchType") +
		"-" + fileVersion + ".jnlp";
		
	// Add jars to the file
	List<JarURL> jurls = ClassPathUtils.getClassPath();
	for (JarURL jurl : jurls) {
		String str = jurl.getUrl().toString();
		int pos = str.indexOf(exec);
		if (pos < 0) continue;
		String jarFile = str.substring(pos + exec.length());
//System.out.println(str);
//System.out.println(jurl.getUrl());
		JarAndType jat = new JarAndType();
			jat.jar = vm.getJawsName(jarFile);
			jat.downloadType = "eager";		// Lazy didn't save us any loading in test
//			jat.downloadType = (
//				jat.jar.startsWith("offstage")
////				jat.jar.startsWith("holyoke") || jat.jar.startsWith("offstage")
//				? "eager" : "lazy");
		template.setAttribute("jars", jat);
	}

	// Write out JNLP file
	File jnlpDir = new File(projDir, "jaws/jnlp");
	jnlpDir.mkdirs();
	File outFile = new File(jnlpDir, outFileName);
	FileUtils.writeStringToFile(outFile, template.toString());
}

public static VersionMap newVersionMap(String arg)
{
	if (arg.equals("release")) {
		return new ReleaseVersionMap();
	} else {
		return new LatestVersionMap();
	}
	
}

public static void main(String[] args) throws Exception
{
	VersionMap vm = newVersionMap(args[0]);
	writeJNLP(vm, JNLP_CHOOSE);
	writeJNLP(vm, JNLP_LAUNCH);
	writeJNLP(vm, JNLP_DEMO);
}

}
