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

/**
 *
 * @author citibob
 */
public class CheckRefundWiz extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public CheckRefundWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, app.swingerMap());
	
	SqlSchema actrans2 = app.getSchema("actrans2");
	SqlSchema actrans2amt = app.getSchema("actrans2amt");
	
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addWidget("amount", "amount", actrans2amt);
	addTextField("description", actrans2);
//	addTextField("name", schema);
	addTextField("ck_number", actrans2);
//	addTextField("phone", schema);
	addWidget("date", actrans2).setValue(actrans2.getCol("date").newDate());
//	addWidget("date", schema).setValue(null);
	loadHtml();
}

}
