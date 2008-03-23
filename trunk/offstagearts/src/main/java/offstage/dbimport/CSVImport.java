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
 * DBImport.java
 *
 * Created on July 24, 2005, 5:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.dbimport;

import com.Ostermiller.util.*;
import java.io.*;
import java.sql.*;
import offstage.db.TestConnPool;
import offstage.db.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class CSVImport {
	
public static void importCsvFile(Statement st, String table)
throws UnsupportedEncodingException, IOException, SQLException
{
	File fin = new File("/export/home/citibob/jmbt/accessexport/" + table + ".csv");
	importCsvFile(st, fin, "aa" + table);
}

public static void importCsvFile(Statement st, File fin, String table)
throws UnsupportedEncodingException, IOException, SQLException
{
	BufferedReader in = new BufferedReader(
		new InputStreamReader(new FileInputStream(fin), "ISO8859_1"));
	importCsvTable(st, in, table);
}
private static void checkstr(String s)
{
	boolean isuni = false;
	for (int i=0; i < s.length(); ++i) {
		if (((int)s.charAt(i)) > 127) isuni = true;
	}
	if (!isuni) return;
	System.out.println(s + ":");
	for (int i = 0; i<s.length(); ++i) System.out.print(" " + (int)(s.charAt(i)));
	System.out.println("");
}

public static void importCsvTable(Statement st, Reader in, String table)
throws IOException, SQLException
{
	CSVParser csvin = new CSVParser(in);
	String[] line;

	// First line of file is column names
	line = csvin.getLine();
	
	try {
		st.executeUpdate("drop table " + table);
	} catch(SQLException e) {}
	String sql = "create table " + table + "(";
	for (int i=0; i < line.length; ++i) {
		if (i != 0) sql += ", ";
		String s = line[i];
		s = s.replace(' ', '_');
		if (s.equalsIgnoreCase("order")) s = "_" + s;
		sql += s + " varchar(300)";
	}
	sql += ")";
System.out.println(sql);
	st.executeUpdate(sql);
	
	while ((line = csvin.getLine()) != null) {
		sql = "insert into " + table + " values (" + SqlString.sql(line[0]);
		for (int i=1; i < line.length; ++i) {
			checkstr(line[i]);
			sql += ", " + SqlString.sql(line[i]);
		}
		sql += ")";
		st.executeUpdate(sql);
	}
}


public static void importCsvTable2(Statement st, BufferedReader in, String table)
throws IOException, SQLException
{
	while (true) {
		String s = in.readLine();
		if (s == null) return;
		checkstr(s);
	}
}

}
