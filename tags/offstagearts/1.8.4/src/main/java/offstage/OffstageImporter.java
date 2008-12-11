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
/*p
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage;

import citibob.app.App;
import citibob.imp.Importer;
import citibob.sql.SqlRun;
import citibob.sql.pgsql.SqlString;

/**
 *
 * @author citibob
 */
public class OffstageImporter extends Importer
{


public OffstageImporter(App app, int defaultYear)
{
	super(app, defaultYear);
}


public void addNote(SqlRun str, String sentityid,
String note, String noteids_name)
{
	if (isNull(note)) return;
	if (noteids_name == null) noteids_name = "NOTES";
	
	String sql =
		" insert into notes (entityid, groupid, date, note) values (" +
		sentityid + ", " +
		"(select groupid from noteids" +
		" where name = " + SqlString.sql(noteids_name) + "),\n" +
		"now(), " + SqlString.sql(note) +
		")";
	str.execSql(sql);
}
public void addPhone(SqlRun str, String sentityid,
String phone, String phoneids_name)
{
	phone = sPhone(phone);
	if (phone == null) return;
	
	String sql =
		" insert into phones (entityid, groupid, phone) values (" +
		sentityid + ", " +
		"(select groupid from phoneids" +
		" where name = " + SqlString.sql(phoneids_name) + "),\n" +
		SqlString.sql(phone) +
		")";
	str.execSql(sql);
}

public void addExtraEmail(SqlRun str, String sentityid,
String email, String emailids_name)
{
	if (isNull(email)) return;
	
	String sql =
		" insert into emails (entityid, groupid, email) values (" +
		sentityid + ", " +
		"(select groupid from emailids" +
		" where name = " + SqlString.sql(emailids_name) + "),\n" +
		SqlString.sql(email) +
		")";
	str.execSql(sql);
}


}
