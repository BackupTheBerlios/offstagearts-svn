/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import offstage.crypt.PBECrypt;
import org.apache.commons.io.FileUtils;

/**
 *
 * 
 * @author citibob
 */
public class MakeLauncher {

public static void makeLauncher(String version, File configDir, File outJar, String spassword)
throws Exception
{
	File oaDir = ClassPathUtils.getMavenProjectRoot();
	File oalaunchDir = new File(oaDir, "../oalaunch");
	File tmpDir = new File(".", "tmp");
	FileUtils.deleteDirectory(tmpDir);
	tmpDir.mkdirs();
	
	// Find the oalaunch JAR file
	File oalaunchJar = null;
	File[] files = new File(oalaunchDir, "target").listFiles();
	for (File f : files) {
		if (f.getName().startsWith("oalaunch") && f.getName().endsWith(".jar")) {
			oalaunchJar = f;
			break;
		}
	}
	
	// Unjar the oalaunch.jar file into the temporary directory
	exec(tmpDir, "jar", "xvf", oalaunchJar.getAbsolutePath());

	
	File tmpOalaunchDir = new File(tmpDir, "oalaunch");
	File tmpConfigDir = new File(tmpOalaunchDir, "config");
	
	// Re-do the config dir
	FileUtils.deleteDirectory(tmpConfigDir);
	FileFilter ff = new FileFilter() {
	public boolean accept(File f) {
		if (f.getName().startsWith(".")) return false;
		if (f.getName().endsWith("~")) return false;
		return true;
	}};
	FileUtils.copyDirectory(configDir, tmpConfigDir, ff);

	// Encrypt .properties files if needed
	if (spassword != null) {
		PBECrypt pbe = new PBECrypt();
		char[] password = spassword.toCharArray();
		for (File fin : configDir.listFiles()) {
			if (fin.getName().endsWith(".properties")) {
				File fout = new File(tmpConfigDir, fin.getName());
				pbe.encrypt(fin, fout, password);
			}
		}
	}
	
	// Use a downloaded JNLP file, not a static one.
	new File(tmpOalaunchDir, "offstagearts.jnlp").delete();
		
	// Open properties file, which we will write to...
	File oalaunchProperties = new File(tmpDir, "oalaunch/oalaunch.properties");
	Writer propertiesOut = new FileWriter(oalaunchProperties);
	propertiesOut.write(
		"jnlp.template.url = " +
		"http://offstagearts.org/releases/offstagearts/offstagearts_oalaunch-" +
		version + ".jnlp.template\n");
	String configName = outJar.getName();
		int dot = configName.lastIndexOf(".jar");
		if (dot >= 0) configName = configName.substring(0,dot);
	propertiesOut.write("config.name = " + configName + "\n");
	propertiesOut.close();
	
	// Jar it back up
	exec(tmpDir, "jar", "cvfm", outJar.getAbsolutePath(),
		"META-INF/MANIFEST.MF", ".");
	
	// Sign it
	exec(null, "jarsigner", "-storepass", "keyst0re", outJar.getAbsolutePath(),
			"offstagearts");
	
	// Remove the tmp directory
	FileUtils.deleteDirectory(tmpDir);
}


static void exec(File dir, String... cmds) throws IOException, InterruptedException
{
	Process proc = Runtime.getRuntime().exec(cmds, null, dir);
	InputStream in = proc.getInputStream();
	int c;
	while ((c = in.read()) >= 0) System.out.write(c);
	proc.waitFor();
	System.out.println("---> exit value = " + proc.exitValue());
}

public static void main(String[] args) throws Exception
{
	if (args.length < 3) {
		System.out.println("Usage: mklauncher <version> <configDir> <outJar> [password]");
	}
	String version = args[0];
	File configDir = new File(args[1]);
	File outJar = new File(args[2]);
	String password = null;
	if (args.length > 3) password = args[3];
	
	makeLauncher(version, configDir, outJar, password);
//	makeLauncher("LATEST",
////		new File("/Users/citibob/offstagearts/configs/test_ballettheatre"),
//		new File("/Users/citibob/mvn/oamisc/yfsc/config_lan"),
//		new File("/Users/citibob/YFSC Database (LAN).jar"));
}
}
