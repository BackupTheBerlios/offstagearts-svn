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
 * EntityListTableModel.java
 *
 * Created on June 4, 2005, 11:26 PM
 */

package offstage.db;

import citibob.jschema.Column;
import citibob.jschema.ConstSchema;
import citibob.jschema.SqlCol;
import citibob.swing.*;
import java.sql.*;
import citibob.sql.SqlTableModel;
import citibob.sql.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class EntityListTableModel extends SqlTableModel {


public EntityListTableModel(SqlTypeSet tset)
{
	super(tset);
	setSchema(new ConstSchema(new Column[] {
		new SqlCol("entityid", new SqlInteger(false)),
		new SqlCol("relation", new SqlInteger(true)),
		new SqlCol("name", new SqlString(true))
	}));
}
public void setIdSql(String idSql, String orderBy)
{
	if (orderBy == null) orderBy = "relation, name";
	sql =
		" create temporary table _ids (id int); delete from _ids;\n" +

		" delete from _ids;\n" +

		" insert into _ids (id) " + idSql + ";\n" +
		
		" (select o.entityid, 'organizations' as relation, name as name" +
		" , o.entityid = o.primaryentityid as isprimary" +
		" from organizations o, _ids" +
		" where o.entityid = _ids.id\n" +
		"   union\n" +
		" select p.entityid, 'persons' as relation," +
		" (case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end ||" +
		" case when orgname is null then '' else ' (' || orgname || ')' end) as name" +
		" , p.entityid = p.primaryentityid as isprimary" +
		" from persons p, _ids" +
		" where p.entityid = _ids.id" +
		" ) order by " + orderBy + ";\n" +
		
		" drop table _ids";
	
}

//	super();
//	setPrototypes(new String[] {"101010", "organizations", "Johan Sebastian Bach"});
//}
// --------------------------------------------------
///** idSql is Sql statement to select a bunch of IDs */
//private void addAllRows(SqlRun str, String idSql, String orderBy) throws SQLException
//{
//	DB.rs_entities_namesByIDList(str, idSql, orderBy, new RsTasklet2() {
//	public void run(SqlRun str, ResultSet rs) throws SQLException {
//		executeQuery(rs);
////		setRowsAndCols(rs);
////		setColHeaders(rs);
////		addAllRows(rs);
////		rs.close();
//	}});
//}
// --------------------------------------------------
///** Hardwire the column names, so they can exist even before data has been put in. */
//public String getColumnName(int columnIndex) 
//{
//	switch(columnIndex) {
//		case 0 : return "entityid";
//		case 1 : return "relation";
//		case 2 : return "name";
//	}
//	return null;
//}
//public int getColumnCount()
//{ return 3; }
// --------------------------------------------------
/** Appends a row in the data */
/*
public void addRow(Statement st, ResultSet rs) throws java.sql.SQLException
{
	String ids = rs.getObject(1).toString();
	addRows(st,ids);
}
 */
// --------------------------------------------------
/** Add data from a result set */
/*
public void addAllRows(Statement st, ResultSet rs) throws java.sql.SQLException
{	
	// Get the list of IDs as a String
	String ids = "-1";
	while (rs.next()) ids = ids + ", " + rs.getObject(1);
	addRows(st,ids);
}
 */
// --------------------------------------------------
//public void setRows(SqlRun str, String idSql, String orderBy) throws java.sql.SQLException
//{
//	setRowCount(0);
//	addAllRows(str, idSql, orderBy);
//}
// --------------------------------------------------
public int getEntityID(int row)
{
	Object obj = getValueAt(row,0);
//System.out.println("getEntityID: obj = " + obj);
	Integer ii = (Integer)obj;
	return ii.intValue();
}
}
