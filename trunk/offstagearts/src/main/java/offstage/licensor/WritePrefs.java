/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author citibob
 */
public class WritePrefs {
	
public static void main(String[] args) throws Exception
{
	try {
		System.out.println("Running WritePrefs");
		Preferences prefs = Preferences.userRoot();
		prefs = prefs.node("offstage/gui/1.0");

		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		writePrefs(prefs, out);
		out.close();
	} catch(Exception e) {
		e.printStackTrace();
	}
}

static void writePrefs(Preferences prefs, PrintWriter out)
throws BackingStoreException
{
	String[] keys = prefs.keys();
	for (int i=0; i<keys.length; ++i) {
		out.println(prefs.absolutePath() + "/" + keys[i] + "=" + prefs.get(keys[i], null));
	}
	
	String[] children = prefs.childrenNames();
	for (String child : children) writePrefs(prefs.node(child), out);
}

}
