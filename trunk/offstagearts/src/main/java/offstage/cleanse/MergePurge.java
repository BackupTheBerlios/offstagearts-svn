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
 * MergePurge.java
 *
 * Created on November 3, 2007, 8:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.cleanse;

import citibob.sql.pgsql.*;
import java.sql.*;
import java.util.*;
import citibob.sql.*;
import com.wcohen.ss.*;
import com.wcohen.ss.api.*;
import com.wcohen.ss.tokens.*;
import java.text.*;
import offstage.db.*;

/**
 *
 * @author citibob
 */
public class MergePurge
{

static NumberFormat nfmt = new DecimalFormat("#0.00");


static class Duo implements Comparable
{
	public Map.Entry<Integer,StringWrapper> aa,bb;
	public double score;
	public Duo(Map.Entry<Integer,StringWrapper> a, Map.Entry<Integer,StringWrapper> b, double score) {
		this.aa = a;
		this.bb = b;
		this.score = score;
	}
	public int compareTo(Object o) {
		Duo d = (Duo)o;
		double x = (score - d.score);
		if (x > 0) return 1;
		if (x < 0) return -1;
		return 0;
	}
	public String toString() {
		return nfmt.format(score) + " " + aa.getKey() + " " + bb.getKey() + " <" + aa.getValue() + "> <" + bb.getValue() + ">";
	}
}


static boolean empty(String s) { return (s == null || "".equals(s)); }
static String upper(String s) { return (s == null ? "" : s.toUpperCase()); }

public static String getCanonical(ResultSet rs) throws SQLException
{
	StringBuffer sb = new StringBuffer();

	// Get the main address line, not the "c/o" line
	String address1 = rs.getString("address1");
	String address2 = rs.getString("address2");
	String addr = (empty(address2) ? address1 :  address2);
	String country = rs.getString("country");
	String zip = rs.getString("zip");
	if (empty(country) || "USA".equalsIgnoreCase(country)) {
		addr = AddrTx.AddressLineStandardization(addr);
		if (zip != null && zip.length() > 5) zip = zip.substring(5);
	}

	
	String ret = (addr + " " +
		upper(rs.getString("city")) + " " +
		upper(rs.getString("state")) + " " +
		upper(zip)).trim();
	return ret;
//	+ " " +
//		upper(rs.getString("firstname")) + " " +
//		upper(rs.getString("lastname")) + " " +
//		upper(rs.getString("orgname"));
}


/** Process for printing to the screen
 @returns sql to update database with. */
static String process(Map<Integer,String> xmap, double thresh, String type)
{
	
	// Prepare strings
	SoftTFIDF fullD = new SoftTFIDF(new SimpleTokenizer(true,true),
		new JaroWinkler(),0.8);
	Map<Integer,StringWrapper> map = new TreeMap();
	for (Map.Entry<Integer,String> aa : xmap.entrySet()) {
		map.put(aa.getKey(), fullD.prepare(aa.getValue()));
	}
	
	List<Duo> report = new ArrayList();
	Hist fullHist = new Hist(0,1,10);
	fullD.train( new BasicStringWrapperIterator(map.values().iterator()));
System.out.println("Full Processing");
	int i,j;
	i=0; j=0;
	for (Map.Entry<Integer,StringWrapper> aa : map.entrySet()) {
		if (i % 1000 == 0) System.out.println("  " + i);
		j=0;
		for (Map.Entry<Integer,StringWrapper> bb : map.entrySet()) {
			if (j >= i) continue;
			double e = fullD.score(aa.getValue(), bb.getValue());
			fullHist.add(e);
			if (e >= thresh) report.add(new Duo(aa,bb,e));
			++j;
		}
		++i;
	}
	
	// Report to screen
	Collections.sort(report);
	for (Duo d : report) {
		System.out.println(d.toString());
	}
	System.out.println(fullHist.toString());
	
	// Report to dups table of database
	StringBuffer sb = new StringBuffer();
	sb.append("delete from dups where type=" + SqlString.sql(type) + ";\n");
	Collections.sort(report);
	for (Duo d : report) {
		Map.Entry<Integer,StringWrapper> e0,e1;
		if (d.aa.getKey().intValue() < d.bb.getKey().intValue()) {
			e0 = d.aa;
			e1 = d.bb;
		} else {
			e1 = d.aa;
			e0 = d.bb;				
		}
		sb.append("insert into dups (type, entityid0, string0, entityid1, string1, score) values (\n" +
			SqlString.sql(type) + ", " +
			SqlInteger.sql(e0.getKey()) + ", " + SqlString.sql(e0.getValue().unwrap()) + ", " +
			SqlInteger.sql(e1.getKey()) + ", " + SqlString.sql(e1.getValue().unwrap()) + ", " +
			SqlDouble.sql(d.score) + ");\n");
		System.out.println(d.toString());
	}
	return sb.toString();
}


/** Creates a new instance of MergePurge */
public MergePurge(SqlRun str)
{
	String sql =
		" SELECT entityid,primaryentityid," +
		" address1,address2,city,state,zip,country," +
		" firstname,lastname,orgname,isorg from persons p" +
		" where not obsolete";
//		" and city = 'Cambridge'";
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws SQLException {
        // create a SoftTFIDF distance learner
        SoftTFIDF nameD = new SoftTFIDF(new SimpleTokenizer(true,true),
			new JaroWinkler(),0.8);
		
		Map<Integer,String> nameMap = new TreeMap();
		Map<Integer,String> addrMap = new TreeMap();
		Map<Integer,String> orgMap = new TreeMap();
//		int n = 0;
		while (rs.next()) {
			// Check for multiple entries at same address
			int eid = rs.getInt("entityid");
			int pid = rs.getInt("primaryentityid");
			
//			if (eid == pid) {
//				String canon = getCanonical(rs);
//				addrMap.put(eid, canon);				
//			}

			// Check for duplicate entries (by name; can catch change of address too)
			String name = upper(rs.getString("firstname")) + " " + upper(rs.getString("lastname"));
			name = name.trim();
			if (!empty(name)) nameMap.put(eid, name);
			
//			String orgname = upper(rs.getString("orgname")).trim();
//			if (rs.getBoolean("isorg") && !empty(orgname)) orgMap.put(eid, orgname);
//			++n;
		}
System.out.println("Done getting names (" + nameMap.size() + " records)");
		// train the distance on some strings - in general, this would
		// be a large corpus of existing strings, so that some
		// meaningful frequency estimates can be accumulated.  for
		// efficiency, you train on an iterator over StringWrapper
		// objects, which are produced with the 'prepare' function.

		str.execSql(process(nameMap, .9, "n"));	// Just merge by name
//		process(addrMap, .8, "a");
//		process(orgMap, .8, "o");
	}});
}
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	SqlBatchSet str = new SqlBatchSet(pool);
//	new MergePurge(str);
//	str.runBatches();
//}

}
