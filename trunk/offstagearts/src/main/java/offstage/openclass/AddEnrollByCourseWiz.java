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

package offstage.openclass;

import citibob.swing.typed.*;
import citibob.swing.html.*;
import citibob.wizard.*;
import java.sql.*;
import citibob.sql.*;
import citibob.types.*;
import offstage.FrontApp;
import offstage.swing.typed.EntityIDDropdown;

/**
 *
 * @author citibob
 */
public class AddEnrollByCourseWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public AddEnrollByCourseWiz(java.awt.Frame owner, SqlRun str, FrontApp app, TypedHashMap v)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, app.swingerMap());
	setSize(600,460);
	final EntityIDDropdown entityid = new EntityIDDropdown();
		entityid.initRuntime(app, v.getInteger("termid"));
	
	final KeyedModel crModel = new DbKeyedModel(str, null,
		"courseroles", "courseroleid", "name", "orderid", null);
//	String sql =
//		" select name from termids where groupid = " + SqlInteger.sql(v.getInteger("termid"));
//	str.execSql(sql, new RsTasklet2() {
//	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
//		rs.next();
//		addComponent("sterm", new JTypedLabel(rs.getString("name")));	
//	}});
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		addComponent("entityid", entityid);
		addComponent("courserole", new JKeyedComboBox(crModel));
//		addComponent("courseid", new JKeyedComboBox(cModel));
		loadHtml();
	}});	


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
