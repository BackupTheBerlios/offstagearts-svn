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
///*
// * EClauseTableModel.java
// *
// * Created on June 23, 2005, 10:34 PM
// */
//
//package offstage.equery.swing;
//
//import citibob.util.*;
//import citibob.swing.*;
//import citibob.swing.table.*;
//import citibob.swing.typed.*;
//import java.sql.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
//import javax.swing.*;
//import java.awt.*;
//import java.util.*;
//import citibob.jschema.*;
//import offstage.equery.*;
//import java.io.*;
//import citibob.types.*;
//
///**
// *
// * @author citibob
// */
//public class WizQueryTableModel extends AbstractJTypeTableModel
//implements EClauseTableConst
//{
//
//public static final int C_ADDSUB = 0;
//public static final int C_COLUMN = 1;
//public static final int C_VALUE = 2;
//public static final int C_FIRSTDT = 3;
//public static final int C_NEXTDT = 4;
//
//public static final String S_ADDSUB = "Add/Sub";
//public static final String S_COLUMN = "Column";
//public static final String S_VALUE = "Value";
//public static final String S_FIRSTDT = "From";
//public static final String S_NEXTDT = "To";
//
//QuerySchema schema;			// Info on valid columns in the query
//WizQuery query;					// The query we're editing
//static JType[] jtypes;
//static {
//	jtypes = new JType[] {
//		new JEnum(new KeyedModel(
//			new Object[] {new Integer(EClause.ADD), new Integer(EClause.SUBTRACT)},
//			new Object[] {"+", "-"})),
//		null,
//		null,
//		new citibob.sql.pgsql.SqlDate(offstage.FrontApp.timeZone, true),
//		new citibob.sql.pgsql.SqlDate(offstage.FrontApp.timeZone, true)};
//}
//public WizQueryTableModel(QuerySchema schema)
//{
//	this.schema = schema;
//}
//// ------------------------------------------------------
//// ------------------------------------------------------
///** Inserts clause before the row'th row of the overall table.  row = rows.size() if we wish to append to end... */
//public void insertClause(int row, WizClause clause)
//{
//	query.insertClause(row, clause);
//}
////public void appendClause(Clause clause)
////	{ insertClause(rows.size(), clause); }
//// ------------------------------------------------------
//public void removeClause(int row)
//{
//	query.removeClause(row);
//}
//// ------------------------------------------------------
//
//public WizQuery getQuery()
//	{ return query; }
//public void setQuery(WizQuery query)
//{
//	this.query = query;
//	this.fireTableDataChanged();
//}
//
//
//// --------------------------------------------------
//public String getColumnName(int column) 
//{
//	switch(column) {
//		case C_ADDSUB : return S_ADDSUB;
//		case C_COLUMN : return S_COLUMN;
//		case C_VALUE : return S_VALUE;
//		case C_FIRSTDT : return S_FIRSTDT;
//		case C_NEXTDT : return S_NEXTDT;
//	}
//	return null;	
//}
//// --------------------------------------------------
///** Allow editing of all non-key fields. */
//public boolean isCellEditable(int rowIndex, int columnIndex)
//{
//	if (query == null) return false;
//	return true;
//}
//// --------------------------------------------------
///** Set entire row.  Normally, setValueAt() will be called with a modified
//version of the object retrieved from getValueAt(). */
//public void setValueAt(Object val, int row, int col)
//{
//	WizClause cl = query.getClause(row);
//	if (cl == null) return;
//	switch(col) {
//		case C_ADDSUB :
//			cl.type = ((Integer)val).intValue();
//		break;
//		case C_COLUMN :
//			JType oldValueType = getJType(row, C_VALUE);
//
//			ColName cn = (ColName)val;
//			cl.colName = cn;
//			this.fireTableCellUpdated(row, C_COLUMN);
//			if (oldValueType == null || !oldValueType.equals(getJType(row, C_VALUE))) {
//				QuerySchema.Col scol = schema.getCol(cl.colName);
//				if (scol == null || scol.col == null) cl.value = null;
//				else cl.value = scol.col.getDefault();
//				this.fireTableCellUpdated(row, C_VALUE);
//			}
//		break;
//		case C_VALUE :
//			cl.value = val;
//			this.fireTableCellUpdated(row, col);
//		break;
//		case C_FIRSTDT :
//			cl.firstDt = (java.util.Date)val;
//		break;
//		case C_NEXTDT :
//			cl.nextDt = (java.util.Date)val;
//		break;
//	}
//	this.fireTableCellUpdated(row, col);		
//
//}
//// --------------------------------------------------
//	public int getRowCount()
//	  { return query.getNumClauses(); }
//	public int getColumnCount()
//	  { return 5; }
//public Object getValueAt(int row, int column)
//{
//	WizClause cl = query.getClause(row);
//	switch(column) {
//		case C_ADDSUB : return new Integer(cl.type);
//		case C_COLUMN : return cl.colName;
//		case C_VALUE : return cl.value;
//		case C_FIRSTDT : return cl.firstDt;
//		case C_NEXTDT : return cl.nextDt;
//	}
//	return null;
//}
//// ===============================================================
//// Implementation of CitibobTableModel (prototype stuff)
//// ===============================================================
//// Implementation of JTypeTableModel (prototype stuff)
//public JType getJType(int row, int column)
//{
//	switch(column) {
//		case C_COLUMN : return schema.getColsJType();
//		case C_VALUE : {
//			WizClause cl = query.getClause(row);
//			if (cl == null) return null;
//			QuerySchema.Col col = schema.getCol(cl.colName);
//			return col.col.getType();
//		}
//		default : return this.jtypes[column];
//	}
//}
//// ===============================================================
//}
