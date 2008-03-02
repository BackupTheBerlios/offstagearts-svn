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
 * JMBT.java
 *
 * Created on July 5, 2005, 7:38 PM
 */

package offstage.db;

import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.util.prefs.*;
import offstage.config.*;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class DB {

// -------------------------------------------------------------------------------
public static ConnPool newConnPool(Properties props)
throws java.util.prefs.BackingStoreException, java.sql.SQLException, ClassNotFoundException
{

final Properties p2 = new Properties();
final String url;

	Class.forName(props.getProperty("db.driverclass", null));
	p2.setProperty("user", props.getProperty("db.user", null));

	// PostgreSQL interprets any setting of the "ssl" property
	// as a request for SSL.
	// See: http://archives.free.net.ph/message/20080128.165732.7c127d6b.en.html
	String sssl = props.getProperty("db.ssl", "false");
	boolean ssl = (sssl.toLowerCase().equals("true"));
	if (ssl) p2.setProperty("ssl", "true");
	
	String pwd = props.getProperty("db.password", null);
	if (pwd != null) p2.setProperty("password", pwd);

	url = "jdbc:" + props.getProperty("db.drivertype", null) + "://" +
		props.getProperty("db.host", null) +
		":" + props.getProperty("db.port", null) +
		"/" + props.getProperty("db.database", null);
	return new RealConnPool(url, p2);
}

//		// Open the Database
//		Preferences dbPref = OffstageVersion.prefs.node("db");
//		Preferences dbGuiPref = OffstageVersion.prefs.node("db/gui");
//		DBPrefsDialog d = new DBPrefsDialog(null, dbPref, dbGuiPref);
//		d.setVisible(true);
//		if (!d.isOkPressed()) {	// User cancelled DB open
//			System.exit(0);
//		}
//		ConnPool pool = d.newConnPool();
//		return pool;
//}
// -------------------------------------------------------------------------------
///** Gets the next value from a sequence. */
//public static int r_nextval(SqlRunner str, String sequence) throws SQLException
//{
//	return SQL.readInt(st, "select nextval('" + sequence + "')");
//}
// -------------------------------------------------------------------------------
//public static int w_mailingids_create(SqlRunner str, String eqXml, String eqSql)
//throws SQLException
//{
//	int groupID = r_nextval(st, "groupids_groupid_seq");
//	//SQL.readInt(st, "select nextval('groupids_groupid_seq')");
//	try { st.executeUpdate("drop table _ids"); } catch(SQLException sqe) {}
//	String sql;
//	sql =
//		// Create Mailing List 
//		" insert into mailingids" + 
//		" (groupid, name, created, equery) values" + 
//		" (" + groupID + ", 'Mailing', now(),\n" + SqlString.sql(eqXml) + "\n);" + 
//
//		// Create temporary table of IDs for this mailing list
//		" create temporary table _ids (id int);" + 
//		" delete from _ids;" + 
//		" insert into _ids (id) " + eqSql + ";" +
//
//		// Insert into Mailing List
//		" insert into mailings (groupid, entityid) select " + groupID + ", id from _ids;" + 
//		" drop table _ids";
//System.out.println(sql);
//	st.executeUpdate(sql);
//	return groupID;
//}
// -------------------------------------------------------------------------------
/** @param eqSql an idSql that selects the entityids we wish to mail to.  Assumes that
 only one of each primaryentityid has already been done. */
public static void w_mailingids_create(SqlRunner str, final String queryName,
final String eqXml, final String eqSql, final UpdRunnable rr)
{
	SqlSerial.getNextVal(str, "groupids_groupid_seq");
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		final int groupID = (Integer)str.get("groupids_groupid_seq");
		
		String sql =
			// Conditionally drop our temporary able before creating it
			" select drop_table('_ids');\n" +

			// Create Mailing List 
			" insert into mailingids" + 
			" (groupid, name, created, equery) values" + 
			" (" + groupID + ", " + SqlString.sql(queryName) + ", now(),\n" + SqlString.sql(eqXml) + "\n);" + 

			// Create temporary table of IDs for this mailing list
			// These are primaryentityids (i.e. the people we REALLY want to send to)
			" create temporary table _ids (id int);" + 
			" delete from _ids;" + 
			" insert into _ids (id) " + eqSql + ";\n" +
//			" insert into _ids (id) " + removeDupsIDSql(eqSql) + ";\n" +

			// Insert into Mailing List
			" insert into mailings (groupid, entityid) select " + groupID + ", id from _ids;" + 
			" drop table _ids;\n" +

			// ========= Set addressto from multiple sources
			// 1. Try customaddressto
			"	update mailings\n" +
			"	set addressto = customaddressto\n" +
			"	from entities p\n" +
			"	where p.entityid = mailings.entityid\n" +
			"	and p.customaddressto is not null\n" +
			"	and addressto is null\n" +
			"	and mailings.groupid = " + groupID + ";\n" +

			// 2. Try pre-computed names
			"	update mailings\n" +
			"	set addressto = ename\n" +
			"	where addressto is null and ename is not null\n" +
			"	and groupid = " + groupID + ";\n" +

			// 3. Try addressto as name of person
			"	update mailings\n" +
			"	set addressto = \n" +
			"		coalesce(p.firstname || ' ', '') ||\n" +
			"		coalesce(p.middlename || ' ', '') ||\n" +
			"		coalesce(p.lastname, '')\n" +
			"	from entities p\n" +
			"	where mailings.entityid = p.entityid\n" +
			"	and mailings.groupid = " + groupID + "\n" +
			"	and addressto is null;\n" +

			// 4. Try addressto as name of organization
			"	update mailings\n" +
			"	set addressto = p.name\n" +
			"	from organizations p\n" +
			"	where mailings.entityid = p.entityid\n" +
			"	and mailings.groupid = " + groupID + "\n" +
			"	and addressto is null;\n" +

			// Set the rest of the address\n" +
			"	update mailings\n" +
			"	set address1 = e.address1,\n" +
			"	address2 = e.address2,\n" +
			"	city = e.city,\n" +
			"	state = e.state,\n" +
			"	zip = e.zip,\n" +
			"	country = e.country\n" +
			"	from entities e\n" +
			"	where mailings.entityid = e.entityid\n" +
			"	and mailings.groupid = " + groupID + ";\n";

		str.execSql(sql, new UpdRunnable() {
		public void run(SqlRunner str) throws Exception {
			if (rr != null) rr.run(str);
		}});
	}});
