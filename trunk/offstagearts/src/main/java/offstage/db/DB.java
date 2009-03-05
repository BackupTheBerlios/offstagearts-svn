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

package offstage.db;

import citibob.sql.SqlSet;
import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;
import citibob.util.IntVal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class DB {

// -------------------------------------------------------------------------------
//public static IntVal getPrimaryEntityID(SqlRun str, int eid)
//// throws SQLException
//{
//	final IntVal ival = new IntVal();
//	String sql =
//		"select primaryentityid from entities where entityid = " + eid;
//	str.execSql(sql, new RsTasklet2() {
//	public void run(SqlRun str, ResultSet rs) throws Exception {
//		rs.next();
//		ival.val = rs.getInt(1);
//	}});
//	return ival;
//}
// -------------------------------------------------------------------------------
/** Creates a temporary table full of entity id's from an SQL query designed
 to select those IDs. */
public static void createIDList(SqlRun str, String idSql, String idTable)
//throws SQLException
{
	String sql =
		" create temporary table " + idTable + " (id int);\n" +
		" delete from " + idTable + ";\n" +
		" insert into " + idTable + " (id) " + idSql + ";\n";
//System.out.println(sql);	
	str.execSql(sql);
}
// -------------------------------------------------------------------------------
/** Creates a temporary table full of entity id's from a list of IDs. */
public static void createIDList(SqlRun str, int[] ids, String idTable)
//throws SQLException
{
	StringBuffer sbuf = new StringBuffer();
	sbuf.append(
		" create temporary table " + idTable + " (entityid int);\n" +
		" COPY " + idTable + " (entityid) FROM stdin;\n");
	for (int i=0; i<ids.length; ++i) sbuf.append("" + ids[i] + "\n");
	sbuf.append("\\.\n");
	str.execSql(sbuf.toString());
}
// -------------------------------------------------------------------------------
/** Counts the number of items in an ID table. */
//"select entityid from entities where lastname = 'Fischer'"
//"select e.entityid from entities e, donations d where e.entityid = d.entityid and d.amount > 500"

public static String sqlCountIDList(String idSql)
{
	String sql = "select count(*) from (" + idSql + ") xx";
	return sql;
}

public static IntVal countIDList(final String retVar, SqlRun str, String idSql)
//throws SQLException
{
	final IntVal ival = new IntVal();
	String sql = sqlCountIDList(idSql);
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		rs.next();
		ival.val = rs.getInt(1);
	}});
	return ival;
}
// -------------------------------------------------------------------------------
public static String dbversion(Statement st)
throws SQLException
{
	ResultSet rs = st.executeQuery("select major,minor,rev from dbversion");
//	String version = 
//	citibob.sql.SqlQuery.
	return null;
}
// --------------------------------------------------
/** Re-encrypts all encrypted data in the database, after a master key has been changed. */
public static void rekeyEncryptedData(SqlRun str, offstage.crypt.KeyRing kr)
{

}
// --------------------------------------------------
/** Given something the user typed into a simple search box, generate a SQL search query. */
public static String simpleSearchSql(String text, Integer dbid)
{
	return simpleSearchSql(text, dbid, "", "");
}
/** Only gives names registered in a particular term */
public static String registeredSearchSql(String text, Integer dbid, int termid)
{
	return simpleSearchSql(text, dbid, ", termregs",
		" and persons.entityid = termregs.entityid and termregs.groupid = " + SqlInteger.sql(termid));
}
protected static String simpleSearchSql(String text, Integer prefDbid, String join, String whereClause)
{
	if (text == null) return null;
	text = text.trim();
	if ("".equals(text)) return null;		// no query
	
	int space = text.indexOf(' ');
	int comma = text.indexOf(',');
	int at = text.indexOf('@');
	boolean allNumeric = true;
	boolean someNumeric = false;
	for (int i=0; i<text.length(); ++i) {
		char c = text.charAt(i);
		if (c < '0' || c > '9') {
			allNumeric = false;
			break;
		} else someNumeric = true;
	}
	
	String whereDbid = (prefDbid == null ? " " : " and persons.dbid = " + prefDbid + " ");

	if ("*".equals(text)) {
		return "select entityid from persons where not obsolete " + whereDbid;

	} else if (allNumeric) {
		// entityid
		return "select persons.entityid from persons" + join +
			" where persons.entityid = " + text;
//			+ whereDbid;
	} else if (someNumeric) {
		// Search by address
		return "select persons.entityid from persons" + join +
			" where (" +
			"address1 ilike " + SqlString.sql('%' + text.trim() + '%') +
			" or address2 ilike " + SqlString.sql('%' + text.trim() + '%') +
			" ) and not obsolete" + whereClause + whereDbid;
			
	} else if (at >= 0) {
		return "select persons.entityid from persons" + join +
			" where email ilike " + SqlString.sql('%' + text.trim() + '%')
			+ whereDbid + whereClause;
	} else if (comma >= 0) {
		// lastname, firstname
		String lastname = text.substring(0,comma).trim();
		String firstname = text.substring(comma+1).trim();
		String idSql = "select persons.entityid from persons " + join +
			" where (" +
			" firstname ilike " + SqlString.sql('%' + firstname + '%') +
			" and lastname ilike " + SqlString.sql('%' + lastname + '%') +
			" ) and not obsolete" + whereClause + whereDbid;
		return idSql;
	} else if (space >= 0) {
		// firstname lastname
		String firstname = text.substring(0,space).trim();
		String lastname = text.substring(space+1).trim();
		String idSql = "select persons.entityid from persons" + join +
			" where (" +
			" firstname ilike " + SqlString.sql('%' + firstname + '%') +
			" and lastname ilike " + SqlString.sql('%' + lastname + '%') +
			" ) and not obsolete" + whereClause + whereDbid;
		return idSql;
	} else {
		String ssearch = SqlString.sql(text, false);
		String idSql = "select persons.entityid from persons " + join +
			" where (" +
			" firstname ilike " + SqlString.sql('%' + ssearch + '%') +
			" or lastname ilike " + SqlString.sql('%' + ssearch + '%') +
			" or orgname ilike " + SqlString.sql('%' + ssearch + '%') +
			" or email ilike " + SqlString.sql('%' + ssearch + '%') +
			" or url ilike " + SqlString.sql('%' + ssearch + '%') +
			" ) and not obsolete" + whereClause + whereDbid;
		return idSql;
	}
}
// --------------------------------------------------
/** Convenience method for compatibility */
public static IntVal getPrimaryEntityID(SqlRun str, int eid)
{
	return getOneOf(str, "headof", -1, eid);
}
/**
 * Finds the "one" side of a one-to-many relationship, given the "many" side.
 * @param temporalid And ID for time-varying relationships (eg, a term ID
 * for term-specific relationships).  Should be -1 for permanent relationships
 * @param entityid1
 * @param relName Name of the relationship in the relids table.  Eg: "headof"
 * @return
 */
