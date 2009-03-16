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
import citibob.types.JType;
import citibob.types.JavaJType;
import com.Ostermiller.util.CSVParser;
import java.sql.*;
import java.io.*;
import offstage.db.*;
import offstage.equery.compare.Comp;

public class EQuery extends Query
{


// Info on the query
ArrayList<EClause> clauses = new ArrayList();
java.util.Date lastUpdatedFirst;
java.util.Date lastUpdatedNext;
Integer dbid = 0;			// Database to search in; default to main (default) database
int distinctType = Query.DISTINCT_HEADID;
// ============================================
/** The database we should be searching in.  null for all databases. */
public Integer getDbid() { return dbid;}
public void setDbid(Integer dbid) { this.dbid = dbid; }

public void setLastUpdatedFirst(java.util.Date dt)
	{ this.lastUpdatedFirst = dt; }
public void setLastUpdatedNext(java.util.Date dt)
	{ this.lastUpdatedNext = dt; }
public void setDistinctType(int dt)
	{ this.distinctType = dt; }
public java.util.Date getLastUpdatedFirst()
	{ return this.lastUpdatedFirst; }
public java.util.Date getLastUpdatedNext()
	{ return this.lastUpdatedNext; }
public int getDistinctType()
	{ return this.distinctType; }

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
	Comp comp = e.getComparator();
	return comp.getSql(c, e.colName.toString(), viewName, e.value);
//	if (comp == eqCP && e.value == null) {
//	if ("=".equals(e.comparator) && e.value == null) {
//		// Ferret out "is null" and "is not null""
//		return e.colName.toString() + " is null";
//	} else if ("<>".equals(e.comparator) && e.value == null) {
//		// Ferret out "is null" and "is not null""
//		return e.colName.toString() + " is not null";
//	} else if (("in".equals(e.comparator) || "not in".equals(e.comparator)) &&
//	String.class.isAssignableFrom(e.value.getClass())) {
//		// Handle in lists for strings
//		StringBuffer sql = new StringBuffer(e.colName.toString() + " " + e.comparator + " (");
//		String[] ll = ((String)(e.value)).trim().split(",");
//		if (ll.length == 0) return "false";
//		for (int i=0; ;) {
//			sql.append(SqlString.sql(ll[i].trim()));
//			if (++i >= ll.length) {
//				sql.append(")");
//				break;
//			}
//			sql.append(",");
//		}
//		return sql.toString();
//	} else if (("in file".equals(e.comparator) || "not in file".equals(e.comparator))) {
//		String vals = readCSVColumn((File)e.value, viewName, c.getType());
//		
//		// Remove "file" from end of string
//		Comp comp = e.comparator;
////		int space = comp.lastIndexOf(' ');
////		comp = comp.substring(0,space);
//		
//		return e.colName.toString() + " " + comp + " (" + vals + ")";
//	} else {
//		return e.colName.toString() + " " + e.comparator + " " +
//			" (" + c.toSql(e.value) + ")";
//	}
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
		addTable(schema, sql, cn.getTable());
		if (ewhere == null) ewhere = new StringBuffer("(");
		else ewhere.append(" and\n");
		ewhere.append(getSql(c, e, qsc.viewName));
	}
	ewhere.append(")");
	return ewhere.toString();
}


/** @param primaryOnly Select only head of household (dinstinct primaryentityid)? */
public String getSqlNoDistinct(QuerySchema schema, EClause clause)
throws IOException
{
	if (clause.elements.size() == 0) return null;
	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	sql.addTable("entities as main");
	if (this.dbid != null) sql.addWhereClause("main.dbid = " + dbid);		// For now, only select out of main database!
	String ewhere = getWhereSql(schema, sql, clause);
	sql.addWhereClause("(" + ewhere + ")");
	sql.addColumn("main.entityid as id");

//	sql.addWhereClause("not main.obsolete");
//	sql.setDistinct(true);
	String ssql = sql.getSql();
//System.out.println("ssql = " + ssql);
	
	// Add the group by and order by stuff
	StringBuffer sb = new StringBuffer(ssql + "\n");
	sb.append(" group by main.entityid\n");
	if (clause.minDups == null && clause.maxDups == null) {
		// Nothing here is equivalent to distinct
//		sb.append(" having count(*) = 1");		// Equivalent to distinct
	} else if (clause.minDups == null) {
		sb.append(" having count(*) <= " + clause.maxDups);
	} else if (clause.maxDups == null) {
		sb.append(" having count(*) >= " + clause.minDups);		
	} else {
		if (clause.minDups.intValue() == clause.maxDups.intValue()) {
			sb.append(" having count(*) = " + clause.minDups);
		} else if (clause.minDups.intValue() > clause.maxDups.intValue()) {
			// Trivially false
			return "false";
		} else {
			sb.append(" having count(*) >= " + clause.minDups +
				" and count(*) <= " + clause.maxDups);
		}
	}
	return sb.toString();

}



