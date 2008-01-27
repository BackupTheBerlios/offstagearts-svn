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
import offstage.*;
import offstage.swing.typed.*;
import citibob.sql.*;

/**
 *
 * @author citibob
 */
public class CcpaymentWiz extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public CcpaymentWiz(java.awt.Frame owner, SqlRunner str, int entityid, FrontApp app)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner, app.getSwingerMap());
	
	Schema schema = app.getSchema("ccpayments");
//	SwingerMap swingers = app.getSwingerMap();
	
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addWidget("namount", "amount", schema);		// Negative of amount...
	addTextField("description", schema);
	offstage.swing.typed.CCChooser ccchooser = new CCChooser();
		ccchooser.initRuntime(app.getKeyRing());
		ccchooser.setEntityID(str, entityid, app);
	addComponent("ccchooser", ccchooser);
	addWidget("date", schema).setValue(schema.getCol("date").newDate());
//	addWidgetRecursive(ccinfo);
	loadHtml();
}

}
