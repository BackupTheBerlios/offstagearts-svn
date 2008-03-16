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
package offstage.crypt.wiz;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.swing.html.*;
import citibob.swing.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import java.awt.Component;
import offstage.crypt.*;
import java.util.*;

/**
 *
 * @author citibob
 */
public class DupKeyWizard extends OffstageWizard {

public DupKeyWizard(offstage.FrontApp xfapp, Component component)
{
	super("New Key", xfapp, component);
// ---------------------------------------------
addStartState(new AbstractWizState("insertkey1", null, "removekey1") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("dupkey_InsertKey1.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.keyRing();
		if (!kr.isUsbInserted()) stateName = "keynotinserted";
		else {
			try {
				List<KeyRing.PlainFile> keys = kr.readAllKeyFiles();
				v.put("keys", keys);
			} catch(Exception e) {
				e.printStackTrace();
				stateName = "keyerror";
			}
		}
	}
});
// ---------------------------------------------
addState(new AbstractWizState("removekey1", null, "insertkey2") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("dupkey_RemoveKey1.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.keyRing();
		if (kr.isUsbInserted()) stateName = "keynotremoved";
	}
});
// ---------------------------------------------
addState(new AbstractWizState("insertkey2", null, "removekey2") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("dupkey_InsertKey2.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.keyRing();
		if (!kr.isUsbInserted()) stateName = "keynotinserted";
		else {
			try {
				kr.writeKeyFiles((List<KeyRing.PlainFile>)v.get("keys"));
			} catch(Exception e) {
				stateName = "keyerror";
			}
		}
	}
});
// ---------------------------------------------
addState(new AbstractWizState("removekey2", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("dupkey_RemoveKey2.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.keyRing();
		if (kr.isUsbInserted()) stateName = "keynotremoved";
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keyerror", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("dupkey_KeyError.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotinserted", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("KeyNotInserted.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotremoved", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, getResourceName("KeyNotRemoved.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
}


}
