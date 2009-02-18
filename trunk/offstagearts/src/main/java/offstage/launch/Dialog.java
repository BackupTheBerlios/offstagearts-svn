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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import offstage.FrontApp;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 */
public class Dialog {

	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		ConfigMaker cmaker = new DialogConfigMaker("offstage/demo");

//OutputStream out = new FileOutputStream("oa.log");
//PrintStream pout = new PrintStream(out);
//System.setOut(pout);
//System.setErr(pout);
		FrontApp.launch(cmaker);
//		int ctType;
//		String configName;
//		
//		
//		if (args.length == 0) {
//			ctType = FrontApp.CT_CONFIGCHOOSE;
//			configName = null;
//		} else {
//			if ("--demo".equals(args[0])) {
//				ctType = FrontApp.CT_DEMO;
//				configName = null;		// Means use default
//			} else if ("--oalaunch".equals(args[0])) {
//				ctType = FrontApp.CT_OALAUNCH;
//				configName = args[1];
//			} else {
//				ctType = FrontApp.CT_CONFIGSET;
//				configName = args[0];
//			}
//		}
//		
//		launch(ctType, configName);
    }

}
