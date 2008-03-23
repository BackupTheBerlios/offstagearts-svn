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
///*
// * EClauseTableModel.java
// *
// * Created on June 23, 2005, 10:34 PM
// */
//
//package offstage.equery.swing;
//
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
//import offstage.equery.EQuery;
//import offstage.equery.EQuerySchema;
//import citibob.util.KeyedModel;
//
///**
// *
// * @author citibob
// */
//public class EClauseTableModel extends AbstractTableModel
//implements JTypeTableModel, EClauseTableConst
//{
//
//EQuerySchema schema;
//EQuery.Clause clause;
//
///** Creates a new instance of EClauseTableModel */
//public EClauseTableModel(EQuerySchema schema, EQuery.Clause clause)
//{
//	this.schema = schema;
//	this.clause = clause;
//}
//public EClauseTableModel(EQuerySchema schema)
//{
//	this.schema = schema;
//	this.clause = null;
//}
//// ------------------------------------------------------
//public EQuerySchema getSchema()
//{
//	return schema;
//}
//public EQuery.Clause getClause()
//	{ return clause; }
//public void setClause(EQuery.Clause clause)
//{
//	this.clause = clause;
//	this.fireTableDataChanged();
//}
//public void addElement()
//{
//	if (clause == null) return;
//	clause.elements.add(new EQuery.Element());
//	int idx = clause.elements.size() - 1;
//	fireTableRowsInserted(idx,idx);
//}
//public void removeElement(int idx)
//{
//	if (clause == null) return;
//	if (clause.elements.size() <= idx) return;
//	if (idx < 0) return;
//	clause.elements.remove(idx);
//	fireTableRowsDeleted(idx,idx);
//}
//// ===============================================================
//// Implementation of TableModel
//
//// --------------------------------------------------
//public String getColumnName(int column) 
//{
//	  switch(column) {
//		  case C_COLUMN : return S_COLUMN;
//		  case C_COMPARE : return S_COMPARE;
//		  case C_VALUE : return S_VALUE;
//	  }
//	  return null;	
//}
////public int findColumn(String colName)
////{
////	if (S_COLUMN.equals(colName)) return C_COLUMN;
////	if (S_COMPARE.equals(colName)) return C_COMPARE;
////	if (S_VALUE.equals(colName)) return C_VALUE;
////	return -1;
////}
//// --------------------------------------------------
///** Allow editing of all non-key fields. */
//public boolean isCellEditable(int rowIndex, int columnIndex)
//	{ return (clause != null); }
//// --------------------------------------------------
//public EQuery.Element getElement(int row)
//	{ return (clause == null ? null : (EQuery.Element)clause.elements.get(row)); }
//public void setValueAt(Object val, int row, int colIndex)
//{
//	EQuery.Element el = getElement(row);
//	if (el == null) return;
//	switch(colIndex) {
//		case C_COLUMN :
//			JType oldCompareType = getJType(row, C_COMPARE);
//			JType oldValueType = getJType(row, C_VALUE);
//			
//			EQuery.ColName cn = (EQuery.ColName)val;
//			el.colName = cn;
//			this.fireTableCellUpdated(row, C_COLUMN);
//			
//			// Update other cols if needed...
//			if (oldCompareType == null || !oldCompareType.equals(getJType(row, C_COMPARE))) {
//				el.comparator = "=";
//				this.fireTableCellUpdated(row, C_COMPARE);
//			}
//			if (oldValueType == null || !oldValueType.equals(getJType(row, C_VALUE))) {
//				EQuerySchema.Col col = schema.getCol(el.colName);
//				if (col == null || col.col == null) el.value = null;
//				else el.value = col.col.getDefault();
//				this.fireTableCellUpdated(row, C_VALUE);
//			}
//			
//		break;
//		case C_COMPARE :
//			String s = (String)val;
//			el.comparator = s;
//			this.fireTableCellUpdated(row, colIndex);
//		break;
//		case C_VALUE :
//			el.value = val;
//			this.fireTableCellUpdated(row, colIndex);
//		break;
//	}
//}
//// --------------------------------------------------
//	public int getRowCount()
//	  { return (clause == null ? 0 : clause.elements.size()); }
//	public int getColumnCount()
//	  { return 3; }
//	public Object getValueAt(int row, int col)
//	{
//		EQuery.Element el = getElement(row);
//		if (el == null) return null;
//		switch(col) {
//			case C_COLUMN : return el.colName;
//			case C_COMPARE : return el.comparator;
//			case C_VALUE : return el.value;
//		}
//		return null;
//	}
//// ===============================================================
//// Implementation of JTypeTableModel (prototype stuff)
/////** Return SqlType for an entire column --- or null, if this column does not have a single SqlType. */
////public JType getColumnJType(int col)
////	{ return null; }
//
///** Return JType for a cell --- used to set up renderers and editors */
//public JType getJType(int row, int colIndex)
//{
//	if (colIndex == C_COLUMN) return schema.getColsJType();
//	
//	EQuery.Element el = getElement(row);
//	if (el == null) return null;
//	EQuerySchema.Col col = schema.getCol(el.colName);
//	if (col == null) return null;
//	if (colIndex == C_COMPARE) return col.comparators;
//	if (col.col == null) return null;
//	if (colIndex == C_VALUE) return col.col.getType();
//	return null;
//}
//// ===============================================================
//}
