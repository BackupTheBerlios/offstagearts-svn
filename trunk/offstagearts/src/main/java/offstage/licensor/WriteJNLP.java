/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import citibob.reflect.JarURL;
import citibob.version.Version;
import java.io.File;
import java.util.List;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author citibob
 */
public class WriteJNLP {


static String exec = "executable-netbeans.dir/";

public static void main(String[] args) throws Exception
{
	// Figure out which version number to use for file
	String releaseVersion = ReleaseVersion.getReleaseVersion();
	String fileVersion;
	try {
		// Use just first two levels of version number.
		Version version = new Version(releaseVersion);
		fileVersion = version.toString(2);
	} catch(Exception e) {
		// It contains non-numerics, just use version as-is
		fileVersion = releaseVersion;
	}
	
	// ===========================
	// Write the .jnlp file
	File projDir = ClassPathUtils.getMavenProjectRoot();
	File prefsFile = new File(projDir, "src/main/resources/offstage/offstagearts.jnlp.st");
	StringTemplate template = new StringTemplate(FileUtils.readFileToString(prefsFile));
		template.setAttribute("releaseVersion", releaseVersion);
		template.setAttribute("jarBase", "/jars/");

	// Add jars to the file
	List<JarURL> jurls = ClassPathUtils.getClassPath();
	for (JarURL jurl : jurls) {
		String str = jurl.getUrl().toString();
		int pos = str.indexOf(exec);
		String jarFile = str.substring(pos + exec.length());
//System.out.println(jurl.getUrl());
		template.setAttribute("jars", jarFile);
	}

	// Write out JNLP file
	File jnlpDir = new File(projDir, "jaws/jnlp");
	jnlpDir.mkdirs();
	File outFile = new File(jnlpDir, "offstagearts-" + fileVersion + ".jnlp");

	FileUtils.writeStringToFile(outFile, template.toString());
}

}
