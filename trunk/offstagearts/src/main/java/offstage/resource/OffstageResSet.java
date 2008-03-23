/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.ResSet;
import citibob.resource.RtResKey;
import citibob.resource.Resource;
import citibob.sql.DbChangeModel;
import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
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

protected void addAllResources()
{
	// Add the resources!!!
	
	// ODT (OpenOffice) Templates
	add(new Res_AcctStatement(this));
	add(new Res_StudentConfirmationLetter(this));
	add(new Res_StudentSchedule(this));

	// XLS (Spreadsheet) Templates
	add(new Res_StudentAccounts(this));
	
	// JRXML (iReport) Templates
	add(new Res_AddressLabels(this));
	add(new Res_CCPayments(this));
	add(new Res_RollBook(this));

	// Other stuff
	add(new Res_Database(this));
	add(new Res_SiteCode(this));
	
}
// ===================================================
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
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) {
		if (dbbExists()) refreshTerms(str);
	}});

	addAllResources();
}

private void refreshTerms(SqlRun str)
{
	
	String sql = "select groupid, name from termids where iscurrent";
	str.execSql(sql, new RsTasklet2() {
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
		String uvType = res.getResourceGroup();
		if (uvType == null || !uvType.equals("school_termids")) continue;
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
