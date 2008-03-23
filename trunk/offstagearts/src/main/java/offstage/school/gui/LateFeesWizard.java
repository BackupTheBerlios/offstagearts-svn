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
package offstage.school.gui;
import citibob.swing.html.*;
import citibob.wizard.*;
import java.awt.Component;
import offstage.wizards.*;

/**
 *
 * @author citibob
 */
public class LateFeesWizard extends OffstageWizard {


public LateFeesWizard(offstage.FrontApp xfapp, Component comp)
{
	super("Late Fees", xfapp, comp);
// ---------------------------------------------
addStartState(new AbstractWizState("latefees", null, "<end>") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new LateFeesWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
		{}
});

}



}
