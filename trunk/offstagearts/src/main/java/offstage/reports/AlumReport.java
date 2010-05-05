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
import citibob.sql.ConsSqlQuery;
import citibob.sql.ConsSqlQuery.TableJoin;
import citibob.sql.RSTableModel;
import citibob.sql.SqlQuery;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import citibob.swing.table.JTypeTableModel;
import citibob.swing.table.StringTableModel;
import citibob.text.SFormat;
import java.io.File;
import java.io.IOException;
import offstage.FrontApp;
import offstage.equery.EQuery;
import offstage.types.PhoneSFormat;


/**

 *
 * @author citibob
 */
public class AlumReport
{


/**
 *
 * @param fapp
 * @param str
 * @param tset
 * @param equery
 * @param cols Columns to display in report
 * @param joins Tables (including persons) that we should join with
 * @throws java.io.IOException
 * @param phoneDistinctType Set to EQuery.DISTINCT_househeadID if you want
 * the parent phones; otherwise, set to EQuery.DISTINCT_PRIMARYENTITYID
 */
public static JTypeTableModel newAlumReport(FrontApp fapp, SqlRun str, EQuery equery)
throws IOException
{
	String idSql = equery.getSql(fapp.equerySchema());

	// Create temp table for our SQL
	str.execSql(
		" create temporary table _ids (id int);\n" +
		" insert into _ids " + idSql + ";\n");

	// Create temp table for phones
	PhoneJoin pu = new PhoneJoin(3);
	str.execSql(pu.pphonesSql("me",
		"select distinct id from _ids"));
	str.execSql(pu.pphonesSql("head",
		"select distinct r.entityid0 as id" +
		" from _ids, rels_o2m r" +
		" where _ids.id = r.entityid1" +
		" and r.relid = (select relid from relids where name='headof')"));

	// Create the main query
	ConsSqlQuery csql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	csql.addTable("_ids xx");

	csql.addOrderClause("e.lastname, e.firstname");

	// Joins
	csql.addTable(new TableJoin("persons", "e", SqlQuery.JT_INNER,
		"e.entityid = xx.id"));
	csql.addTable(new TableJoin("rels_o2m", "rels_p1", SqlQuery.JT_LEFT_OUTER,
		" rels_p1.entityid1 = e.entityid" +
		" and rels_p1.relid = (select relid from relids where name = 'headof')"));
	csql.addTable(new TableJoin("persons", "househead", SqlQuery.JT_LEFT_OUTER,
		"rels_p1.entityid0 = househead.entityid"));

	// Phone numbers
	csql.addTable("(" + pu.pphonesTable("me") + ")", "me",
		SqlQuery.JT_LEFT_OUTER, "me.id = e.entityid");
	csql.addTable("(" + pu.pphonesTable("head") + ")", "head",
		SqlQuery.JT_LEFT_OUTER, "head.id = househead.entityid");

	// Columns
	String[] cols = new String[] {
			"e.entityid as me_entityid", "househead.entityid as head_entityid",
			"e.firstname as me_firstname", "e.lastname as me_lastname", "e.dob as me_dob",
			"me_phonename1", "me_phone1", "me_phonename2", "me_phone2", "me_phonename3", "me_phone3",
			"e.address1 as me_address1", "e.address2 as me_address2", "e.city as me_city", "e.state as me_state", "e.zip as me_zip",

			"househead.customaddressto as head_addressto",
			"househead.firstname as head_firstname", "househead.lastname as head_lastname", "househead.orgname as head_orgname",
			"head_phonename1","head_phone1","head_phonename2","head_phone2","head_phonename3","head_phone3",
			"househead.address1 as head_address1","househead.address2 as head_address2",
			"househead.city as head_city","househead.state as head_state","househead.zip as head_zip"};
	for (String col : cols) csql.addColumn(col);
	StringBuffer sql = new StringBuffer(csql.getSql());
	sql.append(";\n");

	RSTableModel model = new RSTableModel(fapp.sqlTypeSet());
	model.executeQuery(str, sql.toString());

	str.execSql("drop table me");
	str.execSql("drop table head");

	return model;
}


public static void writeAlumCSV(final FrontApp fapp, SqlRun str,
EQuery equery, final File outFile) throws Exception
{
	final JTypeTableModel model = newAlumReport(fapp, str, equery);

	str.execUpdate(new UpdTasklet() {
	public void run() throws Exception {

		Reports rr = fapp.reports();
		StringTableModel stm = rr.format(model);
		SFormat phoneSF = new PhoneSFormat();
		stm.setFormatU("me_phone1", phoneSF);
		stm.setFormatU("me_phone2", phoneSF);
		stm.setFormatU("me_phone3", phoneSF);
		stm.setFormatU("head_phone1", phoneSF);
		stm.setFormatU("head_phone2", phoneSF);
		stm.setFormatU("head_phone3", phoneSF);
		rr.writeCSV(stm, outFile);
	}});
}


}
