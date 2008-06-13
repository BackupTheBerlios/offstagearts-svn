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
import java.net.URL;
import java.util.Date;
import java.util.prefs.*;
import offstage.config.*;
import com.jangomail.api.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import offstage.equery.swing.MailMsg;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class SchoolDB {

// -------------------------------------------------------------------------------
/** Makes a student record for an entity --- error if student already exists. */
public static String registerStudentSql(int termid, int studentid, SqlDateType sqlDate)
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
// -----------------------------------------------------------------
public static void w_meetings_autofill(SqlRun str, int termid,
final int courseid, final TimeZone tz)//, final UpdTasklet2 rr)
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

	str.execSql(sql, new RssTasklet2() {
	public void run(SqlRun str, ResultSet[] rss) throws SQLException {
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
			final Date TstartMS = stm.get(rs, 4);
			final Date TnextMS = stm.get(rs, 5);
			final int xcourseid = rs.getInt(6);

			if (dayofweek < 0) continue;		// day of week
			if (TstartMS == null) continue;
			if (TnextMS == null) continue;
			
			// Start generating the timestamps...
			sbuf.append("delete from meetings where courseid = " + SqlInteger.sql(xcourseid) + ";\n");
			Calendar cal = Calendar.getInstance(tz);
			cal.setTime(day0);
			cal.set(Calendar.DAY_OF_WEEK, dayofweek);
			for (;cal.getTimeInMillis() < day1.getTime(); cal.add(Calendar.WEEK_OF_YEAR, 1)) {
				long ms0 = cal.getTimeInMillis();
				if (holidays.contains(ms0)) continue;
				java.util.Date ts0 = new java.util.Date(ms0 + TstartMS.getTime());
				java.util.Date ts1 = new java.util.Date(ms0 + TnextMS.getTime());
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
/** Returns a map, listing the students for each payer in a given term. */
public static Map<Integer,String> getStudentNames(SqlRun str, int termid, String payerIdSql)
{
	String sql =
		" select pp.entityid as payerid, ss.lastname, ss.firstname" +
		" from persons pp, persons ss, termenrolls te, termregs tr\n" +
		(payerIdSql == null ? "" : ", (" + payerIdSql + ") xx\n") +
		" where te.entityid = ss.entityid" +
		" and te.groupid = " + SqlInteger.sql(termid) +
		" and te.entityid = tr.entityid and te.groupid = tr.groupid\n" +
		" and tr.payerid = pp.entityid" +
		(payerIdSql == null ? "" : " and pp.entityid = xx.id") +
		"\n order by pp.entityid";
	final Map<Integer,String> map = new TreeMap();
	str.execSql(sql, new RsTasklet() {
	public void run(ResultSet rs) throws SQLException {
		StringBuffer studentNames = null;
		int lastPayerid = -1;
		while (rs.next()) {
			String name = rs.getString("firstname") + " " + rs.getString("lastname");
			int payerid = rs.getInt("payerid");
			if (payerid != lastPayerid) {
				if (lastPayerid >= 0) {
					map.put(lastPayerid, studentNames.toString());
				}
				studentNames = new StringBuffer(name);
				lastPayerid = payerid;
			} else {
				studentNames.append(", " + name);
			}
		}
		if (studentNames == null) map.put(lastPayerid, "<none>");
		else map.put(lastPayerid, studentNames.toString());
	}});
	return map;
}

static String currentCustomersIdSql =
	" select distinct te.entityid as id\n" +
	" from termenrolls te, termids t\n" +
	" where te.groupid = t.groupid\n" +
	" and t.iscurrent\n" +
	" 	union\n" +
	" select distinct tr.payerid as id\n" +
	" from termenrolls te, termids t, termregs tr\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = tr.entityid\n" +
	" and t.iscurrent\n" +
	" and tr.payerid is not null\n" +
	" 	union\n" +
	" select distinct e.parent1id as id\n" +
	" from termenrolls te, termids t, entities e\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = e.entityid\n" +
	" and t.iscurrent\n" +
	" and e.parent1id is not null\n" +
	" 	union\n" +
	" select distinct e.parent2id as id\n" +
	" from termenrolls te, termids t, entities e\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = e.entityid\n" +
	" and t.iscurrent\n" +
	" and e.parent2id is not null\n";

public static String checkSchoolEmailQuery(String idSql)
{
	return
		" create temporary table _mm\n" +
		" (id int primary key,\n" +
		" iscurrent bool not null default false,\n" +
		" hasemail bool not null default false);\n" +
		" \n" +
		" insert into _mm (id, iscurrent, hasemail)\n" +
		" select list.id,\n" +
		" case when current.id is not null then true else false end as iscurrent,\n" +
		" case when e.email is not null then true else false end as hasemail\n" +
		" from (" + idSql + ") list\n" +
		" inner join persons e on list.id = e.entityid\n" +
		" left outer join (" + currentCustomersIdSql + ") current on list.id = current.id;\n";

}

static DateFormat groupNameFmt = new SimpleDateFormat("yyMMdd-HHmmss");
static {
	groupNameFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
}

/** @param idSql People to send email to (we need to weed out bad addresses) */
public static void sendSchoolJangoMail(final App app, SqlRun str, final MailMsg msg, String idSql0)
{
	str.execSql(SchoolDB.checkSchoolEmailQuery(idSql0));
	
	String sql =
		" select e.entityid, e.email as EmailAddress, e.firstname, e.lastname" +
		" from _mm, persons e" +
		" where _mm.iscurrent and _mm.hasemail\n" +
		" and _mm.id = e.entityid";

	
	
//ToOther=
//John,Smith,john@smith.com|David,Gary,david@gary.com|Sheila,
//Panther,sheila@panther.com
//
//Options=
//ToOtherRowDelimiter=|,ToOtherColDelimiter=c,
//ToOtherFieldNames=FirstName|LastName|EmailAddress
//
//Subject=
//Hello %%FirstName%%
//
//Message=
//Hello %%FirstName%% %%LastName%% -- your email address is %%EmailAddress%%
		

	str.execSql(sql, new RsTasklet() {
	public void run(ResultSet rs) throws Exception {
		// Get JangoMail Handle
		JangoMail service = new JangoMailLocator();
		JangoMailSoap soap = service.getJangoMailSoap(
			new URL("https://api.jangomail.com/api.asmx"));

		String usr = app.props().getProperty("custmail.jango.user");
		String pwd = app.props().getProperty("custmail.jango.password");

		// GroupName
		String groupName = "Group-" + groupNameFmt.format(new java.util.Date());

		// FieldNames
		ResultSetMetaData md = rs.getMetaData();
		StringBuffer fieldNames = new StringBuffer();
		for (int i=0; i<md.getColumnCount(); ++i) {
			fieldNames.append(md.getColumnName(i));
			if (i < md.getColumnCount() - 1) fieldNames.append(',');
		}

		// ImportData
		StringBuffer importData = new StringBuffer();
		while (rs.next()) {
			for (int i=0; i<md.getColumnCount(); ++i) {
				// Append the String from SQL.
				// Change whitespace to ' '.  The assumption is that if anything
				// is a large block of text, it will end up as part of an HTML
				// email, and thus whitespace is all equivalent anyway.  This prevents
				// whitespace from interfering with our tab and newline delimiters.
				String s = rs.getString(i+1);
				for (int j=0; j<s.length(); ++j) {
					if (Character.isWhitespace(s.charAt(j))) importData.append(' ');
					else importData.append(s.charAt(j));
				}
				
				// Delimit the column with TAB
				if (i < md.getColumnCount()-1) importData.append('\t');
			}
			
			// Delimit the row with a newline.
			importData.append('\n');
		}

		String columnDelimiter = "t";
		String rowDelimiter = "|";
		String textQualifier = "";
				
		// Create Group in JangoMail
		soap.addGroup(usr, pwd, groupName);

		// Set up data in the group
		soap.importGroupMembersFromData(usr, pwd, groupName,
				fieldNames.toString(), importData.toString(),
				columnDelimiter, rowDelimiter, textQualifier);
		
		// Email to the group
		String fromEmail = "citibob@jangomail.com";
		String fromName = "Bob Fischer";
		String toGroups = groupName;
		String toGroupFilter = "";
		String toOther = "";
		String toWebDatabase = "";
		String subject = msg.subject;
		String rawMessage = new String(msg.body);
		String boundary = msg.boundary;
		String options = "";
//		soap.sendMassEmailRaw(fromName, pwd, fromEmail, fromName,
//			toGroups, toGroupFilter, toOther, toWebDatabase,
//			subject, rawMessage, boundary, options);
		

	}});
	
	
	
	str.execSql(" drop table _mm;");
}

// ================================================================
public String classLeadersDropSql(String meetingsIdSql)
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
public String classLeadersSql(String meetingIdSql, String courseroleIdSql)
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
		" ) cc\n\n" +

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
		" drop table _ent;\n";
}
}

//		System.out.println(sql);
//	final RSTableModel mod = new RSTableModel(app.sqlTypeSet());
//	mod.executeQuery(str, sql);
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) throws SQLException {
//		Map<Integer,String> map = new HashMap();
//		TableModelGrouper grouper = new TableModelGrouper(mod,
//			new String[][] {{"adultid"}});
//		List<Map> groups = grouper.groupRowsList();
//		if (groups != null) for (Map gmap : groups) {
//			JTypeTableModel tt = (JTypeTableModel)gmap.get("rs");
//			for (int i=0; i<tt.getRowCount(); ++i) {
//				String fname = (String)tt.getValueAt(i,2) + " " + (String)tt.getValueAt(i,1);
//				Integer id = (Integer)tt.getValueAt(i,0);
//				String names = map.get(id);
//				if (names == null) {
//					names = fname;
//				} else {
//					names = names + ", " + fname;
//				}
//				map.put(id, names);
//			}
//		}
//		AcctStatement.this.studentNames = map;
////		str.put("studentNames", map);
//	}});
//}
//
//}