//System.out.println(sql);
//	st.executeUpdate(sql);
//	return groupID;
}
// -------------------------------------------------------------------------------
public static void w_mailings_makereport(SqlRunner str, int mailingID)
{
	String sql =
		" update mailings set line1=trim(addressto), line2=trim(address1), line3=trim(address2)" +
		" where groupid = " + mailingID +
		" and address1 is not null and address2 is not null\n;" +

		" update mailings set line1=null, line2=trim(addressto), line3=trim(address2)" +
		" where groupid = " + mailingID +
		" and address1 is null and address2 is not null\n;" +

		" update mailings set line1=null, line2=trim(addressto), line3=trim(address1)" +
		" where groupid = " + mailingID +
		" and address1 is not null and address2 is null\n;" +
		
		" update mailings set isgood = true where groupid = " + mailingID + ";\n" +

		" update mailings set address1=null where address1='';\n" +
		" update mailings set address2=null where address2='';\n" +
		" update mailings set zip=null where zip='';\n" +
		" update mailings set city=null where city='';\n" +
		" update mailings set state=null where state='';\n" +
		
		" update mailings set isgood = false where" +
		" groupid = " + mailingID + " and (" +
		" addressto is null" +
		" or (address1 is null and address2 is null)" +
		" or (zip is null and trim(country) = 'USA')" +
		" or city is null" +
		" or state is null" +
		");";
	str.execSql(sql);
}
// -------------------------------------------------------------------------------
public static void getPrimaryEntityID(SqlRunner str, int eid)
// throws SQLException
{
	String sql =
		"select primaryentityid from entities where entityid = " + eid;
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		rs.next();
		int pid = rs.getInt(1);
		str.put("primaryentityid", pid);
	}});
}

