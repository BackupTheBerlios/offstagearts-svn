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

import citibob.config.ConfigMaker;
import citibob.config.DialogConfigMaker;
import citibob.config.MultiConfigMaker;
import java.io.File;
import offstage.FrontApp;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 *
 * java -cp target/executable-netbeans.dir/offstagearts-1.10.23.jar offstage.launch.Custom ~/offstagearts/launchers/offstagearts-ballettheatre.jar ~/mvn/oamisc/ballettheatre/oa_jmbt/target/oa_jmbt-1.2.0.jar
 * 
 */
public class Custom {

	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		ConfigMaker cmaker;
		File launcherFile;
		if (args.length > 0) {
			launcherFile = new File(args[0]);
		} else {
			String home = System.getProperty("user.home");
//			System.out.println("Home Direcotry = " + home);
			launcherFile = new File(home, "offstagearts/launchers/offstagearts-ballettheatre.jar");
		}
//		File launcherFile = new File("/export/home/citibob/mvn/oamisc/bdw/offstagearts-bdw.jar");
//		File launcherFile = new File("/Users/citibob/offstagearts/launchers/offstagearts-bdw.jar");
		cmaker = new MultiConfigMaker(new Object[]{launcherFile});

		// Null = load from database
		// <file> = use a file
		// "<none>" = Don't use any site code
		String siteCodeFileName = null;
		if (args.length > 1) siteCodeFileName = args[1];

//		siteCodeFileName = "<none>";

		System.out.println("********************** Custom, args.length = " + args.length);
		System.out.println("Launcher = " + launcherFile + ", siteCode = " + siteCodeFileName);

		FrontApp.launch(cmaker, siteCodeFileName);
    }

}
