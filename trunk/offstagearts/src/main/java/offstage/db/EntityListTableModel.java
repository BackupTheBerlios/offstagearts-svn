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

import citibob.swing.*;
import java.sql.*;
import citibob.sql.SqlTableModel;
import citibob.sql.*;

/**
 *
 * @author citibob
 */
public class EntityListTableModel extends SqlTableModel {


public EntityListTableModel(SqlTypeSet tset)
{ super(tset); }
//	super();
//	setPrototypes(new String[] {"101010", "organizations", "Johan Sebastian Bach"});
//}
// --------------------------------------------------
/** idSql is Sql statement to select a bunch of IDs */
private void addAllRows(SqlRunner str, String idSql, String orderBy) throws SQLException
{
	DB.rs_entities_namesByIDList(str, idSql, orderBy, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws SQLException {
		setColHeaders(rs);
		addAllRows(rs);
		rs.close();
	}});
}
// --------------------------------------------------
/** Hardwire the column names, so they can exist even before data has been put in. */
public String getColumnName(int columnIndex) 
{
	switch(columnIndex) {
		case 0 : return "entityid";
		case 1 : return "relation";
		case 2 : return "name";
	}
	return null;
}
public int getColumnCount()
{ return 3; }
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
public void setRows(SqlRunner str, String idSql, String orderBy) throws java.sql.SQLException
{
	setRowCount(0);
	addAllRows(str, idSql, orderBy);
}
// --------------------------------------------------
public int getEntityID(int row)
{
	Object obj = getValueAt(row,0);
//System.out.println("getEntityID: obj = " + obj);
	Integer ii = (Integer)obj;
	return ii.intValue();
}
}
