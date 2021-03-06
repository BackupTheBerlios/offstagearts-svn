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
 * FrameSetX.java
 *
 * Created on March 12, 2006, 1:22 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import java.sql.*;
import javax.swing.*;
import java.util.prefs.*;
import citibob.swing.prefs.*;
import citibob.jschema.swing.*;
import citibob.gui.*;
import citibob.mail.MailExpDialog;
import citibob.resource.ResData;
import citibob.resource.UpgradePlan;
import citibob.resource.UpgradePlanSet;
import citibob.sql.*;
import citibob.task.SqlTask;
import java.io.IOException;
import offstage.FrontApp;
import offstage.config.*;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 */
public class MainLauncher {

//protected OffstageGui offstageGui;
//protected ConsoleFrame consoleFrame;




/** Creates a new instance of FrameSetX
 @param ctType How to find the configuration files (see FrontApp.CT_* 
 @configName Dependingon the ctType, the name of the configuration. */
public static void launch(int ctType, String configName) throws Exception {
	/* see:
	http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6604109
	http://www.jasperforge.org/sf/go/artf2423
	*/
	System.setProperty("sun.java2d.print.polling", "false");

	System.setProperty("swing.metalTheme", "ocean");
	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");


	try {
		final FrontApp app = new FrontApp(ctType, configName); //new File("/export/home/citibob/svn/offstage/config"));
		boolean resGood = app.checkResources();
		app.initWithDatabase();

		if (resGood) {
			app.frameSet().openFrame("maintenance");
		} else {
			app.frameSet().openFrame("config");
		}
		app.sqlRun().flush();
	} catch(Exception e) {
		e.printStackTrace();
		MailExpDialog dialog = new MailExpDialog(null, "OffstageArts", e);
		dialog.setVisible(true);
		System.exit(-1);
	}
}


	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		int ctType;
		String configName;
		if (args.length == 0) {
			ctType = FrontApp.CT_CONFIGCHOOSE;
			configName = null;
		} else {
			if ("--demo".equals(args[0])) {
				ctType = FrontApp.CT_DEMO;
				configName = null;		// Means use default
			} else if ("--oalaunch".equals(args[0])) {
				ctType = FrontApp.CT_OALAUNCH;
				configName = args[1];
			} else {
				ctType = FrontApp.CT_CONFIGSET;
				configName = args[0];
			}
		}
		
		launch(ctType, configName);
    }

}
