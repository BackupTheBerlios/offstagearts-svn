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
import java.sql.*;
import java.io.*;
import com.thoughtworks.xstream.*;
import offstage.db.TestConnPool;

/**
 *
 * @author citibob
 */
public abstract class Query {

//protected QuerySchema schema;
//
//protected Query(QuerySchema schema)
//{ this.schema = schema; }

/** Used in constructing queries... */
protected void addTableOuterJoin(QuerySchema schema, ConsSqlQuery sql, ColName cn)
{
	String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
	String tabString = " left outer join " + cn.getTable() + " on (" + joinClause + ")";
	if (!sql.containsTable(tabString)) {
		sql.addTable(tabString);
	}
}
/** Used in constructing queries... */
protected void addTableInnerJoin(QuerySchema schema, ConsSqlQuery sql, ColName cn)
{
	String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
	String tabString = " inner join " + cn.getTable() + " on (" + joinClause + ")";
	if (!sql.containsTable(tabString)) {
		sql.addTable(tabString);
	}
}
///** Creates a standard ConsSqlQuery out of the data in this query. */
//public abstract void writeSqlQuery(QuerySchema schema, ConsSqlQuery sql);
// ------------------------------------------------------
public abstract String getSql(QuerySchema qs, boolean primaryOnly)
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
public static Query fromXML(String squery)
{
	if (squery == null) return null;
	
	Object obj = null;
	try {
		StringReader fin = new StringReader(squery);
		XStream xs = new QueryXStream();
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

public String toXML()
{
	// Serialize using XML
	StringWriter fout = new StringWriter();
	XStream xs = new QueryXStream();
	try {
		ObjectOutputStream oos = xs.createObjectOutputStream(fout);
		oos.writeObject(this);
		oos.close();
	} catch(IOException e) {}	// won't happen
	return fout.getBuffer().toString();
}

}
