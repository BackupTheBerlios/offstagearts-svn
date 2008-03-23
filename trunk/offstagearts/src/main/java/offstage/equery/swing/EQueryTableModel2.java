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
 * EClauseTableModel.java
 *
 * Created on June 23, 2005, 10:34 PM
 */

package offstage.equery.swing;

import citibob.util.*;
import citibob.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import citibob.jschema.*;
import offstage.equery.*;
import java.io.*;
import citibob.types.*;

/**
 *
 * @author citibob
 */
public class EQueryTableModel2 extends AbstractJTypeTableModel
implements EClauseTableConst
{

public static final int C_ADDSUB = 0;
public static final int C_NAME = 1;

public static final String S_ADDSUB = "Add/Sub";
public static final String S_NAME = "Name";


ArrayList rows;		// Description of what goes in each row
QuerySchema schema;			// Info on valid columns in the query
EQuery query;					// The query we're editing
static JType[] jtypesQuery;
static {
	jtypesQuery = new JType[] {
		new JEnum(new KeyedModel(
			new Object[] {new Integer(EClause.ADD), new Integer(EClause.SUBTRACT), new Integer(EClause.ZERO)},
			new Object[] {"+", "-", "0"})),
		new JavaJType(String.class), null};
}
public EQueryTableModel2(QuerySchema schema)
{
	rows = new ArrayList();
	this.schema = schema;
}
// ------------------------------------------------------
static class RowSpec {
//	public Clause clause;
//	public Element element;
	public int cix;
	public int eix;
	public RowSpec(int cix, int eix) {
		this.cix = cix;
		this.eix = eix;
	}
	public boolean isDummy() { return (cix < 0); }
	public boolean isClause() { return (cix >= 0 && eix < 0); }
	public boolean isElement() { return (cix >= 0 && eix >= 0); }
}
public Element getElement(RowSpec rs)
	{ return query.getClause(rs.cix).getElement(rs.eix); }
public EClause getClause(RowSpec rs)
	{ return query.getClause(rs.cix); }
// -------------------------------------------------------
RowSpec getRow(int row)
	{ return (RowSpec)rows.get(row); }
void makeRowSpecs()
{
	rows.clear();
	for (int ci=0; ci<query.getClauses().size(); ++ci) {
		EClause c = (EClause)query.getClauses().get(ci);
		rows.add(new RowSpec(ci, -1));
		for (int ei=0; ei<c.getElements().size(); ++ei) {
			Element e = (Element)c.getElements().get(ei);
			rows.add(new RowSpec(ci, ei));
		}
	}
	rows.add(new RowSpec(-1, -1));		// Dummy row...
}
// ------------------------------------------------------
int baseRow(int row)
{
	return row - (getRow(row).eix + 1);
}
// ------------------------------------------------------
/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public EQuery setSQuery(String squery)
{
	if (squery == null) {	// Set to a blank query
		setQuery(new EQuery());
		return getQuery();
	}
	EQuery eqy = (EQuery)Query.fromXML(squery);
	if (eqy == null) eqy = new EQuery();
	setQuery(eqy);
	return getQuery();
	
//	Object obj = null;
//	try {
//		StringReader fin = new StringReader(squery);
//		EQueryXStream xs = new EQueryXStream();
//		ObjectInputStream ois = xs.createObjectInputStream(fin);
//		obj = ois.readObject();
//	} catch(ClassNotFoundException e) {
//		return null;
////		throw new IOException("Class Not Found in Serialized File");
//	} catch(com.thoughtworks.xstream.io.StreamException se) {
//		return null;
////		throw new IOException("Error reading serialized file");
//	} catch(IOException e) {}	// won't happen
//	
//	if (obj == null) {
//		setQuery(new EQuery());
//	} else if (!(obj instanceof EQuery)) {
//		return null;
////		throw new IOException("Wrong object of class " + obj.getClass() + " found in EQuery file");
//	} else {
//		setQuery((EQuery)obj);
//	}
//	return getQuery();
}

public String getSQuery()
{
	EQuery q = getQuery();
	if (q == null) return null;
	return getQuery().toXML();
//	// Serialize using XML
//	StringWriter fout = new StringWriter();
//	EQueryXStream xs = new EQueryXStream();
//	try {
//		ObjectOutputStream oos = xs.createObjectOutputStream(fout);
//		oos.writeObject(getQuery());
//		oos.close();
//	} catch(IOException e) {}	// won't happen
//	return fout.getBuffer().toString();
}
// ------------------------------------------------------
/** Inserts clause before the row'th row of the overall table.  row = rows.size() if we wish to append to end... */
public void insertClause(int row, EClause clause)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	int cix = (rs.isDummy() ? query.getClauses().size() : rs.cix);
	row = baseRow(row);

	// Add to underlying query
//	int cix = (row < rows.size() ? getRow(row).cix : query.getClauses().size());
//	if (cix < 0) cix = query.getClauses().size();		// Append row
	query.insertClause(cix, clause);

	// Add new rows to table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.setSize(rows, rows.size() + nele + 1);
	ArrayListUtil.shift(rows, row, nele+1);

	// Insert new RowSpecs
	rows.set(row, new RowSpec(cix, -1));
	for (int i=0; i<nele; ++i) rows.set(row + i + 1, new RowSpec(cix, i));

	// Modify indices in all others
	for (int i=row + nele + 1; i<rows.size()-1; ++i) ++getRow(i).cix;

	this.fireTableRowsInserted(row, row+nele+1-1);
}
//public void appendClause(Clause clause)
//	{ insertClause(rows.size(), clause); }
// ------------------------------------------------------
public void removeClause(int row)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	if (!rs.isClause()) return;
	
	row = baseRow(row);
	int cix = getRow(row).cix;
	EClause clause = query.getClause(cix);

	query.removeClause(cix);

	// Remove new rows from table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.shift(rows, row + nele+1, -(nele+1));
	ArrayListUtil.setSize(rows, rows.size() - nele - 1);

	// Modify indices in all others
	for (int i=row; i<rows.size(); ++i) --getRow(i).cix;

	this.fireTableRowsDeleted(row, row+nele+1-1);
}
// ------------------------------------------------------
public void removeElement(int row)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	if (!rs.isElement()) return;
	
