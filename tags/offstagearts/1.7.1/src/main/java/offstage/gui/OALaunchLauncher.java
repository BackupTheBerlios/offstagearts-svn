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
import citibob.sql.*;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import offstage.FrontApp;
import offstage.config.*;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 */
public class OALaunchLauncher {

	public static void main(String[] args) throws Exception
    {
		System.out.println("OALaunchLauncher Started");
//		// Load up our configuration properties
//		Properties oalaunch = new Properties();
//		ClassLoader clr = OALaunchLauncher.class.getClassLoader();
//		URL url = clr.getResource("oalaunch/oalaunch.properties");
//		InputStream in = url.openStream();
//		oalaunch.load(in);
//		in.close();
//		
//		String configName = oalaunch.getProperty("config.name", "OALaunch");
		int ctType = FrontApp.CT_OALAUNCH;
		String configName = null;
		MainLauncher.launch(ctType, configName);
    }

}
