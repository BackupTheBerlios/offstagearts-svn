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

import citibob.app.App;
import citibob.reflect.ClassPathUtils;
import citibob.sql.pgsql.*;
import java.sql.*;
import java.util.*;
import citibob.sql.*;
import com.wcohen.ss.*;
import com.wcohen.ss.api.*;
import com.wcohen.ss.tokens.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.*;
import java.util.Date;
import offstage.FrontApp;
import offstage.db.*;

/**
 *
 * @author citibob
 */
public class MergePurge
{

static NumberFormat nfmt = new DecimalFormat("#0.00");
App app;

// ======================================
static class Entity implements Comparable<Entity>
{
	int entityid;
	String name;
	StringWrapper preparedName;		// Used in approx. matching
	long lastmodifiedMS;

	/** For putting in a TreeSet */
	public int compareTo(Entity e1)
	{
		return entityid - e1.entityid;
	}
}

/** For ordering as entity0 or entity1 */
static class LastModifiedComparator implements Comparator<Entity>
{
	public int compare(Entity e0, Entity e1) {
		long diff = e0.lastmodifiedMS - e1.lastmodifiedMS;
		if (diff < 0) return -1;
		if (diff > 0) return 1;
		return e0.entityid - e1.entityid;
	}
}
static class PositionComparator implements Comparator<Entity>
{
	public int compare(Entity e0, Entity e1) {
		// e0 > e1 ==> e0 is newer than e1
		// (which will cause output to not swap)
		return 1;
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


static List<StringWrapper> prepareMap(Map<Integer,Entity> imap, SoftTFIDF fullD)
{
		// Prepare strings
	List<StringWrapper> list0 = new ArrayList(imap.size());
	for (Entity en : imap.values()) {
//System.out.println(en.name);
		en.preparedName = fullD.prepare(en.name);
		list0.add(en.preparedName);
	}
	return list0;
}

Map<Integer,Entity> loadNameMap(SqlRun str, int dbid)
{
	String sql =
		" SELECT entityid,firstname,lastname,lastupdated" +
		" from persons p" +
		" where dbid = " + dbid +
//" and firstname like 'M%'" +
//" and firstname = 'Margot' and lastname = 'Richardson'" +
		" and not obsolete";

	final Map<Integer,Entity> nameMap = new TreeMap();
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws SQLException {
		SqlDateType timestamp = app.sqlTypeSet().timestamp();
		while (rs.next()) {
			// Check for multiple entries at same address
			int eid = rs.getInt("entityid");

			String name = upper(rs.getString("lastname")).trim() +
				" " + upper(rs.getString("firstname")).trim();
			//name = name.trim();
			if (!empty(name)) {
				Entity en = new Entity();
					en.name = name;
					en.entityid = eid;
					Date dt = ((Date)timestamp.get(rs, "lastupdated"));
					en.lastmodifiedMS = (dt == null ? 0 : dt.getTime());
				nameMap.put(eid, en);
			}
		}
System.out.println("Done getting names (" + nameMap.size() + " records)");
	}});

	return nameMap;
}


public MergePurge(App app)
{
	this.app = app;
}


/** Creates a new instance of MergePurge
 @param out Write SQL inserts to here. */
public void findDups(SqlRun str,
final int dbid0, final int dbid1, final double thresh,
final Writer out)
{
	final Map<Integer,Entity> map0 = loadNameMap(str, dbid0);
	final Map<Integer,Entity> map1 =
		(dbid0 == dbid1 ? map0 : loadNameMap(str, dbid1));
	
	final String type = "n";
	
	str.execUpdate(new UpdTasklet() {
	public void run() throws SQLException, IOException {
		final Comparator<Entity> swapOrder = (dbid0 == dbid1 ?
			new LastModifiedComparator() :
			new PositionComparator());
//		out.write(
//			" delete from dups" +
//			" using entities e0, entities e1" +
//			" where type=" + SqlString.sql(type) + "\n" +
//			" and dups.entityid0 = e0 and dups.entityid1 = e1" +
//			" and e0.dbid = " + SqlInteger.sql(dbid0) +
//			" and e1.dbid = " + SqlInteger.sql(dbid1) + ";\n");
		Hist fullHist = new Hist(0,1,10);		// Histograms

		//process(dbid0, nameMap0, dbid1, nameMap1, .95, "n");

		SoftTFIDF fullD = new SoftTFIDF(new SimpleTokenizer(true,true),
			new JaroWinkler(),0.8);
		
		// Train the matcher...
		List<StringWrapper> list0 = prepareMap(map0, fullD);
		fullD.train(new BasicStringWrapperIterator(list0.iterator()));
		if (dbid1 != dbid0) {
			List<StringWrapper> list1 = prepareMap(map1, fullD);
			fullD.train(new BasicStringWrapperIterator(list1.iterator()));			
		}

	System.out.println("Full Processing: sizes = " + map0.size() + " and " + map1.size());
//		int i,j;
//		i=0; j=0;
		int i=0;
		for (Entity e0 : map0.values()) {
			if (i % 10 == 0) System.out.println("  " + i);
			for (Entity e1 : map1.values()) {
				if (e1.entityid == e0.entityid) continue;
				if (dbid0 == dbid1
					&& e1.entityid >= e0.entityid) continue;
				double score = fullD.score(e0.name, e1.name);
				fullHist.add(score);		// histograms
				if (score >= thresh) {
					Entity aa,bb;
					if (swapOrder.compare(e0, e1) < 0) {
						// e1 newer than e0: swap, so that e0 ends up newest
						aa = e1;
						bb = e0;
					} else {
						// e0 newer than e1: do not swap
						aa = e0;
						bb = e1;
					}
					// In the output, entity0 should be "newer" than entity1
					out.write(
						"insert into dups (type, entityid0, string0, entityid1, string1, score) values (\n" +
						SqlString.sql(type) + ", " +
						SqlInteger.sql(aa.entityid) + ", " + SqlString.sql(aa.name) + ", " +
						SqlInteger.sql(bb.entityid) + ", " + SqlString.sql(bb.name) + ", " +
						SqlDouble.sql(score) + ");\n");
					out.flush();
				}
//				++j;
			}
			++i;
		}
		out.flush();
	}});
}
public static void main(String[] args) throws Exception
{
	final FrontApp app = new FrontApp(FrontApp.CT_CONFIGCHOOSE, null); //new File("/export/home/citibob/svn/offstage/config"));
	boolean resGood = app.checkResources();
	app.initWithDatabase();
	File dir = ClassPathUtils.getMavenProjectRoot();
	Writer out = new FileWriter(new File(dir, "dups.sql"));
//	new MergePurge(app).findDups(app.sqlRun(), 1, 0, .95, out);
	new MergePurge(app).findDups(app.sqlRun(), 0, 0, .95, out);
	app.sqlRun().flush();
	out.close();
}

}