//	row = baseRow(EClause	
	EClause clause = query.getClause(rs.cix);
	clause.removeElement(rs.eix);

	// Remove new rows from table model and shift old rows...
	rows.remove(row);
//	ArrayListUtil.shift(rows, row, -1);
//	ArrayListUtil.setSize(rows, rows.size() - 1);

	// Modify indices in all others
	for (int i=rs.eix+1; i<clause.getElements().size(); ++i) --getRow(row-rs.eix+i).eix;

	this.fireTableRowsDeleted(row, row);
}
// ------------------------------------------------------
public void insertElement(int row, Element ele)
{
	if (row < 0) return;
	if (row == 0) return;		// Cannot insert an element here....
	
	// Get clause and element to insert before
	RowSpec prs = getRow(row-1);
	int cix = prs.cix;			// Clause to insert into
	int eix = prs.eix+1;	// Element index to insert before
	
	//if (eix < 0) return;		// Cannot insert here...
	
	insertElement(row, cix,eix,ele);
}
public void insertElement(int row, int cix, int eix, Element ele)
{
	if (eix < 0) return;		// Cannot insert here...
	
//	int brow = baseRow(row - 1);
//	int prow = row-1;		// Now on row to APPEND after, will always be legal

	// Insert it in the EClause
	EClause clause = query.getClause(cix);
	clause.insertElement(eix, ele);		// Header rows have eix == -1, this is OK

	// Insert it in the table model
//	ArrayListUtil.setSize(rows, rows.size() + 1);
//	ArrayListUtil.shift(rows, row, 1);
//	rows.set(row, new RowSpec(cix, eix));
	rows.add(row, new RowSpec(cix, eix));
	for (int i=eix+1; i<clause.getElements().size(); ++i) ++getRow(row-eix+i).eix;

	this.fireTableRowsInserted(row, row);
}
//public void appendElement(int row, Element ele)
//{
//	int brow = baseRow(row);
//	RowSpec rs = getRow(brow);
//	int eix = getClause(rs).getElements().size();
//	int insertRow = brow + 1 + eix;
//	insertElement(insertRow, rs.cix, rs.eix, ele);
//}
// ------------------------------------------------------
public void removeRow(int row)
{
	RowSpec rs = getRow(row);
	if (rs.isElement()) removeElement(row);
	if (rs.isClause()) removeClause(row);
}
// ------------------------------------------------------

public EQuery getQuery()
	{ return query; }
public void setQuery(EQuery query)
{
	this.query = query;
	makeRowSpecs();
	this.fireTableDataChanged();
}

///** Inserts a new clause before the row'th row in the table. */
//public void newClause(int row)
//{
//	if (row < 0) {
//		// Append at end
//		int idx = query.newClause();
//		Clause c = query.
//	} else {
//		RowSpec rs = getRow(row);
//	}
//}
//
//
//public void addClause(int row)
//{
//	RowSpec rs = getRow(row);
//	rs.
//	if (query == null) return;
//	query.getClauses().add(new Clause());
//	int idx = query.getClauses().size() - 1;
//	fireTableRowsInserted(idx,idx);
//}
//public void addClause(int row)
//{
//	RowSpec rs = getRow(row);
//	rs.
//	if (query == null) return;
//	query.getClauses().add(new Clause());
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
// ===============================================================
// Implementation of TableModel