//public static int getTransitivePrimaryEntityID(SqlRunner str, int eid)
//throws SQLException
//{
//	int origID = eid;
//	TreeSet ids = new TreeSet();
//	for (;;) {
//		ResultSet rs = st.executeQuery(
//			"select primaryentityid from entities where entityid = " + eid);
//		rs.next();
//		int neid = rs.getInt(1);
//		rs.close();
//		if (neid == 0) return eid;	// null in SQL table
//		if (neid == eid) return eid;	// We've found the root
//		Integer Neid = new Integer(neid);
//		if (ids.contains(Neid)) return origID;	// Cycle found :-(
//		ids.add(Neid);
//		eid = neid;
//	}
//}
// -------------------------------------------------------------------------------
/** Creates a temporary table full of entity id's from an SQL query designed
 to select those IDs. */
public static void createIDList(SqlRunner str, String idSql, String idTable)
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
public static void createIDList(SqlRunner str, int[] ids, String idTable)
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

public static void countIDList(final String retVar, SqlRunner str, String idSql)
//throws SQLException
{
	String sql = sqlCountIDList(idSql);
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		rs.next();
		final int nn = rs.getInt(1);
		str.put(retVar, nn);
	}});

//System.out.println(sql);
//	ResultSet rs = st.executeQuery(sql);
//	rs.next();
//	rs.close();
//	return n;
}
// -------------------------------------------------------------------------------
///** Removes duplicates (multiple people in same home) from a list of IDs to mail... replaces with sendentityid */
//public static String removeDupsIDSql(String idSql)
//{
//	xxx
//	String sql =
//		" select distinct e.primaryentityid as id from (\n" +
//			idSql +
//		" ) xx, entities e\n" +
//		" where xx.id = e.entityid\n" +
//		" and e.sendmail"; // and e.primaryentityid is not null\n";
//	return sql;
//}
// -------------------------------------------------------------------------------
//public static String rs_entities_namesByIDList(SqlRunner str, String idSql, String orderBy, final RsRunnable rr)
////throws SQLException
//{
//	if (orderBy == null) orderBy = "relation, name";
//	String sql =
//		" create temporary table _ids (id int); delete from _ids;\n" +
//
//		" delete from _ids;\n" +
//
//		" insert into _ids (id) " + idSql + ";\n" +
//		
//		" (select o.entityid, 'organizations' as relation, name as name" +
//		" , o.entityid = o.primaryentityid as isprimary" +
//		" from organizations o, _ids" +
//		" where o.entityid = _ids.id\n" +
//		"   union\n" +
//		" select p.entityid, 'persons' as relation," +
//		" (case when lastname is null then '' else lastname || ', ' end ||" +
//		" case when firstname is null then '' else firstname || ' ' end ||" +
//		" case when middlename is null then '' else middlename end ||" +
//		" case when orgname is null then '' else ' (' || orgname || ')' end) as name" +
//		" , p.entityid = p.primaryentityid as isprimary" +
//		" from persons p, _ids" +
//		" where p.entityid = _ids.id" +
//		" ) order by " + orderBy + ";\n" +
//		
//		" drop table _ids";
//	return sql;
//}
// -------------------------------------------------------------------------------
public static String sql_entities_namesByIDList2(String idSql, String orderBy)
//throws SQLException
{
	if (orderBy == null) orderBy = "relation, name";
	String sql =
		" create temporary table _ids (id int); delete from _ids;\n" +

		" delete from _ids;\n" +

		" insert into _ids (id) " + idSql + ";\n" +
		
		" select p.entityid, 'persons' as relation," +
		" (case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end ||" +
		" case when orgname is null then '' else ' (' || orgname || ')' end) as name" +
		" , p.entityid = p.primaryentityid as isprimary" +
		", p.city, p.state, p.occupation, p.email" +
		" from persons p, _ids" +
		" where p.entityid = _ids.id" +
		" ) order by " + orderBy + ";\n" +
		
		" drop table _ids";
	return sql;
//System.out.println(sql);
//	ResultSet rs = st.executeQuery(sql);
//	return rs;
}
// -------------------------------------------------------------------------------

