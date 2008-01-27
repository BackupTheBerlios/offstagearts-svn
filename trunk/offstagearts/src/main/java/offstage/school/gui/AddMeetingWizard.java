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
package offstage.school.gui;
import citibob.swing.html.*;
import citibob.wizard.*;
import offstage.wizards.*;

/**
 *
 * @author citibob
 */
public class AddMeetingWizard extends OffstageWizard {


public AddMeetingWizard(offstage.FrontApp xfapp, java.awt.Component comp)
{
	super("Add Meeting to Course", xfapp, comp);
// ---------------------------------------------
addStartState(new AbstractWizState("addmeeting", null, "<end>") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new AddMeetingWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
		{}
});

}



}