/** Returns the SQL for one clause of the query. */
public String getSql(QuerySchema schema, EClause clause)
throws IOException
{
	String sql0 = getSqlNoDistinct(schema, clause);
	if (sql0 == null) return null;

	if (distinctType == DISTINCT_ENTITYID) return sql0;
	
	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	sql.addTable("entities as main");
	sql.addTable("(" + sql0 + ")", "yy", SqlQuery.JT_INNER,
		"main.entityid = yy.id");
	sql.addWhereClause("not main.obsolete");
	sql.setDistinct(true);
	
	//	sql.setDistinct(true);			// Seems like a good idea whether or not we reduce by household/etc
	switch(distinctType) {
		case DISTINCT_HEADID :
			sql.addTable("rels_o2m", "heads", sql.JT_LEFT_OUTER,
				" heads.relid = (select relid from relids where name = 'headof')\n" +
				" and heads.entityid1 = main.entityid");
			sql.addColumn(
				"(case when heads.entityid0 is null then main.entityid else heads.entityid0 end) as id");
//			sql.addColumn("main.primaryentityid as id");
//			sql.addWhereClause("main.primaryentityid is not null");
		break;
		case DISTINCT_PARENT1ID :
			sql.addTable("rels_o2m", "heads", sql.JT_INNER,
				" heads.relid = (select relid from relids where name = 'parent1of')\n" +
				" and heads.entityid1 = main.entityid");
			sql.addColumn("heads.entityid0 as id");
//			sql.addColumn("main.parent1id as id");
//			sql.addWhereClause("main.parent1id is not null");
		break;
		case DISTINCT_PAYERID :
			sql.addTable("rels_o2m", "heads", sql.JT_INNER,
				" heads.relid = (select relid from relids where name = 'payerof')\n" +
				" and heads.temporalid = termregs.groupid\n" +
				" and heads.entityid1 = main.entityid");
			sql.addColumn("heads.entityid0 as id");
			addTable(schema, sql, "termregs");
			
//			sql.addColumn("termregs.payerid as id");
//			sql.addWhereClause("termregs.payerid is not null");
//			addTable(schema, sql, "termregs");
		break;
//		case DISTINCT_ENTITYID :
//		default :
//			sql.addColumn("main.entityid as id");
//		break;
	}
	
//	sql.addWhereClause("not main.obsolete");
//	sql.setDistinct(true);
	String ssql = sql.getSql();
//System.out.println("ssql = " + ssql);
	return ssql;
}

///** @param primaryOnly Select only head of household (dinstinct primaryentityid)? */
//public String getSql(QuerySchema schema, EClause clause)
//throws IOException
//{
//	if (clause.elements.size() == 0) return null;
//	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
//	sql.addTable("entities as main");
//	String ewhere = getWhereSql(schema, sql, clause);
//	sql.addWhereClause("(" + ewhere + ")");
////	sql.setDistinct(true);			// Seems like a good idea whether or not we reduce by household/etc
//	switch(distinctType) {
//		case DISTINCT_PRIMARYENTITYID :
//			sql.addColumn("main.primaryentityid as id");
//		break;
//		case DISTINCT_PARENT1ID :
//			sql.addColumn("main.parent1id as id");
//		break;
//		case DISTINCT_PAYERID :
//			sql.addColumn("termregs.payerid as id");
//			addTable(schema, sql, "termregs");
//// For now, just hack in join to termregs; do this properly later, with
//// transitive table-join requirements in the EQuerySchema.
////			if (!sql.containsTable("termregs"))		// doesn't work, since table names not kept.
////			sql.addTable("termregs", "_tr", SqlQuery.JT_INNER,
////				"_tr.entityid = termenrolls.entityid and _tr.groupid = termenrolls.groupid");
//		break;
//		case DISTINCT_ENTITYID :
//		default :
//			sql.addColumn("main.entityid as id");
//		break;
//	}
//
//	sql.addWhereClause("not main.obsolete");
////	sql.setDistinct(true);
//	String ssql = sql.getSql();
////System.out.println("ssql = " + ssql);
//	
//	// Add the group by and order by stuff
//	StringBuffer sb = new StringBuffer(ssql + "\n");
//	sb.append(" group by id\n");
//	if (clause.minDups == null && clause.maxDups == null) {
//		sb.append(" having count(*) = 1");		// Equivalent to distinct
//	} else if (clause.minDups == null) {
//		sb.append(" having count(*) <= " + clause.maxDups);
//	} else if (clause.maxDups == null) {
//		sb.append(" having count(*) >= " + clause.minDups);		
//	} else {
//		if (clause.minDups.intValue() == clause.maxDups.intValue()) {
//			sb.append(" having count(*) = " + clause.minDups);
//		} else if (clause.minDups.intValue() > clause.maxDups.intValue()) {
//			// Trivially false
//			return "false";
//		} else {
//			sb.append(" having count(*) >= " + clause.minDups +
//				" and count(*) <= " + clause.maxDups);
//		}
//	}
//	return sb.toString();
//
//}


/** @param primaryOnly Select only head of household (dinstinct primaryentityid)?
 @param termid Term we're matching against (if distinctType == DISTINCT_PAYERID.  Can be -1
 for non-school queries (as long as distinctType != DISTINCT_PAYERID). */
public String getSql(QuerySchema schema)
//public String getSql(QuerySchema schema, int termid)
throws IOException
{
//	if (distinctType == DISTINCT_PAYERID && termid < 0)
//		throw new IllegalArgumentException(
//		"EQuery.getSql() does not work on DISTINCT_PAYERID without a termid.");
	
	boolean first = true;
	StringBuffer sql = new StringBuffer();
	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
		EClause clause = (EClause)ii.next();
		String csql = getSql(schema, clause);
		if (csql == null) continue;
		if (clause.type == EClause.ZERO) continue;		// Clause temporary disabled
		if (!first) sql.append(clause.type == EClause.ADD ? "\n    UNION\n" : "\n    EXCEPT\n");
		sql.append("(" + csql + ")");
		first = false;
	}
//	if (distinctType == DISTINCT_PAYERID) {
//		// Wrap WHOLE QUERY to do distinct payer
//		return
//			" select distinct tr.payerid as id\n" +
//			" from termregs tr, (" + sql.toString() + ") xx\n" +
//			" where tr.groupid = " + termid +
//			" and tr.entityid = xx.id";
//	} else {
		// Distinctification was done in individual clauses in an easier fashion.
		return sql.toString();
//	}
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
