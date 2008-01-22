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
package offstage.equery;

import java.util.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.typed.*;
import java.sql.*;
import offstage.schema.OffstageSchemaSet;
import offstage.schema.OrgSchema;
import offstage.schema.NotesSchema;
import offstage.schema.PersonsSchema;
import offstage.schema.PhonesSchema;
import offstage.schema.EventsSchema;
import offstage.schema.DonationsSchema;
import offstage.schema.EntitiesSchema;
import citibob.types.*;
import offstage.equery.*;

public class QuerySchema
{

// Info on the fields we can process; set up by initializer
KeyedModel cols;	// Equery.ColName --> Col
JEnum colsJType;

//HashMap cols = new HashMap();			// Maps "table.col" -> Col
//LinkedList colList = new LinkedList();	// Holds same columns as cols, in order of insertion
HashMap tabs = new HashMap();	// Maps "table" -> String
HashMap typeComparators = new HashMap();	// Maps SqlType --> list of comparators
// -----------------------------------------------
public static class Col
{
	public ColName cname;
//	public String table;
	public Column col;
	public JEnum comparators;
	
	
	String viewName;
	public void setViewName(String vn)
		{ viewName = vn; }

//	private String viewName = null;					// Name user knows this column by
//	void setViewName(String s)
//		{ this.viewName = s; }
//	boolean hasViewName()
//		{ return viewName != null; }
//	public String getViewName()
//	{
//		if (viewName == null) return "---" + col.
//				table + "." + col.getName();
//		return viewName;
//	}
	public String toString()
		{ return (viewName != null ? viewName : cname.toString()); }
}
public static class Tab
{
	public Schema schema;
	public String table;
	public String joinClause;
}
// -----------------------------------------------
public JEnum getColsJType() { return colsJType; }

//private String colKey(String table, String col)
//	{ return table + "." + col; }
public Tab getTab(String table)
	{ return (Tab)tabs.get(table); }
//public Col getCol(String table, String scol)
//	{ return (Col)cols.get(colKey(table, scol)).obj; }
public Col getCol(ColName cname)
{
	if (cname == null) return null;
	return (Col)cols.get(cname).obj;
}
//public Iterator colIterator()
//	{ return colList.iterator(); }
// --------------------------------------------------
//private DbKeyedModel newGroupTypeKeyedModel(Statement st, String table)
//throws SQLException
//{
//	return new DbKeyedModel(st, null, table, "groupid", "name", "name");
//}
private void addTypeComparator(Class klass, String[] vals)
{
	typeComparators.put(klass, new JEnum(new KeyedModel(vals)));
}
// --------------------------------------------------
protected void addSchema(Schema sc, String joinClause, String table)
{
	if (table == null) table = sc.getDefaultTable();
	Tab tab = new Tab();
	tab.schema = sc;
	tab.joinClause = joinClause;
	tab.table = table;
	tabs.put(table, tab);
	for (int i=0; i<sc.getColCount(); ++i) {
		Col col = new Col();
		col.col = sc.getCol(i);
		Class colClass = col.col.getType().getClass();
		col.comparators = (JEnum)typeComparators.get(colClass);
		ColName cname = new ColName(table,  col.col.getName());
		col.cname = cname;
//		col.table = table;
//if (cname.stable.equals("classes")) System.err.println("Adding to Schema: " + cname);
		cols.addItem(cname, col);
	}
}
protected void addSchema(Schema sc, String joinClause)
	{ addSchema(sc,joinClause,null); }

// --------------------------------------------------------------------

/** Sets up column name aliases, and removes all non-aliased coumns. */
protected void doAlias(String[] alias)
{
	// Set aliases
	KeyedModel newCols = new KeyedModel();
	for (int i=0; i<alias.length; i+=2) {
		ColName cname = new ColName(alias[i]);
		String vname = alias[i+1];
//System.out.println((cols.get(cname).getClass()));
System.out.println("Looking in schema: " + cname + "(size = " + cols.getItemMap().size());
		Col col = (Col)cols.get(cname).obj;
		if (col == null) continue;
		col.setViewName(vname);
		newCols.addItem(col.cname, col);
	}

	// Add null column name
	Col c = new Col();
	c.cname = new ColName("", "");
	c.setViewName("<null>");
	newCols.addItem(null, c);
	
	// Remove non-aliased columns
	cols = newCols;
	colsJType = new JEnum(cols);
//	for (Iterator ii=cols.values().iterator(); ii.hasNext(); ) {
//		Col col = (Col)ii.next();
//		if (!col.hasViewName()) ii.remove();
//	}
}
// --------------------------------------------------------
protected QuerySchema()
{
	addTypeComparator(SqlBool.class, new String[] {"="});
	addTypeComparator(SqlDate.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	addTypeComparator(SqlInteger.class, new String[] {"=", "in file", "not in file", ">", "<", ">=", "<=", "<>"});
	addTypeComparator(SqlNumeric.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	addTypeComparator(SqlEnum.class, new String[] {"=", "<>"});
	addTypeComparator(SqlString.class, new String[] {"=", "in", "not in", "in file", "not in file", "<>", "ilike", "not ilike", "similar to", "not similar to"});
	addTypeComparator(SqlTimestamp.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	cols = new KeyedModel();
	colsJType = new JEnum(cols);
}
}
