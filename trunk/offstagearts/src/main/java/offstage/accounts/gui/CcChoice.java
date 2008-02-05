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
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.accounts.gui;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import citibob.app.*;
import offstage.schema.*;
import citibob.jschema.*;
import citibob.util.*;
import citibob.types.*;

/**
 *
 * @author citibob
 */
public class CcChoice extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public CcChoice(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, app.getSwingerMap());
	
	SqlSchema schema = app.getSchema("ccpayments");
//	SwingerMap swingers = app.getSwingerMap();
	
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addWidget("namount", "amount", schema);		// Negative of amount...
	KeyedModel kmodel = new KeyedModel();
		kmodel.addItem(null, "<None>");
		kmodel.addItem("m", "Master Card");
		kmodel.addItem("v", "Visa");
	addComponent("cctype", new JKeyedComboBox(kmodel));
	JTypedTextField tf;
	tf = new JTypedTextField();
		tf.setJType(String.class, new CCSFormat());
		addComponent("ccnumber", tf);
	tf = new JTypedTextField();
		tf.setJType(String.class, new ExpDateSFormat());
		addComponent("expdate", tf);
	tf = new JTypedTextField();
		tf.setJType(String.class, new DigitsSFormat(3));
		addComponent("seccode", tf);
	tf = new JTypedTextField();
		tf.setJType(String.class, new DigitsSFormat(5));
		addComponent("zip", tf);
	addTextField("description", schema);
	addWidget("dtime", schema).setValue(null);
	loadHtml();
}

}
