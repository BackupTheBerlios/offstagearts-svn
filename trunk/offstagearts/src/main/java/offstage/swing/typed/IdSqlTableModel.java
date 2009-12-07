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

import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import java.awt.*;

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
		new SqlCol("isprimary", new SqlBool(true))
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
		" '</html>') as tooltip\n" +
//		" p.entityid = p.primaryentityid as isprimary" +
		" from persons p, " + ids + "" +
		" where p.entityid = " + ids + ".id" +
		" order by " + orderBy + ";",

		" drop table " + ids + "");
	super.executeQuery(str, ssql);
}

}
