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

package offstage.school.gui;

import citibob.app.App;
import citibob.jschema.SqlBufDbModel;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.pgsql.SqlInteger;

/**
 *
 * @author citibob
 */
public class LevelHistoryModel extends SqlBufDbModel
{
	public LevelHistoryModel(SqlRun str, App app) {
		super();
		keys = new Object[1];
		super.init(str, app,
			new String[] {},
			null,
			new String[] {});
	}
	public SqlSet getSelectSql(boolean proto) {
		int entityid = proto ? -1 : (Integer)getKey();
		return new SqlSet(
			" select tid.groupid as termid,pid.programid, tid.name as termname,pid.name as programname\n" +
			" from termregs tr\n" +
			" inner join termids tid on (tid.groupid = tr.groupid)\n" +
			" inner join programids pid on (pid.programid = tr.programid)\n" +
			" where tid.iscurrent\n" +
			" and entityid=" + SqlInteger.sql(entityid) + "\n" +
			"order by tid.firstdate desc\n");
	}
}
