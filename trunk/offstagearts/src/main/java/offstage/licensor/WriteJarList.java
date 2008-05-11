/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import citibob.reflect.JarURL;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author citibob
 */
public class WriteJarList {

public static void main(String[] args) throws Exception
{	
	File projDir = ClassPathUtils.getMavenProjectRoot();
	File jarList = new File(projDir, "src/main/resources/offstage/jarlist.txt");
	Writer out = new FileWriter(jarList);
	
	// Write out jarlist.txt
	List<JarURL> jurls = ClassPathUtils.getClassPath();
	for (JarURL jurl : jurls) {
		out.write(jurl.getName() + "," + jurl.getVersion() + '\n');
	}

	out.close();
}
}
