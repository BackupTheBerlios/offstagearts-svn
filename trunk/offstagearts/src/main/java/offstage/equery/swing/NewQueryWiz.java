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

package offstage.equery.swing;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;

/**
 *
 * @author citibob
 */
public class NewQueryWiz extends HtmlWiz {
	
/**
 * Creates a new instance of PersonWiz 
 */
public NewQueryWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner);
	setSize(600,400);
//	TypedWidgetMap map = new TypedWidgetMap();
	addTextField("queryname");
	
	loadHtml();
}


public static void main(String[] args)
throws Exception
{
	JFrame f = new JFrame();
	f.setVisible(true);
	NewQueryWiz wiz = new NewQueryWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	wiz = new NewQueryWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	System.exit(0);
}
}
