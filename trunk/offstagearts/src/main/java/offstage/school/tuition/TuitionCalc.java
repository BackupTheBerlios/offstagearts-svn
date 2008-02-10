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
 * JMBT.java
 *
 * Created on July 5, 2005, 7:38 PM
 */

package offstage.school.tuition;

import bsh.Interpreter;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.io.IOException;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class TuitionCalc {

// ==========================================================

int termid;
SqlDate date;
String payerIdSql;
TuitionData tdata;
TuitionScale scale;

/** @param payerIdSql IdSql that selects the payers for which we want to recalc tuition. */
public TuitionCalc(TimeZone tz, int termid)
{
	this.termid = termid;
	date = new SqlDate(tz, true);
}

public void setPayerIDs(String payerIdSql)
	{ this.payerIdSql = payerIdSql; }
public void setPayerIDs(int payerID)
	{ setPayerIDs("select " + SqlInteger.sql(payerID) + " as id"); }
public void setPayerIDs(Set<Integer> payerIDs)
	{ setPayerIDs(" select entityid from entities where entityid in " + SQL.intList(payerIDs)); }
public void setAllPayerIDs()
{
	setPayerIDs(
		" select distinct adultid from termregs tr, entities_school es" +
		" where tr.groupid = " + SqlInteger.sql(termid) +
		" and tr.entityid = es.entityid");
}


public void recalcTuition(SqlRunner str)
{
	tdata = new TuitionData(str, termid, payerIdSql, date.getTimeZone());
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		if (tdata.calcTuition) {
			calcTuition();
			String sql = writeTuitionSql();
			str.execSql(sql);
		}
	}});
}

// ==========================================================================

void calcTuition() throws bsh.EvalError
{
//	Interpreter ii = new bsh.Interpreter();
//	scale = (TuitionScale)ii.eval("new " + tdata.tuitionClass);
	scale = new jmbt.JMBTTuitionScale();
	scale.setData(tdata);

	// Go through family by family
	for (Payer payer : tdata.payers.values()) {
		scale.calcTuition(payer);
	}
}
	
/** @return SQL to update tuition records */
String writeTuitionSql()
{
	StringBuffer sql = new StringBuffer();
	
	// Produce the SQL to store this tuition calculation
	for (Payer pp : tdata.payers.values()) {
		for (Student ss : pp.students) {
			// Main tuition in student record
			sql.append(
				" update termregs" +
				" set defaulttuition=" + TuitionData.money.sql(ss.defaulttuition) + "," +
				" tuition=" + (ss.tuition == 0 ? "null" : TuitionData.money.sql(ss.tuition)) +
				" where groupid = " + SqlInteger.sql(tdata.termid) +
				" and entityid = " + SqlInteger.sql(ss.entityid) +
				";\n");

			// Don't mess with accounts if there's no tuition to be charged
			if (ss.tuition == 0) continue;
			
			// Registration fee
			DueDate reg = tdata.duedates.get("r");
			if (reg != null) {
				insertTransaction(sql, pp.entityid, reg.duedate,
					scale.getRegistrationFee(),
					"Registration Fee for " + ss.getName(),
					ss.entityid);
			}

			// Main fees
			switch(pp.billingtype) {
				case 'q' : {
					for (int i=1; i<=scale.numQuarters(); ++i) {
						DueDate dd = tdata.duedates.get("q"+i);
						
						// Main tuition
						insertTransaction(sql, pp.entityid, dd.duedate,
							ss.tuition * .25,
							ss.tuitionDesc + " --- " + dd.description,
							ss.entityid);
						
						// Scholarships
						if (ss.scholarship > 0) {
							insertTransaction(sql, pp.entityid, dd.duedate,
								-ss.scholarship * .25,
								tdata.termName + ": Scholarship for " + ss.getName() + " --- " + dd.description,
								ss.entityid);
						}
					}
				} break;
				case 'y' : {
					DueDate dd = tdata.duedates.get("y");
					
					// Main tuition
					insertTransaction(sql, pp.entityid, dd.duedate,
						ss.tuition,
							ss.tuitionDesc + " --- " + dd.description,
						ss.entityid);
					
					// Scholarships
					if (ss.scholarship > 0) {
						insertTransaction(sql, pp.entityid, dd.duedate,
							-ss.scholarship,
							tdata.termName + ": Scholarship for " + ss.getName() + " --- " + dd.description,
							ss.entityid);
					}
				} break;
			}
		}
	}
	
	return sql.toString();
}

void insertTransaction(StringBuffer sql, int entityid,
java.util.Date duedate, double amount, String description, int studentid)		
{
	sql.append(
		" insert into tuitiontrans " +
		" (entityid, actypeid, date, amount, description, studentid, termid)" +
		" values (" + SqlInteger.sql(entityid) + ", " +
		" (select actypeid from actypes where name = 'school'), " +
		date.toSql(duedate) + ", " + TuitionData.money.toSql(amount) + "," +
		SqlString.sql(description) + ", " + SqlInteger.sql(studentid) + ", " +
		SqlInteger.sql(termid) + ");\n");
	
}		

//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);
//	TuitionCalc.w_recalc(fapp.getBatchSet(), fapp.getTimeZone(), 346,
//		"select 24822 as id");
//	fapp.getBatchSet().runBatches();
//}

}
