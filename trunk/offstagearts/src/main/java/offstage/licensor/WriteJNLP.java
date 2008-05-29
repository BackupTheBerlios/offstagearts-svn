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

public static String getReleaseVersion()
{
	URLClassLoader cl = (URLClassLoader)WriteJNLP.class.getClassLoader();
	URL[] urls = cl.getURLs();
	String surl = urls[0].toString();
//System.out.println(surl);
	int slash = surl.lastIndexOf('/');
	int dash = surl.indexOf('-', slash+1);
	int dot = surl.lastIndexOf('.');
	String version = surl.substring(dash+1,dot);
	return version;
//	System.out.println(version);
}

public static void writeJNLP(VersionMap vm, boolean demo) throws Exception
{
	// Figure out which version number to use for file
	String releaseVersion = getReleaseVersion();
	
	String fileVersion = vm.getFileVersion(releaseVersion);
	
	// ===========================
	// Write the .jnlp file
	File projDir = ClassPathUtils.getMavenProjectRoot();
	File prefsFile = new File(projDir, "src/main/resources/offstage/offstagearts.jnlp.st");
	StringTemplate template = new StringTemplate(FileUtils.readFileToString(prefsFile));
		template.setAttribute("releaseVersion", fileVersion);
		template.setAttribute("jarBase", "/jars/");
//		for (String arg : args) template.setAttribute("args", arg);
		if (demo) template.setAttribute("args", "demo");
		
	// Add jars to the file
	List<JarURL> jurls = ClassPathUtils.getClassPath();
	for (JarURL jurl : jurls) {
		String str = jurl.getUrl().toString();
		int pos = str.indexOf(exec);
		String jarFile = str.substring(pos + exec.length());
//System.out.println(jurl.getUrl());
		template.setAttribute("jars", vm.getJawsName(jarFile));
	}

	// Write out JNLP file
	File jnlpDir = new File(projDir, "jaws/jnlp");
	jnlpDir.mkdirs();
	File outFile = new File(jnlpDir, (demo ? "offstagearts_demo-" : "offstagearts-") + fileVersion + ".jnlp");

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
	writeJNLP(vm, false);
	writeJNLP(vm, true);
}

}
