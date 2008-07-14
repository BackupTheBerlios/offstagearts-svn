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

import citibob.swing.typed.*;
import citibob.swing.html.*;
import citibob.app.*;
import java.util.Properties;
import javax.mail.MessagingException;

/**
 *
 * @author citibob
 */
public class EmailMsgWiz extends HtmlWiz {
	
EmailChooserPanel chooserPanel;
	/**
 * Creates a new instance of PersonWiz 
 */
public EmailMsgWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException, MessagingException
{
	super(owner, app.swingerMap());
	setSize(600,500);
	
	Properties props = app.props();
	EmailChooserPanel tw = chooserPanel = new EmailChooserPanel();
	tw.initRuntime(app, "mail.citibob.net",
		props.getProperty("imaptemplates.user"),
		props.getProperty("imaptemplates.password"));
	addWidget("emails", tw);

//	app.setUserPrefs(this, );

	loadHtml();
}

public void close() throws MessagingException
{
	chooserPanel.close();
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
