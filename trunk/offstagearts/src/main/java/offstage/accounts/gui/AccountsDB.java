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
 * AccountsDB.java
 *
 * Created on December 14, 2007, 12:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.accounts.gui;

import citibob.app.App;
import citibob.sql.ConsSqlQuery;
import citibob.sql.SQL;
import citibob.sql.pgsql.SqlDate;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;
import citibob.wizard.TypedHashMap;
import java.util.Date;
import offstage.school.tuition.TuitionData;

/**
 *
 * @author citibob
 */
public class AccountsDB
{
	
/** @param idSql SQL to return a set of entityids to report on.
 * @param actypeid Which sub-account (openclass, etc)
 * @param assetid Which asset we wish to report on (0=$)
 * @returns SQL for vector-valued asset
 */
public static String w_tmp_acct_balance_sql(String idSql, final int actypeid, final int assetid)
{
	if (idSql == null) idSql =
		" select distinct cr_entityid as id from actrans2 where actypeid = "+ SqlInteger.sql(actypeid) +
		" UNION" +
		" select distinct db_entityid as id from actrans2 where actypeid = "+ SqlInteger.sql(actypeid) +
		" UNION" +
		" select distinct entityid as id from acbal2 where actypeid = "+ SqlInteger.sql(actypeid);
	String sql =
		" create temporary table _bal (entityid int, assetid int default -1, date date, bal numeric(9,2) default 0);\n" +
		" insert into _bal (entityid) " + idSql + ";\n" +
		" update _bal set assetid = " + assetid + ";\n" +
		
		" update _bal" +
		" set bal = acbal.bal" +
		" from acbal" +
		" where _bal.entityid = acbal.entityid\n" +
		" and actypeid = " + SqlInteger.sql(actypeid) + ";\n" +

		" update _bal" +
		" set bal=bal + cr.amount - db.amount\n" +
		" from (\n" +
		"     select actrans2.cr_entityid as entityid, sum(actrans2amt.amount) as amount" +
		"     from actrans2, actrans2amt, _bal" +
		"     where cr_actypeid = " + SqlInteger.sql(actypeid) +
		"     and _bal.entityid = actrans2.cr_entityid" +
		"     and actrans2.actransid = actrans2amt.actransid" +
		"     and actrans2amt.assetid = " + assetid +
		"     and (_bal.date is null or actrans2.date >= _bal.date)" +
		"     group by actrans2.cr_entityid) cr,\n" +
		" (select actrans2.db_entityid as entityid, sum(actrans2amt.amount) as amount" +
		"     from actrans2, actrans2amt, _bal" +
		"     where db_actypeid = " + SqlInteger.sql(actypeid) +
		"     and _bal.entityid = actrans2.db_entityid" +
		"     and actrans2.actransid = actrans2amt.actransid" +
		"     and actrans2amt.assetid = " + assetid +
		"     and (_bal.date is null or actrans2.date >= _bal.date)" +
		"     group by actrans2.db_entityid" +
		
		" \n) db" +
		" where cr.entityid = _bal.entityid and db.entityid = _bal.entityid;\n";
		
//		" drop table _bal0";
	
	return sql;
}


/** Creates the proper double-entry accounting record.  Seeks to always make USD be 0
 * @param sinkName Name of the "sink" account for one of the entries; see acsinks table.
 * @param acTransTypeName Kind of transaction.  See actranstypes table.
 * @param entityid Account to CREDIT
 * @param date Date of the transaction
 * @param datecreated Date we entered the transaction into the system
 * @param optional Values for optional columns in actrans2
 * @param amounts amount to CREDIT to entityid; if debit, amount should be <0
 */
public static String w_actrans2_insert_sql(App app, int entityid,
String sinkName, int actypeid,
String acTransTypeName,
Date date, TypedHashMap optional,
int[] assetids, double[] amounts)
{
	ConsSqlQuery csql = new ConsSqlQuery("actrans2", ConsSqlQuery.INSERT);
	
	// Find which asset is the cash asset, and make sure it's positive in amounts
	boolean reverse = false;
	for (int i=0; i<assetids.length; ++i) {
		if (assetids[i] == 0) {
			if (amounts[i] > 0) break;		// Already positive, nothing to do
			
			// Convert it to positive
			reverse = true;
			for (int j=0; j<assetids.length; ++j) amounts[i] = -amounts[i];
		}
	}

	// Set up which account is "credited" and which is "debited"
	if (!reverse) {
		csql.addColumn("cr_entityid", SqlInteger.sql(entityid));
		csql.addColumn("db_entityid", "(select entityid from entities" +
			" where orgname = " + SqlString.sql(sinkName) + " and sink)");
	} else {
		csql.addColumn("db_entityid", SqlInteger.sql(entityid));
		csql.addColumn("cr_entityid", "(select entityid from entities" +
			" where orgname = " + SqlString.sql(sinkName) + " and sink)");
	}
	
	// Add required fields
	csql.addColumn("cr_actypeid", SqlInteger.sql(actypeid));
	csql.addColumn("db_actypeid", SqlInteger.sql(actypeid));
	csql.addColumn("actranstypeid",
		"(select actranstypeid from actranstypes where name = " + SqlString.sql(acTransTypeName) + ")");
	SqlDate sqlDate = new SqlDate(app.timeZone(), false);
	csql.addColumn("date", sqlDate.toSql(date));		// Store day that it is in home timezone
	csql.addColumn("datecreated", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone

	// Add our optional columns. They won't overwrite existing columns.
	SQL.addColumns(csql, optional, app.getSchema("actrans2"));
	
	// Add on inserts for vector asset amounts
	StringBuffer sql = new StringBuffer(csql.getSql() + ";\n");
	for (int i=0; i<assetids.length; ++i) {
		sql.append("insert into actrans2amt (actransid, assetid, amount) values (lastval(), " +
			assetids[i] + ", " + TuitionData.money.toSql(amounts[i]) + ");\n");
	}

	return sql.toString();
}

public static String w_actrans2_deleteOpenClassByMeeting_sql(int entityid, int meetingid)
{
	String sql =
// Not needed, deletes are cascaded.
//		// Remove payment amount records
//		" delete from actrans2amt where actransid in (" +
//		" select actransid from actrans2" +
//		" where entityid = " + SqlInteger.sql(entityid) + "\n" +
//		" and actypeid = (select actypeid from actypes where name = 'openclass')\n " +
//		" and termid = " + SqlInteger.sql(meetingid) + ");\n" +
	
		// Remove payment record
		" delete from actrans" +
		" where entityid = " + SqlInteger.sql(entityid) + "\n" +
		" and actypeid = (select actypeid from actypes where name = 'openclass')\n " +
		" and termid = " + SqlInteger.sql(meetingid) + ";\n";

	return sql;
}
}
