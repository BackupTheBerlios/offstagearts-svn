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

package oalaunch.citibob.gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author citibob
 */
public class RobustOpen {

// ==========================================================	
static interface Opener {
	public void open(File f) throws Exception;
}

static class MacURLOpen implements Opener {
	public void open(File f) throws Exception {
		URL url = f.toURL();
		Class fileMgr = Class.forName("com.apple.eio.FileManager");
		Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
		openURL.invoke(null, new Object[] {url.toExternalForm()});
	}
	public String toString() { return "MacURLOpen"; }
}

static class WindowsURLOpen implements Opener {
	public void open(File f) throws Exception {
		String[] cmd = {"rundll32", "url.dll,FileProtocolHandler", null};
		URL url = f.toURL();
		cmd[2] = url.toExternalForm();
		Runtime.getRuntime().exec(cmd);
	}
	public String toString() { return "WindowsURLOpen"; }
}

static class CmdOpen implements Opener {
	String cmd;
	boolean waitForExit;		// if true, wait for the subprocess to exit
		// Should be true for commands that are expected to always launch, then
		// return quickly.
	public CmdOpen(String cmd) {
		this(cmd, false);
	}
	public CmdOpen(String cmd, boolean waitForExit) {
		this.cmd = cmd;
		this.waitForExit = waitForExit;
	}
	public void open(File f) throws Exception {
		String[] cmdLine = {cmd, f.getPath()};
		Process proc = Runtime.getRuntime().exec(cmdLine, null, f.getParentFile());
		if (waitForExit) {
			proc.waitFor();
			if (proc.exitValue() != 0) throw new IOException(
					toString() + " " + f + " returns bad exit value of " + proc.exitValue());
		}
	}
	public String toString() { return cmd; }
}

static class MultiOpen implements Opener {
	Opener[] opens;
	public MultiOpen(Opener... opens) { this.opens = opens; }
	public void open(File f) throws Exception {
		for (Opener open : opens) {
			try {
				System.out.println("Trying to open " + f + " with " + open);
				open.open(f);
				return;
			} catch(Exception e) {
				System.out.println("    --> Opener failed with " + e);
			}
		}
		throw new IOException("File " + f + " could not open on any of " + toString());
	}
	public String toString()
	{
		StringBuffer allOpeners = new StringBuffer("MultiOpen(");
		for (Opener open : opens) allOpeners.append(open.toString() + ", ");
		allOpeners.append(")");
		return allOpeners.toString();
		
	}
}


// Best-effort general-purpose openers
static class MacOpen extends MultiOpen {
	public MacOpen() {
		super(new MacURLOpen(), new CmdOpen("open", true));
	}
}
static class WindowsOpen extends MultiOpen {
	public WindowsOpen() {
		super(new WindowsURLOpen(), new CmdOpen("start", true));
	}
}

static class LinuxOpen extends MultiOpen {
public LinuxOpen() {
	super(new CmdOpen("gnome-open", true));
}}

// ==========================================================	


static class ExtOS implements Comparable<ExtOS> {
	String ext;		// Filename extnesion (or maybe MIME type)
	String OS;
	public int compareTo(ExtOS b)
	{
		int cmp = OS.compareTo(b.OS);
		if (cmp != 0) return cmp;
		return ext.compareTo(b.ext);
	}
	public ExtOS(String OS, String ext)
	{
		this.ext = ext;
		this.OS = OS;
	}
}
static Map<ExtOS,Opener> actions = new TreeMap();

static Opener newOpener(Opener... opens)
{
	if (opens.length == 0) return null;
	if (opens.length == 1) {
		return opens[0];
	} else {
		return new MultiOpen(opens);
	}
	
}

static void add(String OS, String ext, Opener... opens)
{
	actions.put(new ExtOS(OS, ext), newOpener(opens));
}
static void add(String OS, String ext, String... cmds)
{
	Opener[] opens = new Opener[cmds.length];
	for (int i=0; i<cmds.length; ++i) opens[i] = new CmdOpen(cmds[i]);
	add(OS, ext, opens);
}

//static final String MAC_OPEN = "open";
//static final String WINDOWS_OPEN = "start";
//static final String LIUX_OPEN = "gnome-open";

static {
	// General open stuff
	add("Mac", "*", new MacOpen());
	add("Windows", "*", new WindowsOpen());
	add("Linux", "*", new LinuxOpen());


	// Java
	add("*", "java", "java");
	add("*", "jnlp", "javaws");
	
	// PDF
	add("Linux", "pdf", "acroread");
	add("Windows", "pdf", "acroread");

}

public static void open(java.io.File f) throws Exception
{
	// get OS Name
	String osName = System.getProperty("os.name");
	int space = osName.indexOf(' ');
	if (space >= 0) osName = osName.substring(0,space);
	
	// Get extension
	String ext;
	String name = f.getName();
	int dot = name.lastIndexOf('.');
	ext = (dot < 0 ? "" : name.substring(dot+1));
	
	ExtOS extos = new ExtOS(osName, ext);
	
	List<Opener> opens = new ArrayList();
	
	// First, use general stuff for this platform
	extos.OS = osName;
	extos.ext = "*";
	Opener open = actions.get(extos);
	if (open != null) opens.add(open);
	
	
	extos.OS = "*";
	extos.ext = ext;
	open = actions.get(extos);
	if (open != null) opens.add(open);

	extos.OS = osName;
	extos.ext = ext;
	open = actions.get(extos);
	if (open != null) opens.add(open);
	
	// Create a final opener
	Opener[] xopens = new Opener[opens.size()];
	opens.toArray(xopens);
	Opener mopen = newOpener(xopens);
	
	if (mopen == null) throw new IOException("Cannot find opener for file " + f);
	mopen.open(f);
}



//public static void open(java.io.File f) throws IOException
//{
//	String osName = System.getProperty("os.name");
//	if (osName.startsWith("Mac")) {
//		Runtime.getRuntime().exec("open " + f.getPath());
//	} else if (osName.startsWith("Windows")) {
//		Runtime.getRuntime().exec("start " + f.getPath());
//	} else { //assume Unix or Linux
//		Runtime.getRuntime().exec("gnome-open " + f.getPath());
//	}
//}
public static void main(String[] args) throws Exception
{
//    open(new File("c:\\x.pdf"));
	open(new File("/usr/share/doc/cups/overview.pdf"));
}
}
