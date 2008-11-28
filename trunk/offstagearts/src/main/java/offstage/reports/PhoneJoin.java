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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.ConsSqlQuery;
import citibob.sql.SqlQuery;

/**
 *
 * @author citibob
 */
public class PhoneJoin {

int nphones = 3;

public PhoneJoin(int nphones)
{
	this.nphones = nphones;
}

/**
 *  Makes a temporary table "pphones" that allows us to easily query a
 * "phone1", "phone2", etc. for a set of people.
 * @param str
 * @param idSql
 * @see dropPphones()
 */
public String pphonesSql(String tableName, String idSql)
{
	StringBuffer sql = new StringBuffer();
	sql.append(" create temporary table " + tableName + "\n" +
		" (id int primary key");
	for (int i=1; i<=nphones; ++i) {
		sql.append(", priority"+i+" int");
	}
	sql.append(");\n" +
		" \n" +
		" insert into " + tableName + " (id)\n" +
		" select id from (\n" + idSql + ") xx;\n" +
		" \n");
	int min = 0;
	for (int i=1; i<=nphones; ++i) {
		sql.append(
			" update " + tableName + "\n" +
			" set priority"+i+" = xx.priority\n" +
			" from (\n" +
			" 	select pp.id, min(pid.priority) as priority\n" +
			" 	from " + tableName + " pp, phones p, phoneids pid\n" +
			" 	where pp.id = p.entityid\n" +
			" 	and p.groupid = pid.groupid\n" +
			(min == 0 ? "" : "   and pid.priority > priority" + min + "\n") +
			" 	group by pp.id\n" +
			" ) xx\n" +
			" where " + tableName + ".id = xx.id;\n");
		min = i;
	}
	return sql.toString();
}

/**
 * 
 * @return SQL with column phoneid1,phonetype1,phone1, pid2,pidname2,phone2, pid3,pidname3,phone3
 * @param idTable name of table and col we're joining to (eg: "xx.id" or "persons.entityid")
 */
public void joinPphones(ConsSqlQuery sql, String tableName, String idTableCol)
{
	if (idTableCol == null) {
		sql.addTable(tableName);
	} else {
		sql.addTable(tableName, null, SqlQuery.JT_LEFT_OUTER,
			idTableCol == null ? null : "pphones.id = " + idTableCol);
	}
	for (int i=1; i<=nphones; ++i) {
		sql.addTable("phoneids", "phoneid"+i, SqlQuery.JT_LEFT_OUTER,
			"phoneid"+i+".priority = " + tableName + ".priority"+i);
		sql.addTable("phones", "phone"+i, SqlQuery.JT_LEFT_OUTER,
			"phone"+i+".groupid = phoneid"+i+".groupid" +
			" and phone"+i+".entityid = " + tableName + ".id");
	}
	
}

public String pphonesTable(String tableName)
{
	ConsSqlQuery sql = new ConsSqlQuery(ConsSqlQuery.SELECT);
	sql.addColumn(tableName + ".id");
	for (int i=1; i<=nphones; ++i) {
		sql.addColumn("phoneid"+i+".groupid as " + tableName + "_phoneid"+i);
		sql.addColumn("phoneid"+i+".name as " + tableName + "_phonename"+i);
		sql.addColumn("phone"+i+".phone as " + tableName + "_phone"+i);
	}
	joinPphones(sql, tableName, null);
	return sql.getSql();
}

public static void main(String[] args)
{
	PhoneJoin pu = new PhoneJoin(3);
	String sql =
		pu.pphonesSql("pphones", "select entityid as id from persons where lastname = 'Fischer'") +
		";\n" +
		pu.pphonesTable("pphones");
	System.out.println(sql);
}

}