// -------------------------------------------------------------------------------
//public static void r_acct_balance(final String retVar, SqlRunner str,
//final int entityid, final int actypeid, final UpdRunnable rr)
////throws SQLException
//{
//	String sql;
//	
//	// TODO: We can do this in one round trip with a temporary table!!!
//	// Figure out latest balance
//	sql =
//		" select dtime, bal from acbal" +
//		" where entityid = " + SqlInteger.sql(entityid) +
//		" and actypeid = " + SqlInteger.sql(actypeid) +
//		" order by dtime desc";
//	str.execSql(sql, new RsRunnable() {
//	public void run(SqlRunner str, ResultSet rs) throws Exception {
//		double bal = 0;
//		String sdtime = null;
//		if (rs.next()) {
//			bal = rs.getDouble("bal");
//			sdtime = rs.getString("dtime");
//		}
//		rs.close();
//		final double fbal = bal;
//		
//		// Get transactions since then
//		String sql =
//			" select sum(amount) from actrans" +
//			" where entityid = " + SqlInteger.sql(entityid) +
//			" and actypeid = " + SqlInteger.sql(actypeid) +
//			(sdtime == null ? "" : " and dtime > '" + sdtime + "'");
//		str.execSql(sql, new RsRunnable() {
//		public void run(SqlRunner str, ResultSet rs) throws Exception {
//			rs.next();
//			double bal = fbal + rs.getDouble(1);
//			str.put(retVar, bal);
//			rr.run(str);
//		}});
//	}});
////	
////	rs = st.executeQuery(sql);
////	rs.next();
////	bal += rs.getDouble(1);
////	rs.close();
////	
////	return bal;
//}
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
public static void rekeyEncryptedData(SqlRunner str, offstage.crypt.KeyRing kr)
{

}
// --------------------------------------------------
/** Given something the user typed into a simple search box, generate a SQL search query. */
public static String simpleSearchSql(String text)
{
	return simpleSearchSql(text, "", "");
}
/** Only gives names registered in a particular term */
public static String registeredSearchSql(String text, int termid)
{
	return simpleSearchSql(text, ", termregs",
		" and persons.entityid = termregs.entityid and termregs.groupid = " + SqlInteger.sql(termid));
}
protected static String simpleSearchSql(String text, String join, String whereClause)
{
	if (text == null) return null;
	text = text.trim();
	if ("".equals(text)) return null;		// no query
	
	int space = text.indexOf(' ');
	int comma = text.indexOf(',');
	int at = text.indexOf('@');
	boolean numeric = true;
	for (int i=0; i<text.length(); ++i) {
		char c = text.charAt(i);
		if (c < '0' || c > '9') {
			numeric = false;
			break;
		}
	}
	
	if (numeric) {
		// entityid
		return "select persons.entityid from persons" + join +
		" where persons.entityid = " + text; // + " and not obsolete" + whereClause;
	} else if (at >= 0) {
		return "select persons.entityid from persons" + join +
			" where email ilike '%" + text.trim() + "%'" + whereClause;
	} else if (comma >= 0) {
		// lastname, firstname
		String lastname = text.substring(0,comma).trim();
		String firstname = text.substring(comma+1).trim();
		String idSql = "select persons.entityid from persons " + join + " where (" +
			" firstname ilike '%" + firstname + "%'" +
			" and lastname ilike '%" + lastname + "%'" +
			" ) and not obsolete" + whereClause;
		return idSql;
	} else if (space >= 0) {
		// firstname lastname
		String firstname = text.substring(0,space).trim();
		String lastname = text.substring(space+1).trim();
		String idSql = "select persons.entityid from persons" + join + " where (" +
			" firstname ilike '%" + firstname + "%'" +
			" and lastname ilike '%" + lastname + "%'" +
			" ) and not obsolete" + whereClause;
		return idSql;
	} else {
		String ssearch = SqlString.sql(text, false);
		String idSql = "select persons.entityid from persons " + join + " where (" +
			" firstname ilike '%" + ssearch + "%'" +
			" or lastname ilike '%" + ssearch + "%'" +
			" or orgname ilike '%" + ssearch + "%'" +
			" or email ilike '%" + ssearch + "%'" +
			" or url ilike '%" + ssearch + "%'" +
			" ) and not obsolete" + whereClause;
		return idSql;
	}
}


}



