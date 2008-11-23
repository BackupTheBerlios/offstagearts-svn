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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;
import offstage.crypt.PBECrypt;
import org.apache.commons.io.FileUtils;

/**
 *
 * 
 * @author citibob
 */
public class MakeLauncher {

/**
 * 
 * @param version
 * @param configDir
 * @param outJar
 * @param spassword
 * @throws java.lang.Exception
 */
public static void makeLauncher(String version,
File configDir, File outJar, String spassword)
throws Exception
{
	File oaDir = ClassPathUtils.getMavenProjectRoot();
	File oalaunchDir = new File(oaDir, "../oalaunch");
	File oasslDir = new File(oaDir, "../oassl");
	File keyDir = new File(oasslDir, "keys/client");
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
	
	// Read app.properties
	Properties props = new Properties();
	InputStream in = new FileInputStream(new File(configDir, "app.properties"));
	props.load(in);
	in.close();
	
	// Re-do the config dir
	FileUtils.deleteDirectory(tmpConfigDir);
	FileFilter ff = new FileFilter() {
	public boolean accept(File f) {
		if (f.getName().startsWith(".")) return false;
		if (f.getName().endsWith("~")) return false;
		return true;
	}};
	FileUtils.copyDirectory(configDir, tmpConfigDir, ff);


	// Set up to encrypt
	char[] password = null;
	PBECrypt pbe = new PBECrypt();
	if (spassword != null) password = spassword.toCharArray();
	
	// Encrypt .properties files if needed
	if (password != null) {
		for (File fin : configDir.listFiles()) {
			if (fin.getName().endsWith(".properties") || fin.getName().endsWith(".jks")) {
				File fout = new File(tmpConfigDir, fin.getName());
				pbe.encrypt(fin, fout, password);
			}
		}
	}

	// Copy the appropriate key and certificate files
	String dbUserName = props.getProperty("db.user", null);
	File[] jksFiles = new File[] {
		new File(keyDir, dbUserName + "-store.jks"),
		new File(keyDir, dbUserName + "-trust.jks")
	};
	for (File fin : jksFiles) {
		if (!fin.exists()) {
			System.out.println("Missing jks file: " + fin.getName());
			continue;
		}
		File fout = new File(tmpConfigDir, fin.getName());
		if (password != null) {
			System.out.println("Encrypting " + fin.getName());
			pbe.encrypt(fin, fout, password);
		} else {
			System.out.println("Copying " + fin.getName());
			FileUtils.copyFile(fin, fout);
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
