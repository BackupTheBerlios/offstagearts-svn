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
import citibob.sql.pgsql.SqlInteger;
import citibob.text.KeyedSFormat;
import citibob.types.*;
import javax.swing.JComponent;

/**
 *
 * @author citibob
 */
public class AddEnrollWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public AddEnrollWiz(java.awt.Frame owner, SqlRun str, citibob.app.App app, final TypedHashMap v)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, app.swingerMap());
	setSize(600,460);
	addComponent("sperson", new JTypedLabel((String)v.get("sperson")));
//	addWidget("sterm", new JTypedLabel((String)v.get("sterm")));
	
//	addWidget("courserole", new JKeyedComboBox((KeyedModel)v.get("courseroleModel"));
	final TypedWidget crWidget;
	KeyedModel crModel = new citibob.sql.DbKeyedModel(str, null,
		"courseroles", "courseroleid", "name", "orderid");
	if (v.get("courserole") == null) {
		crWidget = new JKeyedComboBox(crModel);
	} else {
		final JTypedLabel lab = new JTypedLabel();
			lab.setJType(Integer.class, new KeyedSFormat(crModel));
		str.execUpdate(new UpdTasklet() {
		public void run() throws Exception {
			lab.setValue(v.get("courserole"));
		}});
		crWidget = lab;
//		lab.setJType(String.class, new StringSFormat());
//		crWidget = lab;
//		String sql =
//			" select name from courseroles where courseroleid = " + v.getInt("courserole");
//		str.execSql(sql, new RsTasklet() {
//		public void run(java.sql.ResultSet rs) throws Exception {
//			rs.next();
//			crWidget.setValue(rs.getString("name"));
//		}});
	}
	
	String sql =
		" select courseid, c.name || ' (' || dw.shortname || ')', c.termid" +
		" from courseids c, daysofweek dw" +
		" where c.dayofweek = dw.javaid" +
		" and termid = " + v.get("termid") +
		" order by c.dayofweek, c.name, c.tstart";
	final KeyedModel cModel = new citibob.sql.DbKeyedModel(str, null, "courseids", sql);
	sql =
		" select name from termids where groupid = " + SqlInteger.sql(v.getInteger("termid"));
	str.execSql(sql, new RsTasklet2() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
		rs.next();
		addComponent("sterm", new JTypedLabel(rs.getString("name")));	
	}});
	
	final OpenClassSelector courses = new OpenClassSelector();
	courses.initRuntime(str, app, (Integer)v.getInteger("courserole"), v.getInteger("termid"));


	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		addComponent("courserole", (JComponent)crWidget);
		addComponent("courseid", courses);
		loadHtml();
	}});	


}
}
