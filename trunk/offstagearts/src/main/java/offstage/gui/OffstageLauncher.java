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
import citibob.task.BatchRunnable;
import java.io.IOException;
import offstage.FrontApp;
import offstage.config.*;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 */
public class OffstageLauncher {

protected OffstageGui offstageGui;
protected ConsoleFrame consoleFrame;




    /** Creates a new instance of FrameSetX */
    public OffstageLauncher() throws Exception {
//new com.Ostermiller.util.CSVPrinter(System.out);

//		ConnPool pool = offstage.db.DB.newConnPool();
//		Connection dbb = pool.checkout();
//
//		// Get database version
//		Statement st = dbb.createStatement();
//		OffstageVersion.fetchDbVersion(st);
//		
//// TODO: This should not be needed.  But run for now until upgrade is in place.
//st.execute("update entities set primaryentityid=entityid where primaryentityid is null");
//		st.close();
//		pool.checkin(dbb);

//		SqlBatchSet str = new SqlBatchSet();
		
		
//		FrontApp app = new FrontApp(pool, consoleFrame.getDocument());
		final FrontApp app = new FrontApp(); //new File("/export/home/citibob/svn/offstage/config"));
		boolean resGood = app.checkResources();
		app.initWithDatabase();
		
		if (resGood) {
			app.getFrameSet().openFrame("maintenance");
		} else {
			app.getFrameSet().openFrame("config");
		}
		app.getBatchSet().flush();
    }


	
	public static void main(String[] args) throws Exception
    {
		System.setProperty("swing.metalTheme", "ocean");
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		
		try {
			new OffstageLauncher();
//OffstageResSet.main(args);
		} catch(Exception e) {
			e.printStackTrace();
			MailExpDialog dialog = new MailExpDialog(null, "OffstageArts", e);
			dialog.setVisible(true);
			System.exit(-1);
		}
    }

}
