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

import citibob.sql.AdhocOJSqlTableModel;
import offstage.*;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.sql.*;
import offstage.db.*;
import citibob.sql.pgsql.*;
import java.io.*;

/**
 
 *
 * @author citibob
 */
public class SegmentationReport extends RSTableModel
{

// Segment types available to report on
public static final String[] availSegmentTypes =
	{"classes", "events", "interests", "mailings", "notes", "status", "termenrolls", "ticketeventsales"};

// We'll select rows later; don't know whether to use ColPermuteTableModel, or some kind of
// ColPermuteJTypeTableModel, or just set up the query to only select the cols we want.
//public static final String[] availColumns = {
//	"relname", "groupname", "entityid", "customaddressto", "salutation", "firstname", "lastname",
//	 "address1", "address2", "city", "state", "zip", "orgname", "isorg", "title", "occupation", "email",
//	 "phone1type", "phone1", "phone2type", "phone2", "phone3type", "phone3", "recordsource"};
	
public SegmentationReport(SqlRun str, SqlTypeSet tset, String idSql,
List<String> segmentTypes)
{
	super(tset);

	String sseg = "";
	for (String ss : segmentTypes) sseg += "," + SqlString.sql(ss);
	sseg = sseg.substring(1);

	String sql =
		" create temporary table pphones\n" +
		" (entityid int primary key, priority1 int, priority2 int, priority3 int);\n" +
		" --(entityid int, priority1 int, phoneid1 int, priority2 int, phoneid2 int, priority3 int, phoneid3 int);\n" +
		" \n" +
		" insert into pphones (entityid)\n" +
		" select id from (\n" + idSql + ") xx;\n" +
		" \n" +
		" update pphones\n" +
		" set priority1 = xx.priority\n" +
		" from (\n" +
		" 	select pp.entityid, min(pid.priority) as priority\n" +
		" 	from pphones pp, phones p, phoneids pid\n" +
		" 	where pp.entityid = p.entityid\n" +
		" 	and p.groupid = pid.groupid\n" +
		" 	group by pp.entityid\n" +
		" ) xx\n" +
		" where pphones.entityid = xx.entityid;\n" +
		" \n" +
		" --update pphones set phoneid1 = pid.groupid\n" +
		" --from phoneids pid where pid.priority = priority1;\n" +
		" \n" +
		" \n" +
		" update pphones\n" +
		" set priority2 = xx.priority\n" +
		" from (\n" +
		" 	select pp.entityid, min(pid.priority) as priority\n" +
		" 	from pphones pp, phones p, phoneids pid\n" +
		" 	where pp.entityid = p.entityid\n" +
		" 	and p.groupid = pid.groupid\n" +
		" 	and pid.priority > pp.priority1\n" +
		" 	group by pp.entityid\n" +
		" ) xx\n" +
		" where pphones.entityid = xx.entityid;\n" +
		" \n" +
		" --update pphones set phoneid2 = pid.groupid\n" +
		" --from phoneids pid where pid.priority = priority2;\n" +
		" \n" +
		" update pphones\n" +
		" set priority3 = xx.priority\n" +
		" from (\n" +
		" 	select pp.entityid, min(pid.priority) as priority\n" +
		" 	from pphones pp, phones p, phoneids pid\n" +
		" 	where pp.entityid = p.entityid\n" +
		" 	and p.groupid = pid.groupid\n" +
		" 	and pid.priority > pp.priority2\n" +
		" 	group by pp.entityid\n" +
		" ) xx\n" +
		" where pphones.entityid = xx.entityid;\n" +
		" \n" +
		" --update pphones set phoneid3 = pid.groupid\n" +
		" --from phoneids pid where pid.priority = priority3;\n" +
		" \n" +
		" select pgc.relname, gid.name as groupname,\n" +
		" e.entityid, e.customaddressto, e.salutation, e.firstname, e.lastname,\n" +
		" e.address1, e.address2, e.city, e.state, e.zip,\n" +
		" e.orgname, e.isorg, e.title, e.occupation, e.email,\n" +
		" pid1.name as phone1type, p1.phone as phone1,\n" +
		" pid2.name as phone2type, p2.phone as phone2,\n" +
		" pid3.name as phone3type, p3.phone as phone3,\n" +
		" e.recordsource\n" +
		" from groups g, groupids gid, persons e, pg_class pgc, pphones pp\n" +
		" left outer join phoneids as pid1 on (pid1.priority = pp.priority1)\n" +
		" left outer join phones as p1 on (p1.groupid = pid1.groupid and p1.entityid = pp.entityid)\n" +
		" left outer join phoneids as pid2 on (pid2.priority = pp.priority2)\n" +
		" left outer join phones as p2 on (p2.groupid = pid2.groupid and p2.entityid = pp.entityid)\n" +
		" left outer join phoneids as pid3 on (pid3.priority = pp.priority3)\n" +
		" left outer join phones as p3 on (p3.groupid = pid3.groupid and p3.entityid = pp.entityid)\n" +
		" where e.entityid = pp.entityid\n" +
		" and e.entityid = g.entityid\n" +
		" and g.groupid = gid.groupid\n" +
		" and pgc.oid = g.tableoid\n" +
		" and pgc.relname in (" + sseg + ")\n" +
//		" and pgc.relname not in ('phones', 'mailings')\n" +
		" order by e.lastname,e.firstname,e.entityid,relname, gid.name, e.lastname, e.firstname;\n" +
		" drop table pphones;\n";
	super.executeQuery(str, sql);
}

public static void writeCSV(final App app, SqlRun str,
String idSql, List<String> segmentTypes, final File outFile) throws Exception
{
	final SegmentationReport report = new SegmentationReport(str, app.sqlTypeSet(),
		idSql, segmentTypes);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		citibob.reports.Reports rr = app.reports();
		rr.writeCSV(rr.format(report), outFile);
	}});
}

}
