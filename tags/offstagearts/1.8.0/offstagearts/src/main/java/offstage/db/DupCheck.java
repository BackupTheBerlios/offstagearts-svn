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
 * DupCheck.java
 *
 * Created on October 9, 2006, 12:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.db;

import citibob.wizard.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import java.util.*;
import citibob.sql.*;
import citibob.util.IntVal;

/**
 * Try to find possible duplicate records, given the data from one record
 * (or prospective record)
 * @author citibob
 */
public class DupCheck {

//TypedHashMap v;
// Results of trying to parse


// Parsed Data
String streetName;
String zip;
String lastName;
String firstName;
String phone;
String email;

/** Higher score means more likely to be a duplicate. */
static class Score
{
	int entityID;
	int score;
}
HashMap scores;
int nfields;		// # fields scores is based upon...
// -------------------------------------------------------------
String getString(TypedHashMap v, String name)
{
	String addr1 = v.getString(name);
	if (addr1 == null) addr1 = "";
	else addr1 = addr1.trim();
	return addr1;
}
String getStringNull(TypedHashMap v, String name)
{
	String addr1 = v.getString(name);
	if (addr1 == null) return null;
	addr1 = addr1.trim();
	if ("".equals(addr1)) return null;
	return addr1;
}

String[] splitWords(String s)
{
	s = s.replace("\t", " ");
	s = s.replace("\n", " ");
	String[] tok = s.split(" ");
	return tok;
}

static boolean containsNumeric(String s)
{
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') return true;
	}
	return false;
}

static boolean isNumeric(String s)
{
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') continue;
		return false;
	}
	return true;
}

void parse(TypedHashMap v)
//throws java.text.ParseException
{
	// Try to parse out the street name
	streetName = null;
	String addr1 = getString(v, "addr1");
	String addr2 = getString(v, "addr2");
	String addr = "".equals(addr2) ? addr1 : addr2;
	if (!"".equals(addr)) {
		String[] tok = splitWords(addr);
		if (!(isNumeric(tok[0]))) {
			// Forget about address; probably a PO Box
		} else {
			// First word not containing number is probably street name
			for (int i=0; i<tok.length; ++i) {
				if (!containsNumeric(tok[i])) {
					streetName = tok[i];
					break;
				}
			}
		}
		if ("".equals(streetName)) streetName = null;
	}
	
	// Parse out 5-digit zip code
	zip = null;
	String z = getString(v, "zip");
	if (z.length() >= 5) {
		z = z.substring(0,5);
		if (isNumeric(z)) zip = z;
	}
	
	// Get first and last name --- easy!
	lastName = getStringNull(v, "lastname");
	firstName = getStringNull(v, "firstname");
	
	// Parse out phone number
//	phone = offstage.types.PhoneFormatter.unformat(getStringNull("phone"));
	phone = getStringNull(v, "phone");
	email = getStringNull(v, "email");
}
// --------------------------------------------------------------
public void addScore(Integer EntityID)
{
	Score s = (Score)scores.get(EntityID);
	if (s == null) {
		s = new Score();
		s.entityID = EntityID.intValue();
		s.score = 1;
		scores.put(EntityID, s);
	} else {
		++s.score;
	}
}

void addScores(SqlRun str, String table, String whereClause)
//throws SQLException
{
	String sql = "select distinct entityid from " + table + " where " + whereClause;
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws SQLException {
		while (rs.next()) {
			Integer EntityID = (Integer)rs.getObject(1);
			addScore(EntityID);
		}
		++nfields;
		rs.close();
	}});
}
void addScores(SqlRun str, String whereClause)
//throws SQLException
{
	addScores(str, "entities", "not obsolete and " + whereClause);
}
/** Returns entityid of possible dups */
void scoreDups(SqlRun str)
//throws SQLException
{
	scores = new HashMap();
	String sql;

	if (firstName != null)
		addScores(str, "firstname ilike " + SqlString.sql("%" + firstName + "%"));
	if (lastName != null)
		addScores(str, "lastname ilike " + SqlString.sql("%" + lastName + "%"));
	if (streetName != null)
		addScores(str, "address1 ilike " + SqlString.sql("%" + streetName + "%") +
			" or address2 ilike " + SqlString.sql("%" + streetName + "%"));
	if (zip != null)
		addScores(str, "zip ilike " + SqlString.sql("%" + zip + "%"));
	if (phone != null)
		addScores(str, "phones", "phone = " + SqlString.sql(phone));
	if (email != null)
		addScores(str, "persons", "email ilike " + SqlString.sql(email));
	
}
// --------------------------------------------------------------------
/** Creates a query that selects out all the dups of score >= minScore.
 Returns null if >maxDups entities fit the bill.  This will be used
 in EntityListTableModel. */
public String getIDSql(int minScore, int maxDups)
{
	StringBuffer sql = new StringBuffer(
		"select entityid as id from entities where not obsolete and entityid in (-1");
	int nid = 0;
	for (Iterator ii=scores.entrySet().iterator(); ii.hasNext(); ) {
		Map.Entry e = (Map.Entry)ii.next();
		Score s = (Score)e.getValue();
		if (s.score >= minScore || s.score == nfields) {
			if (++nid > maxDups) return null;
			sql.append(",");
			sql.append(e.getKey());
		}
	}
	sql.append(")");
	return sql.toString();
}
// --------------------------------------------------------------
/** Creates a new instance of DupCheck */
public DupCheck(SqlRun str, TypedHashMap v)
//throws SQLException
{
	parse(v);
	scoreDups(str);
}
// --------------------------------------------------------------
///** @param v a set of (name,value) pairs corresponding to wizard screen or database row. */
//public static void checkDups(SqlRun str, TypedHashMap v,
//final int minScore, final int maxDups, final UpdTasklet2 rr)
////throws SQLException
//{
//	final DupCheck dc = new DupCheck(str, v);
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) throws Exception {
//		String idSql = dc.getIDSql(minScore, maxDups);
//		str
//		str.put("idsql", idSql);
//		rr.run(str);
//	}});
//}
}
