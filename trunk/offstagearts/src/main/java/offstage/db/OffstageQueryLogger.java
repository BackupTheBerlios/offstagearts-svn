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
 * OffstageQueryLogger.java
 *
 * Created on June 8, 2007, 1:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.db;

import static citibob.sql.ConsSqlQuery.*;
import static citibob.jschema.log.QueryLogRec.*;
import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.jschema.*;
import citibob.jschema.log.*;
import citibob.task.*;
import citibob.sql.pgsql.*;

/**
 * Logs queries into Offstage's querylog and querylogcol tables.  This is pretty
 general, could be put in JSchema with a bit of thought.
 * @author citibob
 */
public class OffstageQueryLogger implements QueryLogger
{

int loginID;
JobRun runner;

/** Creates a new instance of OffstageQueryLogger */
public OffstageQueryLogger(JobRun runner, int loginID)
{
	this.runner = runner;
	this.loginID = loginID;
}

public void log(final QueryLogRec rec)
{
	runner.doRun(new StRunnable() {
	public void run(java.sql.Statement st) throws Exception
	{
		char ctype = 'X';
		switch(rec.type) {
			case UPDATE : ctype = 'U'; break;
			case INSERT : ctype = 'I'; break;
			case DELETE : ctype = 'D'; break;
		}
		int id = SQL.readInt(st, "select nextval('querylog_queryid_seq')");
//		int id = DB.r_nextval(st, "querylog_queryid_seq");
		StringBuffer sql = new StringBuffer();
		sql.append("insert into querylog (loginid, queryid, type, dbtable, dtime)" +
			" values (" + SqlInteger.sql(loginID) + ", " + SqlInteger.sql(id) + ", " + SqlChar.sql(ctype) +
			", " + SqlString.sql(rec.table) + ", now());\n");
		for (ColUpdate nv : rec.keyCols) {
			sql.append("insert into querylogcols (queryid, iskey, colname, oldval, sqlval) values (" +
				SqlInteger.sql(id) + ", true, " +
				SqlString.sql(nv.name) + ", " + SqlString.sql(nv.oldval) + ", " + SqlString.sql(nv.value) + ");\n");
		}
		for (ColUpdate nv : rec.valCols) {
			sql.append("insert into querylogcols (queryid, iskey, colname, oldval, sqlval) values (" +
				SqlInteger.sql(id) + ", false, " +
				SqlString.sql(nv.name) + ", " + SqlString.sql(nv.oldval) + ", " + SqlString.sql(nv.value) + ");\n");
		}
System.out.println(sql.toString());
		st.execute(sql.toString());
	}});
}

}
