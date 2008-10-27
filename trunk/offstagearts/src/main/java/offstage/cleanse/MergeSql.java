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
 * Cleanse.java
 *
 * Created on November 4, 2007, 11:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.cleanse;

import citibob.jschema.*;
import citibob.sql.*;
import java.util.*;
import java.sql.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;
import offstage.*;
import citibob.app.*;
import citibob.sql.pgsql.*;
import offstage.datatab.DataTab;
import offstage.datatab.DataTabSet;
import offstage.devel.gui.DevelModel;

public class MergeSql
{

StringBuffer sql = new StringBuffer();
FrontApp app;

public MergeSql(FrontApp app) //SchemaSet sset)
	{ this.app = app; }//sset = sset; }

public String toSql()
	{ return sql.toString(); }


///** Merges data FROM dm0 TO dm1 */
//public static String mergeEntities(FrontApp app, Object entityid0, Object entityid1)
//{
//	MergeSql merge = new MergeSql(app);//.schemaSet());
//	merge.mergeEntities(entityid0, entityid1);
//	String sql = merge.toSql();
//	return sql;
//}

public void subordinateEntities(Object entityid0, Object entityid1)
{
	// Move the main record
	sql.append("update entities set primaryentityid=" + entityid1 + " where entityid=" + entityid0 + ";\n");

	// Move the rest of the household (if we were head of household)
	searchAndReplace(app.schemaSet().get("persons"), "primaryentityid", entityid0, entityid1);
}

// These columns should NOT be auto-merged.
static final Set<String> noDOB = new TreeSet();
static {
	noDOB.add("dob");
	noDOB.add("dobapprox");
}

void mergeDOB(Integer entityid0, Integer entityid1)
{
	sql.append(" update persons set\n");
	
	sql.append(
		" dob =\n" +
		" (case when persons.dob is null or\n" +
		" (persons.dobapprox and p0.dob is not null and not p0.dobapprox)\n" +
		" then p0.dob else persons.dob end)\n" +
		"," +
		" dobapprox =\n" +
		" (case when persons.dob is null or\n" +
		" (persons.dobapprox and p0.dob is not null and not p0.dobapprox)\n" +
		" then p0.dobapprox else persons.dobapprox end)\n");

	sql.append(
		" from persons as p0" +
		" where persons.entityid = " + entityid1 +
		" and p0.entityid = " + entityid0 +
		";\n");	
}

/** Merges data FROM dm0 TO dm1 */
public void mergeEntities(Integer entityid0, Integer entityid1)
{
// ONE MORE THING: need to tell mergeOneRow() about columns that default to entityid.
// This can be done in a special update statement after-the-fact.
// Also need to do a simple search-and-replace of entityid0 -> entityid1 on primaryentityid, adultid, etc.
// (This is all done)
	DataTabSet tabs = app.dataTabSet();
	SchemaSet sset = app.schemaSet();

	// =================== Main Data
	mergeOneRow(sset.get("persons"), "entityid", entityid0, entityid1, noDOB);
		mergeDOB(entityid0, entityid1);		// Special treatment for the DOB entry.
	mergeOneRowEntityID(sset.get("persons"), "entityid", new String[] {"primaryentityid"}, entityid0, entityid1);
	searchAndReplace(sset.get("persons"), "primaryentityid", entityid0, entityid1);
	searchAndReplace(sset.get("persons"), "parent1id", entityid0, entityid1);
	searchAndReplace(sset.get("persons"), "parent2id", entityid0, entityid1);
	moveRows(sset.get("phones"), "entityid", entityid0, entityid1);
	
//	moveRows(sset.get("classes"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("donations"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("events"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("flags"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("interests"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("notes"), "entityid", entityid0, entityid1);
//	moveRows(sset.get("tickets"), "entityid", entityid0, entityid1);

	for (DataTab tab : tabs.allTabs()) {
		if (tab.getSchema().getType() == SqlSchema.ST_VIEW) continue;
		moveRows(tab.getSchema(), "entityid", entityid0, entityid1);
	}
	
	// Don't forget to delete old now-orphaned records!!
	// (or at least set to obsolete!)
	sql.append("update entities set obsolete=true where entityid=" + entityid0 + ";\n");

	// Accounting
	moveRows(sset.get("actrans2"), "cr_entityid", entityid0, entityid1);
	moveRows(sset.get("actrans2"), "db_entityid", entityid0, entityid1);

	// School
//	moveRows(sset.get("entities_school"), "entityid", entityid0, entityid1);
//	mergeOneRow(sset.get("entities_school"), "entityid", entityid0, entityid1);
//	mergeOneRowEntityID(sset.get("entities_school"), "entityid",
//		new String[] {"adultid", "parentid", "parent2id"}, entityid0, entityid1);
//	searchAndReplace(sset.get("entities_school"), "adultid", entityid0, entityid1);
//	searchAndReplace(sset.get("entities_school"), "parentid", entityid0, entityid1);
//	searchAndReplace(sset.get("entities_school"), "parent2id", entityid0, entityid1);
	moveRows(sset.get("termregs"), "entityid", entityid0, entityid1);
	searchAndReplace(sset.get("termregs"), "payerid", entityid0, entityid1);
	moveRows(sset.get("payertermregs"), "entityid", entityid0, entityid1);
	moveRows(sset.get("enrollments"), "entityid", entityid0, entityid1);
	moveRows(sset.get("subs"), "entityid", entityid0, entityid1);

//Main Record
//===========
//entities:
//	primaryentityid
//classes,entityid
//donations,entityid
//events,entityid
//flags,entityid
//interests,entityid
//notes,entityid
//phones,entityid
//ticketevents,entityid
//XXmailings,entityid  (don't do mailings)
//
//Accounting
//==========
//actrans,entityid
//	(Really: adjpayments,cashpayments,ccpayments,checkpayments,tuitiontrans)
//
//School
//======
//entities_school:
//	(entityid)
//	adultid
//	parentid
//	parent2id
//termregs,entityid
//enrollments,entityid


}

///** Merge main part of the record.. */
//public void mergePersons(SchemaBuf sb0, SchemaBuf sb1)
//{
//	mergeRecMain(sb0, sb1);
//	mergeEntityIDCol(sb0, sb1, sb0.findColumn("primaryentityid"));
//}

// -------------------------------------------------------------------
public void searchAndReplace(SqlSchema schema, String sEntityCol, Object entityid0, Object entityid1)
{
	int entityColIx = schema.findCol(sEntityCol);
	SqlCol entityCol = (SqlCol)schema.getCol(entityColIx);
	String table = schema.getDefaultTable();
//	StringBuffer sql = new StringBuffer();

	sql.append("update " + table + " set " + entityCol.getName() + " = " +
		entityCol.toSql(entityid1) + " where " + entityCol.getName() + " = " + entityCol.toSql(entityid0) + ";\n");
}
// -------------------------------------------------------------------
/** Merges the (one) row fully keyed by sKeyCol.  Only changes columns
 in sUpdateCols with value == sEntityCol (typically "entityid"). */
public void mergeOneRowEntityID(SqlSchema schema, String sEntityCol,
String[] sUpdateCols,
Object entityid0, Object entityid1)
{
	int entityColIx = schema.findCol(sEntityCol);
	SqlCol entityCol = (SqlCol)schema.getCol(entityColIx);
	String table = schema.getDefaultTable();
	int[] keyCols = getKeyCols(schema, entityColIx);
//	StringBuffer sql = new StringBuffer();

	sql.append(" update " + table);
	sql.append(" set\n");
	for (int i=0; ;) {
		SqlCol col = (SqlCol)schema.getCol(sUpdateCols[i]);
		sql.append(col.getName() + " = " +
			" (case when " + table + "." + col.getName() + " = " + table + "." + entityCol.getName() + " then " +
			" t0." + col.getName() + " else " + table + "." + col.getName() + " end)");
		if (++i >= sUpdateCols.length) break;
		sql.append(",\n");
	}
	sql.append("\n");
	sql.append(" from " + table + " as t0");
	sql.append(" where " + table + "." + entityCol.getName() + " = " + entityCol.toSql(entityid1) +
		" and t0." + entityCol.getName() + " = " + entityCol.toSql(entityid0));
	sql.append(";\n");
	System.out.println(sql);
}
// -------------------------------------------------------------------
/** Merges the (one) row fully keyed by sKeyCol.  Only changes columns with null values.
 @param exceptionCols Ignore these columns. */
public void mergeOneRow(SqlSchema schema, String sEntityCol,
Object entityid0, Object entityid1, Set<String> exceptionCols)
{
	int entityColIx = schema.findCol(sEntityCol);
	SqlCol entityCol = (SqlCol)schema.getCol(entityColIx);
	String table = schema.getDefaultTable();
	int[] keyCols = getKeyCols(schema, entityColIx);
//	StringBuffer sql = new StringBuffer();

	sql.append(" update " + table);
	sql.append(" set\n");
	for (int i=0; ;) {
		SqlCol col = (SqlCol)schema.getCol(i);
		if (col.isKey() || exceptionCols.contains(col.getName())) {
			++i;
			continue;
		}
		
		// Treat blank fields as null
		String colVal = table + "." + col.getName();
		if (col.getSqlType().getObjClass() == String.class) {
			colVal = "(case when char_length(trim(" + colVal + ")) = 0 then null else " + colVal + " end)";
		}
		sql.append(col.getName() + " = " +
			" (case when " + colVal + " is null then " +
			" t0." + col.getName() + " else " + table + "." + col.getName() + " end)");
		if (++i >= schema.size()) break;
		sql.append(",\n");
	}
	
	
	sql.append("\n");
	sql.append(" from " + table + " as t0");
	sql.append(" where " + table + "." + entityCol.getName() + " = " + entityCol.toSql(entityid1) +
		" and t0." + entityCol.getName() + " = " + entityCol.toSql(entityid0));
	sql.append(";\n");

	// Take care of lastupdated
	if (schema.findCol("lastupdated") >= 0) {
		// Set last updated to now!
		sql.append(
			" update " + table + " set lastupdated = now()" +
			" where " + entityCol.getName() + " = " + entityCol.toSql(entityid1) + ";\n");

		// Set last updated to the most recent of the two records.
		if (false) {
			sql.append(" update " + table);
			sql.append(" set\n");
			sql.append(" lastupdated = (case when " + table + ".lastupdated > t0.lastupdated or t0.lastupdated is null" +
				" then " + table + ".lastupdated else t0.lastupdated end)");
			sql.append(" from " + table + " as t0");
			sql.append(" where " + table + "." + entityCol.getName() + " = " + entityCol.toSql(entityid1) +
				" and t0." + entityCol.getName() + " = " + entityCol.toSql(entityid0));
			sql.append(";\n");
		}
	}
	
//	System.out.println(sql);
}
// -------------------------------------------------------------------
public static int[] getKeyCols(SqlSchema schema, int entityColIx)
{
	// Collect keys from schema
	int ncols = schema.size();
	int nkeys = 0;
	for (int i=0; i<ncols; ++i) if (i != entityColIx && ((SqlCol)schema.getCol(i)).isKey()) ++nkeys;
	int[] keyCols = new int[nkeys];
	int k=0;
	for (int i=0; i<ncols; ++i) if (i != entityColIx && ((SqlCol)schema.getCol(i)).isKey()) keyCols[k++] = i;

	return keyCols;
}
// -------------------------------------------------------------------
///** Moves rows from keyCol=entityid0 to keyCol=entityid1 */
//public static void moveRows(SqlSchema schema, String sEntityCol, Object entityid0, Object entityid1)
//{
//	int entityColIx = schema.findCol(sEntityCol);
//	Column entityCol = schema.getCol(entityColIx);
//	String table = schema.getDefaultTable();
//	int[] keyCols = getKeyCols(schema, entityColIx);
//	StringBuffer sql = new StringBuffer();
//
//	// Create list of keys in table 0 --- which we will transfer to table 1
//	sql.append("create temporary table keys0 (");
//	for (int i=0; ;) {
//		Column col = schema.getCol(keyCols[i]);
//		sql.append(col.getName() + " " + col.getType().sqlType());
//		if (++i >= keyCols.length) break;
//		sql.append(",");
//	}
//	sql.append(");\n");
//
//	// Fill it in
//	sql.append("insert into keys0 select ");
//	for (int i=0; ;) {
//		Column col = schema.getCol(keyCols[i]);
//		sql.append(col.getName());
//		if (++i >= keyCols.length) break;
//		sql.append(",");
//	}
//	sql.append(" from " + table +
//		" where " + entityCol.getName() + " = " + entityCol.toSql(entityid0) + ";\n");
//
//	// Remove duplicates already under entityid1
//	sql.append("delete from keys0 using " + table);
//	sql.append(" where " + entityCol.getName() + " = " + entityCol.toSql(entityid1));
//	for (int i=0; i<keyCols.length; ++i) {
//		Column col = schema.getCol(keyCols[i]);
//		sql.append(" and keys0." + col.getName() + " = " + table + "." + col.getName());
//	}
//	sql.append(";\n");
//
//	// Move the rest over to entityid1
//	sql.append("update " + table + 
//		" set " + entityCol.getName() + " = " + entityCol.toSql(entityid1) +
//		" from keys0" +
//		" where " + table + "." + entityCol.getName() + " = " + entityCol.toSql(entityid0));
//	for (int i=0; i<keyCols.length; ++i) {
//		Column col = schema.getCol(keyCols[i]);
//		sql.append(" and keys0." + col.getName() + " = " + table + "." + col.getName());
//	}
//	sql.append(";\n");
//
//	sql.append("drop table keys0;\n");
//
//	System.out.println(sql);
//}
// -------------------------------------------------------------------
/** Moves rows from keyCol=entityid0 to keyCol=entityid1 -- in which there are no other key columns */
public void moveRows(SqlSchema schema, String sEntityCol, Object entityid0, Object entityid1)
{
	int entityColIx = schema.findCol(sEntityCol);
	SqlCol entityCol = (SqlCol)schema.getCol(entityColIx);
	String table = schema.getDefaultTable();
	int[] keyCols = getKeyCols(schema, entityColIx);
//	StringBuffer sql = new StringBuffer();

	// Create list of keys in table 0 --- which we will transfer to table 1
	sql.append("create temporary table keys0 (dummy int");
	for (int i=0; i<keyCols.length; ++i) {
		SqlCol col = (SqlCol)schema.getCol(keyCols[i]);
		sql.append(", " + col.getName() + " " + col.getSqlType().sqlType());
	}
	sql.append(");\n");

	// Fill it in
	sql.append("insert into keys0 select -1");
	for (int i=0; i<keyCols.length; ++i) {
		SqlCol col = (SqlCol)schema.getCol(keyCols[i]);
		sql.append(", " + col.getName());
	}
	sql.append(" from " + table +
		" where " + entityCol.getName() + " = " + entityCol.toSql(entityid0) + ";\n");

	// Remove duplicates already under entityid1
	sql.append("delete from keys0 using " + table);
	sql.append(" where " + entityCol.getName() + " = " + entityCol.toSql(entityid1));
	for (int i=0; i<keyCols.length; ++i) {
		SqlCol col = (SqlCol)schema.getCol(keyCols[i]);
		sql.append(" and keys0." + col.getName() + " = " + table + "." + col.getName());
	}
	sql.append(";\n");

	// Move the rest over to entityid1
	sql.append("update " + table + 
		" set " + entityCol.getName() + " = " + entityCol.toSql(entityid1) +
		" from keys0" +
		" where " + table + "." + entityCol.getName() + " = " + entityCol.toSql(entityid0));
	sql.append(" and keys0.dummy = -1");
	for (int i=0; i<keyCols.length; ++i) {
		SqlCol col = (SqlCol)schema.getCol(keyCols[i]);
		sql.append(" and keys0." + col.getName() + " = " + table + "." + col.getName());
	}
	sql.append(";\n");

	sql.append("drop table keys0;\n");

	System.out.println(sql);
}
// -------------------------------------------------------------------
// =====================================================================
// Schema-based merges --- for preview

public static void bufMerge(DataTabSet tabs, DevelModel dmod0, DevelModel dmod1)
{
	bufMergeMain(dmod0.getPersonSb(), dmod1.getPersonSb());
	Integer entityid1 = (Integer)dmod1.getPersonSb().getValueAt(0, "entityid");
	bufMoveRows("entityid", entityid1, dmod0.getPhonesSb(), dmod1.getPhonesSb());

	for (DataTab tab : tabs.allTabs()) {
		if (tab.getSchema().getType() == SqlSchema.ST_VIEW) continue;
		String name = tab.getTableName();
		bufMoveRows("entityid", entityid1,
			dmod0.getTabSb(name), dmod1.getTabSb(name));
	}
	
//	//	bufMoveRows("entityid", entityid1, dmod0.getClassesSb(), dmod1.getClassesSb());
//	bufMoveRows("entityid", entityid1, dmod0.getDonationSb(), dmod1.getDonationSb());
//	bufMoveRows("entityid", entityid1, dmod0.getEventsSb(), dmod1.getEventsSb());
//	bufMoveRows("entityid", entityid1, dmod0.getFlagSb(), dmod1.getFlagSb());
//	bufMoveRows("entityid", entityid1, dmod0.getInterestsSb(), dmod1.getInterestsSb());
//	bufMoveRows("entityid", entityid1, dmod0.getNotesSb(), dmod1.getNotesSb());
//	bufMoveRows("entityid", entityid1, dmod0.getTicketsSb(), dmod1.getTicketsSb());
//	bufMoveRows("entityid", entityid1, dmod0.getTermsSb(), dmod1.getTermsSb());
}
/** Merge main part of the record.. */
public static void bufMergeMain(SchemaBuf sb0, SchemaBuf sb1)
{
	// Decide whether to overwrite sb1's dob with sb0's dob
	int dobCol = sb0.findColumn("dob");
	int dobapproxCol = sb0.findColumn("dobapprox");
	Object dob0 = sb0.getValueAt(0, dobCol);
	Object dob1 = sb1.getValueAt(0, dobCol);
	boolean dobapprox0 = (Boolean)sb0.getValueAt(0, dobapproxCol);
	boolean dobapprox1 = (Boolean)sb1.getValueAt(0, dobapproxCol);

	if (dob1 == null || (dobapprox1 && dob0 != null && !dobapprox0)) {
		sb1.setValueAt(dob0, 0, dobCol);
		sb1.setValueAt(dobapprox0, 0, dobapproxCol);
	}
	
	for (int col=0; col < sb0.getColumnCount(); ++col) {
		String colName = sb0.getColumnName(col);
		if (col == dobCol || col == dobapproxCol) continue;
		bufMergeCol(sb0, sb1, col);
	}
}

/** Accept a blank string as null also. */
static boolean isnull(Object val)
{
	if (val == null) return true;
	if (val instanceof String) {
		String sval = (String)val;
		return sval.trim().length() == 0;
	} else return false;
}

/** Merge main part of the record.. */
public static void bufMergeCol(SchemaBuf sb0, SchemaBuf sb1, int col)
{
	Object val1 = sb1.getValueAt(0, col);
//System.out.println(col + " val1 = " + val1);
//	if (val1 == null) {
	if (isnull(val1)) {
		Object val0 = sb0.getValueAt(0, col);
		sb1.setValueAt(val0, 0, col);
	}
}


/** Moves row from one JTypeTableModel to another: from aux0 to aux1 */
public static void bufMoveRows(String sEntityCol, Object entityid1,
SchemaBuf aux0, SchemaBuf aux1)
{
	SqlSchema schema = (SqlSchema)aux0.getSchema();
	int entityColIx = schema.findCol(sEntityCol);
//	SqlCol entityCol = (SqlCol)schema.getCol(entityColIx);
//	String table = schema.getDefaultTable();
	int[] keyCols = getKeyCols(schema, entityColIx);

level0:
	for (int row=0; row<aux0.getRowCount(); ++row) {
level1:
		// Look for a matching row in aux1
		for (int i=0; i<aux1.getRowCount(); ++i) {
			// Compare aux0(row,...) with aux1(i,...)
			for (int j=0; ;) {
				int col = keyCols[j];
				Object val0 = aux0.getValueAt(row, col);
				Object val1 = aux1.getValueAt(i, col);
				boolean eq = (val0 == val1 || (val0 != null && val0.equals(val1)));
				if (!eq) continue level1;	// Rows do not match
				// Increment
				++j;
				if (j == keyCols.length) {
					// Found a match in aux1; don't copy this
					continue level0;
				}
			}
		}
		
		// No rows in aux1 match; copy from aux0 to aux1
		int newRow = aux1.insertRow(-1);
		for (int col=0; col<aux0.getColumnCount(); ++col) {
			aux1.setValueAt(aux0.getValueAt(row, col),newRow, col);
		}
		aux1.setValueAt(entityid1, newRow, entityColIx);
		
	}
}

//
///** Merges columns that refer to other records, and by default are set to self. */
//public static void mergeEntityIDCol(SchemaBuf sb0, SchemaBuf sb1, int col)
//{
//	int eidCol = sb0.findColumn("entityid");
//	
//	int eid1 = (Integer)sb1.getValueAt(0, eidCol);
//	int pid1 = (Integer)sb1.getValueAt(0, col);
//	if (eid1 == pid1) {
//		Integer Pid0 = (Integer)sb0.getValueAt(0, col);
//		sb1.setValueAt(Pid0, 0, col);
//	}
//}
//// -------------------------------------------------------------------
//
//
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	FrontApp fapp = new FrontApp(pool,null);
//
//
//	moveRows(fapp.getSchemaSet().get("entities_school"), "entityid",
//		new Integer(12633), new Integer(16840));
//
//}


}
