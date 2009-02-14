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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
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
OutputStream out = new FileOutputStream("oa.log");
PrintStream pout = new PrintStream(out);
System.setOut(pout);
System.setErr(pout);

		// Find the zip file to read for the configuration
		File configsFile;
		File propsFile = null;
		boolean delete;
		if (args.length > 0) {
			configsFile = new File(args[0]);
			delete = false;
		} else {
			configsFile = new File(System.getProperty("user.home"), "offstagearts_config.zips");
			delete = true;
		}
		
		// Read the configuration
		System.out.println("configsFile: " + configsFile);
		if (!configsFile.exists()) {
			System.out.println("Configs file does not exist: " + configsFile);
			System.exit(-1);
		}
		Config config = MultiConfig.loadFromFile(configsFile);
		
		// Load the props file to maybe set the name
		String fileName = configsFile.getPath();
		int dot = fileName.lastIndexOf('.');
		if (dot >= 0) {
			propsFile = new File(fileName.substring(0,dot) + ".properties");
			if (propsFile.exists()) {
				Properties props = new Properties();
				InputStream in = new FileInputStream(propsFile);
				props.load(in);
				in.close();
				
				String configName = props.getProperty("config.name");
				config.setName(configName);
			}
		}
		

		
		
		// Delete the file, now that we've read it
		if (delete) {
			configsFile.delete();
			propsFile.delete();
		}
		
		// Launch the program!
		ConfigMaker cmaker = new MultiConfigMaker(config);
		FrontApp.launch(cmaker);
    }

}
