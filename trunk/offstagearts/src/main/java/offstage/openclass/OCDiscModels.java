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
 * OCDiscPanel.java
 *
 * Created on June 10, 2008, 9:03 AM
 */

package offstage.openclass;

import citibob.jschema.DbModel;
import citibob.jschema.IntsKeyedDbModel;
import citibob.jschema.KeyViolationException;
import citibob.jschema.MultiDbModel;
import citibob.jschema.PivotSchemaBuf;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SchemaBufDbModel;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.table.ExtPivotTableModel;
import citibob.text.KeyedSFormat;
import citibob.types.KeyedModel;
import java.util.ArrayList;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class OCDiscModels {
	
//Integer TeacherID;		// Teacher for which this shows discounts (or null for studio discounts)
	
SchemaBufDbModel discDm;
SchemaBufDbModel discAmtDm;
MultiDbModel pivotDm;
ExtPivotTableModel pivotTm;

//String ocdiscIdSql;			// Current "key" --- the ocdiscids we want to display

	/** @returns Main DbModel used to control data in this widget.  It has one key,
	 which is TeacherID. */
	public DbModel getDm()
		{ return pivotDm; }

	/** @returns Main table model */
	public ExtPivotTableModel getTm()
		{ return pivotTm; }

	public OCDiscModels(SqlRun str, final FrontApp app)
	{
		
		// Main discount table
		String[] keyFields = new String[] {"teacherid"};
		discDm = new IntsKeyedDbModel(app.getSchema("ocdiscids"),
			keyFields,
			false, app.dbChange());

		// Discount amount table --- to be pivoted
		discAmtDm = new SchemaBufDbModel(new SchemaBuf(app.getSchema("ocdiscidsamt")),
			"ocdiscidsamt", app.dbChange()) {
		public void doSelect(SqlRun str) {
			Integer TeacherID = (Integer)getKey();//getDm().getKey();
			String sql =
				" select amt.*" +
				" from ocdiscidsamt amt, ocdiscids ids" +
				" where ids.ocdiscid = amt.ocdiscid" +
				(TeacherID == null ?
					" and teacherid is null" :
					" and teacherid = " + SqlInteger.sql(TeacherID));
			getSchemaBuf().setRows(str, sql);
		}};
		discAmtDm.setNumKeys(keyFields.length);

		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) {
			// Set up pivot --- discAmtDm should be before discDm so PivotSchemaBuf works right
			pivotDm = new MultiDbModel(discAmtDm, discDm);
				// TODO: Need to make own class here, to remove null values from data table when saving
			KeyedModel discCatKm = app.schemaSet().getKeyedModel("ocdiscidsamt", "ocdisccatid");
			ArrayList pivotVals = new ArrayList();
			for (int i=0; i<discCatKm.size(); ++i) {
				Integer val = (Integer)discCatKm.getKey(i);
				if (val.intValue() > 0) pivotVals.add(val);
			}
			PivotSchemaBuf pivotSb = new PivotSchemaBuf(
				discDm.getSchemaBuf(), discAmtDm.getSchemaBuf(),
				new String[] {"ocdiscid"},
				"ocdisccatid", "pct",
				pivotVals, new KeyedSFormat(discCatKm));

			// Now put it together as one table that we can display
			pivotTm = new ExtPivotTableModel(discDm.getSchemaBuf(), pivotSb);
		}});
		// --------------------------------------------------------
	}

//	public void insertDiscount(int ocdiscid) throws KeyViolationException
//	{ discDm.getSchemaBuf().insertRow(-1, "ocdiscid", new Integer(ocdiscid)); }

}
