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
 * Query.java
 *
 * Created on October 10, 2006, 6:15 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.equery;

import java.util.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.types.KeyedModel;
import java.sql.*;
import java.io.*;
import com.thoughtworks.xstream.*;

/**
 *
 * @author citibob
 */
public abstract class Query {

public static final int DISTINCT_ENTITYID = 0;
public static final int DISTINCT_HEADID = 1;
public static final int DISTINCT_PARENT1ID = 2;
public static final int DISTINCT_PARENT2ID = 3;
public static final int DISTINCT_BOTHPARENTSID = 4;
public static final int DISTINCT_PAYERID = 5;
public static final int DISTINCT_ALLADULTSID = 6;
public static final KeyedModel distinctKmodel = KeyedModel.intKeys(
		"Main Person", "Head of Household", "Parent1", "Parent2",
		"Both Parents", "Payer", "All Adults");

//protected QuerySchema schema;
//
//protected Query(QuerySchema schema)
//{ this.schema = schema; }

///** Used in constructing queries... */
//protected void addTableOuterJoin(QuerySchema schema, ConsSqlQuery sql, ColName cn)
//{
//	String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
//	String tabString = " left outer join " + cn.getTable() + " on (" + joinClause + ")";
//	if (!sql.containsTable(tabString)) {
//		sql.addTable(tabString);
//	}
//}
///** Used in constructing queries... */
//protected void addTableForColumn(QuerySchema schema, ConsSqlQuery sql, ColName cn)
//{
////	String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
//	String tabString = " inner join " + cn.getTable() + " on (" + joinClause + ")";
//	if (!sql.containsTable(tabString)) {
//		sql.addTable(tabString);
//	}
//}
/** Used in constructing queries... */
protected void addTable(QuerySchema schema, ConsSqlQuery sql, String tableName)
{

	QuerySchema.Tab tab = schema.getTab(tableName);
	if (sql.getTables(tableName) == null) {
		// This table hasn't been added yet
		
		// Add dependencies (circular dependencies will be broken)
		if (tab.requiredTables != null) {
			for (String req : tab.requiredTables) addTable(schema, sql, req);
		}

		// Add this table (AFTER dependencies; required for SQL)
//		tab.addTable(sql);
		sql.addTable(tableName, null, SqlQuery.JT_INNER, tab.joinClause);
		if (tab.columns != null) sql.addColumn(tab.columns);
	}
}
///** Creates a standard ConsSqlQuery out of the data in this query. */
//public abstract void writeSqlQuery(QuerySchema schema, ConsSqlQuery sql);
// ------------------------------------------------------
public abstract String getSql(QuerySchema qs) 
throws IOException;
//{
//	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
//	sql.addTable("entities as main");
//	this.writeSqlQuery(qs, sql);
//	sql.addColumn("main.entityid as id");
//	sql.addWhereClause("not main.obsolete");
//	sql.setDistinct(true);
//	String ssql = sql.getSql();
////System.out.println("ssql = " + ssql);
//	return ssql;
//}
// ------------------------------------------------------
/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public static Query fromXML(QuerySchema schema, String squery)
{
	if (squery == null) return null;
	
	Object obj = null;
	try {
		StringReader fin = new StringReader(squery);
		XStream xs = new QueryXStream(schema);
		ObjectInputStream ois = xs.createObjectInputStream(fin);
		obj = ois.readObject();
	} catch(ClassNotFoundException e) {
		return null;
//		throw new IOException("Class Not Found in Serialized File");
	} catch(com.thoughtworks.xstream.io.StreamException se) {
		return null;
//		throw new IOException("Error reading serialized file");
	} catch(IOException e) {}	// won't happen
	
	if (obj == null) {
		return null;
	} else if (!(obj instanceof Query)) {
		return null;
//		throw new IOException("Wrong object of class " + obj.getClass() + " found in WizQuery file");
	} else {
		return (Query)obj;
	}
}

public String toXML(QuerySchema schema)
{
	// Serialize using XML
	StringWriter fout = new StringWriter();
	XStream xs = new QueryXStream(schema);
	try {
		ObjectOutputStream oos = xs.createObjectOutputStream(fout);
		oos.writeObject(this);
		oos.close();
	} catch(IOException e) {}	// won't happen
	return fout.getBuffer().toString();
}

}
