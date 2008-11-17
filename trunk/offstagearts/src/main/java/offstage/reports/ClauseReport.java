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

import citibob.reports.Reports;
import offstage.*;
import citibob.swing.table.*;
import java.util.*;
import citibob.sql.*;
import citibob.sql.ConsSqlQuery.TableJoin;
import citibob.text.SFormat;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.io.*;
import offstage.equery.*;
import offstage.types.PhoneSFormat;

/**
 
 *
 * @author citibob
 */
public class ClauseReport
{


public JTypeTableModel model;

//// Segment types available to report on
//public static final String[] availSegmentTypes =
//	{"classes", "events", "interests", "mailings", "notes", "status", "termenrolls", "ticketeventsales"};

// We'll select rows later; don't know whether to use ColPermuteTableModel, or some kind of
// ColPermuteJTypeTableModel, or just set up the query to only select the cols we want.
//public static final String[] availColumns = {
//	"relname", "groupname", "entityid", "customaddressto", "salutation", "firstname", "lastname",
//	 "address1", "address2", "city", "state", "zip", "orgname", "isorg", "title", "occupation", "email",
//	 "phone1type", "phone1", "phone2type", "phone2", "phone3type", "phone3", "recordsource"};

/**
 * 
 * @param fapp
 * @param str
 * @param tset
 * @param equery
 * @param cols Columns to display in report
 * @param joins Tables (including persons) that we should join with
 * @throws java.io.IOException
 * @param phoneDistinctType Set to EQuery.DISTINCT_PARENT1ID if you want
 * the parent phones; otherwise, set to EQuery.DISTINCT_PRIMARYENTITYID
 */
public ClauseReport(FrontApp fapp, SqlRun str, final SqlTypeSet tset, EQuery equery,
String[] cols, TableJoin[] joins, boolean parentPhones)
//int phoneDistinctType, String phoneJoin)
throws IOException
{
	String idSql = equery.getSql(fapp.equerySchema());

	// Create temp table for our SQL
	str.execSql(
		" create temporary table _ids (id int);\n" +
		" insert into _ids " + idSql + ";\n");
	
	// Create temp table for phones
	PhoneJoin pu = new PhoneJoin(3);
	if (parentPhones) {
		str.execSql(pu.pphonesSql(
			"select parent.entityid as id" +
			" from _ids, persons e, persons parent" +
			" where _ids.id = e.entityid" +
			" and e.parent1id = parent.entityid"));
	} else {
		str.execSql(pu.pphonesSql("select id from _ids"));
	}
	
	// Create the main query
	ConsSqlQuery csql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	csql.addTable("_ids xx");
	
	// Joins
	for (TableJoin tj : joins) csql.addTable(tj);
	String phoneJoin = parentPhones ?
		"pphones.id = parent.entityid" :
		"pphones.id = xx.id";
	csql.addTable("(" + pu.pphonesTable() + ")", "pphones",
		SqlQuery.JT_LEFT_OUTER, phoneJoin);
	
	// Columns
	for (String col : cols) csql.addColumn(col);
	StringBuffer sql = new StringBuffer(csql.getSql());
	sql.append(";\n");
	
//	StringBuffer sql = new StringBuffer(
//		" select e.entityid,customaddressto,salutation,firstname,lastname," +
//		" address1,address2,city,state,zip," +
//		" orgname,isorg,title,occupation,email\n" +
//		" from persons e, (" + idSql + ") xx\n" +
//		" where e.entityid = xx.id;\n");
	
	// Add the per-clause queries, so we know which records came from which clauses
	final List<EClause> clauses = equery.getClauses();
	for (EClause clause : clauses) {
		String clauseSql = equery.getSql(fapp.equerySchema(), clause);
		if (clauseSql == null) clauseSql =				// a dummy...
			"select entityid as id from entities\n" +
			"where entityid = 0 and entityid = 1\n";

		sql.append(clauseSql);
		sql.append(";\n");
	}
	sql.append("drop table _ids;\n");

	str.execSql(sql.toString(), new RssTasklet() {
	public void run(java.sql.ResultSet[] rss) throws Exception {
		JTypeTableModel[] models = new JTypeTableModel[clauses.size() + 1];
		int n=0;
		
		// rss[0]: The main query
		RSTableModel mod0 = new RSTableModel(tset);
		mod0.executeQuery(rss[n]);
//		mod0.setRowsAndCols(rss[n]);
//		mod0.setColHeaders(rss[n]);
//		mod0.addAllRows(rss[n]);
		models[n++] = mod0;
		int nrow = mod0.getRowCount();
		
		// Set up map of entityids for join: entityid -> row in table
		Map<Integer,Integer> rowMap = new TreeMap();
		int entityidCol = mod0.findColumn("entityid");
		for (int i=0; i<nrow; ++i) {
			int entityid = (Integer)mod0.getValueAt(i, entityidCol);
			rowMap.put(entityid, i);
		}
		
		// Outer-join in rss[1..x]: Queries telling us which clauses were which
		Integer One = new Integer(1);
		//Integer Zero = new Integer(0);
		for (EClause clause : clauses) {
			JTypeTableModel mod = new DefaultJTypeTableModel(
				new String[] {clause.getName()},
				new JType[] {JavaJType.jtInteger},
				nrow);
			for (int i=0; i<nrow; ++i) mod.setValueAt(null,i,0);
			while (rss[n].next()) {
				int entityid = rss[n].getInt(1);
				Integer Row = rowMap.get(entityid);
				if (Row == null) continue;		// Not included in final output, maybe a subtract clause
				mod.setValueAt(One, Row, 0);
			}
			models[n] = mod;
			++n;
		}
		
		// Now we have all the data, create one big JTypeTableModel
		MultiJTypeTableModel multi = new MultiJTypeTableModel(models);
		model = multi;
	}});
	
}

public static void writeClauseCSV(final FrontApp fapp, SqlRun str,
EQuery equery, final File outFile) throws Exception
{
//	StringBuffer sql = new StringBuffer(
//		" select e.entityid,customaddressto,salutation,firstname,lastname," +
//		" address1,address2,city,state,zip," +
//		" orgname,isorg,title,occupation,email\n" +
//		" from persons e, (" + idSql + ") xx\n" +
//		" where e.entityid = xx.id;\n");
	final ClauseReport report = new ClauseReport(fapp, str,
		fapp.sqlTypeSet(), equery, new String[] {
			"e.entityid","customaddressto","salutation","firstname",
			"lastname",
			"phonename1","phone1","phonename2","phone2",
			"address1","address2","city","state","zip"
		}, new TableJoin[] {
			new TableJoin("persons", "e", SqlQuery.JT_INNER, "e.entityid = xx.id")
		}, false
	);
	str.execSql("drop table pphones");
	
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		Reports rr = fapp.reports();
		StringTableModel stm = rr.format(report.model);
		SFormat phoneSF = new PhoneSFormat();
		stm.setFormatU("phone1", phoneSF);
		stm.setFormatU("phone2", phoneSF);
		rr.writeCSV(stm, outFile);
	}});
}
public static void writeCastingCSV(final FrontApp fapp, SqlRun str,
EQuery equery, final File outFile) throws Exception
{
	// Figure out which show the main query is for, by digging through
	// the query.  The query must have a "showid = xxx" somewhere
	// in it.
	Integer xshowid = null;
	outer : for (EClause clause : equery.getClauses()) {
		for (Element el : clause.getElements()) {
			ColName cn = el.getColName();
			if (cn.getTable().equals("shows")
				&& cn.getSCol().equals("groupid")
				&& el.getComparator().getDisplayName().equals("=")) {
				xshowid = (Integer)el.value;
				break outer;
			}
		}
	}
	final Integer showid = xshowid;
	
	final ClauseReport report = new ClauseReport(fapp, str,
		fapp.sqlTypeSet(), equery, new String[] {
			"e.entityid", "e.firstname", "e.lastname",
			"shows.castno", "shows.showroleid", "shows.partno",
			"parent.firstname as parent_firstname", "parent.lastname as parent_lastname",
			"phonename1","phone1","phonename2","phone2",
			"parent.address1","parent.address2","parent.city","parent.state","parent.zip"
		}, new TableJoin[] {
			new TableJoin("persons", "e", SqlQuery.JT_INNER,
				"e.entityid = xx.id"),
			new TableJoin("persons", "parent", SqlQuery.JT_LEFT_OUTER,
				"e.parent1id = parent.entityid"),
			new TableJoin("shows", null, SqlQuery.JT_LEFT_OUTER,
				"shows.groupid = " + showid + " and shows.entityid = e.entityid")
		}, true
	);
	str.execSql("drop table pphones");
	
	
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
				Reports rr = fapp.reports();
		StringTableModel stm = rr.format(report.model);
		SFormat phoneSF = new PhoneSFormat();
		stm.setFormatU("phone1", phoneSF);
		stm.setFormatU("phone2", phoneSF);
		stm.setFormatU("castno", fapp.schemaSet().getKeyedModel("shows", "castno"));
		stm.setFormatU("showroleid", fapp.schemaSet().getKeyedModel("shows", "showroleid"));
//		stm.setFormatU("castno", fapp.schemaSet().getJType("shows", "castno"));
		rr.writeCSV(stm, outFile);
	}});
}

}
