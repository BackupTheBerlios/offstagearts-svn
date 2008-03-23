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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.config;

import citibob.types.JFile;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author citibob
 */
public class ConfigModel extends citibob.swing.table.AbstractJTypeTableModel
{

public static final int C_NAME = 0;
public static final int C_FILE = 1;
final static FileFilter dirFilter = new javax.swing.filechooser.FileFilter() {
	public boolean accept(File f) { return f.isDirectory(); }
	public String getDescription() { return "Offstage Config Folders";
}};
final static JType[] jTypes = {new JavaJType(String.class),
	new JFile(dirFilter, new File(System.getProperty("user.home")), false, true)};
// -----------------------------------------------------------------------------

Preferences prefs;
public ConfigModel(Preferences prefs)
{
	this.prefs = prefs;
}

public Object getValueAt(int row, int col)
{
	try {
		String[] keys = prefs.keys();
		switch(col) {
			case C_NAME : return keys[row];
			case C_FILE : return new File(prefs.get(keys[row], null));
		}
	} catch(Exception e) {}
	
	return null;
}

public void setValueAt(Object val, int row, int col)
{
	try {
		switch(col) {
			case C_NAME : return;
			case C_FILE : {
				prefs.put(prefs.keys()[row], val.toString());
			} break;
		}
	} catch(Exception e) { }
}

public int getColumnCount() { return 2; }
public int getRowCount() {
	try {
		return prefs.keys().length;
	} catch(Exception e) { return 0; }
}
public String getColumnName(int col)
{
	switch(col) {
		case C_NAME : return "Name";
		case C_FILE : return "Folder";
	}
	return null;
}
public JType getJType(int row, int col) { return jTypes[col]; }
// ==================================================================
public void addRow(String name)
{
	prefs.put(name, "");
	this.fireTableDataChanged();
}
public void delRow(int row)
{
	try {
		prefs.remove(prefs.keys()[row]);
	} catch(Exception e) { }
	this.fireTableDataChanged();
}
}
