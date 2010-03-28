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
 * SimpleSearchPanel.java
 *
 * Created on June 5, 2005, 5:47 PM
 */

package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.swing.table.StyledTM.ButtonAdapter;
import citibob.swing.table.StyledTM.ButtonListener;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 *
 * @author  citibob
 */
public class IdSqlTable extends SingleSelectStyledTable {

int popupEntityID;		// The entityID implied when the user popped up a menu
JPopupMenu popup;		// The popup menu (if any)
private IdSqlStyledTM myModel;
String[] menuKeys;		// Original menu items to display
JMenuItem[] menuItems;

// =====================================================================
/** Events also via the "value" property */
public static interface PopupListener {
	void onMenuSelected(int menuIndex, String menuString, int entityID);
}
	
protected void showPopup(int row, int col, MouseEvent me)
{
	int entityIDCol = getModelU().findColumn("entityid");
	popupEntityID = (Integer)getModelU().getValueAt(row, entityIDCol);
	System.out.println("dropdown context menu...");

	// Customize the menu items
	int nameCol = getModelU().findColumn("name");
	String name = (String)getModelU().getValueAt(row, nameCol);
	for (int i=0; i<menuKeys.length; ++i) {
		String key = menuKeys[i];
		if (key.indexOf('%') < 0) continue;
		key = key.replace("%", name);
		menuItems[i].setText(key);
	}

	// Show the menu
	popup.show(me.getComponent(), me.getX(), me.getY());
}
	

public void addPopupMenu(String[] keys, final PopupListener listener)
{
	menuKeys = keys;
	popup = new JPopupMenu();
	menuItems = new JMenuItem[keys.length];
	for (int i=0; i<keys.length; ++i) {
		final int index = i;
		final String key = keys[i];
		JMenuItem mi = new JMenuItem(key);
		menuItems[i] = mi;
		
		mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int col = getModelU().findColumn("entityid");
			listener.onMenuSelected(index, key, popupEntityID);
		}});

		popup.add(mi);
	}

	// Set up to receive right-click events
	// .... maybe just do this a simpler way, with a simple MouseListener
	ButtonListener buttonListener = new ButtonListener() {
		boolean hasPopup = false;
		public boolean onPressed(int row, int col, MouseEvent me) {
			if (!me.isPopupTrigger()) {
				hasPopup = true;
				return false;
			}
			hasPopup = true;
			showPopup(row,col,me);
			return true;
		}
		public boolean onReleased(int row, int col, MouseEvent me) {
			if (!me.isPopupTrigger()) return hasPopup;
			showPopup(row,col,me);
			return true;
		}
		public boolean onClicked(int row, int col, MouseEvent me) {
			if (!me.isPopupTrigger()) return hasPopup;
			showPopup(row,col,me);
			return true;
		}
	};
	DataCols<ButtonListener> listenerCols = new DataCols(
		ButtonListener.class, myModel.getModel().getColumnCount());
	listenerCols.setColumn(0, buttonListener);
	myModel.setButtonListenerModel(listenerCols);


}

public void initRuntime(citibob.app.App app) //SqlRun str, FullEntityDbModel dm)
{
//	setFocusable(false);
	myModel = new IdSqlStyledTM(app.swingerMap());
	super.setStyledTM(myModel);
}

IdSqlTableModel getIdSqlTableModel()
	{ return (IdSqlTableModel)getModelU(); }

/** Re-query */
public void executeQuery(SqlRun str, final String idSql, String orderBy)// throws SQLException
{
	executeQuery(str, new SqlSet(idSql), orderBy);
}
public void executeQuery(SqlRun str, final SqlSet idSql, String orderBy)// throws SQLException
{
	((IdSqlTableModel)getStyledTM().getModelU()).executeQuery(str, idSql, orderBy);
}
public void executeQuery(SqlRun str, final SqlSet idSql, boolean hasSortCol, String orderBy)// throws SQLException
{
	((IdSqlTableModel)getStyledTM().getModelU()).executeQuery(str, idSql, hasSortCol, orderBy);
}

// ----------------------------------------------------------------------
// ================================================================
public class IdSqlStyledTM extends DelegateStyledTM
{
	public IdSqlStyledTM(SwingerMap smap)
	{
		super(new IdSqlTableModel());
		RenderEditCols re = super.setColumns(smap,
//			"dotdotdot", "", false, null,
			"name", "Name", false, null);
		int ncol = re.getColumnCount();


	}
	
	// ---------------------------------------------------
	public boolean isEditable(int row, int col) { return false; }
//	setValueColU("entityid");
}
// ======================================================
static class ComponentRenderer implements TableCellRenderer
{
	public int selectedRow = -1;

	JButton component;
	ComponentRenderer(JButton component) {
		this.component = component;
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus,
		int row, int column)
	{
//		component.setSelected(isSelected && hasFocus);
		boolean selected = (row == selectedRow);
//		System.out.println("row = " + row + ", selectedRow = " + selectedRow);
		component.setSelected(selected);
		return component;
	}
}


}
