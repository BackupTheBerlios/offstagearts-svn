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

package offstage.equery.swing;

import citibob.app.App;
import citibob.sql.SqlRun;
import citibob.swing.html.HtmlWiz;
import citibob.swing.table.TableRowCounter;
import javax.mail.MessagingException;
import javax.swing.JScrollPane;
import offstage.FrontApp;
import offstage.email.VettEmail;
import offstage.equery.EQuery;
import offstage.swing.typed.IdSqlPanel;

/**
 *
 * @author citibob
 */
public class CheckBulkWiz extends HtmlWiz {

public IdSqlPanel newIdSqlPanel(App app)
{
	final IdSqlPanel noEmail = new IdSqlPanel();
		noEmail.initRuntime(app);
		noEmail.getTable().setHighlightMouseover(false);
//		noEmail.getTable().setHighlightSelected(false);
	return noEmail;
}

/**
 * Creates a new instance of PersonWiz 
 */
public CheckBulkWiz(SqlRun str, java.awt.Frame owner, FrontApp fapp, EQuery equery)
throws org.xml.sax.SAXException, java.io.IOException, MessagingException
{
	super(owner, fapp.swingerMap());
	setSize(700,530);

	str.execSql(VettEmail.checkBulkEmailQuery(
		equery.getSql(fapp.equerySchema())));
	

	JScrollPane scroll;
	
	final IdSqlPanel noEmail = newIdSqlPanel(fapp);
	final TableRowCounter noEmailCount = noEmail.newRowCounter();
	noEmail.getTable().executeQuery(str,
		" select id from _mm" +
		" where _mm.iscurrent and not _mm.hasemail;\n"
		, "lastname,firstname");
	
	
//	final IdSqlPanel notCurrent = newIdSqlPanel(fapp);
//	final TableRowCounter notCurrentCount = notCurrent.newRowCounter();
//	notCurrent.getTable().executeQuery(str,
//		" select id from _mm" +
//		" where not _mm.iscurrent;\n"
//		, "lastname,firstname");

	final IdSqlPanel goodAddr = newIdSqlPanel(fapp);
	final TableRowCounter goodAddrCount = goodAddr.newRowCounter();
	goodAddr.getTable().executeQuery(str,
		" select id from _mm" +
		" where _mm.iscurrent and _mm.hasemail;\n"
		, "lastname,firstname");

	str.execSql(" drop table _mm;\n");

	str.flush();
	
	addWidget("goodAddr", goodAddr);
	addWidget("goodAddrCount", goodAddrCount);
//	addWidget("notCurrent", notCurrent);
//	addWidget("notCurrentCount", notCurrentCount);
	addWidget("noEmail", noEmail);
	addWidget("noEmailCount", noEmailCount);
	super.addSubmitButton("updateaddr", "Update Emails");
	
	loadHtml();
}

//
//public static void main(String[] args)
//throws Exception
//{
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	TicketParamsWiz wiz = new TicketParamsWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	wiz = new TicketParamsWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
