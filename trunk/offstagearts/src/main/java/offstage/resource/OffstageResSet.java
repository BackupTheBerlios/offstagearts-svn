/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.ResSet;
import citibob.resource.RtResKey;
import citibob.resource.Resource;
import citibob.sql.DbChangeModel;
import citibob.sql.RsRunnable;
import citibob.sql.SqlRun;
import citibob.sql.UpdRunnable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author citibob
 */
public class OffstageResSet
extends ResSet
{

static class UVersion {
	public int uversionid;
	public String name;
	public UVersion(int uversionid, String name) {
		this.uversionid = uversionid;
		this.name = name;
	}
}
List<UVersion> terms = new ArrayList();
//List<UVersion> shows;

public OffstageResSet(SqlRun str, DbChangeModel dbChange)
throws SQLException
{
	super(str, 17, OffstageResSet.class.getClassLoader(),  "offstage/resource/");

	dbChange.addListener("termids", new DbChangeModel.Listener() {
    public void tableWillChange(SqlRun str, String table) {
		refreshTerms(str);
	}});
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRun str) {
		if (dbbExists()) refreshTerms(str);
	}});

	// Add the resources!!!
	add(new Res_YDPConfirmationLetter(this));
	add(new Res_SiteCode(this));
//	add(new Res_Database(this));
	
}

private void refreshTerms(SqlRun str)
{
	
	String sql = "select groupid, name from termids where iscurrent";
	str.execSql(sql, new RsRunnable() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
		terms.clear();
		while (rs.next()) {
			UVersion uv = new UVersion(rs.getInt("groupid"), rs.getString("name"));
			terms.add(uv);
		}
		dbbExists = true;
	}});
}

public SortedSet<RtResKey> newRelevant()
{
	SortedSet<RtResKey> ret = super.newRelevant();

	// Process term-only resources
	for (Resource res : resources.values()) {
		String uvType = res.getUversionType();
		if (uvType == null || !uvType.equals("termids")) continue;
		for (UVersion uv : terms) {
			ret.add(new RtResKey(res, uv.uversionid, uv.name));
		}
	}
	return ret;
}

//public static void main(String[] args) throws Exception
//{
//	FrontApp app = new FrontApp(); 
//	SqlBatchSet str = app.getBatchSet();
//	System.out.println("========================================");
//	OffstageResSet rset = new OffstageResSet(str, app.getDbChange());
//	rset.createAllResourceIDs(str);
//	str.flush();
//
//	SortedSet<RtResKey> rel = rset.newRelevant();
//	ResUtil.fetchAvailableVersions(str, rel);		// Puts into rel...
//	str.flush();
//
////	rset.getA
//	for (RtResKey rk : rel) {
//		System.out.println("Relevant (Resource,uversionid): " + rk);
//		int reqVersion = rk.res.getRequiredVersion(rset.sysVersion);
//		System.out.println("   required version = " + reqVersion);
//
//		if (rk.availVersions != null) {
//			System.out.print  ("   avail versions =");
//			for (int v : rk.availVersions) System.out.println(" " + v);
//		}
//		
////		ResResult rr = rk.res.loadJar(3);
////		System.out.println("    Read bytes: " + rr.bytes.length);
////		rk.res.get
//		UpgradePlan uplan = rk.getCreatorPlan(reqVersion);
////		Upgrader[] path = rk.res.getUpgradePlan(2, reqVersion);
////		for (Upgrader up : path) { System.out.println("        " + up); }
//		System.out.println("    " + uplan);
//		rk.res.applyPlan(str, app.getPool(), uplan);
//		System.out.println();
//	}
//	str.flush();
//	
//	System.out.println("Done!");
//	System.exit(0);
//}

}
