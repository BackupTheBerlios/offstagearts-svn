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

import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.util.prefs.*;
import offstage.config.*;
import citibob.gui.DBPrefsDialog;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class SchoolDB {

// -------------------------------------------------------------------------------
/** Makes a student record for an entity --- error if student already exists. */
//public static String createStudentSql(int studentid)
//{
//	if (studentid < 0) return "";
//	
//	return "select w_student_create(" + SqlInteger.sql(studentid) + ")";
//}
/** Makes a student record for an entity --- error if student already exists. */
public static String registerStudentSql(int termid, int studentid, SqlDate sqlDate)
{
	if (termid < 0 || studentid < 0) return "";
	
	return "select w_student_register(" +
		SqlInteger.sql(termid) + ", " +
		SqlInteger.sql(studentid) + ", " +
		sqlDate.toSql(new java.util.Date()) + ")";	// Register NOW
}
public static String registerPayerSql(int termid, int payerid)
{
	if (termid < 0 || payerid < 0) return "";
	
//	return "select w_payer_create(" + SqlInteger.sql(payerid) + ")";
	return "select w_payer_register(" + SqlInteger.sql(termid) + ", " + SqlInteger.sql(payerid) + ")";
}

public static String w_delteOrpanMeetingsSql()
{
	return
		"delete from meetings\n" +
		"using (\n" +
		"	select m.meetingid\n" +
		"	from meetings m left outer join courseids c on m.courseid = c.courseid\n" +
		"	where c.courseid is null\n" +
		") xx\n" +
		"where meetings.meetingid = xx.meetingid;";
}


///** Recalculate tuition for adultid associated with this student. */
//public static void w_tuitiontrans_calcTuition(SqlRunner str, int termid, int studentid)
//throws SQLException
//{
//	ResultSet rs;
//
//	// Find responsible paying party for this student
//	int adultid = SQL.readInt(st,
//		" select adultid from entities_school" +
//		" where entityid = " + SqlInteger.sql(studentid));
//	
//	// Find old responsible paying party
//	int oldadultid = -1;
//	try {
//		oldadultid = SQL.readInt(st,
//			" select distinct entityid from tuitiontrans where termid = " + SqlInteger.sql(termid) +
//			" and studentid = " + SqlInteger.sql(studentid));
////		w_tuitiontrans_calcTuitionByAdult(st, termid, oldadultid);
//	} catch(SQLException e) {}	// There is no old adultid; it's OK.
//
//	// Recalculate tuition on both accounts
//	if (oldadultid >= 0 && oldadultid != adultid) w_tuitiontrans_calcTuitionByAdult(st, termid, oldadultid);
//	w_tuitiontrans_calcTuitionByAdult(st, termid, adultid);
//
////	// Remove previous tuition invoice records, just for this student
////	st.executeUpdate(
////		" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
////		" and studentid = " + SqlInteger.sql(studentid));
//	
//	
//}


/** A record of data for one student */
private static class TermregRec
{
	
}

/** A line in the account */
private static class TuitionRec implements Comparable<TuitionRec>
{
	public int studentid;
	public String studentName;
	public String sdate;
	public String description;
	public double scholarship;
	public double tuition;
	public Double tuitionOverride;
	public int compareTo(TuitionRec o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}
// -----------------------------------------------------------------
public static void w_meetings_autofill(SqlRunner str, int termid,
final int courseid, final TimeZone tz)//, final UpdRunnable rr)
//throws SQLException
{
	String sql =
		// rss[0]: Holidays
		" select h.firstday, h.lastday + 1 as nextday\n" +
		" from holidays h, termids t\n" +
		" where ((h.firstday <= t.firstdate and h.lastday > t.firstdate-1)\n" +
		" or (h.lastday >= t.nextdate-1 and h.firstday < t.nextdate)\n" +
		" or (h.firstday >= t.firstdate and h.lastday <= t.nextdate-1)\n" +
		" or (h.firstday >= t.firstdate and h.firstday < t.nextdate and h.lastday is null))\n" +
		" and (h.lastday >= h.firstday or h.lastday is null)\n" +
		" and h.termid = t.groupid\n" +
		" and t.groupid=" + SqlInteger.sql(termid) + ";\n" +

		// rss[1]: Start and end of each course
		"select t.firstdate, t.nextdate, c.dayofweek, c.tstart, c.tnext, c.courseid" +
		" from termids t, courseids c" +
		" where t.groupid = c.termid" +
		(courseid < 0
			? " and t.groupid = " + SqlInteger.sql(termid)
			: " and c.courseid = " + SqlInteger.sql(courseid));

	str.execSql(sql, new RssTasklet() {
	public void run(SqlRunner str, ResultSet[] rss) throws SQLException {
		SqlTimestamp sts = new SqlTimestamp("GMT");
		SqlDate sdt = new SqlDate(tz, true);
		SqlTime stm = new SqlTime();

		// Get the holidays into a Set.
		ResultSet rs = rss[0];
		Set<Long> holidays = new TreeSet();
		while (rs.next()) {
			java.util.Date firstDt = sdt.get(rs, "firstday");
			java.util.Date nextDt = sdt.get(rs, "nextday");
			Calendar cal = Calendar.getInstance(tz);
			cal.setTime(sdt.get(rs, "firstday"));
			if (nextDt == null) holidays.add(cal.getTimeInMillis());
			else for (;;) {
				long ms = cal.getTimeInMillis();
				if (ms >= nextDt.getTime()) break;
				holidays.add(ms);
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
		}
		rs.close();

		// Get the actual meetings of each course
		rs = rss[1];
		StringBuffer sbuf = new StringBuffer();
		while (rs.next()) {
			final java.util.Date day0 = sdt.get(rs, 1);
			final java.util.Date day1 = sdt.get(rs, 2);
			final int dayofweek = rs.getInt(3);
			final long tstartMS = stm.get(rs, 4).getTime();
			final long tnextMS = stm.get(rs, 5).getTime();
			final int xcourseid = rs.getInt(6);

			// Start generating the timestamps...
			sbuf.append("delete from meetings where courseid = " + SqlInteger.sql(xcourseid) + ";\n");
			Calendar cal = Calendar.getInstance(tz);
			cal.setTime(day0);
			cal.set(Calendar.DAY_OF_WEEK, dayofweek);
			for (;cal.getTimeInMillis() < day1.getTime(); cal.add(Calendar.WEEK_OF_YEAR, 1)) {
				long ms0 = cal.getTimeInMillis();
				if (holidays.contains(ms0)) continue;
				java.util.Date ts0 = new java.util.Date(ms0 + tstartMS);
				java.util.Date ts1 = new java.util.Date(ms0 + tnextMS);
				sbuf.append("insert into meetings (courseid, dtstart, dtnext)" +
					" values (" + SqlInteger.sql(xcourseid) + ", " +
					sts.toSql(ts0) + ", " +
					sts.toSql(ts1) + ");\n");
			}
		}
		rs.close();
		str.execSql(sbuf.toString());
	}});
}
// --------------------------------------------------

//public static void w_tuitiontrans_recalcAllTuitions(SqlRunner str, final int termid)
//throws SQLException
//{
//	ResultSet rs;
//
//	if (termid < 0) return;
//	
//	String sql = "select distinct adultid from entities_school union select entityid from entities_school";
//	str.execSql(sql, new RsRunnable() {
//	public void run(SqlRunner str, ResultSet rs) throws Exception {
//		final List<Integer> payers = new ArrayList();
//		while (rs.next()) payers.add(rs.getInt(1));
//		
//		for (Integer payerid : payers) {
//			System.out.println("Recalculating tuition for " + payerid);
//			w_tuitiontrans_calcTuitionByAdult(str, termid, payerid, null);
//		}
//	}});
//}

//static SqlMoney nullableMoney = new SqlMoney(true);
//public static void w_tuitiontrans_calcTuitionByAdult(SqlRunner str,
//final int termid, final int adultid, final UpdRunnable rr)
//throws SQLException
//{
//	String sql =
//		// rss[0]
//		" select name,duedate from termduedates" +
//		" where termid = " + SqlInteger.sql(termid) + ";\n" +
//
//		// rss[1]
//		" select t.name as termname" +
//		" from termids t" +
//		" where t.groupid = " + SqlInteger.sql(termid) + ";\n" +
//
//		// rss[2] --- NOP... make sure next ResultSet has data
//		"select w_student_create(" + SqlInteger.sql(adultid) + ");\n" +
//
//		// rss[3]
//		" select es.billingtype, e.isorg\n" +
//		" from entities_school es, entities e\n" +
//		" where es.entityid = " + SqlInteger.sql(adultid) + "\n" +
//		" and es.entityid = e.entityid;" +
//
//		// Speed up rss[4+5] queries below
//		" create temporary table _ids (entityid int);\n" +
//		" insert into _ids select entityid from entities_school" +
//		"    where adultid = " + SqlInteger.sql(adultid) + ";\n" +
//		// Use _ids to clear tuition, in case no enrollments down below
//		" update termregs set tuition = 0" +
//		" from _ids st where termregs.entityid = st.entityid" +
//		" and termregs.groupid = " + SqlInteger.sql(termid) + ";\n" +
//		
//		// rss[4]
//		" select st.entityid as studentid, tr.tuitionoverride\n" +
//		" from _ids st, termregs tr\n" +
//		" where st.entityid = tr.entityid\n" +
//		" and tr.groupid = " + SqlInteger.sql(termid) + "\n" +
//		" and tr.tuitionoverride is not null;\n" +
//
//		// rss[5]
//		" select st.entityid as studentid, p.lastname, p.firstname, tr.scholarship, tr.tuitionoverride, c.*\n" +
//		" from _ids st, entities p, courseids c, enrollments e, termregs tr\n" +
//		" where st.entityid = p.entityid\n" +
//		" and tr.entityid = p.entityid and tr.groupid = c.termid" +
//		" and c.termid = " + SqlInteger.sql(termid) + "\n" +
//		" and e.courseid = c.courseid and e.entityid = st.entityid" +
//		" and e.courserole = (select courseroleid from courseroles where name = 'student')" +
//		" order by st.entityid, dayofweek, c.tstart;" +
//		
//		" drop table _ids;";
//
//	str.execSql(sql, new RssRunnable() {
//	public void run(SqlRunner str, ResultSet[] rss) throws Exception {
//System.out.println("Processing results, adultid = " + adultid);
//		ResultSet rs;
//		HashMap<String,String> duedates = new HashMap();
//		final StringBuffer sqlOut = new StringBuffer();	// SQL to store results
//		SqlNumeric money = new SqlNumeric(9, 2);
//		SqlTime time = new SqlTime(true);
//		
//		// rss[0]
//		// Read term due dates: rss[0]
//		rs = rss[0];
//		while (rs.next()) duedates.put(rs.getString("name"), rs.getString("duedate"));
//		rs.close();
//		
//		// Get name of the term: rss[1]
//		rs = rss[1];
//		rs.next();
//		String termName = rs.getString("termname");
//	
//		// Get billing type for this payer
//		rs = rss[3];
//		rs.next();
//		String sBillingType = rs.getString("billingtype");
//		char btype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
//		boolean isorg = rs.getBoolean("isorg");
//		rs.close();
//
//		// Remove previous tuition invoice records
//		sqlOut.append(
//			" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
//			" and entityid = " + SqlInteger.sql(adultid) + ";\n");
//	
//		// Get tuition overrides, even for people who have dropped all courses
//		Map<Integer,Double> tuitionOverrides = new TreeMap();
//		rs = rss[5];
//		while (rs.next()) {
//			tuitionOverrides.put(rs.getInt("studentid"),
//				(Double)nullableMoney.get(rs, "tuitionoverride"));
//		}
//		
//		// Calculate sum of hours in enrolled courses, per student
//		rs = rss[5];
//		TuitionRec tr = null;
//		double scholarship = 0;
//		int nsiblings = 0;				// Total number of siblings
//		int weeklyS = 0;
//		ArrayList<TuitionRec> tuitions = new ArrayList();
//		for (;;) {
//			boolean hasNext = rs.next();
//			if (!hasNext || tr == null || (tr.studentid != rs.getInt("studentid"))) {
//				if (tr != null) {
//					tr.tuition += TuitionRate.getRateY(weeklyS);
//					tuitions.add(tr);
//					++nsiblings;
//
//					// Apply the scholarship
//					if (scholarship > 0) {
//						TuitionRec trs = new TuitionRec();
//						trs.studentid = tr.studentid;
//						trs.studentName = tr.studentName;
//						trs.description = termName + ": Scholarship for " + tr.studentName;
//						tuitions.add(trs);
//						trs.tuition = -scholarship;
//					}
//				}
//
//				// Make up description for this next student record.
//				if (hasNext) {
//					tr = new TuitionRec();
//					tr.studentid = rs.getInt("studentid");
//					tr.studentName = rs.getString("firstname") + " " + rs.getString("lastname");
//					tr.description = termName + ": Tuition for " + tr.studentName;
//					tr.tuition = 0;
//					tr.tuitionOverride = (Double)nullableMoney.get(rs, "tuitionoverride");
//					tuitionOverrides.remove(tr.studentid);
//					scholarship = rs.getDouble("scholarship");
//					weeklyS = 0;
//				}
//			}
//			if (!hasNext) break;
//	System.out.println(rs.getString("studentid") + " " + rs.getString("name"));
//			// Add the price of this course to the tuition
//			String Price = rs.getString("price");
//			if (Price != null) {
//				tr.tuition += Double.parseDouble(Price);
//			} else {
//				long tstart = time.get(rs, "tstart").getTime();
//				long tnext = time.get(rs, "tnext").getTime();
//				int lengthS = (int)(tnext - tstart) / 1000;
//				weeklyS += lengthS;
//			}
//		}
//		rs.close();
//
//		// Give sibling discounts
//		if (nsiblings > 1 && (!isorg)) {
//			Collections.sort(tuitions);		// Sorted by tuition; scholarships are at end
//			Iterator<TuitionRec> ii = tuitions.iterator();
//			ii.next();
//			while (ii.hasNext()) {
//				TuitionRec trx = ii.next();
//				if (trx.tuition < 0) break;		// Scholarships
//				trx.tuition *= .9;
//				trx.description += " (w/ sibling discount)";
//			}
//		}
//
//	
//		for (TuitionRec trx : tuitions) {
//			if (trx.tuition < 0) continue;		// Scholarships
//			// Store in termregs table
//			sqlOut.append("update termregs" +
//				" set tuition = " + trx.tuition +
//				" where entityid = " + trx.studentid +
//				" and groupid = " + termid + ";\n");
//		}
//	
//		// ====================================================
//		// Make up final list of bills to account
//		ArrayList<TuitionRec> t2 = new ArrayList();
//	
//		// Add registration fees
//		for (TuitionRec trx : tuitions) {
//			if (trx.tuition < 0) continue;		// Ignore scholarhips
//			TuitionRec tq = new TuitionRec();
//			tq.sdate = duedates.get("r");
//			tq.description = "Registration Fee for " + trx.studentName;
//			tq.studentid = trx.studentid;
//			tq.tuition = 25;
//			t2.add(tq);
//		}
//	
//		// Convert to actual billing records that are quarterly or yearly...
//		if (btype == 'q') {
//			for (TuitionRec trx : tuitions) {
//				for (int i=1; i<=4; ++i) {
//					TuitionRec tq = new TuitionRec();
//					tq.sdate = duedates.get("q" + i);
//					tq.description = trx.description + " --- Quarter " + i;
//					tq.studentid = trx.studentid;
//					tq.tuition = trx.tuition * .25;
//					if (trx.tuitionOverride != null) tq.tuitionOverride = new Double(trx.tuitionOverride.doubleValue() * .25);
//					t2.add(tq);
//				}
//			}
//		} else {	// Bill Yearly
//			for (TuitionRec trx : tuitions) {
//				trx.sdate = duedates.get("y");
//				trx.description += " --- Full Year";
//				t2.add(trx);		
//			}
//		}
//	
//		// Write them out
//		for (TuitionRec trx : t2) {
//			// This student's records have ended; write out
//			sqlOut.append(" insert into tuitiontrans" +
//				" (entityid, actypeid, date, amount, description, studentid, termid)" +
//				" values (" + SqlInteger.sql(adultid) + ", " +
//				" (select actypeid from actypes where name = 'school'), " +
//				(trx.sdate == null ? "null" : "'" + trx.sdate + "'") + ", " +
//				money.sql(trx.tuitionOverride != null ? trx.tuitionOverride : trx.tuition) + ", " +
//				SqlString.sql(trx.description) + ", " +
//				SqlInteger.sql(trx.studentid) + ", " + SqlInteger.sql(termid) + ");\n");
//		}
//	
//System.out.println(sqlOut);
//		str.execSql(sqlOut.toString());
//		str.execUpdate(rr);
//	}});
//}
// -------------------------------------------------------------------------------
///** Returns true only if ALL IDs are in the entities_school table. */
//public static boolean isInSchool(SqlRunner str, int entityid) throws SQLException
//{
//	ResultSet rs = null;
//	try {
//		rs = st.executeQuery(
//			"select entityid from entities_school where entityid = " +
//			SqlInteger.sql(entityid));
//		return rs.next();
//	} finally {
//		rs.close();
//	}
//}
//public static boolean isInSchool(SqlRunner str, int[] entityids) throws SQLException
//{
//	ResultSet rs = null;
//	return SQL.readInt(st,
//		"select count(*) from entities_school where entityid in " +
//		SQL.intList(entityids)) == entityids.length;
//}
// -------------------------------------------------------------------------------
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	SqlRunner str = pool.checkout().createStatement();
//	w_tuitiontrans_calcTuition(st, 8, 12633);
//
//}
}
