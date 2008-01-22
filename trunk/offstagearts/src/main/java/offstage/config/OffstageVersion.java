/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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
 * OffstageVersion.java
 *
 * Created on April 16, 2006, 5:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.config;

import citibob.version.*;
import java.util.prefs.*;
import java.util.*;
import java.sql.*;
import java.io.*;

/**
 *
 * @author citibob
 */
public class OffstageVersion {

static final Version.Entry[] dbWorks;

public static Version frontendVersion;
public static Version dbVersion;
static Version.Entry dbEntry;
public static Preferences prefs;			// Root preferences node
public static Preferences guiPrefs;		// Root prefs node for GUI

// ------------------------------------------------------------------
static {
	// Version of this front-end
	frontendVersion = new Version(0,3,0);

	// Mappings of what database, etc. versions this front-end works with.
	// Most-preferred mappings are first!
	dbWorks = new Version.Entry[] {
		new Version.Entry(new Version.Range(0,3,0), null)
	};

	try {
		fetchPrefRoots();
	} catch(Exception e) {
		e.printStackTrace(System.err);
		System.exit(-1);
	}
}
// ------------------------------------------------------------------
private static void fetchPrefRoots()
throws BackingStoreException, IOException, InvalidPreferencesFormatException
{
	Preferences n = Preferences.userRoot();
	n = n.node("offstage");
	String sversion = frontendVersion.toString();
	if (!n.nodeExists(sversion)) {
		// No preferences; create them.
		Preferences.importPreferences(
			OffstageVersion.class.getResourceAsStream("prefs.xml"));
	}
	prefs = n.node(sversion);
	guiPrefs = prefs.node("gui");
}
// ------------------------------------------------------------------
/** Creates a new instance of OffstageVersion */
public static void fetchDbVersion(Statement st)
throws SQLException, BackingStoreException, IOException, InvalidPreferencesFormatException
{
	// Make sure the database is right version
	dbVersion = new Version(st, "dbversion");
	dbEntry = getEntry(dbWorks, dbVersion);

//	// Decide on version of preferences to use.
//	Preferences prefRoot = Preferences.userRoot();
//	prefRoot = prefRoot.node("offstage");
//	Version[] prefVersions = citibob.version.Version.getAvailablePrefVersions(prefRoot);
//	Arrays.sort(prefVersions);
//	boolean goodPref = false;
//	for (int i=prefVersions.length-1; ; --i) {	// search from largest
//		if (i < 0) break;
//		Version.Entry e = getEntry(prefWorks, prefVersions[i]);
//		if (e != null && e.converter == null) {
//			prefEntry = e;
//			prefVersion = prefVersions[i];
//			pref = prefRoot.node(prefVersion.toString());
//			guiPref = pref.node("gui");
//			goodPref = true;
//			break;
//		}
//	}
}

/** Make sure the versions found are appropriate. */
public static void checkVersions() throws VersionException
{
	if (dbEntry == null || dbEntry.converter != null) throw new VersionException(
		"Database version " + dbVersion + " not appropriate for front-end version " + frontendVersion);
	
//	if (prefEntry == null || prefEntry.converter != null) throw new VersionException(
//		"No suitable preferences for front-end version " + frontendVersion + " found!");
}


/** Gets the entry corresponding to v. */
private static Version.Entry getEntry(Version.Entry[] e, Version v)
{
	for (int i=0; i<e.length; ++i) {
		if (e[i].range.inRange(v)) return e[i];
	}
	return null;
}


}
