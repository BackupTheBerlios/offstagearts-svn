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
///*
// * MailingDbModel.java
// *
// * Created on July 11, 2005, 10:31 PM
// */
//
//package offstage;
//import citibob.jschema.*;
//import citibob.swing.*;
//import citibob.sql.*;
//import citibob.multithread.*;
//import java.sql.*;
//import java.sql.*;
//import java.io.*;
//import net.sf.jasperreports.engine.*;
//import java.util.*;
//import javax.swing.event.*;
//import javax.swing.*;
//import offstage.db.DB;
//import offstage.schema.*;
//
///**
// *
// * @author citibob
// */
//public class MailingModel {
//
//WhereClauseDbModel mailingids;	// List of mailings
////SchemaBufRowModel curMailingid;		// mailingids record for currently selected mailing list
////int mailingID;					// Current mailing ID
//IntKeyedDbModel mailings;		// Set to data for currently selected mailing list
////Statement st;
//int groupidCol;
////ConnPool pool;
//ActionRunner runner;		// Needs an appRunner...
//
//public IntKeyedDbModel getMailings() { return mailings; }
////public SchemaBuf getMailingsSb()
////	{ return mailings.getSchemaBuf(); }
////public SchemaRowModel getCurMailingidRm()
////	{ return curMailingid; }
//public SchemaBufDbModel getMailingids()
//	{ return mailingids; }
////public ListSelectionModel getMailingidsSelectModel()
////	{ return curMailingid; }
//
//// -------------------------------------------------------------
///** Gets mailing id of a particular row in the mailingids table. */
//int getMailingID(int row)
//{
//	SchemaBuf sb = getMailingids().getSchemaBuf();
//	Integer ii = (Integer)sb.getValueAt(row, groupidCol);
//	return ii.intValue();
//}
//
///** Gets the row on which a given mailingID is stored. */
//int getMailingIDRow(int mailingID)
//{
//	SchemaBuf sb = getMailingids().getSchemaBuf();
//	for (int i=0; i < sb.getRowCount(); ++i) {
//		Integer M = (Integer)sb.getValueAt(i, groupidCol);
//		if (M.intValue() == mailingID) return i;
//	}
//	return -1;
//}
//// -------------------------------------------------------------
/////** Gets the ID of the currently selected (& displayed) mailing.  Returns -1 if none selected. */
////public int getMailingID()
////{
////	int row = curMailingid.getCurRow();
////	if (row == -1) return -1;
////	return getMailingID(row);
////}
////public void setMailingID(int mailingID)
////{
////	int row = getMailingIDRow(mailingID);
////	curMailingid.setCurRow(row);
////	mailings.setKey(mailingID);
////}
//public void refreshMailingids(Statement st) throws SQLException
//{
//	mailingids.doSelect(st);
//}
//
///** Creates a new instance of MailingDbModel */
//public MailingModel(Statement st, OffstageSchemaSet sset, ActionRunner xrunner)
//throws SQLException {
//	this.runner = xrunner;
//	//this.st=st;
//	mailings = new IntKeyedDbModel(sset.mailings, "groupid", false, null);
////	mailings.setInstantUpdate(xrunner, true);
//	
//	mailingids = new WhereClauseDbModel(sset.mailingids,
//		"created >= now() - interval '30 days'", "created desc", null);
//    mailingids.doSelect(st);
////	add(mailingids, "groupid", false));
////	mailingids.setInstantUpdate(xrunner, true);
//	
//	SchemaBuf sb = getMailingids().getSchemaBuf();
//	groupidCol = sb.findColumn("groupid");
////	curMailingid = new SchemaBufRowModel(sb, false);
//
////	// Pass selection events from mailingids to mailings
////	curMailingid.addListSelectionListener(new ListSelectionListener() {
////	public void  valueChanged(ListSelectionEvent e) {
////		runner.doRun(new StRunnable() {
////		public void run(Statement st) throws Exception {
////			int id = getMailingID();
////			mailings.setKey(id);
////System.out.println("Selected mailing: " + id);
////			mailings.doSelect(st);
////		}});
////	}});
//}
//
//
//public void makeReport(Statement st) throws SQLException, JRException
//{
//	ResultSet rs = null;
//	InputStream in = null;
//	try {
//		DB.w_mailings_makereport(st, getMailingID());
//
//		in = Object.class.getResourceAsStream("/offstage/reports/AddressLabels.jrxml");
//		String sql =
//			"select * from mailings" +
//			" where groupid=" + getMailingID() +
//			" and isgood = 't'" +
//			" order by country, zip";
//		rs = st.executeQuery(sql);
//		HashMap params = new HashMap();
//		JRResultSetDataSource jrdata = new JRResultSetDataSource(rs);
//		JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(in, params, jrdata);
//		net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
//	} finally {
//		try { rs.close(); } catch(Exception e) {}
//		try { in.close(); } catch(Exception e) {}		
//	}
//
//	
//}
//
//}
