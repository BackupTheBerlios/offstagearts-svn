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
 * OrgWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.frontdesk.wizards;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import offstage.schema.*;
import citibob.util.*;
import java.sql.*;
import citibob.sql.*;
import citibob.types.*;

/**
 *
 * @author citibob
 */
public class OrgWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public OrgWiz(java.awt.Frame owner, SqlRun str, citibob.app.App app)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, app.swingerMap());
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addTextField("firstname");
	addTextField("middlename");
	addTextField("lastname");
	addTextField("address1");
	addTextField("address2");
	addTextField("city");
	addTextField("state");
	addTextField("zip");
	addTextField("phone", new PhoneSwinger());
	addTextField("occupation");
	addTextField("title");
	addTextField("orgname");
	addTextField("email");
	addTextField("url");
	
//	addWidget("interestid", "groupid", app.getSchema("interests"));
	
	KeyedModel kmodel = new citibob.sql.DbKeyedModel(str, null,
		"interestids", "groupid", "name", "name");
	kmodel.addItem(null, "<No Interest Specified>");
	JKeyedComboBox interests = new JKeyedComboBox(kmodel);
	interests.setValue(null);
	addComponent("interestid", interests);

	loadHtml();
}


//public static void main(String[] args)
//throws Exception
//{
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	OrgWiz wiz = new OrgWiz(f, app);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	wiz = new OrgWiz(f, app);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
