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

import citibob.app.*;
import java.sql.*;
import citibob.reports.Reports;
import citibob.sql.*;

/**
 
 *
 * @author citibob
 */
public class LabelReport
{

//SqlDbModel main;
//String idSql;		// Set of IDs for our report

//public void doSelect(Statement st)
//throws java.sql.SQLException
//{
//	DB.createIDList(st, idSql, "ids_donor");
//	super.doSelect(st);
//	st.executeUpdate("drop table ids_donor");
//}

/** Creates a new instance of DonorReport
@param orderBy column(s) to order by.  If null, use natural order of idSql. */
public static String getSql(String idSql, String orderBy)
{
	String sql =
		// Create temporary table of IDs for this mailing list
		// These are primaryentityids (i.e. the people we REALLY want to send to)
		" CREATE TEMPORARY TABLE _mailings (\n" +
		"  orderid serial,\n" +
		"  entityid int4 NOT NULL,\n" +
		"  firstname varchar(100),\n" +
		"  ename varchar(100),\n" +
		"  addressto varchar(100),\n" +
		"  address1 varchar(100),\n" +
		"  address2 varchar(100),\n" +
		"  city varchar(50),\n" +
		"  state varchar(50),\n" +
		"  zip varchar(30),\n" +
		"  sendentityid int4,\n" +
		"  country varchar(50),\n" +
		"  line1 varchar(100),\n" +
		"  line2 varchar(100),\n" +
		"  line3 varchar(100),\n" +
		"  isgood bool,\n" +
		"  addressto2 varchar(100)\n" +
		");\n" +
		" delete from _mailings;" + 
		" insert into _mailings (entityid) " + idSql + ";\n" +
	
		"	update _mailings\n" +
		"	set firstname = p.firstname\n" +
		"	from entities p\n" +
		"	where p.entityid = _mailings.entityid;\n" +
		
		// ========= Set addressto from multiple sources
		// 1. Try customaddressto
		"	update _mailings\n" +
		"	set addressto = customaddressto\n" +
		"	from entities p\n" +
		"	where p.entityid = _mailings.entityid\n" +
		"	and p.customaddressto is not null\n" +
		"	and addressto is null;\n" +
		
		// 2. Try pre-computed names
		"	update _mailings\n" +
		"	set addressto = ename\n" +
		"	where addressto is null and ename is not null;\n" +
		
		// 3. Try addressto as name of person
		"	update _mailings\n" +
		"	set addressto = \n" +
		"		coalesce(p.firstname || ' ', '') ||\n" +
		"		coalesce(p.middlename || ' ', '') ||\n" +
		"		coalesce(p.lastname, '')\n" +
		"	from entities p\n" +
		"	where _mailings.entityid = p.entityid\n" +
		"	and addressto is null;\n" +
		
		// 4. Try addressto as name of organization
		"	update _mailings\n" +
		"	set addressto = p.name\n" +
		"	from organizations p\n" +
		"	where _mailings.entityid = p.entityid\n" +
		"	and addressto is null;\n" +

		// Set the rest of the address\n" +
		"	update _mailings\n" +
		"	set address1 = e.address1,\n" +
		"	address2 = e.address2,\n" +
		"	city = e.city,\n" +
		"	state = e.state,\n" +
		"	zip = e.zip,\n" +
		"	country = e.country\n" +
		"	from entities e\n" +
		"	where _mailings.entityid = e.entityid;\n" +
	
		// ================ Check that logical label is good
		" update _mailings set isgood = true;\n" +

		" update _mailings set address1=null where address1='';\n" +
		" update _mailings set address2=null where address2='';\n" +
		" update _mailings set zip=null where zip='';\n" +
		" update _mailings set city=null where city='';\n" +
		" update _mailings set state=null where state='';\n" +
		
		" update _mailings set isgood = false where" +
		" addressto is null" +
		" or (address1 is null and address2 is null)" +
		" or (zip is null and trim(country) = 'USA')" +
		" or city is null" +
		" or state is null;\n" +
		
		// ================== Set physical albel
		" update _mailings set line1=trim(addressto), line2=trim(address1), line3=trim(address2)" +
		" where address1 is not null and address2 is not null;\n" +

		" update _mailings set line1=null, line2=trim(addressto), line3=trim(address2)" +
		" where address1 is null and address2 is not null;\n" +
		
		" update _mailings set line1=null, line2=trim(addressto), line3=trim(address1)" +
		" where address1 is not null and address2 is null;\n" +
		
		// ================ Select, and then drop temp tables
		" select * from _mailings where isgood\n" +
		(orderBy == null ? " order by orderid" : " order by " + orderBy) + ";" +
		" drop table _mailings;";
	return sql;
}

public static void viewReport(SqlRun str, final App app, String idSql, String orderBy)
{
	String sql = LabelReport.getSql(idSql, orderBy);
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		Reports rr = app.reports();
		rr.viewJasper(rr.toJasper(rs), null, "AddressLabels.jrxml", 0);
	}});
}


//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	RollBook report = new RollBook(fapp, 8);
//	report.doSelect(st);
//	JTypeTableModel model = report.newTableModel();
//
//	HashMap params = new HashMap();
//	JRDataSource jrdata = new JRTableModelDataSource(model);
//	offstage.reports.ReportOutput.viewJasperReport("RollBook.jrxml", jrdata, params);
//
//}
}
