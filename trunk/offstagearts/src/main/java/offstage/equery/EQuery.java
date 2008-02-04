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
import citibob.types.JType;
import citibob.types.JavaJType;
import com.Ostermiller.util.CSVParser;
import java.sql.*;
import java.io.*;
import offstage.db.*;

public class EQuery extends Query
{

// Info on the query
ArrayList<EClause> clauses = new ArrayList();
java.util.Date lastUpdatedFirst;
java.util.Date lastUpdatedNext;
// ============================================

public void setLastUpdatedFirst(java.util.Date dt)
	{ this.lastUpdatedFirst = dt; }
public void setLastUpdatedNext(java.util.Date dt)
	{ this.lastUpdatedNext = dt; }
public java.util.Date getLastUpdatedFirst()
	{ return this.lastUpdatedFirst; }
public java.util.Date getLastUpdatedNext()
	{ return this.lastUpdatedNext; }


/** Inserts clause before clause #ix */
public void insertClause(int ix, EClause c)
	{ clauses.add(ix, c); }
public void appendClause(EClause c)
	{ clauses.add(c); }
public EClause removeClause(int ix)
	{ return (EClause)clauses.remove(ix); }
public EClause getClause(int n)
{
	return (EClause)clauses.get(n);
}

public int getNumClauses()
	{ return clauses.size(); }

public ArrayList<EClause> getClauses()
	{ return clauses; }
// -----------------------------------------------
/** @param viewName The user's view of this column name */
public String getSql(SqlCol c, Element e, String viewName)
throws IOException
{
	if ("=".equals(e.comparator) && e.value == null) {
		// Ferret out "is null" and "is not null""
		return e.colName.toString() + " is null";
	} else if ("<>".equals(e.comparator) && e.value == null) {
		// Ferret out "is null" and "is not null""
		return e.colName.toString() + " is not null";
	} else if (("in".equals(e.comparator) || "not in".equals(e.comparator)) &&
	String.class.isAssignableFrom(e.value.getClass())) {
		// Handle in lists for strings
		StringBuffer sql = new StringBuffer(e.colName.toString() + " " + e.comparator + " (");
		String[] ll = ((String)(e.value)).trim().split(",");
		if (ll.length == 0) return "false";
		for (int i=0; ;) {
			sql.append(SqlString.sql(ll[i].trim()));
			if (++i >= ll.length) {
				sql.append(")");
				break;
			}
			sql.append(",");
		}
		return sql.toString();
	} else if (("in file".equals(e.comparator) || "not in file".equals(e.comparator))) {
		String vals = readCSVColumn((File)e.value, viewName, c.getType());
		
		// Remove "file" from end of string
		String comp = e.comparator;
		int space = comp.lastIndexOf(' ');
		comp = comp.substring(0,space);
		
		return e.colName.toString() + " " + comp + " (" + vals + ")";
	} else {
		return e.colName.toString() + " " + e.comparator + " " +
			" (" + c.toSql(e.value) + ")";
	}
}
// -----------------------------------------------
public String getWhereSql(QuerySchema schema, ConsSqlQuery sql, EClause clause)
throws IOException
{
	List elements = clause.elements;
	if (elements.size() == 0) return null;		// Degenerate clause
	StringBuffer ewhere = null;
	for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
		Element e = (Element)jj.next();
		ColName cn = e.colName;
		QuerySchema.Col qsc = (QuerySchema.Col) schema.getCol(cn);
		SqlCol c = qsc.col;
		addTableInnerJoin(schema, sql, cn);
		if (ewhere == null) ewhere = new StringBuffer("(");
		else ewhere.append(" and\n");
		ewhere.append(getSql(c, e, qsc.viewName));
	}
	ewhere.append(")");
	return ewhere.toString();
}


/** @param primaryOnly Select only head of household (dinstinct primaryentityid)? */
public String getSql(QuerySchema schema, EClause clause, boolean primaryOnly)
throws IOException
{
	if (clause.elements.size() == 0) return null;
	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	sql.addTable("entities as main");
	String ewhere = getWhereSql(schema, sql, clause);
	sql.addWhereClause("(" + ewhere + ")");
	if (primaryOnly) {
		sql.addColumn("main.primaryentityid as id");
		sql.setDistinct(true);
	} else {
		sql.addColumn("main.entityid as id");
	}
	sql.addWhereClause("not main.obsolete");
	sql.setDistinct(true);
	String ssql = sql.getSql();
//System.out.println("ssql = " + ssql);
	return ssql;

}

/** @param primaryOnly Select only head of household (dinstinct primaryentityid)? */
public String getSql(QuerySchema schema, boolean primaryOnly)
throws IOException
{
	boolean first = true;
	StringBuffer sql = new StringBuffer();
	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
		EClause clause = (EClause)ii.next();
		String csql = getSql(schema, clause, primaryOnly);
		if (csql == null) continue;
		if (clause.type == EClause.ZERO) continue;		// Clause temporary disabled
		if (!first) sql.append(clause.type == EClause.ADD ? "\n    UNION\n" : "\n    EXCEPT\n");
		sql.append("(" + csql + ")");
		first = false;
	}
	return sql.toString();
}

