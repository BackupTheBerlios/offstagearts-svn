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
 * IdSqlEntityTableModel.java
 *
 * Created on August 9, 2007, 1:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.swing.typed;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Given an idSql statement, produces a table model of those people
 * @author citibob
 */
public class IdSqlTableModel extends RSTableModel
{	
		
public IdSqlTableModel()
{
	// PostgreSQL doesn't properly return data types of headings above, given
	// the computed columns.  So we must set the column types ourselves.
	setSchema(new ConstSchema(new Column[] {
		new SqlCol("entityid", new SqlInteger(true)),
		new SqlCol("dotdotdot", new SqlString(false)),
		new SqlCol("name", new SqlString(true)),
		new SqlCol("tooltip", new SqlString(true)),
		new SqlCol("email", new SqlString(true))
	}));
}


//public void executeQuery(SqlRun str, String idSql) throws SQLException {
//	executeQuery(st, idSql, "name");
//}

public void executeQuery(SqlRun str, SqlSet idSsql, String orderBy)
{
	executeQuery(str, idSsql, false, orderBy);
}
public void executeQuery(SqlRun str, SqlSet idSsql, boolean hasSortCol, String orderBy)
{
	// Convert text to a search query for entityid's
	if (idSsql == null) return;		// no query
	if (orderBy == null) orderBy = "name";
	
	// Search for appropriate set of columns, given that list of IDs.
	String ids = str.getTableName("_ids");
	SqlSet ssql = new SqlSet(idSsql,
		(hasSortCol ?
			" create temporary table " + ids + " (id int, sort int);\n" +
			" insert into " + ids + " (id, sort) " + idSsql.getSql() + ";\n"
			:
			" create temporary table " + ids + " (id int);\n" +
			" insert into " + ids + " (id) " + idSsql.getSql() + ";\n"),
			
		" select p.entityid," +
		" '...' as dotdotdot, " +
		" (case when lastname is null then '' else lastname || ', ' end ||\n" +
		" case when firstname is null then '' else firstname || ' ' end ||\n" +
		" case when middlename is null then '' else middlename end ||\n" +
		" case when orgname is null then '' else ' (' || orgname || ')' end ||\n" +
		" case when obsolete then '*' else '' end\n" +
		" ) as name,\n" +
		" ('<html>' ||" +
		" case when city is null then '' else city || ', ' end ||" +
		" case when state is null then '' else state end || '<br>' ||" +
		" case when occupation is null then '' else occupation || '<br>' end ||" +
		" case when email is null then '' else email || '' end ||" +
		" '</html>') as tooltip,\n" +
		" p.email as email\n" +
//		" p.entityid = p.primaryentityid as isprimary" +
		" from persons p, " + ids + "" +
		" where p.entityid = " + ids + ".id" +
		" order by " + orderBy + ";",

		" drop table " + ids + "");
	super.executeQuery(str, ssql);
}

/** Constructs a string representing the email addresses of everyone
 * in the IdSqlTableModel. */
public String getEmailList()
{
	// email -> Name mapping
	LinkedHashMap<String, String> emailMap = new LinkedHashMap();
	
	// Get our set of email->Name mappings
	int nameCol = this.findColumn("name");
	int emailCol = this.findColumn("email");
	for (int row=0; row<getRowCount(); ++row) {
		String name = (String)getValueAt(row, nameCol);
		String email = (String)getValueAt(row, emailCol);
		if (email == null) continue;
		
		// Scan through possibility of compoint emails in one field
		String[] emails = email.split(",");
		for (int i=0; i<emails.length; ++i) {
			String em = emails[i].trim();
			if (em.length() == 0) continue;

			// We have a name/email pair.  Add it in!
			emailMap.put(em, name);
		}
	}

	// Scan through the map to generate emails
	if (emailMap.size() == 0) return "";
	StringBuffer ret = new StringBuffer();
	Iterator<Map.Entry<String,String>> ii = emailMap.entrySet().iterator();
	for (;;) {
		// Add the name/email pair
		Map.Entry<String,String> entry = ii.next();
		String email = entry.getKey();
		String name = entry.getValue();
		ret.append(name + " <" + email + ">");
		
		// Append the comma
		if (!ii.hasNext()) break;
		ret.append(", ");
	}


	return ret.toString();
}
}
