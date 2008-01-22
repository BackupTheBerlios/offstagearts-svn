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

package offstage.school.gui;

import citibob.app.App;
import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class TuitionCalc {

static SqlNumeric money = new SqlNumeric(9, 2, true);
static SqlTime time = new SqlTime(true);
static Double getMoney(ResultSet rs, String col) throws SQLException
	{ return (Double)money.get(rs, col); }
// ==========================================================
String termName;

/** When different charges are due throughout the term */
static class DueDate
{
	String name;
	String description;
	java.util.Date duedate;
	public DueDate(ResultSet rs, SqlDate date) throws SQLException
	{
		name = rs.getString("name");
		description = rs.getString("description");
		duedate = date.get(rs, "duedate");
	}
}
Map<String,DueDate> duedates;

/** Info on the payer */
static class Payer
{
	int entityid;
	boolean isorg;
	char billingtype;
	List<Student> students;
	public Payer(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		String sBillingType = rs.getString("billingtype");
		billingtype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
		isorg = rs.getBoolean("isorg");
		students = new ArrayList(1);
	}
}
Map<Integer,Payer> payers;

/** A record of data for one student -- from termregs */
static class Student implements Comparable<Student>
{
	int entityid;
	int payerid;
	String lastname, firstname;
	double scholarship;
	Double tuitionoverride;
	List<Enrollment> enrollments;	// Courses we're enrolled in

	// Calculated Stuff
	double defaulttuition = 0;
	double secProrate;				// Pro-rate multiplier for seconds per week of class
	int sec;						// Seconds of week per class
	double tuition = 0;				// Tuition we calculated
	String tuitionDesc;				// Discription of our tuition for account
	
	public Student(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		payerid = rs.getInt("payerid");
			if (payerid == 0) payerid = entityid;
		lastname = rs.getString("lastname");
		firstname = rs.getString("firstname");
		scholarship = getMoney(rs, "scholarship");
		Double Tuition = getMoney(rs, "tuition");
			tuition = (Tuition == null ? 0 : Tuition);
		tuitionoverride = getMoney(rs, "tuitionoverride");
		Double Defaulttuition = getMoney(rs, "defaulttuition");
			defaulttuition = (Defaulttuition == null ? 0 : Defaulttuition);
		enrollments = new ArrayList(1);
	}
	public String toString() { return "Student(" + entityid + ", " + getName() + ")"; }
	public double getProratedPrice()
	{
		double price = 0;
		for (Enrollment e : enrollments) {
			price += e.getPrice() * e.getProrate();
System.out.println(entityid + "     : price += " + e.getPrice() + " * " + e.getProrate());
		}
System.out.println(entityid + ": price = " + price);
		return price;
	}
	public void setSec()
	{
		sec = 0;
		double psec = 0;		// Prorated # of seconds
		for (Enrollment e : enrollments) {
			int s = e.getSec();
			sec += s;
			psec += s * e.getProrate();
System.out.println(entityid + "     : sec += " + e.getSec() + " * " + e.getProrate());
		}
System.out.println(entityid + ": sec = " + sec + " " + psec);
		secProrate = (double)psec / (double)sec;
		if (Double.isNaN(secProrate)) secProrate = 1.0D;	// 0/0 = 1 in this case.
	}
	public String getName() { return firstname + " " + lastname; }

	public int compareTo(Student o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}
Map<Integer,Student> students;

/** One student enrolled in one course */
static class Enrollment
{
	// From enrollments
	int entityid;			// student
	int courseid;
	java.util.Date dstart;	// Custom start date for pro-rating
	java.util.Date dend;

	// From courseids...
	int tstartMS;		// Time of day for start and end of course
	int tnextMS;
	Double price;		// Price of just this one course
	int locationid;		// Where the course is held
	
	// Calculated
	private double prorate;		// Pro-rating fraction
	public double getProrate() { return prorate; }
	
	public Enrollment(ResultSet rs, SqlDate date, Map<Integer,Course> courses) throws SQLException
	{
		entityid = rs.getInt("entityid");
		courseid = rs.getInt("courseid");
		dstart = date.get(rs, "dstart");
		dend = date.get(rs, "dend");
		tstartMS = (int)time.get(rs, "tstart").getTime();
		tnextMS = (int)time.get(rs, "tnext").getTime();
		price = (Double)money.get(rs, "price");
		locationid = rs.getInt("locationid");
		
		Course course = courses.get(courseid);
		prorate = (course == null ? 1.0D :
			(double)course.numMeetings(dstart,dend) / (double)course.numMeetings());
	}

	public double getPrice()
	{
		if (price == null) return 0;
		return price.doubleValue();
	}
	public int getSec()
	{
		if (price != null) return 0;
		return (tnextMS - tstartMS) / 1000;
	}
}

static class Course
{
	int courseid;
	List<Meeting> meetings;
	/** Full number of meetings in course */
	public int numMeetings() { return meetings.size(); }
	
	/** Finds number of meetings >=dstart and <=dend.  dstart and dend are dates ONLY,
	 they have no time component. */
	public int numMeetings(java.util.Date dstart, java.util.Date dend)
	{
		int n = 0;
		if (dstart != null && dend != null) {
			long startMS = dstart.getTime();
			long nextMS = dend.getTime() + 86400*1000L;
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms >= startMS && ms < nextMS) ++n;
			}
		} else if (dstart != null) {
			long startMS = dstart.getTime();
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms >= startMS) ++n;
			}
		} else if (dend != null) {
			long nextMS = dend.getTime() + 86400*1000L;
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms < nextMS) ++n;
			}
		} else {
			n = numMeetings();
		}
		return n;
	}
	public Course(int courseid)
	{
		this.courseid = courseid;
		meetings = new LinkedList();
	}
}
Map<Integer,Course> courses;

