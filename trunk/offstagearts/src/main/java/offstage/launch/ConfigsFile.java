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

package offstage.launch;

import citibob.config.Config;
import citibob.config.ConfigMaker;
import citibob.config.MultiConfig;
import citibob.config.MultiConfigMaker;
import java.sql.*;
import javax.swing.*;
import java.util.prefs.*;
import citibob.swing.prefs.*;
import citibob.jschema.swing.*;
import citibob.gui.*;
import citibob.sql.*;
import java.io.File;
import offstage.FrontApp;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 */
public class ConfigsFile {

	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		// Find the zip file to read for the configuration
		File configsFile;
		boolean delete;
		if (args.length > 0) {
			configsFile = new File(args[0]);
			delete = false;
		} else {
			configsFile = new File(System.getProperty("user.home"), "offstagearts.zips");
			delete = true;

////configsFile = new File("/export/home/citibob/mvn/oamisc/ballettheatre/config_lan.zips");
//configsFile = new File("/export/home/citibob/offstagearts/configs/test_ballettheatre.zips");
//delete = false;
		}
//		long modified = configsFile.lastModified();
		
		// Read the configuration
		System.out.println("configsFile: " + configsFile);
		if (!configsFile.exists()) {
			System.out.println("Configs file does not exist: " + configsFile);
			System.exit(-1);
		}
		Config config = MultiConfig.loadFromFile(configsFile);
		
		// Delete the file, now that we've read it
		if (delete) configsFile.delete();
		
		// Launch the program!
		ConfigMaker cmaker = new MultiConfigMaker(config);
		FrontApp.launch(cmaker);
    }

}
