/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.openclass;

import citibob.sql.RssTasklet;
import citibob.sql.SqlRun;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 *
 * @author citibob
 */
public class OpenClassDB {
public static String classLeadersDropSql()
{
	return " drop table _c;";
		
}

//" 	select m.meetingid as id\n" +
//" 	from meetings m\n" +
//" 	inner join courseids c on (m.courseid = c.courseid)\n" +
//" 	inner join termids t on (c.termid = t.groupid)\n" +
//" 	where m.dtstart >= '2008-06-09' and m.dtstart < '2008-06-10'\n" +
//" 	and c.dayofweek >= 0\n" +
//" 	and t.termtypeid = 2\n" +

//		" (select courseroleid as courserole from courseroles\n" +
//		" where courseroleid in (2,3)) cc\n"


/** Figure out who is teacher and pianist for a class meeting.
 * SQL will create the table _c. */
public static String classLeadersSql(String meetingIdSql, String courseroleIdSql)
 {
	String updateEntSql =
		" update _c\n" +
		" set mainid = _ent.entityid\n" +
		" from _ent\n" +
		" where _c.meetingid = _ent.meetingid\n" +
		" and _c.courserole = _ent.courserole;\n" +
		" \n" +
		" update _c\n" +
		" set nmain = xx.n\n" +
		" from (\n" +
		" 	select _ent.meetingid,_ent.courserole,count(*) as n\n" +
		" 	from _ent\n" +
		" 	group by meetingid,courserole\n" +
		" ) xx where _c.meetingid = xx.meetingid and _c.courserole = xx.courserole;\n";

	return
		// Create temporary tables
		" create temporary table _c (meetingid int, courserole int,\n" +
		" mainid int, nmain int,\n" +
		" subid int, nsub int,\n" +
		" primary key(meetingid, courserole));\n\n" +
		
		" create temporary table _ent (meetingid int, courserole int, entityid int,\n" +
		" primary key(meetingid, courserole, entityid));\n\n" +
		
		// Set up the rows (meetingids and courseroles product)
		" insert into _c\n" +
		" select mm.id as meetingid, cc.id as courserole\n" +
		" from (\n" +
			meetingIdSql +
		" ) mm, (\n" +
			courseroleIdSql +
		" ) cc;\n\n" +

		// Look in enrollments
		" insert into _ent\n" +
		" 	select _c.meetingid,_c.courserole,e.entityid\n" +
		" 	from _c\n" +
		" 	inner join meetings m on (_c.meetingid = m.meetingid)\n" +
		" 	inner join enrollments e on (e.courserole = _c.courserole and e.courseid = m.courseid\n" +
		" 		and (e.dstart is null or e.dstart < m.dtnext)\n" +
		" 		and (e.dend is null or (e.dend + cast('1 day' as interval)) > m.dtstart));\n" +

		// Use data from _ent table to update our main table.
		updateEntSql +
		" delete from _ent;\n" +

		// Look in subs
		" insert into _ent\n" +
		" 	select _c.meetingid,_c.courserole, s.entityid\n" +
		" 	from _c\n" +
		" 	inner join meetings m on (_c.meetingid = m.meetingid)\n" +
		" 	inner join subs s on (s.courserole = _c.courserole" +
		"	and s.meetingid = m.meetingid and s.subtype = '+');\n" +
		
		// Use data from _ent table to update our main table.
		updateEntSql +
		" drop table _ent;\n" +
		
		// Clean up our table at the end
		"update _c set mainid = null where nmain > 1;\n" +
		"update _c set subid = null where nsub > 1;\n";
}

/** Calculates the price of an open class.  Assumes table _c (classLeaderSql) exists.
 Discounts student on both the main teacher and the substitute teacher.
 @returns ocdisccatid, pct-discount */
public static TreeMap<Integer,Double> getOCDiscounts(SqlRun str, Integer meetingid, Integer studentid)
{
	final TreeMap<Integer,Double> ret = new TreeMap();
	
	// Percent in each category we should be discount.
	// Allow discounts for either main teacher or sub.
	String sql =
		classLeadersSql(
			"select meetingid as id from meetings where meetingid = " + meetingid,
			"select courseroleid as id from courseroles where name = 'teacher'") +

			
		// KLUDGE: Set up studio/teacher percentages
		// Use primary percentage
		" create temporary table _alloc " +
			" (ocdisccatid int, pct double, primary key(ocdisccatid,pct));\n" +
			
		" insert into _alloc\n" +
		" select ocdisccatid, teachers.ocpct\n" +
		" from ocdisccatids,\n" +
		" teachers, _c, courseroles cr\n" +
		" where ocdisccatids.name = 'teacher'\n" +
		" and _c.meetingid = 2 and _c.courserole = cr.courseroleid\n" +
		" and cr.name = 'teacher'\n" +
		" and _c.mainid = teachers.mainid;\n" +
	
		" insert into _alloc\n" +
		" select id.ocdisccatid, 1-pct" +
		" from _alloc, ocdisccatids id" +
		" where id.name = 'studio';\n" +

		
		// rss[0]
		// Calculate the percentage discount
		// Allow discount if student signed up for teacher or sub
		// Select it out for display in wizard, etc.
//		" insert into subsamt (meetingid, entityid, ocdisccatid, dollars)" +
		" select " + meetingid + ", " + studentid + ", " +
		"     xx.ocdisccatid,_alloc.pct * xx.pct * cid.price as dollars" +
		" from _alloc, courseids cid, meetings m" +
		" (select ocdisccatid,max(pct) as pct\n" +
		" from ocdiscs x\n" +
		" inner join ocdiscids id on (x.ocdiscid = id.ocdiscid)\n" +
		" inner join ocdiscidsamt amt on (id.ocdiscid = amt.ocdiscid)\n" +
		" inner join _c on (_c.meetingid = " + meetingid +
			" and _c.courserole = (select courseroleid from courseroles where name='teacher')\n" +
			" and (id.teacherid is null or id.teacherid = _c.mainid or id.teacherid = _c.subid)" +
			" and _c.pct is not null)\n" +
		" where x.entityid = " + studentid +
		" group by ocdisccatid) xx\n" +
		" where xx.ocdisccatid = _alloc.ocdisccatid" +
		" and cid.courseid = m.courseid;" +
		
//		// Insert...
//		" insert into subs (meetingid, entityid, subtype, courserole)" +
//		" select " + meetingid + ", " + studentid + ", '+', courseroleid" +
//		" from courseroleids where name = 'student'" +
//		
//		" insert into subsamt (meetingid, entityid, ocdisccatid, dollars)" +
//		" select " + meetingid + ", " + studentid + ", _disc.ocdisccatid, _disc.dollars\n" +
//		" from _disc" +
//		
//		// Add the regular price
//		" insert into subsamt (meetingid, entityid, ocdisccatid, dollars)" +
//		" select " + meetingid + ", " + studentid + ", 0, price" +
		
		// rss[1]
		" select price" +
		" from courseids cid, meetings m, _c " +
		" where cid.courseid = m.courseid" +
		" and m.meetingid = _c.meetingid" +

		// Drop temp tables
		" drop table _alloc;" +
//		" drop table _c;";

		classLeadersDropSql();
	
	
	str.execSql(sql, new RssTasklet() {
	public void run(ResultSet[] rss) throws SQLException {
		// rss[0]: dollar discount
		ResultSet rs = rss[0];
		while (rs.next()) {
			ret.put(rs.getInt("ocdisccatid"), rs.getDouble("dollars"));
		}
		
		// rss[1]: main price
		rs = rss[1];
//		if (rs.next()) {
		ret.put(0, rs.getDouble(1));
//		}
	}});		
		
	return ret;
		
}


}
