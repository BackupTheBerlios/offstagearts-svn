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
package offstage.crypt;
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
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.jschema.log.*;
import offstage.crypt.*;
import offstage.swing.typed.*;
import net.sf.jasperreports.engine.data.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import java.io.*;
import citibob.text.*;
import offstage.types.*;

/**
 *
 * @author citibob
 */
public class CCDecodeTest {

static final SFormat fccnumber, fexpdate;
static {
	fccnumber = new CCSFormat();
	fexpdate = new ExpDateSFormat();
}
//	cctype.setKeyedModel(offstage.schema.EntitiesSchema.ccTypeModel);
//	ccnumber.setJType(String.class, new CCFormatter());
//	expdate.setJType(String.class, new ExpDateFormatter());
//	ccv.setJType(String.class, new DigitsFormatter(3));
//	zip.setJType(String.class, new DigitsFormatter(5));
//	ccname.setJType(String.class, new StringFormatter());
//}
// ---------------------------------------------

//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	KeyRing kr = fapp.keyRing();
//	kr.loadPrivKeys();
//	SqlTimestamp sqlt = new SqlTimestamp("GMT");
//	
//	// Process empty batch
//	int ccbatchid = DB.r_nextval(st, "ccbatch_ccbatchid_seq");
//	String sql =
//		" update ccpayments set ccbatchid = null;" +
//		" insert into ccbatches (ccbatchid) values (" + SqlInteger.sql(ccbatchid) + ");" +
//		" update ccpayments set ccbatchid = " + SqlInteger.sql(ccbatchid) +
//		" where ccbatchid is null and ccinfo is not null";
//	st.executeUpdate(sql);
//
//	// Get main report parameters
//	HashMap params = new HashMap();
//	ResultSet rs = st.executeQuery("select dtime from ccbatches where ccbatchid = " + SqlInteger.sql(ccbatchid) );
//	rs.next();
//	params.put("ccbatchid", ccbatchid);
//	params.put("dtime", sqlt.get(rs, "dtime"));
//	
//	ArrayList<Map> details = new ArrayList();
//	sql = "select e.firstname, e.lastname, p.* from ccpayments p, entities e" +
//		" where e.entityid = p.entityid" +
//		" and p.ccbatchid = " + SqlInteger.sql(ccbatchid) +
//		" order by dtime";
//	rs = st.executeQuery(sql);
//	while (rs.next()) {
//		String cryptCcinfo = rs.getString("ccinfo");
//		String ccinfo = kr.decrypt(cryptCcinfo);
////System.out.println(rs.getDouble("amount") + " " + ccinfo);
//		Map map = CCEncoding.decode(ccinfo);
//		map.put("ccbatchid", ccbatchid);
//		map.put("ccnumber", fccnumber.valueToString(map.get("ccnumber")));
//		map.put("expdate", fexpdate.valueToString(map.get("expdate")));
//		map.put("firstname", rs.getString("firstname"));
//		map.put("lastname", rs.getString("lastname"));
//		map.put("entityid", rs.getInt("entityid"));
//		map.put("dtime", sqlt.get(rs, "dtime"));
//		map.put("amount", -rs.getDouble("amount"));
//		
//		details.add(map);
//	}
//	JRMapCollectionDataSource jrdata = new JRMapCollectionDataSource(details);
////	offstage.reports.ReportOutput.viewJasperReport("CCPayments.jrxml", jrdata, params);
//	
////	// Convert ccinfo to full table representation
////	HashMap params = new HashMap();
////	InputStream in = Object.class.getResourceAsStream("/offstage/reports/CCPayments.jrxml");
////System.out.println("MailingModel2: BBB");
////	JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(in, params, jrdata);
////System.out.println("MailingModel2: CCC");
////	offstage.reports.PrintersTest.checkAvailablePrinters();		// Java/CUPS/JasperReports bug workaround for Mac OS X
////	net.sf.jasperreports.view.JasperViewer.viewReport(jprint, true);
////System.out.println("MailingModel2: DDD");
//
//	
//	
//	
////	System.exit(0);
//}

}
