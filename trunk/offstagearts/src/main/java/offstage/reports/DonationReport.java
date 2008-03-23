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
 * DonorReport.java
 *
 * Created on February 10, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.AdhocOJSqlTableModel;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import java.util.*;
import citibob.sql.*;
import offstage.db.*;
import citibob.sql.pgsql.*;
import java.io.File;
//import offstage.equery.QuerySchema.Col;

/**
 
 *
 * @author citibob
 */
public class DonationReport extends MultiTableDbModel
{

//SqlDbModel main;
String idSql;		// Set of IDs for our report

public void doSelect(SqlRun str)
{
	DB.createIDList(str, idSql, "ids_donor");
	super.doSelect(str);
	str.execSql("drop table ids_donor");
}
	
/** Creates a new instance of DonorReport */
public DonationReport(App app, String idSql, int minYear, int maxYear)
{
	this.idSql = idSql;

	String sql;
	sql =
		" select p.* from persons p, ids_donor ids where p.entityid = ids.id";
//		int x=5;
	MainSqlTableModel main = new MainSqlTableModel(
		app.sqlTypeSet(), sql);
	this.add(new SqlDbModel(main));

	// Outer Join the Fiscal Year summaries
//	final int[] years = new int[] {1989, 1990, 1991, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007};
	int nyear = maxYear - minYear + 1;
	final int[] years = new int[nyear];
	for (int i=0; i<nyear; ++i) years[i] = minYear + i;

	Column[] cols = new Column[years.length];
//	for (int i=0; i<years.length; ++i) cols[i] = new Col(""+years[i], new JavaJType(Double.class));
	for (int i=0; i<years.length; ++i) cols[i] = new Column(""+years[i], new SqlNumeric(10,2,true));
	sql =
		" select d.entityid, di.fiscalyear, sum(amount) as amount" +
		" from donations d, donationids di, ids_donor ids" +
		" where d.entityid = ids.id" +
		" and d.groupid = di.groupid" +
		" and di.fiscalyear >= " + SqlInteger.sql(minYear) +
		" and di.fiscalyear <= " + SqlInteger.sql(maxYear) +
		" group by d.entityid, di.fiscalyear";

	SqlDbModel model = new SqlDbModel(new AdhocOJSqlTableModel(
		main, "entityid", "entityid", cols, app.sqlTypeSet(),
		sql) {
			public void setRow(int row, ResultSet rs) throws SQLException
			{
				int year = rs.getInt("fiscalyear");
				//int col = findColumn(""+year);
				int col = Arrays.binarySearch(years, year); //findColumn(""+year);
System.out.println("(" + row + ", " + col + ") = " + rs.getDouble("amount"));
System.out.println("year = " + year);
				setValueAt(rs.getDouble("amount"), row, col);
			}
		});
	this.add(model);
//	setTableModel();
}

public static void writeCSV(final App app, SqlRun str,
String idSql, int minYear, int maxYear, final File outFile) throws Exception
{
	final DonationReport report = new DonationReport(app, idSql, minYear, maxYear);
	report.doSelect(str);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		citibob.reports.Reports rr = app.reports();
		rr.writeCSV(rr.format(report.newTableModel()), outFile);
	}});
}


//public static void writeCSV(final App app, SqlRun str, final java.awt.Frame frame,
//final String title, String sql) throws Exception
//{
//	final DonationReport report = new DonationReport(app, sql);
//	report.doSelect(str);
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) throws Exception {
//		Reports rr = app.getReports();
//		rr.writeCSV(rr.format(report.newTableModel()),
//			frame, "Save" + title);
////		ReportOutput.saveCSVReport(fapp, frame, "Save" + title, report.newTableModel());	
//	}});
//}
//


}
