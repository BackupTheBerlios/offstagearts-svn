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
 * JDate.java
 *
 * Created on May 14, 2003, 8:52 PM
 */

package offstage.swing.typed;

import citibob.swing.text.SFormatAbsFormatter;
import citibob.swing.typed.TypedWidget;
import citibob.types.JType;
import citibob.types.JavaJType;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.text.*;
import citibob.text.*;

/**
 *
 * @author  citibob
 */
public class JTypedButton
extends JButton
implements TypedWidget, KeyListener {

/** Our best guess of the class this takes. */
//Class objClass = null;
protected JType jType;
protected boolean selectOnSet = false;

public JTypedButton()
{
	super();
	addKeyListener(this);
}
/** Once a formatter has figured out what the underlying value and display
 should be, set it.  This is for DBFormatter, when we need to make a DB
 query to format an item.  Only need to implement this method if we're
 planning on making a "DB" subclass of this widget. */
public void setDisplayValue(Object val, String display) {}

// --------------------------------------------------------------
public void setJType(JType jt, javax.swing.text.DefaultFormatterFactory ffactory,
int horizAlign)
{
	jType = jt;
//	super.setFormatterFactory(ffactory);	
	super.setHorizontalAlignment(horizAlign);
}
public void setJType(JType jt, SFormat sformat)
{
	setJType(jt,
		new DefaultFormatterFactory(new SFormatAbsFormatter(sformat)),
		sformat.getHorizontalAlignment());
}
public void setJType(Class klass, SFormat sformat)
{
	setJType(new JavaJType(klass), sformat);
}
public void setJTypeString()
	{ setJType(String.class, new StringSFormat()); }
//
//public void setJType(Class klass, AbstractFormatterFactory ffactory)
//{
//	jType = new JavaJType(klass);
//	super.setFormatterFactory(ffactory);	
//}
//public void setJType(JType jt, JFormattedTextField.AbstractFormatter afmt)
//{
//	jType = jt;
//	super.setFormatterFactory(newFormatterFactory(afmt));
//}
//public void setJType(Class klass, JFormattedTextField.AbstractFormatter afmt)
//{
//	jType = new JavaJType(klass);
//	super.setFormatterFactory(newFormatterFactory(afmt));
//}
//
// --------------------------------------------------------------
/** Override */
//public void setText(String t)
//{
//	super.setText(t);
//	if (selectOnSet) selectAll();
//}
/** Should we do a "select all" on the field when it is re-set (which
happens whenever focus is gained, among other times)? */
public void setSelectOnSet(boolean b) { selectOnSet = b; }
public boolean getSelectOnset() { return selectOnSet; }
// --------------------------------------------------------------
public boolean isInstance(Object o)
{
	return (jType == null ? true : jType.isInstance(o));
}
public boolean stopEditing()
{
//System.out.println("stopEditing: value = " + super.getText() + " --> " + super.getValue());
//	try {
//		super.commitEdit();
		return true;
//	} catch(java.text.ParseException e) {
//		return false;
//	}
}
// --------------------------------------------------------------
public Object getValue()
{
////	String text = super.getText();
//	Object o = super.getValue();
////	Class oclass = (o == null ? null : o.getClass());
////	System.out.println("JTypedTextField returning value: " + text + " --> " + o + "(" + oclass + ")");
//	return o;
    return "somestuff";
}
// --------------------------------------------------------------
//public Class getObjClass()
//	{ return objClass; }
//private void resetValue()
//{
//	setValue(getValue());	// Sets text in accordance with last good value
//}
// JFormatterTextField already calls PropertyChangeEvent
public void setValue(Object val)
{
//if ("tuitionoverride".equals(getColName())) {
//	System.out.println("hoi");
//}
//	super.setValue(val);
}
// ---------------------------------------------------
String colName;
/** Row (if any) in a RowModel we will bind this to at runtime. */
public String getColName() { return colName; }
/** Row (if any) in a RowModel we will bind this to at runtime. */
public void setColName(String col) { colName = col; }
public Object clone() throws CloneNotSupportedException { return super.clone(); }
// ---------------------------------------------------
// =================================================================
// Convenience functions for subclasses that want to override newFormatterFactory()
public static DefaultFormatterFactory newFormatterFactory(Format fmt, String nullText)
{
	return newFormatterFactory(new citibob.swing.text.FormatAbsFormatter(fmt, nullText));
}
public static DefaultFormatterFactory newFormatterFactory(Format fmt)
	{ return newFormatterFactory(fmt, ""); }

public static DefaultFormatterFactory newFormatterFactory(citibob.text.SFormat sfmt)
{
	return new DefaultFormatterFactory(new citibob.swing.text.SFormatAbsFormatter(sfmt));	
}

public static DefaultFormatterFactory newFormatterFactory(
JFormattedTextField.AbstractFormatter afmt)
{
	return new DefaultFormatterFactory(afmt);	
}
// -------------------------------------------------------------------
// ===================== KeyListener =====================
public void keyTyped(KeyEvent evt) {

}
        
public void keyReleased(KeyEvent e) {
//	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) setValue(getValue());
//        System.out.println("released");
}
public void keyPressed(KeyEvent evt) {
    
}    

}