//// -----------------------------------------------
///** Creates a standard SqlQuery out of the data in this query. */
//public void writeSqlQuery(QuerySchema schema, ConsSqlQuery sql)
//{
//	String cwhere = "(1=0";
//	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
//		EClause clause = (EClause)ii.next();
//		List elements = clause.elements;
//		if (elements.size() == 0) continue;
//		StringBuffer ewhere = null;
//		for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
//			Element e = (Element)jj.next();
//			ColName cn = e.colName;
//System.out.println("cn = " + cn);
//			QuerySchema.Col qsc = (QuerySchema.Col) schema.getCol(cn);
//System.out.println("qsc = " + qsc);
//			Column c = qsc.col;
//			addTableOuterJoin(schema, sql, cn);
////			if (!sql.containsTable(e.colName.getTable())) {
////				String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
////				sql.addWhereClause(joinClause);
////				sql.addTable(cn.getTable());
////			}
//			if (ewhere == null) ewhere = new StringBuffer("(");
//			else ewhere.append(" and\n");
//			if (clause.type == EClause.SUBTRACT) {
//				// subtle outer join semantics issue...
//				ewhere.append(e.colName.toString() + " is not null and ");
//			}
//			ewhere.append(e.colName.toString() + " " + e.comparator + " " +
//				" (" + c.getType().toSql(e.value) + ")");
//		}
//		ewhere.append(")");
//		String joiner = (clause.type == EClause.ADD ? "or" : "and not");
//		cwhere = "(" + cwhere + ") " + joiner + " \n" + ewhere.toString();
//	}
//	cwhere = cwhere + ")";
//	sql.addWhereClause(cwhere);
//	
//	// Add where clause for lastupdated date range
//	if (lastUpdatedFirst != null) sql.addWhereClause(
//		"main.lastupdated >= " + SqlTimestamp.gmt(lastUpdatedFirst));
//	if (lastUpdatedNext != null) sql.addWhereClause(
//		"main.lastupdated < " + SqlTimestamp.gmt(lastUpdatedNext));
//}
//// ------------------------------------------------------
//// ------------------------------------------------------
//public String getSql(QuerySchema qs)
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
/** Returns the mailing id */
public void makeMailing(SqlRunner str, String queryName, EQuerySchema schema,
final UpdRunnable rr) throws SQLException, IOException
{
	String eqXml = toXML();
	String eqSql = getSql(schema, true);

	// Create the mailing list and insert EntityID records
	DB.w_mailingids_create(str, queryName, eqXml, eqSql, rr);
}
// ------------------------------------------------------

static final int STRING = 0;
static final int INTEGER = 1;
static final int NUMBER = 2;
/** @param file the CSV file to read.  (FUTURE: work on .xls as well)
 @param colName name of column in file to read out.
 @param sqlType Type that column should be */
public static String readCSVColumn(File file, String colName, JType sqlType)
throws IOException
{
	int type;
	
	// Figure out which of three types we handle
	Class klass = sqlType.getObjClass();
	if (String.class.isAssignableFrom(klass)) {
		type = STRING;
	} else if (Integer.class.isAssignableFrom(klass)) {
		type = INTEGER;
	} else if (Number.class.isAssignableFrom(klass)) {
		type = NUMBER;
	} else throw new IOException("Invalid SqlType " + sqlType + " (" + sqlType.getClass().getName() + ") for CSV column");
	
	CSVParser csv = new CSVParser(new FileReader(file));
	try {
		String[] ll;
		String[] headers;
		int col;
		Set<String> vals = new TreeSet();

		// Search for the headers
		for (;;) {
			if ((ll = csv.getLine()) == null) return null;
			if (ll.length == 0) continue;

			// Assume first non-zero line is the header
			headers = ll;
			break;
		}

		// Find the column # that's the column we want
		for (col=0; ; ++col) {
			if (col >= headers.length) throw new IOException("Column " + colName + " not available in CSV file " + file);
			if (colName.equals(headers[col])) break;
		}

		// Collect values
		for (;;) {
			if ((ll = csv.getLine()) == null) break;
			if (ll.length <= col) continue;
			String sval = ll[col].trim();
			if (type == STRING) {
				sval = SqlString.sql(sval);
			} else {
//				sval = sval.replace(",", "");
				if (type == INTEGER) {
					try {
						sval = SqlInteger.sql(Integer.parseInt(sval));
					} catch(NumberFormatException e) {
						// Ignore any non-integers in the column
						sval = null;
					}
				} else if (type == NUMBER) {
					try {
						Double.parseDouble(ll[col]);		// try to parse
					} catch(NumberFormatException e) {
						// Ignore any unparseable numbers
						sval = null;
					}
				}
			}
			if (sval != null) vals.add(sval);
		}

		// Convert to list for inclusion in SQL
		int nval = 0;
		StringBuffer sb = new StringBuffer();
		for (String sval : vals) {
			if (nval++ > 0) sb.append(',');
			if (nval % 10 == 0) sb.append('\n');
			sb.append(sval);
		}
		return sb.toString();
	} finally {
		csv.close();
	}
}
// ------------------------------------------------------
// ------------------------------------------------------
public static void main(String[] args) throws Exception
{
	File f = new File("/export/home/citibob/x.csv");
	String s = readCSVColumn(f, "entityid", JavaJType.jtInteger);
System.out.println(s);
}
}
