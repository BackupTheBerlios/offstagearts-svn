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
/*
 * JDate.java
 *
 * Created on May 14, 2003, 8:52 PM
 */

package offstage.swing.typed;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import citibob.exception.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import citibob.app.*;
import citibob.sql.pgsql.*;
import citibob.types.*;

public class EntityIDEditableLabel extends JTypedEditableLabelDB
{


public EntityIDEditableLabel() {}

App app;
EntitySelector sel;


// ---------------------------------------------------------------
// Must override stuff in TextTypedWidget
public void setJType(JType jt, SqlRunner str)
{
	super.setJType(jt, str, new EntityIDLabel.EntityIDDBFormat(this));
}
//public void setJType(JType jt, JFormattedTextField.AbstractFormatter formatter)
//{
//	label.setJType(jt, formatter);
//	ckNull.setEnabled(jt.isInstance(null));	
//}
//public void setJType(JType jt, javax.swing.text.DefaultFormatterFactory ffactory)
//{
//	
//}
// -----------------------------------------------

public void initRuntime(App app) { initRuntime(app, -1); }

public void initRuntime(App app, int termid)
{
	this.app = app;
//	super.setJType(new SqlInteger(), ));
	setJType(new SqlInteger(), app.getBatchSet());
	label.setText("<Select a Person>");
	sel = new EntitySelector();
	sel.setDropDown(true);
	sel.initRuntime(app, termid);
	super.setPopupWidget(sel);		// Makes superclass listen to sel
}

protected void showPopup()
{
	sel.setValue(null);
	super.showPopup();
	sel.requestTextFocus();
}
public void setSearch(SqlRunner str, String text)
//throws SQLException
{
	sel.setSearch(str, text);
}
//// =========================EntityIDFormatter=============
//static class EntityIDFormatter extends DBFormatter
//{
//
//public EntityIDFormatter(ConnPool pool)
//{
//	super(pool);
////	nullText = "<No Person Selected>";
//}
//
//public String valueToString(Statement st, Object value)
//throws java.sql.SQLException
//{
//	String s = SQL.readString(st,
//		" select " +
//			" (case when firstname is null then '' else firstname || ' ' end ||" +
//			" case when middlename is null then '' else middlename || ' ' end ||" +
//			" case when lastname is null then '' else lastname end" +
////			" case when orgname is null then '' else ' (' || orgname || ')' end" +
//			" ) as name" +
//		" from entities" +
//		" where entityid = " + SqlInteger.sql((Integer)value));
//	return s;
//}
//
//}

}

