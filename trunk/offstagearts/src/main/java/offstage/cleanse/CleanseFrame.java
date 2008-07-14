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
 * CleanseFrame.java
 *
 * Created on February 13, 2008, 9:13 PM
 */

package offstage.cleanse;

import citibob.sql.SqlRun;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class CleanseFrame extends javax.swing.JFrame {
	
	/** Creates new form CleanseFrame */
	public CleanseFrame() {
		initComponents();
	}
	/** @param dupType = 'a' (address), 'n' (names), 'o' (organization) */
	public void initRuntime(SqlRun str, FrontApp fapp, String dupType, int cleanseMode)
	{
		switch(cleanseMode) {
			case CleansePanel.M_PROVISIONAL :
				setTitle("Duplicates (provisional)");
			break;
			case CleansePanel.M_APPROVE :
				setTitle("Approve Duplicates");
			break;
			case CleansePanel.M_REGULAR :
				setTitle("Duplicates");
			break;
		}
		cleansePanel1.initRuntime(str, fapp, dupType, cleanseMode);
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        cleansePanel1 = new offstage.cleanse.CleansePanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Duplicates");
        getContentPane().add(cleansePanel1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Window");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new CleanseFrame().setVisible(true);
			}
		});
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.cleanse.CleansePanel cleansePanel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
	
}
