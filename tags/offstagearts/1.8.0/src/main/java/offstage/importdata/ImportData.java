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

package offstage.importdata;

import citibob.app.App;
import citibob.sql.SqlDateType;
import citibob.sql.SqlDouble;
import citibob.sql.SqlRun;
import citibob.sql.ansi.SqlBool;
import citibob.sql.pgsql.SqlTimestamp;
import citibob.sql.pgsql.SqlDate;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 *
 * @author citibob
 */
public class ImportData {

protected App app;
protected DateFormat dateIn = new SimpleDateFormat("MM/dd/yy");	// change depending on the file format
protected DateFormat timestampIn = new SimpleDateFormat("MM/dd/yy HH:mm:ss");	// change depending on the file format

SqlDate dateOut;
SqlTimestamp tsOut;


Map<String,Boolean> boolValues = new TreeMap();
public ImportData(App app)
{
	this.app = app;

	boolValues.put("1", Boolean.TRUE);
	boolValues.put("0", Boolean.FALSE);
	boolValues.put("t", Boolean.TRUE);
	boolValues.put("f", Boolean.FALSE);
	boolValues.put("true", Boolean.TRUE);
	boolValues.put("false", Boolean.FALSE);
	boolValues.put("y", Boolean.TRUE);
	boolValues.put("n", Boolean.FALSE);
	boolValues.put("yes", Boolean.TRUE);
	boolValues.put("no", Boolean.FALSE);
	
	dateOut = new SqlDate(app.timeZone(), true);
	tsOut = new SqlTimestamp(TimeZone.getDefault(), true);
}

// ==================================================================
// Convert directly from raw CSV input to SQL atoms
public boolean isNull(String raw)
	{ raw = raw.trim(); return (raw == null || "".equals(raw)); }

public String sInt(String raw) {
	if (isNull(raw)) return "null";
	return SqlInteger.sql(Integer.parseInt(raw));
}

public String sString(String raw) {
	if ("".equals(raw)) raw = null;
	return SqlString.sql(raw);
}

public String sDate(String raw) throws ParseException
{
	if ("".equals(raw)) return "null";
	return dateOut.toSql(dateIn.parse(raw));	
}

public String sTimestamp(String raw) throws ParseException
{
	if ("".equals(raw)) return "null";
	return tsOut.toSql(timestampIn.parse(raw));	
}

public Boolean pBool(String str)
{
	return boolValues.get(str.toLowerCase());
}


public String sBool(String sbool) throws ParseException
{
	return SqlBool.sql(pBool(sbool));
}

public String sInches(String sin)
{
	return SqlDouble.sql(pInches(sin));
}
public Double pInches(String sin)
{
	if (isNull(sin)) return null;
	
	double inches;
	sin = sin.replace('"', ' ');
	sin = sin.replace("”", "");
	sin = sin.trim();
	int space = sin.indexOf(' ');
	int slash = sin.indexOf('/');
//System.out.println("sin = " + sin);
	if (space >= 0) {
		int main = Integer.parseInt(sin.substring(0,space).trim());
		int num = Integer.parseInt(sin.substring(space+1,slash).trim());
		int denom = Integer.parseInt(sin.substring(slash+1).trim());
		inches = (double)main + (double)num / (double)denom;
	} else {
		inches = Double.parseDouble(sin);
	}
	return inches;
}

public String sZip(String zip)
{
	String main;
	String plus4;
	
	if (isNull(zip)) return "null";
	
	zip = zip.trim();
	int dash = zip.indexOf('-');
	if (dash < 0) {
		main = zip;
		plus4 = null;
	} else {
		main = zip.substring(0,dash);
		plus4 = zip.substring(dash+1);
	}
	
	while (main.length() < 5) main = "0" + main;
	if (plus4 == null) {
		return SqlString.sql(main);
	} else {
		return SqlString.sql(main + "-" + plus4);
	}
}
public String sPhone(String phone)
{
	if (isNull(phone)) return null;
	
	StringBuffer sb = new StringBuffer();
	for (int i=0; i<phone.length(); ++i) {
		char c = phone.charAt(i);
		if (Character.isDigit(c) || c == 'x' || c == 'X') sb.append(c);
	}
	phone = sb.length() == 10 ? sb.toString() : phone;
	return phone;
}

// ==================================================================
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
// ==================================================================
protected int defaultYear = 2008;
public String sDateNoYear(String sdt) throws ParseException
{
	DateFormat dfmt = new SimpleDateFormat("MM/dd/yyyy");
	dfmt.setTimeZone(app.timeZone());
	SqlDateType sqlDate = app.sqlTypeSet().date();
	
	sdt = sdt.trim();
	int slash1 = sdt.indexOf('/');
	int slash2 = sdt.indexOf('/',slash1+1);
	if (slash2 < 0) sdt = sdt + "/" + defaultYear;

	return sqlDate.toSql(dfmt.parse(sdt));
}


}
