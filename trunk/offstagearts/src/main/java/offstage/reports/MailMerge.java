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
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.io.*;
import java.sql.*;
import citibob.sql.*;
import citibob.reports.*;
import offstage.FrontApp;
import offstage.equery.EQuery;

/**
 *
 * @author citibob
 */
public class MailMerge
{

public static void viewReport(SqlRunner str, final FrontApp fapp,
EQuery equery, final File templateFile)
throws Exception
{
	
	String idSql = equery.getSql(fapp.getEquerySchema(), true);
	String sql = LabelReport.getSql(idSql, null);
	
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		Reports rr = fapp.reports();
//		Map map = new HashMap();
//		rr.viewJodPdfs(rr.toJodList(rs, null), templateFile.getParentFile(), templateFile.getName());
		rr.viewJodPdfs(rr.toJodList(rs,
//			new String[][] {{"entityid"}}),
			new String[][] {{"line1", "line2", "line3", "city", "state", "zip", "firstname"}}),
			templateFile.getParentFile(), templateFile.getName());
	}});
}

}
