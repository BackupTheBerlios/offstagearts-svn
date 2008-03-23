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
//package offstage.equery;
//
//import java.util.*;
//import citibob.sql.*;
//import citibob.sql.pgsql.*;
//import citibob.jschema.*;
//import java.sql.*;
//import java.io.*;
//import com.thoughtworks.xstream.*;
//import offstage.db.TestConnPool;
//
//public class WizQuery extends Query
//{
//
//// Info on the query
//ArrayList clauses = new ArrayList();
////java.util.Date lastUpdatedFirst;
////java.util.Date lastUpdatedNext;
//// ============================================
//
////public void setLastUpdatedFirst(java.util.Date dt)
////	{ this.lastUpdatedFirst = dt; }
////public void setLastUpdatedNext(java.util.Date dt)
////	{ this.lastUpdatedNext = dt; }
////public java.util.Date getLastUpdatedFirst()
////	{ return this.lastUpdatedFirst; }
////public java.util.Date getLastUpdatedNext()
////	{ return this.lastUpdatedNext; }
//
//
///** Inserts clause before clause #ix */
//public void insertClause(int ix, WizClause c)
//	{ clauses.add(ix, c); }
//public void appendClause(WizClause c)
//	{ clauses.add(c); }
//public WizClause removeClause(int ix)
//	{ return (WizClause)clauses.remove(ix); }
//public WizClause getClause(int n)
//	{ return (WizClause)clauses.get(n); }
//
//public int getNumClauses()
//	{ return clauses.size(); }
//
//public ArrayList getClauses()
//	{ return clauses; }
//// -----------------------------------------------
///** Creates a standard SqlQuery out of the data in this query. */
//public void writeSqlQuery(QuerySchema schema, ConsSqlQuery sql)
//{
//	String cwhere = "(1=0";
//	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
//		WizClause clause = (WizClause)ii.next();
//		ColName cn = clause.colName;
//		Column c = (((QuerySchema.Col) schema.getCol(cn)).col);
//		addTableOuterJoin(schema, sql, cn);
//		String ewhere = cn.toString() + " = " + c.getType().toSql(clause.value);
//		if (clause.firstDt != null) ewhere += " and main.lastupdated >= " + SqlTimestamp.gmt(clause.firstDt);
//		if (clause.nextDt != null) ewhere += " and main.lastupdated >= " + SqlTimestamp.gmt(clause.nextDt);
//		String joiner = (clause.type == WizClause.ADD ? " or " : " and not ");
//		cwhere = cwhere + joiner + "(" + ewhere + ")";
//	}
//	cwhere = cwhere + ")";
//	sql.addWhereClause(cwhere);
//	
////	// Add where clause for lastupdated date range
////	if (lastUpdatedFirst != null) sql.addWhereClause("main.lastupdated >= " + SqlTimestamp.sql(lastUpdatedFirst));
////	if (lastUpdatedNext != null) sql.addWhereClause("main.lastupdated < " + SqlTimestamp.sql(lastUpdatedNext));
//}
//// ------------------------------------------------------
//
//}