public static IntVal getOneOf(SqlRun str, String relName, int temporalid, int entityid1)
{
	final IntVal ival = new IntVal();
	// Find the headof for this record (replace null with self)
	String sql =
		" select\n" +
		" rels.entityid0,\n" +
		" case when rels.entityid0 is null then e.entityid else rels.entityid0 end as entityid0_notnull,\n" +
		" e.entityid as entityid1,\n" +
		" e.obsolete\n" +
		" from entities e\n" +
		" left outer join rels on (e.entityid = rels.entityid1 " +
			" and rels.temporalid=" + temporalid + ")\n" +
		" left outer join relids on (rels.relid = relids.relid " +
			" and relids.name=" + SqlString.sql(relName) + ")\n" +
		" where e.entityid=" + entityid1;
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		rs.next();
		ival.val = rs.getInt("entityid0_notnull");
	}});
	return ival;
}

/**
 * <p>Produces the set of IDs e1 such that (e0, e1) is a valid relationship
 * in relName (with temporalid), and (e0, entityid1) is also a valid relationship.
 * If (e0,entityid1) does not exist, then (entityid1, entityid1) is assumed.</p>
 * <p> Informally: Lists the others in the same household/payer group/etc.</p>
 * @param relName
 * @param temporalid
 * @param entityid1
 * @return
 */
public static SqlSet listRelGroupSql(String relName, int temporalid, int entityid1)
{
	return new SqlSet(
		// pre SQL
		// Figure out our head of household (_r.entityid0)
		" create temporary table _r (relid int, temporalid int, entityid0 int, entityid1 int);\n" +
		" insert into _r (relid, temporalid, entityid1)\n" +
		" 	select relid," + temporalid + ", " + entityid1 + "\n" +
		" 	from relids where name=" + SqlString.sql(relName) + ";\n" +
		" update _r set entityid0 = rels.entityid0\n" +
		" 	from rels\n" +
		" 	where rels.relid = _r.relid\n" +
		" 	and rels.temporalid = _r.temporalid\n" +
		" 	and rels.entityid1 = _r.entityid1;\n" +
		" update _r set entityid0 = entityid1 where entityid0 is null;\n" +
		" \n" +
		// Find all members of household _r.entityid0
		" create temporary table _group (entityid0 int, entityid1 int, obsolete1 bool);\n" +
		" insert into _group (entityid1)\n" +
		" 	(select rels.entityid1 from rels,_r\n" +
		" 	where rels.entityid0 = _r.entityid0\n" +
		" 	and rels.temporalid = _r.temporalid\n" +
		" 	and rels.relid = _r.relid\n" +
		" 		union\n" +
		" 	select _r.entityid0 from _r);\n" +
		" update _group set entityid0 = rels.entityid0\n" +
		" 	from rels,_r\n" +
		" 	where rels.entityid1 = _group.entityid1\n" +
		" 	and rels.relid = _r.relid\n" +
		" 	and rels.temporalid = _r.temporalid;\n" +
		" drop table _r;" +
		" update _group set entityid0 = entityid1 where entityid0 is null;\n" +
//		" update _group set obsolete0 = e.obsolete from entities e where _group.entityid0 = e.entityid;\n" +
		" update _group set obsolete1 = e.obsolete from entities e where _group.entityid1 = e.entityid;\n",

		// SQL
		" select case when entityid0=entityid1 then true else false end as head,\n" +
		" _group.entityid1, _group.obsolete1\n" +
		" from _group\n" +
		" where entityid0=entityid1 or not _group.obsolete1\n" +
		" order by head desc\n",
//		" select case when entityid0=entityid1 then true else false end as head,\n" +
//		" _group.entityid1, _group.obsolete1\n" +
//		" from _group\n" +
//		" where entityid0=entityid1 or not _group.obsolete1\n" +
//		" order by head desc;\n",
		
		// post SQL
		" drop table _group;\n");
}


}
