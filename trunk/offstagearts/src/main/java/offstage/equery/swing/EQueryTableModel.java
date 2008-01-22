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
//import offstage.equery.EQuery;
//
///**
// *
// * @author citibob
// */
//public class EQueryTableModel extends AbstractTableModel
//implements JTypeTableModel
//{
//
//public static final int C_ADDSUB = 0;
//public static final int C_NAME = 1;
//
//public static final String S_ADDSUB = "Add/Sub";
//public static final String S_NAME = "Name";
//
//EQuery query;
//EClauseTableModel clauseModel;		// Allows us to control current clause.
//int curRow;							// Index of current row in our model --- the clause we're currently editing
//static JType[] jtypes;
//static {
//	jtypes = new JType[] {
//		new JEnum(new KeyedModel(
//			new Object[] {new Integer(EQuery.ADD), new Integer(EQuery.SUBTRACT)},
//			new Object[] {"+", "-"})),
//		new JavaJType(String.class)};
//}
///** Creates a new instance of EClauseTableModel */
//public EQueryTableModel(EClauseTableModel clauseModel)
//{
//	this.query = null;
//	this.clauseModel = clauseModel;
//	curRow = -1;
//}
//public EQuery getQuery()
//	{ return query; }
//public void setCurRow(int curRow)
//{
//	this.curRow = curRow;
//	EQuery.Clause c;
//	if (curRow == -1 || query == null) {
//		c = null;
//	} else {
//		c = query.getClause(curRow);
//	}
//	clauseModel.setClause(c);
//}
//public void setQuery(EQuery query)
//{
//	this.query = query;
//	this.fireTableDataChanged();
//	setCurRow(getRowCount() > 0 ? 0 : -1);
//}
//public void addClause()
//{
//	if (query == null) return;
//	query.getClauses().add(new EQuery.Clause());
//	int idx = query.getClauses().size() - 1;
//	fireTableRowsInserted(idx,idx);
//}
//public void removeClause(int idx)
//{
//	if (query == null) return;
//	if (query.getClauses().size() <= idx) return;
//	if (idx < 0) return;
//	query.getClauses().remove(idx);
//	if (curRow == idx) setCurRow(-1);
//	fireTableRowsDeleted(idx,idx);
//}
//// ===============================================================
//// Implementation of TableModel
//
//// --------------------------------------------------
//public String getColumnName(int column) 
//{
//	switch(column) {
//		case C_ADDSUB : return S_ADDSUB;
//		case C_NAME : return S_NAME;
//	}
//	return null;	
//}
////public int findCol(String colName)
////{
////	if ("Name".equals(colName)) return 0;
////	return -1;
////}
//// --------------------------------------------------
///** Allow editing of all non-key fields. */
//public boolean isCellEditable(int rowIndex, int columnIndex)
//	{ return (query != null); }
//// --------------------------------------------------
///** Set entire row.  Normally, setValueAt() will be called with a modified
//version of the object retrieved from getValueAt(). */
//public void setValueAt(Object val, int rowIndex, int colIndex)
//{
//	if (query == null) return;
//	EQuery.Clause c = query.getClause(rowIndex);
//	switch(colIndex) {
//		case C_ADDSUB : c.type = ((Integer)val).intValue(); break;
//		case C_NAME : c.name = (String)val; break;
//	}
//	// Redisplay the entire row!
//	this.fireTableCellUpdated(rowIndex, colIndex);
//}
//// --------------------------------------------------
//	public int getRowCount()
//	  { return (query == null ? 0 : query.getNumClauses()); }
//	public int getColumnCount()
//	  { return 2; }
//public Object getValueAt(int row, int column)
//{
//	EQuery.Clause c = query.getClause(row);
//	switch(column) {
//		case C_ADDSUB : return new Integer(c.type);
//		case C_NAME : return c.name;
//	}
//	return null;
//}
//public Class getColumnClass(int column) 
//{
//	switch(column) {
//		case C_ADDSUB : return Integer.class;
//		case C_NAME : return String.class;
//	}
//	return String.class;
//}
//// ===============================================================
//// Implementation of CitibobTableModel (prototype stuff)
//// ===============================================================
//// Implementation of JTypeTableModel (prototype stuff)
///** Return SqlType for an entire column --- or null, if this column does not have a single SqlType. */
//public JType getJType(int row, int column)
//{
//	return jtypes[column];
//}
//
/////** Return JType for a cell --- used to set up renderers and editors */
////public JType getJType(int row, int colIndex)
////{ return null; }
//// ===============================================================
//}
