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
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.openclass;

import citibob.swing.html.HtmlWiz;
import javax.swing.*;
import offstage.*;
import citibob.sql.*;
import offstage.swing.typed.EntitySelector;

/**
 *
 * @author citibob
 */
public class SelectCourseWiz extends HtmlWiz {

/** Should this Wiz screen be cached when "Back" is pressed? */
public boolean getCacheWiz() { return false; }

/**
 * Creates a new instance of NewRecordWiz2 
 */
public SelectCourseWiz(java.awt.Frame owner, SqlRun str, FrontApp fapp)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner);
	this.setSize(500, 650);

	EntitySelector selector = new EntitySelector();
		selector.initRuntime(fapp);
	addWidget("entityid", selector);
	
	addSubmitButton("newteacher", "Create New Teacher");
	loadHtml();
}
}
