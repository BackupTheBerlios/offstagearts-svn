/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author citibob
 */
public class ReleaseVersion {
public static void main(String[] args)
{
	try {
		URLClassLoader cl = (URLClassLoader)ReleaseVersion.class.getClassLoader();
		URL[] urls = cl.getURLs();
		String surl = urls[0].toString();
//System.out.println(surl);
		int slash = surl.lastIndexOf('/');
		int dash = surl.indexOf('-', slash+1);
		int dot = surl.lastIndexOf('.');
		String version = surl.substring(dash+1,dot);
		System.out.println(version);
	} catch(Exception e) {
		e.printStackTrace();
	}
}
}
