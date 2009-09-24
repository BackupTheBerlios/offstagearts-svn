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
package offstage.equery;

import java.util.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.types.*;
import citibob.types.KeyedModel.Item;
import java.io.File;
import java.io.IOException;
import offstage.equery.*;
import offstage.equery.compare.BaseComp;
import offstage.equery.compare.Comp;
import offstage.equery.compare.InFileComp;
import offstage.equery.compare.JEnum_InComp;
import offstage.equery.compare.String_InComp;
import offstage.types.SqlPhone;

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
public static interface ColTyper
{
	/** Returns the type of a particular column in a particular clause in a particular element. */
	public JType getType(EClause clause, Element el, QuerySchema.Col col);
}
public static class DefaultColTyper implements ColTyper {
public JType getType(EClause clause, Element el, QuerySchema.Col col) {
	return col.col.getType();
}}
public static final ColTyper defaultColTyper = new DefaultColTyper();
// -----------------------------------------------
public static class Col
{
	public ColName cname;
//	public String table;
	public SqlCol col;
	public JEnum comparators;
	public ColTyper typer = defaultColTyper;			// How to get the type of this column.  Can create new object...
	
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
	public SqlSchema schema;
	public String table;
	public String joinClause;
	public String[] requiredTables;		// Other tables we must join when joining this one.
	public String columns;				// Columns we select when we add this table.
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
public Col getCol(String fullName)
{
	return getCol(new ColName(fullName));
}
//public Iterator colIterator()
//	{ return colList.iterator(); }
// --------------------------------------------------
//private DbKeyedModel newGroupTypeKeyedModel(Statement st, String table)
//throws SQLException
//{
//	return new DbKeyedModel(st, null, table, "groupid", "name", "name");
//}
//private void addTypeComparator(Class klass, String[] vals)
TreeMap<String,Comp> compsBySaveName = new TreeMap();
static final JType jtComp = new JavaJType(Comp.class);
private void addTypeComparator(Class klass, Comp... vals)
{
	KeyedModel kmodel = new KeyedModel();
	for (Comp c : vals) {
		kmodel.addItem(c, c.getDisplayName());
		compsBySaveName.put(c.getSaveName(), c);
	}
	typeComparators.put(klass, new JEnum(jtComp, kmodel));
}
public Comp getComp(String saveName) { return compsBySaveName.get(saveName); }
// --------------------------------------------------
public void addSchema(SqlSchema sc, String columns, String joinClause, String... requiredTables)
{
	String table = sc.getDefaultTable();
	Tab tab = new Tab();
	tab.schema = sc;
	tab.joinClause = joinClause;
	tab.table = table;
	tab.requiredTables = requiredTables;
	tab.columns = columns;
	tabs.put(table, tab);
	for (int i=0; i<sc.size(); ++i) {
		Col col = new Col();
		col.col = (SqlCol)sc.getCol(i);
		Class colClass = col.col.getType().getClass();
		col.comparators = (JEnum)typeComparators.get(colClass);
		ColName cname = new ColName(table,  col.col.getName());
		col.cname = cname;
//		col.table = table;
//if (cname.stable.equals("interests")) 
	System.out.println("Adding to SqlSchema: " + cname + " (" + col + ")");
		cols.addItem(cname, col);
	}
}

// --------------------------------------------------------------------

/** Sets up column name aliases, and removes all non-aliased coumns. */
protected void doAlias(List<String[]> aliasLists)
{
	// Set aliases
	KeyedModel newCols = new KeyedModel();
	for (String[] alias : aliasLists) {
		for (int i=0; i<alias.length; i+=2) {
			ColName cname = new ColName(alias[i]);
			String vname = alias[i+1];
	//System.out.println((cols.get(cname).getClass()));
	System.out.println("Looking in schema: " + cname + "(size = " + cols.getItemMap().size());
			Item item = cols.get(cname);
			Col col = (Col)item.obj;
			if (col == null) continue;
			col.setViewName(vname);
			newCols.addItem(col.cname, col);
		}
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

JFile jFile = new JFile(new javax.swing.filechooser.FileFilter() {
	public boolean accept(File file) { return file.getName().endsWith(".csv"); }
	public String getDescription() { return "*.csv"; }
}, new File("."), true);

/** Returns the type of a particular column in a particular clause in a particular element. */
public JType getType(EClause clause, Element el, QuerySchema.Col col)
{
	if (el.cachedValueType != null) return el.cachedValueType;
	
	Comp comp = el.getComparator();
	JType jt;
	if (comp == inFileCP || comp == ninFileCP) {
		jt = jFile;
	} else if (comp == inCP_JEnum || comp == ninCP_JEnum) {
		JEnum baseEnum = (JEnum)col.typer.getType(clause, el, col);
		JEnumMulti multi = new JEnumMulti(baseEnum.getBaseJType(),
			baseEnum.getKeyedModel(), baseEnum.getSegment());
//		jt = new ListAndRangeJType(multi);
		jt = multi;
	} else {
		// Type not cached, fetch it.
		jt = col.typer.getType(clause, el, col);
	}
	
	// Save and return
	el.cachedValueType = jt;
System.out.println("QuerySchema.getType = ) " + jt + " (" + el.colName + ")");
	return jt;
}
//				return schema.getType(getClause(rs), el, col.col);
//				return col.col.getType();
//					Clause clause, Element el, SqlCol col

// --------------------------------------------------------
protected QuerySchema()
{
//	addTypeComparator(SqlBool.class, new String[] {"="});
//	addTypeComparator(SqlDate.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
//	addTypeComparator(SqlInteger.class, new String[] {"=", "in file", "not in file", ">", "<", ">=", "<=", "<>"});
//	addTypeComparator(SqlNumeric.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
//	addTypeComparator(SqlEnum.class, new String[] {"=", "<>"});
//	addTypeComparator(SqlString.class, new String[] {"=", "in", "not in", "in file", "not in file", "<>", "ilike", "not ilike", "similar to", "not similar to"});
//	addTypeComparator(SqlTimestamp.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});

	addTypeComparator(SqlBool.class, eqCP);
	addTypeComparator(SqlDate.class, eqCP, gtCP, ltCP, geqCP, leqCP, neqCP);
	addTypeComparator(SqlInteger.class, eqCP, inFileCP, ninFileCP, gtCP, ltCP, geqCP, leqCP, neqCP);
	addTypeComparator(SqlNumeric.class, eqCP, gtCP, ltCP, geqCP, leqCP, neqCP);
	addTypeComparator(SqlEnum.class, eqCP, neqCP, inCP_JEnum, ninCP_JEnum);
	addTypeComparator(SqlString.class, eqCP,
			inCP_String, ninCP_String, inFileCP, ninFileCP,
			neqCP, ilikeCP, nilikeCP, similarCP, nsimilarCP);
	addTypeComparator(SqlPhone.class, eqCP,
			inCP_String, ninCP_String, inFileCP, ninFileCP,
			neqCP, ilikeCP, nilikeCP, similarCP, nsimilarCP);
	addTypeComparator(SqlTimestamp.class, eqCP, gtCP, ltCP, geqCP, leqCP, neqCP);
	cols = new KeyedModel();
	colsJType = new JEnum(cols);
}

// =======================================================================
// Comparators

public static final Comp eqCP = new BaseComp("=") {
protected String getSql(SqlCol sqlCol, String colName, Object value)
throws IOException
{
	if (value == null) return colName + " is null";
	return super.getSql(sqlCol, colName, value);
}};

public static final Comp neqCP = new BaseComp("<>") {
protected String getSql(SqlCol sqlCol, String colName, Object value)
throws IOException
{
	if (value == null) return colName + " is not null";
	return super.getSql(sqlCol, colName, value);
}};

public static final Comp inFileCP = new InFileComp(true);
public static final Comp ninFileCP = new InFileComp(false);

public static final Comp inCP_JEnum = new JEnum_InComp(true);
public static final Comp ninCP_JEnum = new JEnum_InComp(false);

public static final Comp inCP_String = new String_InComp(true);
public static final Comp ninCP_String = new String_InComp(false);


public static final Comp gtCP = new BaseComp(">");
public static final Comp ltCP = new BaseComp("<");
public static final Comp geqCP = new BaseComp(">=");
public static final Comp leqCP = new BaseComp("<=");

public static final Comp ilikeCP = new BaseComp("ilike");
public static final Comp similarCP = new BaseComp("silimar to");
public static final Comp nilikeCP = new BaseComp("not ilike");
public static final Comp nsimilarCP = new BaseComp("not silimar to");

}
