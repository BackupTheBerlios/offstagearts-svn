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

import offstage.*;
import citibob.swing.table.*;
import java.util.*;
import citibob.sql.*;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.io.*;
import offstage.equery.*;

/**
 
 *
 * @author citibob
 */
public class ClauseReport
{


public JTypeTableModel model;

// Segment types available to report on
public static final String[] availSegmentTypes =
	{"classes", "events", "interests", "mailings", "notes", "status", "termenrolls", "ticketeventsales"};

// We'll select rows later; don't know whether to use ColPermuteTableModel, or some kind of
// ColPermuteJTypeTableModel, or just set up the query to only select the cols we want.
//public static final String[] availColumns = {
//	"relname", "groupname", "entityid", "customaddressto", "salutation", "firstname", "lastname",
//	 "address1", "address2", "city", "state", "zip", "orgname", "isorg", "title", "occupation", "email",
//	 "phone1type", "phone1", "phone2type", "phone2", "phone3type", "phone3", "recordsource"};
	
public ClauseReport(FrontApp fapp, SqlRun str, final SqlTypeSet tset, EQuery equery)
throws IOException
{
	// Create the main query
	String idSql = equery.getSql(fapp.getEquerySchema(), true);
	StringBuffer sql = new StringBuffer(
		" select e.entityid,customaddressto,salutation,firstname,lastname," +
		" address1,address2,city,state,zip," +
		" orgname,isorg,title,occupation,email\n" +
		" from persons e, (" + idSql + ") xx\n" +
		" where e.entityid = xx.id;\n");
	
	// Add the per-clause queries, so we know which records came from which clauses
	final List<EClause> clauses = equery.getClauses();
	for (EClause clause : clauses) {
		sql.append(equery.getSql(fapp.getEquerySchema(), clause, true));
		sql.append(";\n");
	}

	str.execSql(sql.toString(), new RssTasklet() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet[] rss) throws Exception {
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
		for (EClause clause : clauses) {
			JTypeTableModel mod = new DefaultJTypeTableModel(
				new String[] {clause.getName()},
				new JType[] {new JavaJType(Boolean.class, false)},
				nrow);
			for (int i=0; i<nrow; ++i) mod.setValueAt(Boolean.FALSE,i,0);
			while (rss[n].next()) {
				int entityid = rss[n].getInt(1);
				Integer Row = rowMap.get(entityid);
				if (Row == null) continue;		// Not included in final output, maybe a subtract clause
				mod.setValueAt(Boolean.TRUE, Row, 0);
			}
			models[n] = mod;
			++n;
		}
		
		// Now we have all the data, create one big JTypeTableModel
		MultiJTypeTableModel multi = new MultiJTypeTableModel(models);
		model = multi;
	}});
	
}

public static void writeCSV(final FrontApp fapp, SqlRun str,
EQuery equery, final File outFile) throws Exception
{
	final ClauseReport report = new ClauseReport(fapp, str,
		fapp.sqlTypeSet(), equery);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRun str) throws Exception {
		citibob.reports.Reports rr = fapp.reports();
		rr.writeCSV(rr.format(report.model), outFile);
	}});
}

}
