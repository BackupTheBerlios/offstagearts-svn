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
import citibob.multithread.*;
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
	setColHeaders(new RSTableModel.Col[] {
		new RSTableModel.Col("entityid", new SqlInteger(true)),
		new RSTableModel.Col("name", new SqlString(true)),
		new RSTableModel.Col("tooltip", new SqlString(true)),
		new RSTableModel.Col("isprimary", new SqlBool(true))
	});
}

//public void executeQuery(SqlRunner str, String idSql) throws SQLException {
//	executeQuery(st, idSql, "name");
//}
public void executeQuery(SqlRunner str, String idSql, String orderBy)
//throws SQLException
{
	// Convert text to a search query for entityid's
	if (idSql == null) return;		// no query
	if (orderBy == null) orderBy = "name";
	
	// Search for appropriate set of columns, given that list of IDs.
	String sql =
		" create temporary table _ids (id int); delete from _ids;\n" +

		" delete from _ids;\n" +

		" insert into _ids (id) " + idSql + ";\n" +

		" select p.entityid," +
		" (case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end ||" +
		" case when orgname is null then '' else ' (' || orgname || ')' end" +
		" ) as name," +
//			" city as tooltip," +
		" ('<html>' ||" +
		" case when city is null then '' else city || ', ' end ||" +
		" case when state is null then '' else state end || '<br>' ||" +
		" case when occupation is null then '' else occupation || '<br>' end ||" +
		" case when email is null then '' else email || '' end ||" +
		" '</html>') as tooltip," +
		" p.entityid = p.primaryentityid as isprimary" +
		" from persons p, _ids" +
		" where p.entityid = _ids.id" +
		" order by " + orderBy + ";" +

		" drop table _ids";
System.out.println(sql);
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws SQLException {
		setNumRows(0);
		addAllRows(rs);
		rs.close();
	}});
}

}
