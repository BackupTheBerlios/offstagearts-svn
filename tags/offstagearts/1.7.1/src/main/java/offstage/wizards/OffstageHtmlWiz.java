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
 * OffstageHtmlWiz.java
 *
 * Created on June 24, 2007, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.wizards;

import citibob.swing.html.*;
import java.awt.*;

/**
 *
 * @author citibob
 */
public class OffstageHtmlWiz extends HtmlWiz
{
	
public OffstageHtmlWiz(Frame owner)
{ super(owner); }

//public EntitySelector addEntitySelector(String name, citibob.app.App app)
//{ return addEntitySelector(name, new EntitySelector(), app); }
//
///** Adds an EntitySelector JPanel object, one that submits on double-click.  Convenience method. */
//public EntitySelector addEntitySelector(final String name, EntitySelector sel, citibob.app.App app)
//{
//	sel.initRuntime(app);
//	// Double-clicking will go to selected person
//	sel.getSearchTable().addMouseListener(new MouseAdapter() {
//	public void mouseClicked(MouseEvent e) {
//		if( e.getClickCount() != 2 ) return;
//		setSubmit(name);
//		setVisible(false);
//	}});
//	html.addWidget(name, sel);
//	return sel;
//}

}
