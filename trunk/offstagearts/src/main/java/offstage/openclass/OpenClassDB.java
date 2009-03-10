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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.openclass;

import citibob.sql.RsTasklet;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.ansi.SqlInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 *
 * @author citibob
 */
public class OpenClassDB {


static String updateEntSql(String scol, String sncol)
{
	return
		" update _c\n" +
		" set " + scol + " = _ent.entityid\n" +
		" from _ent\n" +
		" where _c.meetingid = _ent.meetingid\n" +
		" and _c.courserole = _ent.courserole;\n" +
		" \n" +
		" update _c\n" +
		" set " + sncol + " = xx.n\n" +
		" from (\n" +
		" 	select _ent.meetingid,_ent.courserole,count(*) as n\n" +
		" 	from _ent\n" +
		" 	group by meetingid,courserole\n" +
		" ) xx where _c.meetingid = xx.meetingid and _c.courserole = xx.courserole;\n";
}

/** Figure out who is teacher and pianist for a class meeting.
 * SQL will create the table _c. */
public static SqlSet classLeadersSql(String meetingIdSql, String courseroleIdSql)
 {

	return new SqlSet(
		// Create temporary tables
		" create temporary table _c (meetingid int, courserole int,\n" +
		" mainid int, nmain int,\n" +
		" subid int, nsub int,\n" +
		" mainname varchar(100), subname varchar(100),\n" +
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
		updateEntSql("mainid", "nmain") +
		" delete from _ent;\n" +

		// Look in subs
		" insert into _ent\n" +
		" 	select _c.meetingid,_c.courserole, s.entityid\n" +
		" 	from _c\n" +
		" 	inner join meetings m on (_c.meetingid = m.meetingid)\n" +
		" 	inner join subs s on (s.courserole = _c.courserole" +
		"	and s.meetingid = m.meetingid and s.subtype = '+');\n" +
		
		// Use data from _ent table to update our main table.
		updateEntSql("subid", "nsub") +
		" drop table _ent;\n" +
		
		// Clean up our table at the end
		" update _c set mainid = null where nmain > 1;\n" +
		" update _c set subid = null where nsub > 1;\n" +
	
		// Set names
		" update _c set mainname = displayname\n" +
		" from teachers t" +
		" where _c.mainid = t.entityid;\n" +
	
		" update _c set subname = displayname\n" +
		" from teachers t" +
		" where _c.subid = t.entityid;\n" +
		
		" update _c set mainname = lastname\n" +
		" from entities t" +
		" where _c.mainid = t.entityid and _c.mainname is null;\n" +
		
		" update _c set subname = lastname\n" +
		" from entities t" +
		" where _c.subid = t.entityid and _c.subname is null;\n",
		
		// sql
		"",
		
		// postSql
		" drop table _c;");	
}

/** Calculates the price of an open class.  Assumes table _c (classLeaderSql) exists.
 Discounts student on both the main teacher and the substitute teacher.
 @returns ocdisccatid, dollar-discount (off of original full price) */
public static TreeMap<Integer,Double> getOCDiscounts(SqlRun str,
Integer meetingid,
//String sqlToday,					// Date of class we're looking at, SQL string representation
final double fullPrice,
Integer mainid, Integer subid,		// teachers
Integer studentid)
{
	// Percent in each category we should be discount.
	// Allow discounts for either main teacher or sub.
	String sql =
		// KLUDGE: Set up studio/teacher percentages
		// Use primary percentage
		" create temporary table _alloc " +
			" (ocdisccatid int, pct float, primary key(ocdisccatid,pct));\n" +
			
		" insert into _alloc\n" +
		" select ocdisccatid, teachers.ocpct\n" +
		" from ocdisccatids,\n" +
		" teachers\n" +
		" where ocdisccatids.name = 'teacher'\n" +
		" and teachers.entityid = " + mainid + ";\n" +
	
		" insert into _alloc\n" +
		" select id.ocdisccatid, 1-pct" +
		" from _alloc, ocdisccatids id" +
		" where id.name = 'studio';\n" +

		// rss[0]: (ocdisccatid, allocpct, discpct)
		// Hybrid discount schedule to apply to this (student,teacher,sub) combo
		// Allocation schedule based on MAIN teacher, not substitute teacher
//		" create temporary table _alloc\n"  +
//		" (ocdisccatid int, pct float, primary key(ocdisccatid,pct));\n" +
//		" \n" +
//		" insert into _alloc values (1,.55);\n" +
//		" insert into _alloc values (2,.45);\n" +
		" \n" +
		" select _alloc.ocdisccatid,_alloc.pct as allocpct, xx.pct as discpct\n" +
		" from _alloc\n" +
		" inner join (\n" +
			  // Get available discounts.
			  // Use discounts for main or sub teacher.
		"     select amt.ocdisccatid, max(amt.pct) as pct\n" +
		"     from meetings m, ocdiscs d\n" +
		"     inner join ocdiscids did on (d.ocdiscid = did.ocdiscid)\n" +
		"     inner join ocdiscidsamt amt on (amt.ocdiscid = did.ocdiscid)\n" +
		"     --inner join _alloc on (amt.ocdisccatid = _alloc.ocdisccatid)\n" +
		"     where d.entityid = " + SqlInteger.sql(studentid) + "\n" +
		"     and (did.teacherid = " + SqlInteger.sql(mainid) +
				(subid == null ? "" : " or did.teacherid = " + SqlInteger.sql(subid)) +
				" or did.teacherid is null)\n" +
		"     and (did.dstart <= m.dtstart or did.dstart is null)\n" +
		"     and (did.dend + cast('1 day' as interval) >= m.dtstart or did.dend is null)\n" +
		"     and (d.dstart <= m.dtstart or d.dstart is null)\n" +
		"     and (d.dend + cast('1 day' as interval) >= m.dtstart or d.dend is null)\n" +
		"     and amt.ocdisccatid <> 0\n" +
		"     and m.meetingid = " + meetingid + "\n" +
		"     group by amt.ocdisccatid\n" +
		" ) xx on (_alloc.ocdisccatid = xx.ocdisccatid);\n" +
		" drop table _alloc;\n";
	
	final TreeMap<Integer,Double> ret = new TreeMap();
	str.execSql(sql, new RsTasklet() {
	public void run(ResultSet rs) throws SQLException {
		// rss[0]: dollar discount
		while (rs.next()) {
System.out.println("Discount in ocdisccatid=" + rs.getInt("ocdisccatid") + " = " +
rs.getDouble("allocpct") + " * " + rs.getDouble("discpct"));
			double dollars = fullPrice *
				rs.getDouble("allocpct") * rs.getDouble("discpct");
			dollars = .01 * (double)((int)(dollars * 100.0));		// Round down the discounts
			ret.put(rs.getInt("ocdisccatid"), dollars);
		}
	}});		
		
	return ret;
		
}


}
