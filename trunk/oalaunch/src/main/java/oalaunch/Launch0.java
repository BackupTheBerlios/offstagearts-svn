package oalaunch;

import oalaunch.citibob.reflect.ClassPathUtils;
import oalaunch.citibob.reflect.JarURL;
import oalaunch.citibob.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;
import oalaunch.citibob.gui.RobustOpen;

/**
 * Hello world!
 *
 */
public class Launch0 
{
    public static void main( String[] args ) throws Exception
    {
		// Find location of our .jar file
		URL thisJarURL = null;
		List<JarURL> jurls = ClassPathUtils.getBaseClassPath((URLClassLoader)Launch0.class.getClassLoader());
		for (JarURL jurl : jurls) {
			System.out.println(jurl.getName());
//			if (jurl.getName().equals("oalaunch")) {
				thisJarURL = jurl.getUrl();
				break;
//			}
		}

//		URL thisJarURL = thisJarFile.toURL();
		System.out.println("thisJarURL = " + thisJarURL);
		
		// Get the configuration name from the name of our JAR file
		String surl = thisJarURL.getFile();
//System.out.println(surl);
		int slash = surl.lastIndexOf('/');
		int dot = surl.indexOf('.', slash+1);
//System.out.println(slash);
//System.out.println(dot);
		String configName = URLDecoder.decode(surl.substring(slash+1,dot), "UTF-8");
		
//		// Get the configuration name from a resource file
//		InputStream in = Launch0.class.getClassLoader().getResourceAsStream("offstage/configname.txt");
//		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
//		String configName = bin.readLine();
//System.out.println("configName = " + configName);
//		bin.close();
		
		// Load up our configuration properties
		Properties oalaunch = new Properties();
		ClassLoader clr = Launch0.class.getClassLoader();
		URL url = clr.getResource("oalaunch/oalaunch.properties");
		InputStream in = url.openStream();
		oalaunch.load(in);
		in.close();

//		configName = oalaunch.getProperty("config.name", configName);
		
		// Get URL to load our JNLP template
		URL templateURL = clr.getResource("oalaunch/offstagearts.jnlp.template");
		String sTemplateURL = oalaunch.getProperty("jnlp.template.url", null);
		if (sTemplateURL != null) templateURL = new URL(sTemplateURL);
		
System.out.println("sTemplateURL = " + sTemplateURL);
System.out.println("templateURL = " + templateURL);
		
		// Create temporary .JNLP file based on the template
		// This will insert our launcher jar as the first in the classpath,
		// so it will override the correct app.property, etc. files
		Template jnlp = new Template(templateURL);
		
		jnlp.put("oalaunch.jar", thisJarURL.toString());
		jnlp.put("oalaunch", "true");
//		jnlp.put("configName", configName);
		
		File jnlpFile = File.createTempFile("offstagearts", ".jnlp");
System.out.println("jnlpFile = " + jnlpFile);
		jnlpFile.deleteOnExit();
		FileWriter out = new FileWriter(jnlpFile);
		jnlp.write(out);
		out.close();
		
		RobustOpen.open(jnlpFile);
		
	
		// Wait long enough for Java Web Start to pick up the JNLP file
		Thread.currentThread().sleep(20 * 1000);
        System.out.println("Launcher Exiting!!!");
	}
//		
//		// Download the .jnlp file to a temporary location
//		URL jnlpURL = new File("/Users/citibob/mvn/oalaunch/offstagearts-1.6.jnlp").toURL();
//		
//
//		// Copy the .property files
//		File appFile = copyResourceToFile("app.properties");
//		String os = System.getProperty("os.name");
//			int space = os.indexOf(' ');
//			if (space >= 0) os = os.substring(0,space);
//		File osFile = copyResourceToFile(os + ".properties");
//
//		// Copy the JNLP file, filling in template
//		Template jnlp = new ResourceTemplate("offstage/offstagearts.jnlp");
//		jnlp.put("jnlp.app.properties", appFile.getPath());
//		jnlp.put("jnlp.os.properties", osFile.getPath());
//		
//		File jnlpFile = File.createTempFile("offstagearts", ".jnlp");
//		FileWriter out = new FileWriter(jnlpFile);
//		jnlp.write(out);
//		out.close();
//
//		// Launch the JNLP file
//		BareBonesOpen.open(jnlpFile);
//		
//        System.out.println("Launcher Exiting!!!");
//    }


//static File copyResourceToFile(String name) throws IOException
//{
//	int dot = name.indexOf('.');
//	String prefix = name.substring(0,dot);
//	String suffix = name.substring(dot+1);
//	
//	InputStream in = Launch0.class.getClassLoader().getResourceAsStream(
//		"oalaunch/config/" + prefix + suffix);
//	File outFile = File.createTempFile(prefix, suffix);
//	outFile.deleteOnExit();
//	OutputStream out = new FileOutputStream(outFile);
//	byte[] buf = new byte[8192];
//	int n;
//	while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
//	
//	
//	in.close();
//	out.close();
//	
//	return outFile;
//}
}