static class Meeting
{
	java.util.Date dtstart;
	java.util.Date dtnext;
	public Meeting(ResultSet rs, SqlDate date) throws SQLException
	{
		dtstart = date.get(rs, "dtstart");
		dtnext = date.get(rs, "dtnext");
	}
}
// ==========================================================================
int termid;
String payerIdSql;
SqlDate date;

/** @param payerIdSql IdSql that selects the payers for which we want to recalc tuition. */
public TuitionCalc(TimeZone tz, int termid, String payerIdSql)
{
	date = new SqlDate(tz, true);
	this.termid = termid;
	this.payerIdSql = payerIdSql;
}

void doAll(SqlRunner str)
{
	readTuitionData(str);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		calcTuition();
		String sql = writeTuitionSql();
		str.execSql(sql);
	}});
}

public static void w_recalc(SqlRunner str, TimeZone tz, int termid, String payerIdSql)
{
	TuitionCalc tc = new TuitionCalc(tz, termid, payerIdSql);
	tc.doAll(str);
}
public static void w_recalc(SqlRunner str, TimeZone tz, int termid, int payerID)
{
	w_recalc(str, tz, termid, "select " + SqlInteger.sql(payerID) + " as id");
}
public static void w_recalc(SqlRunner str, TimeZone tz, int termid, Set<Integer> payerIDs)
{
	String payerIdSql =
		" select entityid from entities where entityid in " + SQL.intList(payerIDs);
	w_recalc(str, tz, termid, payerIdSql);
}
public static void w_recalcAll(SqlRunner str, TimeZone tz, int termid)
{
	// It means we do ALL
	String payerIdSql =
		" select distinct adultid from termregs tr, entities_school es" +
		" where tr.groupid = " + SqlInteger.sql(termid) +
		" and tr.entityid = es.entityid";
	w_recalc(str, tz, termid, payerIdSql);
}

// ==========================================================================

