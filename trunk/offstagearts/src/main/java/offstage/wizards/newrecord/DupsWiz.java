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
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.newrecord;

import citibob.swing.html.HtmlWiz;
import javax.swing.*;
import offstage.*;
import citibob.sql.*;
import offstage.devel.gui.DevelModel;

/**
 *
 * @author citibob
 */
public class DupsWiz extends HtmlWiz {

/** Should this Wiz screen be cached when "Back" is pressed? */
public boolean getCacheWiz() { return false; }

/**
 * Creates a new instance of NewRecordWiz2 
 */
public DupsWiz(java.awt.Frame owner, SqlRunner str, FrontApp fapp, String idSql)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner, "Possible Duplicate Name", true);

	
	IDListViewer listView = new IDListViewer();
	DevelModel dmod = new DevelModel(fapp);
	listView.initRuntime(str, dmod,
		idSql, null, fapp);
//		fapp.getGuiRunner(), fapp.getSwingerMap());
//	html.getMap().put("idlistviewer", listView);
	html.addWidget("idlistviewer", listView);
	addSubmitButton("dontadd", "Don't Add");
	addSubmitButton("addanyway", "Add Anyway");
	this.setSize(new java.awt.Dimension(750, 550));
	loadHtml();
}


public static void main(String[] args)
throws Exception
{
	JFrame f = new JFrame();
	f.setVisible(true);
	InitWiz wiz = new InitWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	System.exit(0);
}
}
