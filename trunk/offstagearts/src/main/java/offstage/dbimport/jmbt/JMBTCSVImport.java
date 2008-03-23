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
 * JMBTCSVImport.java
 *
 * Created on January 20, 2006, 3:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.dbimport.jmbt;

import offstage.dbimport.*;
import offstage.db.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class JMBTCSVImport extends CSVImport
{

/** Creates a new instance of DBImport */
public static void main(String[] args) throws Exception
{
	Connection db = new TestConnPool().checkout();
	Statement st = db.createStatement();
//	importCsvFile(st, "People");
//		String ss="\u0042\u0069\u0065\u006E\u0076" +
//         "\u0065\u006E\u0075\u0065\u0020\u0061\u0075\u0020\u00e9";
//	checkstr(ss);
	
	importCsvFile(st, "Classes");
	importCsvFile(st, "ClassesData");
	importCsvFile(st, "DonationData");
	importCsvFile(st, "DonationEvents");
	importCsvFile(st, "dup");
	importCsvFile(st, "EventData");
	importCsvFile(st, "Events");
	importCsvFile(st, "Feb16Labels");
	importCsvFile(st, "FunQUery");
	importCsvFile(st, "InformationRequests");
	importCsvFile(st, "InformationRequestsData");
	importCsvFile(st, "Mailing_backup");
	importCsvFile(st, "Mailing");
	importCsvFile(st, "MSysACEs");
	importCsvFile(st, "MSysIMEXColumns");
	importCsvFile(st, "MSysIMEXSpecs");
	importCsvFile(st, "MSysObjects");
	importCsvFile(st, "MSysQueries");
	importCsvFile(st, "MSysRelationships");
	importCsvFile(st, "OneLine");
	importCsvFile(st, "People");
	importCsvFile(st, "PeopleNotes");
	importCsvFile(st, "PlegePayments");
	importCsvFile(st, "temp");
	importCsvFile(st, "TicketData");
	importCsvFile(st, "TicketData_Hosed_Copy2");
	importCsvFile(st, "TicketData_Hosed");
	importCsvFile(st, "TicketEvents");
}

}