//-- Function: w_mailings_correctlist(int4, bool)
//
//-- DROP FUNCTION w_mailings_correctlist(int4, bool);
//
//CREATE OR REPLACE FUNCTION w_mailings_correctlist(int4, bool)
//  RETURNS void AS
//$BODY$DECLARE
//	vgroupid alias for $1;			-- Mailing to work on
//	vkeepnosend alias for $2;	-- Keep "no send" people from list?
//BEGIN
//	-- Clear...
//	update mailings set addressto = null
//	where groupid = vgroupid;
//
//	-- Send to the primary
//	update mailings
//	set sendentityid = e.primaryentityid, ename = null
//	from entities e
//	where mailings.entityid = e.entityid
//	and mailings.groupid = vgroupid;
//
//	-- Eliminate duplicates
//	update mailings
//	set minoid = xx.minoid
//	from (
//		select sendentityid, min(oid) as minoid
//		from mailings m
//		where m.groupid = vgroupid
//		group by sendentityid
//	) xx
//	where mailings.sendentityid = xx.sendentityid
//	and groupid = vgroupid;
//
//	delete from mailings
//	where groupid = vgroupid
//	and oid <> minoid;
//
//	-- Keep "no send" people
//	if (not vkeepnosend) then
//		update mailings
//		set groupid = -2
//		from entities e
//		where mailings.sendentityid = e.entityid
//		and not e.sendmail
//		and mailings.groupid = vgroupid;
//
//		delete from mailings where groupid = -2;
//	end if;
//	
//	-- ========= Set addressto from multiple sources
//	-- Set addressto by custom address to
//	update mailings
//	set addressto = customaddressto
//	from entities p
//	where p.entityid = sendentityid
//	and p.customaddressto is not null
//	and addressto is null
//	and mailings.groupid = vgroupid;
//
//	-- Use pre-computed names
//	update mailings
//	set addressto = ename
//	where addressto is null and ename is not null
//	and groupid = vgroupid;
//
//	-- Set addressto as name of person
//	update mailings
//	set addressto = 
//		coalesce(p.firstname || ' ', '') ||
//		coalesce(p.middlename || ' ', '') ||
//		coalesce(p.lastname, '')
//	from entities p
//	where mailings.sendentityid = p.entityid
//	and mailings.groupid = vgroupid
//	and addressto is null;
//
//	-- Set addressto as name of organization
//	update mailings
//	set addressto2 = p.name
//	from organizations p
//	where mailings.sendentityid = p.entityid
//	and mailings.groupid = vgroupid;
//--	and addressto is null;
//
//
//update mailings set addressto = addressto2
//where addressto is null;
//
//	-- ==================================
//
//	-- Set the rest of the address
//	update mailings
//	set address1 = e.address1,
//	address2 = e.address2,
//	city = e.city,
//	state = e.state,
//	zip = e.zip,
//	country = e.country
//	from entities e
//	where mailings.sendentityid = e.entityid
//	and mailings.groupid = vgroupid;
//
//	return;
//END
