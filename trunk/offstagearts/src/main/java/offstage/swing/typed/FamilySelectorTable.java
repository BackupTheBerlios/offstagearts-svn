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
package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

/**
 * Allows users to select other people from within a family.
 * @author  citibob
 */
public class FamilySelectorTable extends IdSqlTable
{

	
//public void initRuntime(citibob.app.App app) //SqlRun str, FullEntityDbModel dm)
//{
//	super.initRuntime(app);
//}
		
/** Overrides setting the value.  NOTE: getValue() won't necessarily return
 the value that setValue() just set.  It will always return the head of household
 of the value just set. */
public void setValue(final Object o)
{
//	if (o == null) {
//		getSelectionModel().clearSelection();
//		return;
//	}
////	int row = rowOfValue(o);
////	if (row >= 0) {
////		// Only set value by user...
//////		this.getSelectionModel().setSelectionInterval(row,row);
////	} else {
//	
//	// User is setting the value; we should change it normally
//	if (inSelect) return;
//
//	// Machine is setting "value", it really means it wants to
//	// change entities...
//	app.runApp(new StRunnable() {
//	public void run(java.sql.SqlRun str) throws Exception {
//		// We don't have the value in our table; re-load
//		setPrimaryEntityID(st, (Integer)o);			
//	}});
////	}
}

//int primaryEntityID;
public void setHeadofID(SqlRun str, int headID)
//throws SQLException
{
//System.out.println("***** FamilySelectorTable.setPrimaryEntityID(" + primaryEntityID + ")");
	SqlSet groups = DB.listRelGroupSql("headof", -1, headID);
	SqlSet ssql = new SqlSet(groups,
		" select xx.entityid1 as id," +
		" case when head then 0 else 1 end as sort" +
		" from (" + groups.getSql() + ") xx\n");
	executeQuery(str, ssql, true, "sort, name");
	
////int primaryEntityID;
//	executeQuery(str,
//		" select pe.entityid from entities pe, entities pq" +
//		" where pq.entityid = " + SqlInteger.sql(primaryEntityID) +
//		" and pe.primaryentityid = pq.primaryentityid" +
//		" and not pe.obsolete",
//		"isprimary desc, name");
}
// ===========================================================
//
//	
//public static void main(String[] args) throws Exception
//{	
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	SqlRun str = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//	
//	javax.swing.JFrame frame = new javax.swing.JFrame();
//	FamilySelectorTable table = new FamilySelectorTable();
//	table.initRuntime(fapp);
//	table.setPreferredSize(new Dimension(400,300));
//	JScrollPane pane = new JScrollPane();
//		pane.add(table);
//	JPanel panel = new JPanel();
//		panel.add(pane);
//	frame.getContentPane().add(panel);
//	frame.pack();
//	table.setPrimaryEntityID(st, 12633);
//	frame.setVisible(true);
//}

	
}
