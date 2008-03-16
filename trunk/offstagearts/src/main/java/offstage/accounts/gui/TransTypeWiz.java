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
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.accounts.gui;

import citibob.swing.typed.*;
import citibob.swing.html.*;
import citibob.wizard.*;
import citibob.app.*;
import citibob.jschema.*;
import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;

/**
 *
 * @author citibob
 */
public class TransTypeWiz extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public TransTypeWiz(java.awt.Frame owner, SqlRun str, App app, TypedHashMap v)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, app.swingerMap());

	setSize(600,460);
	String sql = "select" +
		" (case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end) as name" +
		" from entities where entityid = " + v.get("entityid");
	str.execSql(sql, new RsTasklet2() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
		rs.next();
		addComponent("name", new JTypedLabel(rs.getString("name")));	
		loadHtml();
	}});	
}

}
