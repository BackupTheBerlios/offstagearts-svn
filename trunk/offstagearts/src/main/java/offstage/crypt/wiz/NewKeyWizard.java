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

import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import citibob.swing.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.crypt.wiz.NewKeyWizard;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import offstage.crypt.*;

/**NewKeyWizardor citibob
 */
public class NewKeyWizard extends OffstageWizard {

//	StatemNewKeyWizardDatbase connection
	
public NewKeyWizard(offstage.FrontApp xfapp, java.awt.Frame xframe)
{
	super("New Key", xfapp, xframe, "insertkey");
// ---------------------------------------------
addState(new AbstractWizState("insertkey", null, "removekey") {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Insert Key", true,
			getResourceName("newkey_InsertKey.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.getKeyRing();
		if (!kr.isUsbInserted()) stateName = "keynotinserted";
		else {
			try {
				kr.createNewMasterKey();
				kr.clearPrivKeys();
				kr.loadPubKey();
				DB.rekeyEncryptedData(con.str, kr);
			} catch(Exception e) {
				stateName = "keyerror";
			}
		}
	}
});
// ---------------------------------------------
addState(new AbstractWizState("removekey", "insertkey", null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Remove Key", true,
			getResourceName("newkey_RemoveKey.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
		KeyRing kr = fapp.getKeyRing();
		if (kr.isUsbInserted()) stateName = "keynotremoved";
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keyerror", "insertkey", null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Error", true,
			getResourceName("newkey_KeyError.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotinserted", "insertkey", null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Not Inserted", true,
			getResourceName("KeyNotInserted.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
addState(new AbstractWizState("keynotremoved", "removekey", null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		return new HtmlWiz(frame, "Key Not Removed", true,
			getResourceName("KeyNotRemoved.html"));
	}
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
}


}
