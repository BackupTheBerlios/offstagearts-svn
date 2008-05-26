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
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.io.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.text.*;
import citibob.app.*;
import citibob.reports.Reports;
import citibob.reports.TableModelGrouper;
import citibob.reports.TemplateTableModel;
import offstage.schema.*;
import java.text.*;
import citibob.types.*;
import offstage.accounts.gui.AccountsDB;
import offstage.school.gui.SchoolDB;

/**
 *
 * @author citibob
 */
public class AcctStatement
{


/** Data created in making this report.  To be read out later. */
public List<HashMap<String,Object>> models;
/** Temporary data... */
//private Map<Integer,String> studentNames;

	
///**
//     * Get a listing of students for each parent.  Calls rr on 
//     * @param app 
//     * @param str Returns result (HashMap<Integer, String>) under name studentNames
//     * @param termid 
//     * @param payerid 
// @param Handler for results, takes type 
//     * @throws java.sql.SQLException 
//     *
//     */
//void getStudentNames(SqlRun str, App app, int termid, String payerIdSql)
////throws SQLException
//{
//	String sql =
//		" select es.adultid, p.lastname, p.firstname" +
//		" from persons p, entities_school es, termregs tr\n" +
//		(payerIdSql == null ? "" : ", (" + payerIdSql + ") xx") +
//		" where p.entityid = es.entityid\n" +
////		" and es.entityid <> es.adultid" +
//		" and tr.entityid = es.entityid" +
//		" and tr.groupid = " + SqlInteger.sql(termid) +
//		" and tr.tuition <> 0" +
////		(payerid < 0 ? "" : " and es.adultid = " + SqlInteger.sql(payerid)) +
//		(payerIdSql == null ? "" : " and es.adultid = xx.id") +
//		" order by es.adultid";
//System.out.println(sql);
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

///* @param str Stores result under "models" List<HashMap<String,Object>> */
////public static void makeJodModels(
//public AcctStatement(
//SqlRun str, final App app,
//int termid, int payerid, final java.util.Date today)
//{
//	this(str, app, termid, payerid < 0 ? null : "select " + payerid + " as id", today);
//}
public AcctStatement(
SqlRun str, final App app,
int termid, String payerIdSql, final java.util.Date today)
{
	// Fetch name of students in family
	final Map<Integer,String> studentNames = SchoolDB.getStudentNames(str, termid, payerIdSql);
	
	// Fetch main stuff
	int actypeid = ((Actrans2Schema)app.getSchema("actrans2")).actypeKmodel.getIntKey("school");
	String sql =
		" select act.*,-amt.amount as amount,act.cr_entityid as entityid," +		// amt.amount Sign reversed for this report
		" p.cc_last4,p.lastname,p.firstname,\n" +		// lastname, firstname just for sorting.
		" (case when p.firstname is null then '' else p.firstname || ' ' end ||" +
		" case when p.lastname is null then '' else p.lastname end) as payername\n" +
		" from actrans2 act, actrans2amt amt, persons p" +		// p is payer
		(payerIdSql == null ? "" : ", (" + payerIdSql + ") xx") +
		" where act.cr_entityid = p.entityid" +
		" and act.actransid = amt.actransid and amt.assetid = 0" +
		" and cr_actypeid = " + SqlInteger.sql(actypeid) +
		(payerIdSql == null ? "" : " and act.cr_entityid = xx.id") +
		"        UNION\n" +
		" select act.*,amt.amount as amount,act.db_entityid as entityid," +
		" p.cc_last4,p.lastname,p.firstname,\n" +
		" (case when p.firstname is null then '' else p.firstname || ' ' end ||" +
		" case when p.lastname is null then '' else p.lastname end) as payername\n" +
		" from actrans2 act, actrans2amt amt, persons p" +		// p is payer
		(payerIdSql == null ? "" : ", (" + payerIdSql + ") xx") +
		" where act.db_entityid = p.entityid" +
		" and act.actransid = amt.actransid and amt.assetid = 0" +
		" and db_actypeid = " + SqlInteger.sql(actypeid) +
		(payerIdSql == null ? "" : " and act.db_entityid = xx.id") +
		" order by lastname, firstname, entityid, date, actransid";		// p.lastname, p.firstname, act.date, act.actransid
	final RSTableModel rsmod = new RSTableModel(app.sqlTypeSet());
	rsmod.executeQuery(str, sql);
	
	// Fetch name of term and do main processig
	sql =
		"select name from termids where groupid = " + termid + ";\n";
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		// Retrieve name of term (rss[0])
		rs.next();
		String sterm = rs.getString(1);
		
		// Retrieve from getStudentNames
//		final Map<Integer,String> studentMap = (Map<Integer,String>)str.get("studentNames");

		// =========== Main processing


		// Group it by payer...
		String[][] sgcols = new String[][] {{"entityid"}};
//		List<HashMap<String,Object>> models = new ArrayList();
		models = new ArrayList();
		TableModelGrouper grouper = new TableModelGrouper(rsmod, sgcols);
		for (Map sbo : grouper.groupRowsList()) {
			JTypeTableModel sb = (JTypeTableModel)sbo.get("rs"); //JTypeTableModel)sbo;

			HashMap<String,Object> data = new HashMap();
			models.add(data);
			Integer PayerID = (Integer)sb.getValueAt(0, sb.findColumn("entityid"));

			// Add on account balance
			BalTableModel bal = new BalTableModel(sb.getRowCount());
			int amtcol = sb.findColumn("amount");
			int desccol = sb.findColumn("description");
			double dbal = 0;
			double sumtuition = 0;
			double sumfees = 0;
			double sumpayments = 0;
			double sumscholarship = 0;
			String paymenttype = "";
			for (int i=0; i<sb.getRowCount(); ++i) {
				// Set balance
				double amt = (Double)sb.getValueAt(i, amtcol);
				dbal += amt;
				bal.setValueAt(dbal, i, 0);

				// Correct description if there is none
				String desc = (String)sb.getValueAt(i, desccol);
				if (amt < 0 && (desc == null || "".equals(desc.trim()))) {
					desc = "Payment, Thank You!";
					sb.setValueAt(desc, i, desccol);
				}

				// Sort into tuition, registration, payment records
				if (amt < 0) sumpayments += amt;
				else if (desc != null) {
					if (desc.contains("Fee")) sumfees += amt;
					if (desc.contains("Tuition")) sumtuition += amt;
					if (desc.contains("Scholarship")) sumscholarship += amt;
				}

				// Set payment type
				if (sb.getValueAt(i, "cc_last4") != null) {
					paymenttype = "cc";
				}
			}
			MultiJTypeTableModel mod = new MultiJTypeTableModel(
				new JTypeTableModel[] {sb, bal});

			// Scan for first row after today
		//	java.util.Date tomorrow = new java.util.Date(today.getTime() + 86400*1000L);
			int dtcol = mod.findColumn("date");
		//	java.util.Date splitDt;
			int i=0;
			int split = 0;

			// Past charges
			for (; i< mod.getRowCount(); ++i) {
				java.util.Date dti = (java.util.Date)mod.getValueAt(i, dtcol);
				if (dti.getTime() >= today.getTime() + 86400*1000L-1L) break;
			}
			SubrowTableModel rs0 = new SubrowTableModel(mod, split, i);
			split = i;

			// Current charges
			SubrowTableModel rs1 = null;
			if (i < mod.getRowCount()) {
				java.util.Date splitDt = (java.util.Date)mod.getValueAt(i, dtcol);
				for (; i< mod.getRowCount(); ++i) {
					java.util.Date dti = (java.util.Date)mod.getValueAt(i, dtcol);
					if (dti.getTime() >= splitDt.getTime() + 86400*1000L-1L) break;
				}
				rs1 = new SubrowTableModel(mod, split, i);
				split = i;
			} else {
				rs1 = new SubrowTableModel(mod, i,i);
			}

			// Future charges
			SubrowTableModel rs2 = new SubrowTableModel(mod, split, mod.getRowCount());

			// Split into things owed and things not yet owed
			data.put("rs0", new TemplateTableModel(new StatementTableModel(rs0, app.sFormatMap())));
			data.put("rs1", new TemplateTableModel(new StatementTableModel(rs1, app.sFormatMap())));
			data.put("rs2", new TemplateTableModel(new StatementTableModel(rs2, app.sFormatMap())));

			// Add totals...
			int balcol = mod.findColumn("balance");

			// TODO: This will throw exception if no rows...
			NumberFormat mfmt = NumberFormat.getCurrencyInstance();
//			mfmt.setMaximumFractionDigits(2);
//			mfmt.setMinimumFractionDigits(2);
			double overdue = (rs0.getRowCount() == 0 ? 0 : (Double)rs0.getValueAt(rs0.getRowCount()-1, balcol));
			data.put("overdue", overdue <= 0 ? mfmt.format(0.0D) : mfmt.format(overdue));
			double paynow = (Double)rs1.getValueAt(rs1.getRowCount()-1, balcol);
			data.put("paynow", paynow <= 0 ? mfmt.format(0.0D) : mfmt.format(paynow));
			data.put("sumtuition", mfmt.format(Math.abs(sumtuition)));
			data.put("sumfees", mfmt.format(Math.abs(sumfees)));
			data.put("sumscholarship", sumscholarship == 0 ? "" : mfmt.format(Math.abs(sumscholarship)));
			data.put("sumpayments", mfmt.format(Math.abs(sumpayments)));
			data.put("balance", mfmt.format(Math.abs(dbal)) +
				(dbal < 0 ? " CREDIT" : ""));
			data.put("paymenttype", paymenttype);

			// Add misc stuff
			data.put("sterm", sterm);
			String studentName = studentNames.get(PayerID);
			data.put("studentname", studentName == null ? "<none>" : studentName);
			data.put("payername", rs0.getValueAt(0, mod.findColumn("payername")));
			DateFormat dfmt = new SimpleDateFormat("MMM dd, yyyy");
				dfmt.setTimeZone(app.timeZone());
			data.put("date", dfmt.format(today));
			data.put("duedate", (rs1.getRowCount() == 0 ? "Immediately" :
				dfmt.format((java.util.Date)rs1.getValueAt(0,dtcol))));
		}
		// str.put("models", models);
	}});
}
public static void doAccountStatementsAndLabels(SqlRun str, final FrontApp fapp,
final int termid, int payerid, java.util.Date dt)
throws Exception
{
	if (dt == null) dt = new java.util.Date();
	String idSql;
	if (payerid < 0) {
		int actypeid = ((Actrans2Schema)fapp.getSchema("actrans2")).actypeKmodel.getIntKey("school");
		str.execSql(AccountsDB.w_tmp_acct_balance_sql(null, actypeid, 0));
		idSql = " select e.entityid as id" +
			" from _bal, entities e" +
			" where _bal.entityid = e.entityid" +
			" and bal <> 0" +
			" order by e.lastname, e.firstname";
		LabelReport.viewReport(str, fapp, idSql, null);
	} else {
		idSql = "select " + payerid + " as id";
	}

	// Only generate statements with non-zero balances
	final AcctStatement rep = new AcctStatement(str, fapp, termid, idSql,
		fapp.sqlTypeSet().date().truncate(dt));
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		//List models = (List)str.get("models");
		Reports reports = fapp.reports(); //new OffstageReports(fapp);
		File f = reports.writeJodPdfs(rep.models, null, "AcctStatement.odt", termid, null);
		reports.viewPdf(f);
	}});
	if (payerid < 0) {
		str.execSql("drop table _bal");
	}
}
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	java.util.Calendar cal = new java.util.GregorianCalendar();
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//	
//	JodPdfWriter jout = new JodPdfWriter("/Applications/NeoOffice.app/Contents/program/soffice", new FileOutputStream("x.pdf"));
//	try {
//		HashMap data;
//		data = makeJodModel(fapp, st, 8, 12633, cal.getTime());
//		jout.writeReport(ReportOutput.openTemplateStream(fapp, "AcctStatement.odt"), data);
//		cal.add(Calendar.MONTH, 1);
//		data = makeJodModel(fapp, st, 8, 12633, cal.getTime());
//		jout.writeReport(ReportOutput.openTemplateStream(fapp, "AcctStatement.odt"), data);
//	} finally {
//		jout.close();
//	}
//	
//	Runtime.getRuntime().exec("acroread x.pdf");
//}
////public static void doTest(String oofficeExe) throws Exception
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	SqlBatchSet str = new SqlBatchSet(pool);
//	int termid = 346;
//	AcctStatement.doAccountStatementsAndLabels(str, fapp, termid, 12633, new java.util.Date());
//	str.runBatches();
//}
// ================================================================
static class BalTableModel extends DefaultJTypeTableModel
{
	public BalTableModel(int nrow) {
		super(new String[] {"balance"}, nrow);
	}
	static final JType jString = new JavaJType(String.class);
	public JType getJType(int row, int col) { return jString; }
}

/** Set up currency formatting on balance and amount columns. */
private static class StatementTableModel extends StringTableModel {
public StatementTableModel(JTypeTableModel mod, SFormatMap sfmap) {
	super(mod, sfmap);
	setSFormat("balance", NumberFormat.getCurrencyInstance());
	setSFormat("amount", NumberFormat.getCurrencyInstance());
}}
}
