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
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import offstage.FrontApp;
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


static Map<Integer,StringWrapper> prepareMap(Map<Integer,String> imap, SoftTFIDF fullD)
{
		// Prepare strings
	Map<Integer,StringWrapper> map = new TreeMap();
	for (Map.Entry<Integer,String> aa : imap.entrySet()) {
		map.put(aa.getKey(), fullD.prepare(aa.getValue()));
	}
	return map;
}

/** Process for printing to the screen
 @param xmap0 One set of names to merge/purge on
 @param xmap1 The other set of names...
 @returns sql to update database with. */
static String process(int dbid0, Map<Integer,String> xmap0,
int dbid1, Map<Integer,String> xmap1, double thresh, String type)
{
	SoftTFIDF fullD = new SoftTFIDF(new SimpleTokenizer(true,true),
		new JaroWinkler(),0.8);
	Map<Integer,StringWrapper> map0 = prepareMap(xmap0, fullD);
	Map<Integer,StringWrapper> map1 = (xmap0 == xmap1 ? map0 : prepareMap(xmap1, fullD));
	
	List<Duo> report = new ArrayList();
	Hist fullHist = new Hist(0,1,10);
	fullD.train( new BasicStringWrapperIterator(map0.values().iterator()));
	if (map1 != map0) fullD.train( new BasicStringWrapperIterator(map1.values().iterator()));
System.out.println("Full Processing: sizes = " + map0.size() + " and " + map1.size());
	int i,j;
	i=0; j=0;
	for (Map.Entry<Integer,StringWrapper> aa : map0.entrySet()) {
		if (i % 10 == 0) System.out.println("  " + i);
		j=0;
		for (Map.Entry<Integer,StringWrapper> bb : map1.entrySet()) {
			if (map1 == map0 && j >= i) continue;
			double e = fullD.score(aa.getValue(), bb.getValue());
//System.out.println(aa.getValue() + " : " + bb.getValue() + " = " + e);
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
	sb.append(
		" delete from dups" +
		" using entities e0, entities e1" +
		" where type=" + SqlString.sql(type) + "\n" +
		" and dups.entityid0 = e0 and dups.entityid1 = e1" +
		" and e0.dbid = " + SqlInteger.sql(dbid0) +
		" and e1.dbid = " + SqlInteger.sql(dbid1));
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

Map<Integer,String> loadNameMap(SqlRun str, int dbid)
{
	String sql =
		" SELECT entityid,firstname,lastname" +
		" from persons p" +
		" where dbid = " + dbid +
		" and not obsolete";

	final Map<Integer,String> nameMap = new TreeMap();
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws SQLException {
		while (rs.next()) {
			// Check for multiple entries at same address
			int eid = rs.getInt("entityid");

			String name = upper(rs.getString("firstname")) + " " + upper(rs.getString("lastname"));
			name = name.trim();
			if (!empty(name)) nameMap.put(eid, name);
		}
System.out.println("Done getting names (" + nameMap.size() + " records)");
	}});

	return nameMap;
}


/** Creates a new instance of MergePurge */
public MergePurge(SqlRun str, final int dbid0, final int dbid1)
{
	final Map<Integer,String> nameMap0 = loadNameMap(str, dbid0);
	final Map<Integer,String> nameMap1 = loadNameMap(str, dbid1);
	
	str.execUpdate(new UpdTasklet() {
	public void run() throws SQLException, IOException {

		String sql = process(dbid0, nameMap0, dbid1, nameMap1, .95, "n");
		
		FileWriter out = new FileWriter("dups.sql");
		out.write(sql);
		out.write("\n");
		out.close();
	}});
}
public static void main(String[] args) throws Exception
{
	final FrontApp app = new FrontApp(false); //new File("/export/home/citibob/svn/offstage/config"));
	boolean resGood = app.checkResources();
	app.initWithDatabase();
	new MergePurge(app.sqlRun(), 1, 0);
	app.sqlRun().flush();
}

}
