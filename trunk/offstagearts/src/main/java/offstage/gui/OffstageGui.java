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
 * FrontGui.java
 *
 * Created on July 13, 2005, 10:52 PM
 */

package offstage.gui;
import java.sql.*;
import javax.swing.*;
import java.util.prefs.*;
import citibob.swing.prefs.*;
import offstage.FrontApp;
//import offstage.EQueryBrowserApp;
import citibob.sql.*;

/**
 *
 * @author  citibob
 */
public class OffstageGui extends javax.swing.JFrame {

//FrameSet frameSet;
FrontApp fapp;

	/** Creates new form FrontGui */
	public OffstageGui() {
		initComponents();
	}

	public void initRuntime(final FrontApp fapp)
	throws org.xml.sax.SAXException, java.io.IOException
	{
		this.fapp = fapp;
		actions.initRuntime(fapp);

		this.oaDatabase.setText(fapp.configName());
		
		// Mess with preferences
		Preferences prefs = fapp.userRoot().node("OffstageGui");
		new SwingPrefs().setPrefs(this, "", prefs);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        tabs = new javax.swing.JTabbedPane();
        actions = new offstage.gui.MaintenanceActionPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        oaDatabase = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        miThrowException = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        miQuit = new javax.swing.JMenuItem();
        mWindow1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Maintenance");

        tabs.addTab("Actions", actions);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel1.setText("OffstageArts Database:");

        oaDatabase.setFont(new java.awt.Font("Dialog", 0, 12));
        oaDatabase.setText("OffstageArts Database: ");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oaDatabase)
                .addContainerGap(488, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(oaDatabase))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jMenu1.setText("File");

        miThrowException.setText("Throw Exception");
        miThrowException.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miThrowExceptionActionPerformed(evt);
            }
        });
        jMenu1.add(miThrowException);
        jMenu1.add(jSeparator1);

        miQuit.setText("Quit");
        miQuit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miQuitActionPerformed(evt);
            }
        });
        jMenu1.add(miQuit);

        jMenuBar1.add(jMenu1);

        mWindow1.setText("Window");
        jMenuBar1.add(mWindow1);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void miThrowExceptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miThrowExceptionActionPerformed
	fapp.guiRun().run(this, new citibob.task.StRunnable() {
	public void run(Statement st) throws Exception {
		throw new Exception("Hello");
	}});
}//GEN-LAST:event_miThrowExceptionActionPerformed

private void miQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miQuitActionPerformed
	System.exit(0);
// TODO add your handling code here:
}//GEN-LAST:event_miQuitActionPerformed
	
	/**
	 * @param args the command line arguments
	 */
//	public static void main(String[] args) throws Exception
//    {
//		JFrame jf = new JFrame();
//
//		DBPrefsDialog d = new DBPrefsDialog(jf, "offstage/db", "offstage/gui/DBPrefsDialog");
//		d.setVisible(true);
//		if (!d.isOkPressed()) return;	// User cancelled DB open
//
//		FrontApp app = new FrontApp(d.newConnPool());
//		OffstageGui gui = new OffstageGui();
//		gui.initRuntime(app, null);
//	    gui.setVisible(true);
//    }
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.gui.MaintenanceActionPanel actions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu mWindow1;
    private javax.swing.JMenuItem miQuit;
    private javax.swing.JMenuItem miThrowException;
    private javax.swing.JLabel oaDatabase;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

/** @param frameTitle Text for frame's title bar
 @param frameName Name of frame, for defaults. */
public static void showPanelInFrame(SqlRun str, final FrontApp fapp,
final JPanel panel, final String frameTitle, final String frameName)
{
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		JFrame frame = new JFrame(frameTitle);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
			new citibob.swing.prefs.SwingPrefs().setPrefs(frame, "", fapp.userRoot().node(frameName));

		frame.setVisible(true);
	}});
}

}