// --------------------------------------------------
public String getColumnName(int column) 
{
	  switch(column) {
		  case C_COLUMN : return S_COLUMN;
		  case C_COMPARE : return S_COMPARE;
		  case C_VALUE : return S_VALUE;
	  }
	  return null;	
}
// --------------------------------------------------
/** Allow editing of all non-key fields. */
public boolean isCellEditable(int rowIndex, int columnIndex)
{
	if (query == null) return false;
	
	RowSpec rs = getRow(rowIndex);
	if (rs.isDummy()) return false;
	if (rs.isClause()) {
		return (columnIndex < 2);
	} else {
		return (query != null);
	}
}
// --------------------------------------------------
/** Set entire row.  Normally, setValueAt() will be called with a modified
version of the object retrieved from getValueAt(). */
public void setValueAt(Object val, int row, int col)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) return;
	if (rs.isClause()) {
		if (query == null) return;
		EClause c = query.getClause(rs.cix);
		switch(col) {
			case C_ADDSUB : c.type = ((Integer)val).intValue(); break;
			case C_NAME : c.name = (String)val; break;
		}
		// Redisplay the entire row!
		this.fireTableCellUpdated(row, col);		
	} else {	// Body (ElemeEClausew
		EClause c = query.getClause(rs.cix);
		Element el = c.getElement(rs.eix);
		//EQuery.Element el = getElement(row);
		if (el == null) return;
		switch(col) {
			case C_COLUMN :
				JType oldCompareType = getJType(row, C_COMPARE);
				JType oldValueType = getJType(row, C_VALUE);

				ColName cn = (ColName)val;
				el.colName = cn;
				this.fireTableCellUpdated(row, C_COLUMN);

				// Update other cols if needed...
				if (oldCompareType == null || !oldCompareType.equals(getJType(row, C_COMPARE))) {
					el.comparator = "=";
					this.fireTableCellUpdated(row, C_COMPARE);
				}
				if (oldValueType == null || !oldValueType.equals(getJType(row, C_VALUE))) {
					QuerySchema.Col scol = schema.getCol(el.colName);
					if (scol == null || scol.col == null) el.value = null;
					else el.value = scol.col.getDefault();
					this.fireTableCellUpdated(row, C_VALUE);
				}

			break;
			case C_COMPARE :
				String s = (String)val;
				el.comparator = s;
				this.fireTableCellUpdated(row, col);
			break;
			case C_VALUE :
				el.value = val;
				this.fireTableCellUpdated(row, col);
			break;
		}
	}	
}
// --------------------------------------------------
	public int getRowCount()
	  { return rows.size(); }
	public int getColumnCount()
	  { return 3; }
public Object getValueAt(int row, int column)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) {
		switch(column) {
			case C_COLUMN : return "Append";
		}
		return null;
	} else if (rs.isClause()) {
		EClause c = query.getClause(rs.cix);
		switch(column) {
			case C_ADDSUB : return new Integer(c.type);
			case C_NAME : return c.name;
		}
		return null;
	} else {
//System.err.println(row + ", " + column);
		Element el = getElement(rs);
		if (el == null) return null;
		switch(column) {
			case C_COLUMN : return el.colName;
			case C_COMPARE : return el.comparator;
			case C_VALUE : return el.value;
		}
		return null;
		
	}
}
//public Class getColumnClass(int column) 
//{
//	switch(column) {
//		case C_ADDSUB : return Integer.class;
//		case C_NAME : return String.class;
//	}
//	return String.class;
//}
// ===============================================================
// Implementation of CitibobTableModel (prototype stuff)
// ===============================================================
// Implementation of JTypeTableModel (prototype stuff)
JFile jFile = new JFile(new javax.swing.filechooser.FileFilter() {
	public boolean accept(File file) { return file.getName().endsWith(".csv"); }
	public String getDescription() { return "*.csv"; }
}, new File("."), true);
public JType getJType(int row, int column)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) {
		return null;
	} if (rs.isClause()) {
		return jtypesQuery[column];	
	} else {
		if (column == C_COLUMN) return schema.getColsJType();
	
		Element el = getElement(rs);
		if (el == null) return null;
		QuerySchema.Col col = schema.getCol(el.colName);
		if (col == null) return null;
		if (column == C_COMPARE) return col.comparators;
		if (col.col == null) return null;
		if (column == C_VALUE) {
			if (el.comparator.contains("file")) {
//				return JavaJType.jtString;
				return jFile;
			} else {
				return col.col.getType();
			}
		}
		return null;
	}
}

///** Return JType for a cell --- used to set up renderers and editors */
//public JType getJType(int row, int colIndex)
//{ return null; }
// ===============================================================
}
