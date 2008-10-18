/*
Holyoke Framework: library for GUI-based database applications
This file Copyright (c) 2006-2008 by Robert Fischer

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

package oalaunch.citibob.reflect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassPathUtils {
	

static final int ST_INIT = 0;
static final int ST_INCP = 1;
	
/** Adds to a classpath the stuff found in the Class-Path of a manifest file. */
public static void getClassPath(URL jarURL, Collection<JarURL> out)
throws MalformedURLException, IOException
{
//	List<JarURL> urls = new LinkedList();
//	URL url = clurl.findResource("META-INF/MANIFEST.MF");
	URL manifestURL = new URL("jar:" + jarURL.toString() + "!/META-INF/MANIFEST.MF");
	
	// Open the Manifest file and read the Class-Path item
	StringBuffer sb = new StringBuffer();
	BufferedReader in = new BufferedReader(new InputStreamReader(
		manifestURL.openStream()));
	String line;
	int state = ST_INIT;
	outer : while ((line = in.readLine()) != null) {
		switch(state) {
			case ST_INIT : {
				int colon = line.indexOf(':');
				if (colon < 0) continue;
				String label = line.substring(0, colon).trim().toLowerCase();
				if (!"class-path".equals(label)) continue;
				state = ST_INCP;
				sb.append(line.substring(colon+1));
			} break;
			case ST_INCP : {
				if (line.length() > 0 && line.charAt(0) == ' ') {
					sb.append(line.substring(1));
				} else {
					state = ST_INIT;
					break outer;		// Only one Class-Path item, we're done!
				}
			}
		}
	}
	in.close();

	// Parse the classpath
	String[] sclasspath = sb.toString().trim().split(" ");
	for (String spec : sclasspath) {
		out.add(new JarURL(new URL(jarURL, spec)));
	}
	
//	return urls;
}

/** Recursively gets classpath, looking into all MANIFEST files. 
 Starts from a base classpath of jar files. */
public static void expandClassPath(URLClassLoader loader, List<JarURL> base, Collection<JarURL> out)
throws MalformedURLException, IOException
{
	// Look at each item nominally on classloader's classpath
	for (JarURL url : base) {
		out.add(url);
		if (url.getUrl().getPath().endsWith(".jar")) {
			// We have a jar URL; add in any items on the jar's classpath
			getClassPath(url.getUrl(), out);
		}
	}
}

/** Recursively gets classpath, looking into all MANIFEST files. */
public static List<JarURL> getBaseClassPath(URLClassLoader loader)
throws MalformedURLException, IOException
{
	List<JarURL> out = new LinkedList();
	// Look at each item nominally on classloader's classpath
	URL[] urls = loader.getURLs();
	for (URL url : urls) out.add(new JarURL(url));
	return out;
}

static void eliminateDups(List<JarURL> urls)
{
	Set<JarURL> set = new HashSet();
	for (Iterator<JarURL> ii = urls.iterator(); ii.hasNext(); ) {
		JarURL url = ii.next();
		if (set.contains(url)) {
			ii.remove();
		} else {
			set.add(url);
		}
	}
}

public static void subtractCP(URLClassLoader loader, List<JarURL> aa, String... jarFiles)
throws MalformedURLException, IOException
{
	TreeSet<String> set = new TreeSet();
	for (String name : jarFiles) set.add(name);
	subtractCP(loader, aa, set);
}
	/** Computes A - B */
static void subtractCP(URLClassLoader loader, List<JarURL> aa, Set<String> jarFiles)
throws MalformedURLException, IOException
{
	// Find jarFiles in aa
	List<JarURL> base = new LinkedList();
	for (JarURL url : aa) {
		if (jarFiles.contains(url.getName())) base.add(url);
	}

	// Expand to all dependencies
	SortedSet<JarURL> fullJarFiles = new TreeSet();
	expandClassPath(loader, base, fullJarFiles);
	
	
	// Remove fullJarFiles from aa
	List<JarURL> out = new LinkedList();
	for (Iterator<JarURL> ii = aa.iterator(); ii.hasNext();) {
		JarURL url = ii.next();
		if (fullJarFiles.contains(url)) ii.remove();
	}
	
//	
//	Set<URL> bbset = new HashSet();
//	for (URL url : bb) bbset.add(url);
//	for (Iterator<URL> ii = urls.iterator(); ii.hasNext(); ) {
//		URL url = ii.next();
//		if (set.contains(url)) {
//			ii.remove();
//		} else {
//			set.add(url);
//		}
//	}
}

public static List<JarURL> getClassPath(URLClassLoader loader)
throws MalformedURLException, IOException
{
	List<JarURL> out = new LinkedList();
	expandClassPath(loader, getBaseClassPath(loader), out);
	eliminateDups(out);
	return out;
}

public static List<JarURL> getClassPath()
throws MalformedURLException, IOException
{
	return getClassPath((URLClassLoader)ClassPathUtils.class.getClassLoader());
}

public static File getMavenProjectRoot()
{
	URLClassLoader cl = (URLClassLoader)ClassPathUtils.class.getClassLoader();
	URL[] urls = cl.getURLs();
	String surl = urls[0].toString();
	surl = surl.substring("file:/".length());
	int target = surl.indexOf("/target/");
	surl = "/" + surl.substring(0,target);
	return new File(surl.replace('/', File.separatorChar));
}
public static void main(String[] args) throws Exception
{
	ClassLoader clMain = ClassPathUtils.class.getClassLoader();
	System.out.println("====== Parent Classloaders");
	for (ClassLoader loader = clMain; loader != null; loader = loader.getParent()) {
		// Main classloader
		System.out.println(loader);
		
		// The classloader's inheritence structure
		Class klass = loader.getClass();
		while (klass != null) {
			System.out.println("     " + klass.getName());
			klass = klass.getSuperclass();
		}
		
		// The Classloader's classpath
		System.out.println("     ---- Classpath:");
		try {
			URLClassLoader urlcl = (URLClassLoader)loader;
//			URL[] urls = urlcl.getURLs();
			List<JarURL> urls = getClassPath(urlcl);
			for (JarURL url : urls) System.out.println("      " + url);
		} catch(ClassCastException e) {}
	}

	URLClassLoader clurl = (URLClassLoader)clMain;
	System.out.println("======= Sample resource searches");
	System.out.println(clurl.getResource("citibob/app/App.class"));
	System.out.println(clurl.getResource("bsh/Interpreter.class"));
	System.out.println(clurl.getResource("java/lang/Integer.class"));
	System.out.println(clurl.findResource("META-INF/MANIFEST.MF"));
	System.out.println(clurl.getResource("META-INF/MANIFEST.MF"));
	
	
	System.out.println("Project Root = " + getMavenProjectRoot());
	// Get the project directory from the URL of the main classpath
	
//	// Parse the manifest 
//	URL url = clurl.findResource("META-INF/MANIFEST.MF");
//	Reader in = new InputStreamReader(url.openStream());
//	int c;
//	while ((c = in.read()) >= 0) System.out.print((char)c);
//	in.close();
}
}
