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
 * DonorReport.java
 *
 * Created on February 10, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.AdhocOJSqlTableModel;
import offstage.*;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.sql.*;
import offstage.db.*;
import citibob.sql.pgsql.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

/**
 
 *
 * @author citibob
 */
public class RollBook extends MultiTableDbModel
{

//SqlDbModel main;
//String idSql;		// Set of IDs for our report

//public void doSelect(Statement st)
//throws java.sql.SQLException
//{
//	DB.createIDList(st, idSql, "ids_donor");
//	super.doSelect(st);
//	st.executeUpdate("drop table ids_donor");
//}

/** Creates a new instance of DonorReport */
public RollBook(App app, int termid)
{

	String sql;
	sql =
		" select t.name as termname, c.courseid,c.name as coursename,\n" +
		" dow.longname as dayofweek, " +
		" to_char(c.tstart,'HH:MI') as tstart," +
		" to_char(c.tnext,'HH:MI') as tnext,\n" +
		" p.entityid, p.lastname, p.firstname, (t.firstdate - p.dob) / 365.25 as age, pr.name as programname,\n" +
		" xx.lastname as tlastname, xx.firstname as tfirstname," +
		" cast(yy.nstudents as int4) as nstudents" +
		" from termids t\n" +
		" inner join courseids c on (c.termid = t.groupid)\n" +
		" inner join enrollments en on (en.courseid = c.courseid)\n" +
		" inner join persons p on (p.entityid = en.entityid)\n" +
		" inner join entities_school ps on (p.entityid = ps.entityid)\n" +
		" inner join daysofweek dow on (dow.javaid = c.dayofweek)\n" +
		" inner join courseroles cr on (cr.courseroleid = en.courserole)" +
		" inner join termregs tr on (tr.groupid = t.groupid and tr.entityid = p.entityid)\n" +
		" left outer join programids pr  on (pr.programid = tr.programid)\n" +
		" left outer join\n" +
			" (select c.courseid, tp.lastname, tp.firstname\n" +
			" from enrollments en, courseids c, termids t, entities tp, courseroles cr\n" +
			" where en.courseid =  c.courseid and en.entityid = tp.entityid\n" +
			" and en.courserole = cr.courseroleid and cr.name = 'teacher'\n" +
			" and t.groupid = " + SqlInteger.sql(termid) + ") xx on (c.courseid = xx.courseid)\n" +
		" left outer join\n" +
			" (select c.courseid, count(*) as nstudents\n" +
			" from termids t\n" +
			" inner join courseids c on (c.termid = t.groupid)\n" +
			" inner join enrollments en on (en.courseid = c.courseid)\n" +
			" inner join courseroles cr on (cr.courseroleid = en.courserole)" +
			" where cr.name = 'student'" +
			" and t.groupid = " + SqlInteger.sql(termid) + "\n" +
			" group by c.courseid) yy on (c.courseid = yy.courseid)\n" +
		" where cr.name = 'student'" +
		" and t.groupid = " + SqlInteger.sql(termid) + "\n" +
		" order by c.dayofweek, c.tstart, c.courseid, p.lastname, p.firstname\n";
System.out.println(sql);
	MainSqlTableModel main = new MainSqlTableModel(
		app.sqlTypeSet(), sql);
	this.add(new SqlDbModel(main));

	// Outer Join the "Day Registered" stuff
	Column[] cols = new Column[] {new Column("days", new SqlString(true))};
	sql =
		" select distinct p.entityid, c.dayofweek, dow.lettername\n" +
		" from enrollments en, courseids c, entities p, daysofweek dow\n" +
		" where c.courseid = en.courseid\n" +
		" and en.entityid = p.entityid\n" +
		" and c.dayofweek = dow.javaid\n" +
		" and c.termid = " + SqlInteger.sql(termid) + "\n" +
		" order by p.entityid, c.dayofweek\n";
	SqlDbModel model = new SqlDbModel(new AdhocOJSqlTableModel(
		main, "entityid", "entityid", cols, app.sqlTypeSet(),
		sql) {
			public void setRow(int row, ResultSet rs) throws SQLException
			{
				String dayname = rs.getString("lettername");
				String s = (String)getValueAt(row, 0);
				s = (s == null ? "" : s) + dayname;
				setValueAt(s, row, 0);
			}
		});
	this.add(model);
}

	
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	RollBook report = new RollBook(fapp, 8);
//	report.doSelect(st);
//	JTypeTableModel model = report.newTableModel();
//
//	HashMap params = new HashMap();
//	JRDataSource jrdata = new JRTableModelDataSource(model);
//	offstage.reports.ReportOutput.viewJasperReport("RollBook.jrxml", jrdata, params);
//
//}
}
