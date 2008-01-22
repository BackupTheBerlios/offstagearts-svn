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
package offstage.crypt.wiz;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import citibob.swing.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import offstage.crypt.*;
import java.util.*;
import citibob.text.*;
import citibob.reports.*;

/**
 *
 * @author citibob
 */
public class CCBatchWizard extends OffstageWizard {
	
public CCBatchWizard(offstage.FrontApp xfapp, java.awt.Frame xframe)
{
	super("New Key", xfapp, xframe, "initial");

addState(new AbstractWizState("initial", null, "insertkey1") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new CCBatchInitial(frame, con.str, fapp);
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.getKeyRing();
		if (kr.privKeysLoaded()) {
			processBatch(con.str);
			stateName = null;
		}
	}
});
// ---------------------------------------------
addState(new AbstractWizState("insertkey1", null, "removekey1") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Remove Key", true,
			getResourceName("ccbatch_InsertKey1.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.getKeyRing();
		if (!kr.isUsbInserted()) stateName = "keynotinserted";
		else {
			try {
				kr.loadPrivKeys();
			} catch(Exception e) {
				e.printStackTrace();
				stateName = "keyerror";
			}
		}
	}
});
// ---------------------------------------------
addState(new AbstractWizState("removekey1", null, "insertkey2") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Remove Key", true,
			getResourceName("ccbatch_RemoveKey1.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		if (fapp.getKeyRing().isUsbInserted()) stateName = "keynotremoved";
		else processBatch(con.str);
//		KeyRing kr = fapp.getKeyRing();
	}
});
// ---------------------------------------------
// ---------------------------------------------
addState(new AbstractWizState("keyerror", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Error", true,
			getResourceName("dupkey_KeyError.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotinserted", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Not Inserted", true,
			getResourceName("KeyNotInserted.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotremoved", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Not Removed", true,
			getResourceName("KeyNotRemoved.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
}
// ======================================================
static final SFormat fccnumber, fexpdate;
static {
	fccnumber = new offstage.types.CCSFormat();
	fexpdate = new offstage.types.ExpDateSFormat();
}

void processBatch(SqlRunner str)
//throws SQLException, java.io.IOException,
//java.security.GeneralSecurityException, java.text.ParseException, JRException
{
	final SqlTimestamp sqlt = new SqlTimestamp("GMT");
	final SqlDate sqld = new SqlDate(fapp.getTimeZone(), false);

	// Process empty batch
	SqlSerial.getNextVal(str, "ccbatch_ccbatchid_seq");
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) {
		final int ccbatchid = (Integer)str.get("ccbatch_ccbatchid_seq");
		String sql =
			" insert into ccbatches (ccbatchid) values (" + SqlInteger.sql(ccbatchid) + ");" +
			
			" update ccpayments set ccbatchid = " + SqlInteger.sql(ccbatchid) +
			" where ccbatchid is null and ccinfo is not null;\n"+
			// rss[0]
			"select dtime from ccbatches where ccbatchid = " + SqlInteger.sql(ccbatchid) + ";\n" +
			// rss[1]
			"select e.firstname, e.lastname, p.* from ccpayments p, entities e" +
				" where e.entityid = p.entityid" +
				" and p.ccbatchid = " + SqlInteger.sql(ccbatchid) +
				" order by date";
		str.execSql(sql, new RssRunnable() {
		public void run(SqlRunner str, ResultSet[] rss) throws Exception {
			ResultSet rs;
			
			// =============== rss[0]: incidental items
			rs = rss[0];
			rs.next();
			
			final HashMap params = new HashMap();
			params.put("ccbatchid", ccbatchid);
			params.put("dtime", sqlt.get(rs, "dtime"));
			rs.close();

			// =============== rss[1]: main report
			rs = rss[1];
			KeyRing kr = fapp.getKeyRing();
			ArrayList<Map> details = new ArrayList();
			while (rs.next()) {
				String cryptCcinfo = rs.getString("ccinfo");
				String ccinfo = kr.decrypt(cryptCcinfo);
		//System.out.println(rs.getDouble("amount") + " " + ccinfo);
				Map map = CCEncoding.decode(ccinfo);
				map.put("ccbatchid", ccbatchid);
				map.put("ccnumber", fccnumber.valueToString(map.get("ccnumber")));
				map.put("expdate", fexpdate.valueToString(map.get("expdate")));
				map.put("firstname", rs.getString("firstname"));
				map.put("lastname", rs.getString("lastname"));
				map.put("entityid", rs.getInt("entityid"));
				map.put("actransid", rs.getInt("actransid"));
				map.put("date", sqld.get(rs, "date"));
				map.put("amount", -rs.getDouble("amount"));

				details.add(map);
			}
			Reports rr = fapp.getReports();
			rr.viewJasper(rr.toJasper(details), params, null, "CCPayments.jrxml");
//			JRMapCollectionDataSource jrdata = new JRMapCollectionDataSource(details);
//			offstage.reports.ReportOutput.viewJasperReport(fapp, "CCPayments.jrxml", jrdata, params);
		}});
	}});

}



}
