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
 * OffstageReports.java
 *
 * Created on October 27, 2007, 8:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.app.*;
import java.io.*;
import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author citibob
 */
public class OffstageReports extends citibob.reports.Reports
{

public OffstageReports(App app)
{
	this.app = app;
	oofficeExe = app.getProps().getProperty("ooffice.exe");
}

public InputStream openTemplateFile(File dir, String name) throws IOException
{
	if (dir != null) return super.openTemplateFile(dir, name);

	// First: try loading external file
//	File dir = new File(System.getProperty("user.dir"), "config");
	File f = new File(app.getConfigDir().getPath() + File.separatorChar + "reports" + File.separatorChar + name);
	if (f.exists()) {
System.out.println("Loading template from filesystem: " + f);
		return new FileInputStream(f);
	}

	// File doesn't exist; read from inside JAR file instead.
//	Class klass = offstage.config.OffstageVersion.class;
//	String resourceName = klass.getPackage().getName().replace('.', '/') + "/" + name;
//	return klass.getClassLoader().getResourceAsStream(resourceName);
	String resourceName = "offstage/config/reports/" + name;
System.out.println("Loading template as resource: " + resourceName);
	return OffstageReports.class.getClassLoader().getResourceAsStream(resourceName);
}

// =============================================================

}
