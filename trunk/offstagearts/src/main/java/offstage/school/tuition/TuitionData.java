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

package offstage.school.tuition;

import bsh.Interpreter;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class TuitionData {

	
// ==========================================================
public int termid;
public String termName;
public String rbPlanSetClass;
//public boolean calcTuition;
public boolean calcTuition() { return rbPlanSetClass != null; }

public Map<String,DueDate> duedates;
public Map<Integer,Payer> payers;
public Map<Integer,Student> students;
public Map<Integer,Course> courses;

// ==========================================================
static SqlNumeric money = new SqlNumeric(9, 2, true);
static SqlTime time = new SqlTime(true);
static Double getMoney(ResultSet rs, String col) throws SQLException
	{ return (Double)money.get(rs, col); }
// ==========================================================
public TuitionData(SqlRun str, int termid, String payerIdSql, TimeZone tz)
//throws SQLException
{
	final SqlDate date = new SqlDate(tz, true);
	this.termid = termid;
	String sql =
		// rss[0]: Name of Term
		" select t.name, t.rbplansetclass" +
		" from termids t" +
		" where t.groupid = " + SqlInteger.sql(termid) + ";\n" +
		
		// rss[1]: DueDate
		" select id.name,dd.duedate,id.description" +
		" from duedates dd, duedateids id" +
		" where termid = " + SqlInteger.sql(termid) +
		" and dd.duedateid = id.duedateid;\n" +

		// Make temporary tables for below
		" create temporary table _payers (entityid int);\n" +
		" insert into _payers " + payerIdSql + ";\n" +
		
		" create temporary table _students (entityid int);\n" +
		" insert into _students\n" +
		" select distinct es.entityid\n" +
		" from entities_school es, entities e, _payers\n" +
		" where es.adultid = _payers.entityid\n" +
		" and e.entityid = es.entityid\n" +
		" and not e.obsolete;\n" +
		
		// Delete previous tuition records in account
		" delete from actrans using _payers,actranstypes" +
		" where actrans.entityid = _payers.entityid\n" +
		" and actrans.actranstypeid = actranstypes.actranstypeid\n" +
		" and actranstypes.name = 'tuition'\n" +
		" and actrans.termid = " + SqlInteger.sql(termid) + ";\n" +

		// Zero out previous tuitions
		" update termregs" +
		" set tuition = null, defaulttuition=null" +
		" from _students" +
		" where groupid = " + SqlInteger.sql(termid) +
		" and termregs.entityid = _students.entityid;\n" +
		
//		// rss[2]: Payers
//		" select e.entityid, e.isorg,\n" +
//		" (case when es.billingtype is null then 'y' else es.billingtype end) as billingtype\n" +
//		" from entities e left outer join entities_school es on (e.entityid = es.entityid), _payers" +
//		" where _payers.entityid = e.entityid\n" +
//		" and not e.obsolete;\n" +
		
		// rss[2]: Payers
		" select e.entityid, e.isorg, ptr.rbplan\n" +
		" from _payers,\n" +
		"     entities e, payertermregs ptr\n" +
		" where ptr.termid = " + SqlInteger.sql(termid) + "\n" +
		" and _payers.entityid = ptr.entityid\n" +
		" and _payers.entityid = e.entityid\n" +
		" and not e.obsolete;\n" +
		
		// rss[3]: Students
		" select _students.entityid, es.adultid as payerid, e.lastname, e.firstname,\n" +
		" tr.scholarship, tr.tuition, tr.defaulttuition, tr.tuitionoverride\n" +
		" from _students,\n" +
		"     entities e left outer join entities_school es on (e.entityid = es.entityid),\n" +
		"     termregs tr\n" +
		" where tr.groupid = " + SqlInteger.sql(termid) + "\n" +
		" and _students.entityid = tr.entityid\n" +
		" and _students.entityid = e.entityid\n" +
		" and not e.obsolete;\n" +
		
		// Set up list of courses
		" create temporary table _courses (courseid int);\n" +
		" insert into _courses" +
		"   select distinct c.courseid\n" +
		"   from _students, enrollments en, courseids c\n" +
		"   where _students.entityid = en.entityid" +
		"   and en.courseid = c.courseid" +
		"   and c.termid = " + SqlInteger.sql(termid) + ";\n" +
		
		// rss[4]: Meetings (for pro-rating)
		" select m.courseid,dtstart,dtnext" +
		" from meetings m, _courses" +
		" where m.courseid = _courses.courseid\n" +
		" order by m.courseid,m.dtstart;\n" +

		// rss[5]: Enrollments
		" select _students.entityid, en.dstart, en.dend, c.*\n" +
		" from _students, enrollments en, courseids c, courseroles cr" +
		" where _students.entityid = en.entityid" +
		" and en.courseid = c.courseid" +
		" and c.termid = " + SqlInteger.sql(termid) +
		" and en.courserole = cr.courseroleid and cr.name = 'student';\n" +
//		" order by st.entityid, c.dayofweek, c.tstart;\n" + 


		// Drop temporary tables
		" drop table _payers;" +
		" drop table _courses;" +
		" drop table _students;";
	str.execSql(sql, new RssTasklet2() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet[] rss) throws Exception {
		ResultSet rs;
		
		// rss[0]: Name of term
		rs = rss[0];
		rs.next();
		termName = rs.getString("name");
		rbPlanSetClass = rs.getString("rbplansetclass");
//		calcTuition = rs.getBoolean("calctuition");
		
		// rss[1]: DueDate
		rs = rss[1];
		duedates = new TreeMap();
		while (rs.next()) {
			DueDate dd = new DueDate(rs, date);
			duedates.put(dd.name, dd);
		}
		
		// rss[2]: Payers
		rs = rss[2];
		payers = new TreeMap();
		while (rs.next()) {
			Payer pp = new Payer(rs);
			payers.put(pp.entityid, pp);
		}
		
		// rss[3]: Students
		rs = rss[3];
		students = new TreeMap();
		while (rs.next()) {
			Student ss = new Student(rs);
			Payer pp = payers.get(ss.payerid);
			if (pp != null) pp.students.add(ss);
			students.put(ss.entityid, ss);
		}
		
		// rss[4]: Meetings
		rs = rss[4];
		courses = new TreeMap();
		while (rs.next()) {
			int courseid = rs.getInt("courseid");
			Course course = courses.get(courseid);
			if (course == null) {
				course = new Course(courseid);
				courses.put(courseid, course);
			}
			course.meetings.add(new Meeting(rs, date));
		}
		
		// rss[5]: Enrollments
		rs = rss[5];
		while (rs.next()) {
			Enrollment ee = new Enrollment(rs, date, courses);
			Student ss = students.get(ee.entityid);
			if (ss != null) ss.enrollments.add(ee);
		}
	}});
}





}
