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
 * EmailChooserPanel.java
 *
 * Created on May 5, 2008, 8:53 PM
 */

package offstage.equery.swing;

import citibob.app.App;
import citibob.swing.table.FixedColTableModel;
import citibob.swing.typed.JTypedPanel;
import citibob.types.JDate;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import offstage.equery.swing.MailMsg;

/**
 *
 * @author  citibob
 */
public class EmailChooserPanel extends JTypedPanel
{
	App app;
	Message[] messages;
	Folder folder;
	Store store;
	
	/** Creates new form EmailChooserPanel */
	public EmailChooserPanel() {
		initComponents();
	}

static String getHeader(Message msg, String headerName) throws Exception
{
	String[] ct = msg.getHeader(headerName);
	if (ct == null || ct.length == 0) return null;
	
	return ct[0];
}

static String getBoundary(Message msg) throws Exception
{
	String header = getHeader(msg, "Content-type");
	if (header == null) return null;
	
	String lheader = header.toLowerCase();
	
	int c = lheader.indexOf("boundary");
	if (c < 0) return null;
	c += "boundary".length() + 1;
	
	for (; Character.isWhitespace(lheader.charAt(c)); ++c) ;	// Skip whitespace
	if (lheader.charAt(c) != '=') throw new ParseException("Cannot parse header: " + header, c);
	for (; Character.isWhitespace(lheader.charAt(c)); ++c) ;	// Skip whitespace

	int start,next;
	if (lheader.charAt(c) == '"') {
		++c;
		start = c;
		for (; c < lheader.length() && lheader.charAt(c) != '"'; ++c);
		next = c;
	} else {
		start = c;
		for (; c < lheader.length() && lheader.charAt(c) != ';'; ++c);
		next = c;
	}
	return header.substring(start, next);
}
	
	public void setValue(Object obj) {}
	public Object getValue()
	{
		try {
			int row = table.getSelectedRow();
			if (row < 0) return null;

			Message msg = messages[row];
			MailMsg omsg = new MailMsg();
			
			// Parse out the boundary
			omsg.boundary = getBoundary(msg);
			omsg.subject = getHeader(msg, "Subject");
			
//System.out.println("** Getting Content-type");
//String[] ct = msg.getHeader("Content-type");
//if (ct != null && ct.length > 0) System.out.println("** Content-type: " + ct[0]);
			// Read the main body of the message
			InputStream in = msg.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int n;
			while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
			in.close();
			omsg.body = out.toByteArray();
			return omsg;
		} catch(Exception e) {
			app.expHandler().consume(e);
			return null;
		}
	}
	
	public void initRuntime(App app, String host, String username, String password)
	throws javax.mail.MessagingException
	{
		this.app = app;
		
		// ---------------------- Set up the model
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
 
		// Get the store
		store = session.getStore("imap");
		store.connect(host, username, password);
 
		// Get folder
		folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
 
		// Get directory
		messages = folder.getMessages();
		FetchProfile fp=new FetchProfile();
			fp.add("Subject");
			fp.add("Date");
		folder.fetch(messages, fp);

		MyModel model = new MyModel();
		table.setModelU(model, app.swingerMap());
		
//for (int i=0, n=message.length; i<n; i++) {
//   System.out.println(i + ": " + message[i].getFrom()[0] 
//     + "\t" + message[i].getSubject());
//}
	}
	
	public void close()
	throws javax.mail.MessagingException
	{
		// Close connection 
		folder.close(false);
		store.close();	
	}
	
// ===============================================================
static final String[] colNames = {"messageNumber", "Date", "Subject"};
class MyModel extends FixedColTableModel
{
	public MyModel()
	{
		super(colNames,
			new JType[] {JavaJType.jtInteger, new JDate(app.timeZone(), false), JavaJType.jtString},
			new boolean[] {false,false,false});
	}
		
	public int getRowCount()
		{ return messages.length; }

	public Object getValueAt(int row, int col)
	{
		Message msg = messages[row];
		try {
			switch(col) {
				case 0 : return msg.getMessageNumber();
				case 1 : return msg.getSentDate();
				case 2 : return msg.getSubject();
			}
		} catch(Exception e) {
			app.expHandler().consume(e);
		}
		return null;
	}
}
// ===============================================================

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTypedScrollPane1 = new citibob.swing.typed.JTypedScrollPane();
        table = new citibob.swing.typed.JTypedSelectTable();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTypedScrollPane1.setViewportView(table);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTypedScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTypedScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.swing.typed.JTypedScrollPane jTypedScrollPane1;
    private citibob.swing.typed.JTypedSelectTable table;
    // End of variables declaration//GEN-END:variables
	
}
