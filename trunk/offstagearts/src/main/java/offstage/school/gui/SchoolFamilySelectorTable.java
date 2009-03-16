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
package offstage.school.gui;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import offstage.db.DB;
import offstage.swing.typed.FamilySelectorTable;

/**
 * Allows users to select other people from within a family.
 * @author  citibob
 */
public class SchoolFamilySelectorTable extends FamilySelectorTable
{
Integer termid;
Integer payerID;

public void setTermID(Integer termid) { this.termid = termid; }
	
public void setPayerID(SqlRun str, Integer payerID)
{
	this.payerID = payerID;
	requery(str);
}

public void requery(SqlRun str)
{
	if (payerID == null) {
		executeQuery(str, "select 0 as id where 1=0;", null);
	} else {
		SqlSet groups = DB.listRelGroupSql(str, "payerof", -1, payerID);

		SqlSet ssql = new SqlSet(groups,
			" select xx.entityid1 as id," +
			" case when head then 0 else 1 end as sort" +
			" from (" + groups.getSql() + ") xx\n");
		executeQuery(str, ssql, true, "sort, name");
	}
//	executeQuery(str,
//		" select e.entityid from entities e, termregs tr" +
//		" where tr.payerid = " + SqlInteger.sql(payerID) +
//		" and e.entityid = tr.entityid and tr.groupid = " + SqlInteger.sql(termid) +
//		" and not e.obsolete",
////		" select pe.entityid from entities_school pe, entities ee, entities_school pq" +
////		" where pq.entityid = " + SqlInteger.sql(primaryEntityID) +
////		" and pe.adultid = pq.adultid" +
////		" and pe.entityid = ee.entityid" +
////		" and not ee.obsolete",
//		"isprimary desc, name");
//

	
}
	
}