void readTuitionData(SqlRunner str)
//throws SQLException
{
	String sql =
		// rss[0]: Name of Term
		" select t.name" +
		" from termids t" +
		" where t.groupid = " + SqlInteger.sql(termid) + ";\n" +
		
		// rss[1]: DueDate
		" select name,duedate,description from termduedates" +
		" where termid = " + SqlInteger.sql(termid) + ";\n" +

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
		" delete from tuitiontrans using _payers" +
		" where tuitiontrans.entityid = _payers.entityid\n" +
		" and tuitiontrans.termid = " + SqlInteger.sql(termid) + ";\n" +

		// Zero out previous tuitions
		" update termregs" +
		" set tuition = null, defaulttuition=null" +
		" from _students" +
		" where groupid = " + SqlInteger.sql(termid) +
		" and termregs.entityid = _students.entityid;\n" +
		
		// rss[2]: Payers
		" select e.entityid, e.isorg,\n" +
		" (case when es.billingtype is null then 'y' else es.billingtype end) as billingtype\n" +
		" from entities e left outer join entities_school es on (e.entityid = es.entityid), _payers" +
		" where _payers.entityid = e.entityid\n" +
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
	str.execSql(sql, new RssRunnable() {
	public void run(citibob.sql.SqlRunner str, java.sql.ResultSet[] rss) throws Exception {
		ResultSet rs;
		
		// rss[0]: Name of term
		rs = rss[0];
		rs.next();
		termName = rs.getString("name");
		
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

void calcTuition()
{
	// Go through family by family
	for (Payer payer : payers.values()) {
		calcTuition(payer);
	}
}
	
/** @return SQL to update tuition records */
String writeTuitionSql()
{
	StringBuffer sql = new StringBuffer();
	
	// Produce the SQL to store this tuition calculation
	for (Payer pp : payers.values()) {
		for (Student ss : pp.students) {
			// Main tuition in student record
			sql.append(
				" update termregs" +
				" set defaulttuition=" + money.sql(ss.defaulttuition) + "," +
				" tuition=" + (ss.tuition == 0 ? "null" : money.sql(ss.tuition)) +
				" where groupid = " + SqlInteger.sql(termid) +
				" and entityid = " + SqlInteger.sql(ss.entityid) +
				";\n");

			// Don't mess with accounts if there's no tuition to be charged
			if (ss.tuition == 0) continue;
			
			// Registration fee
			DueDate reg = duedates.get("r");
			if (reg != null) {
				insertTransaction(sql, pp.entityid, reg.duedate,
					getRegistrationFee(),
					"Registration Fee for " + ss.getName(),
					ss.entityid);
			}

			// Main fees
			switch(pp.billingtype) {
				case 'q' : {
					for (int i=1; i<=4; ++i) {
						DueDate dd = duedates.get("q"+i);
						
						// Main tuition
						insertTransaction(sql, pp.entityid, dd.duedate,
							ss.tuition * .25,
							ss.tuitionDesc + " --- " + dd.description,
							ss.entityid);
						
						// Scholarships
						if (ss.scholarship > 0) {
							insertTransaction(sql, pp.entityid, dd.duedate,
								-ss.scholarship * .25,
								termName + ": Scholarship for " + ss.getName() + " --- " + dd.description,
								ss.entityid);
						}
					}
				} break;
				case 'y' : {
					DueDate dd = duedates.get("y");
					
					// Main tuition
					insertTransaction(sql, pp.entityid, dd.duedate,
						ss.tuition,
							ss.tuitionDesc + " --- " + dd.description,
						ss.entityid);
					
					// Scholarships
					if (ss.scholarship > 0) {
						insertTransaction(sql, pp.entityid, dd.duedate,
							-ss.scholarship,
							termName + ": Scholarship for " + ss.getName() + " --- " + dd.description,
							ss.entityid);
					}
				} break;
			}
		}
	}
	
	return sql.toString();
}

void insertTransaction(StringBuffer sql, int entityid,
java.util.Date duedate, double amount, String description, int studentid)		
{
	sql.append(
		" insert into tuitiontrans " +
		" (entityid, actypeid, date, amount, description, studentid, termid)" +
		" values (" + SqlInteger.sql(entityid) + ", " +
		" (select actypeid from actypes where name = 'school'), " +
		date.toSql(duedate) + ", " + money.toSql(amount) + "," +
		SqlString.sql(description) + ", " + SqlInteger.sql(studentid) + ", " +
		SqlInteger.sql(termid) + ");\n");
	
}		
double getRegistrationFee() { return 25; }
	
/** Changes the student records inside payer.students */
void calcTuition(Payer payer)
{
	for (Student ss : payer.students) {
		// Get the tuition...
		ss.defaulttuition = calcTuition(ss);
//if (Double.isNaN(ss.defaulttuition)) {
//			System.out.println("hoi");
//}
		if (ss.tuitionoverride != null) {
			// Manual override of tuition --- just set it
			ss.tuition = ss.tuitionoverride;
		} else {
			// No override, use the tuition we calculated.
			ss.tuition = ss.defaulttuition;
		}
		ss.tuitionDesc = termName + ": Tuition for " + ss.getName();
	}

	if (payer.isorg) return;	// No sibling discounts for organizational payers
	Collections.sort(payer.students);
	int n = 0;
	for (Student ss : payer.students) {
System.out.println("student: " + ss + ", tuition=" + ss.tuition);
		if (ss.tuition == 0) continue;		// Don't count non-paying "siblings" (such as payer)
		if (n++ == 0) continue;		// Don't apply to first child
		if (ss.tuitionoverride != null) continue;		// Don't apply if we've manually set tuition
		
		// Apply discount, we're on a sibling
		ss.defaulttuition *= .9;
		ss.tuition = ss.defaulttuition;
		ss.tuitionDesc += " (w/ sibling discount)";
	}

}

/** Returns the (one) tuition number for a particular student. */
double calcTuition(Student ss)
{
	ss.setSec();
	double price = ss.getProratedPrice();		// Non-timed items	
	double tuition = getPrice(ss.sec) * ss.secProrate + price;
	return tuition;
}

/** @param weeklyS Number of seconds of class per week for this student.  Does
 not include classes with fixed price. */
double getPrice(int weeklyS)
{
	return TuitionRate.getRateY(weeklyS);
}

//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);
//	TuitionCalc.w_recalc(fapp.getBatchSet(), fapp.getTimeZone(), 346,
//		"select 24822 as id");
//	fapp.getBatchSet().runBatches();
//}

}
