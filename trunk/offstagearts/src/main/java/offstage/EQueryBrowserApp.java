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
//package offstage;
//import java.sql.*;
//import java.util.*;
////import com.thoughtworks.XStream.*;
//import java.io.*;
//import citibob.sql.*;
//import citibob.sql.pgsql.*;
//import javax.swing.table.*;
//import citibob.swing.*;
//import citibob.jschema.*;
//import javax.swing.event.*;
//import offstage.db.DB;
//import offstage.equery.EQuery;
//import offstage.equery.EQuerySchema;
//import offstage.equery.EQueryXStream;
//import offstage.equery.swing.EQueryTableModel2;
//import offstage.schema.*;
//
//public class EQueryBrowserApp
//{
//
///** Connection to our SQL database. */
////Statement st;
//
//EQuery equery = null;		// Currently loaded EQuery; null if none
//int equeryId;				// ID of currently-edited Equery
////File eQueryFname = null;	// Filename from which rent EQuery was loaded; null if none
////int mailingID = -1;			// ID of mailing made from loaded EQuery
//
//
//// Current EQuery and EClause being edited
//EQueryTableModel2 queryModel;
//EQuerySchema schema;
//EQueryDbModel equeriesDb;		// List of queries in the system
//
//MailingModel2 mailingModel;
////SchemaBufDbModel mailingids;
//// -------------------------------------------------------
//public EQueryBrowserApp(Statement st, MailingModel2 mailingModel, OffstageSchemaSet dbSchemaSet) throws SQLException
//{
//	schema = new EQuerySchema(st, dbSchemaSet);
//	queryModel = new EQueryTableModel2(schema);
//	equeriesDb = new EQueryDbModel(dbSchemaSet);
////	equeriesDb = new SchemaBufDbModel(new SchemaBuf(dbSchemaSet.equeries), null);
//	this.mailingModel = mailingModel;
//
////	mailings = new IntKeyedDbModel(new MailingidsSchema(), "groupid");
//
//}
//
////	//ArrayList cla = eq.getClause(0);
////System.out.println("new EClauseTableModel");
////		eQueryEditor.initRuntime(null, queryModel,clauseModel);
////		qm.setQuery(eq);
////
////}
//
//// ------------------------------------
////public int getMailingID()
////	{ return mailingID; }
//public EQuerySchema getSchema()
//	{ return schema; }
//public EQueryTableModel2 getQueryModel()
//	{ return queryModel; }
//
//public EQuery getEQuery()
//	{ return queryModel.getQuery(); }
////public File getEQueryFname()
////	{ return eQueryFname; }
//
//public SchemaBufDbModel getEqueriesDb()
//	{ return equeriesDb; }
//
//public void setEQuery(Statement st, int equeryId) throws SQLException
//{
////	eQueryFname = fname;
//	this.equeryId = equeryId;
//	String sql = "select equery from equeries where equeryid = " + equeryId;
//	ResultSet rs = st.executeQuery(sql);
//	rs.next();
//	
//	queryModel.setQuery(eq);
//	fireEQueryChanged();
//}
//
//
//
////public TableModel newMailingTableModel() throws SQLException
////{
////	IntKeyedDbModel m = new IntKeyedDbModel(new MailingidsSchema(), "groupid");
////
////	return new RSTableModel(st,
////		" select groupid, name, created from mailingids where created > now() - 30");
//////		" select addressto, address1, address2, city, state, zip, country" +
//////		" maili"
//////));
////}
//
//public void loadEQuery(File fname)
//throws IOException
//{
//	Object obj = null;
//	ObjectInputStream ois = null;
//System.out.println("Loading EQuery: " + fname.toString());
//	try {
//		// Deserialize using XML
//		FileReader fin = new FileReader(fname);
//		EQueryXStream xs = new EQueryXStream();
//		ois = xs.createObjectInputStream(fin);
//		obj = ois.readObject();
//	} catch(ClassNotFoundException e) {
//		throw new IOException("Class Not Found in Serialized File");
//	} catch(com.thoughtworks.xstream.io.StreamException se) {
//		throw new IOException("Error reading serialized file");
//	} finally {
//		try { ois.close(); } catch(Exception ee) {}
//	}
//	if (obj == null) {
//		setEQuery(new EQuery(), fname);
//	} else if (!(obj instanceof EQuery)) {
//		throw new IOException("Wrong object of class " + obj.getClass() + " found in EQuery file");
//	} else {
//		setEQuery((EQuery)obj, fname);
//	}
//}
//
//// fname != null ==> Save As
//public void saveEQuery(File fname) throws IOException
//{
//	if (fname == null) fname = eQueryFname;
////System.out.println("Saving EQuery to: " + eQueryFname.toString());
//	if (fname == null) throw new IOException("No valid file name to save as");
//
//	// Serialize using XML
//	FileWriter fout = new FileWriter(fname);
//	EQueryXStream xs = new EQueryXStream();
//	ObjectOutputStream oos = xs.createObjectOutputStream(fout);
//	oos.writeObject(getEQuery());
//	oos.close();
//
//}
//public void makeMailing(Statement st, EQuery eq) throws SQLException
//{
//	// Get the Query, in original, Sql and XML formats
//	if (eq == null) eq = getEQuery();
//	EQueryXStream xs = new EQueryXStream();
//	String eqXml = xs.toXML(eq);
//	String eqSql = eq.getSql(getSchema());
//
//	
//	String sql;
//
//	// Create the mailing list and insert EntityID records
////	sql = "select w_mailingids_create(" + SqlString.sql(eqXml) + ", " + SqlString.sql(eqSql) + ")";
////	int xmailingID = SQL.readInt(st, sql);
//	int xmailingID = DB.w_mailingids_create(st, eqXml, eqSql);
//System.out.println("Created Mailing list ID: " + xmailingID);
//	sql = "select w_mailings_correctlist(" + SqlInteger.sql(xmailingID) + ", FALSE)";
//	st.executeQuery(sql);
//	
//	mailingModel.getMailingidsDb().doSelect(st);
////	mailingModel.setMailingID(xmailingID);
//}
//// ===================================================
//public static interface Listener
//{
//	void eQueryChanged();
////	void mailingIDChanged();
//}
//public static class Adapter implements Listener
//{
//	public void eQueryChanged() {}
////	public void mailingIDChanged() {}
//}
//// ===================================================
//// ===================================================
//// Listener code
//public LinkedList listeners = new LinkedList();
//public void addListener(Listener l)
//{ listeners.add(l); }
//public void fireEQueryChanged()
//{
//	for (Iterator ii = listeners.iterator(); ii.hasNext(); ) {
//		Listener l = (Listener)ii.next();
//		l.eQueryChanged();
//	}
//}
////public void fireMailingIDChanged()
////{
////	for (Iterator ii = listeners.iterator(); ii.hasNext(); ) {
////		Listener l = (Listener)ii.next();
////		l.mailingIDChanged();
////	}
////}
//// ===================================================
//
//}
