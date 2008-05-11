/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import offstage.FrontApp;

/**
 *
 * @author citibob
 */
public class WritePrefs {
	
public static void main(String[] args) throws Exception
{
	try {
		File projDir = ClassPathUtils.getMavenProjectRoot();
		File prefsFile = new File(projDir, "src/main/resources/offstage/config/prefs.txt");
		PrintWriter out = new PrintWriter(new FileWriter(prefsFile));

		Map<String,String> basePrefs = FrontApp.readBasePrefs();
		
		System.out.println("Running WritePrefs");
		Preferences prefs = Preferences.userRoot();
		prefs = prefs.node("offstagearts/gui");

		// PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		addPrefs(basePrefs, prefs);
		for (Map.Entry<String,String> e : basePrefs.entrySet()) {
			out.println(e.getKey() + "=" + e.getValue());
		}
		out.close();
	} catch(Exception e) {
		e.printStackTrace();
	}
}

/** Adds preferences tree to a base set of preferences.  Recursive */
static void addPrefs(Map<String,String> basePrefs, Preferences prefs)
throws BackingStoreException
{
	String[] keys = prefs.keys();
	for (int i=0; i<keys.length; ++i) {
		if (keys[i].startsWith("_")) continue;	// Don't store "transient" prefs
		basePrefs.put(prefs.absolutePath() + "/" + keys[i], prefs.get(keys[i], null));
	}
	String[] children = prefs.childrenNames();
	for (String child : children) addPrefs(basePrefs, prefs.node(child));
}


}
