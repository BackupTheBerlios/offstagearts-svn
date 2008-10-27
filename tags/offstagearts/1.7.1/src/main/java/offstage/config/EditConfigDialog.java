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
 * EditConfigDialog.java
 *
 * Created on April 19, 2008, 11:22 PM
 */

package offstage.config;

import citibob.text.FileSFormat;
import citibob.text.StringSFormat;
import citibob.types.JavaJType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 
 @author  citibob
 */
public class EditConfigDialog extends javax.swing.JDialog
{
	
public static final int MAIN = 0;
public static final int MAC = 1;
public static final int WINDOWS = 2;
public static final int LINUX = 3;
public static final int NFILES = 4;

public static final String[] fileNames = {"app.properties", "Mac.properties",
	"Windows.properties", "Linux.properties"};

JTextArea[] texts;
String[] values;


File dir;
boolean okPressed;


/** Creates new form EditConfigDialog */
	public EditConfigDialog(JDialog owner)
	{
		super(owner, "Edit Configuration", true);
		initComponents();

		texts = new JTextArea[NFILES];
			
		texts[MAIN] = this.allText;
		texts[MAC] = macText;
		texts[WINDOWS] = windowsText;
		texts[LINUX] = linuxText;
		
		this.lDir.setJType(File.class, new FileSFormat());
		this.tfName.setJType(JavaJType.jtString, new StringSFormat());
	}

	/** Loads up... */
	public void loadConfig(String name, File dir) throws IOException
	{
		this.dir = dir;
System.out.println("dir = " + dir);
		tfName.setValue(name);
		lDir.setValue(dir);
		
		// Load the files
		for (int i=0; i<NFILES; ++i) {
			File f = new File(dir, fileNames[i]);
			int len = (int)f.length();
			try {
				InputStream in = new FileInputStream(f);
				byte[] buf = new byte[len];
				in.read(buf);
				in.close();
				texts[i].setText(new String(buf));
				texts[i].setEnabled(true);
			} catch(FileNotFoundException e) {
				texts[i].setEnabled(false);
			}
		}
	}

	/** Saves after changes... */
	public void saveConfig() throws IOException
	{
		for (int i=0; i<NFILES; ++i) {
			if (!texts[i].isEnabled()) continue;
			
			File f = new File(dir, fileNames[i]);
			
			Writer out = new FileWriter(f);
			out.write(texts[i].getText());
			out.close();
		}
	}

	public String getName() { return (String)tfName.getValue(); }

	
	/** This method is called from within the constructor to
	 initialize the form.
	 WARNING: Do NOT modify this code. The content of this method is
	 always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        allText = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        macText = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        windowsText = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        linuxText = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bCacnel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        tfName = new citibob.swing.typed.JTypedTextField();
        lDir = new citibob.swing.typed.JTypedLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(500, 700));
        jPanel1.setLayout(new java.awt.BorderLayout());

        allText.setColumns(20);
        allText.setRows(5);
        jScrollPane1.setViewportView(allText);

        jTabbedPane1.addTab("All", jScrollPane1);

        macText.setColumns(20);
        macText.setRows(5);
        jScrollPane2.setViewportView(macText);

        jTabbedPane1.addTab("Mac", jScrollPane2);

        windowsText.setColumns(20);
        windowsText.setRows(5);
        jScrollPane3.setViewportView(windowsText);

        jTabbedPane1.addTab("Windows", jScrollPane3);

        linuxText.setColumns(20);
        linuxText.setRows(5);
        jScrollPane4.setViewportView(linuxText);

        jTabbedPane1.addTab("Linux", jScrollPane4);

        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        bOK.setText("OK");
        bOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bOKActionPerformed(evt);
            }
        });
        jPanel2.add(bOK);

        bCacnel.setText("Cancel");
        bCacnel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCacnelActionPerformed(evt);
            }
        });
        jPanel2.add(bCacnel);

        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel1, gridBagConstraints);

        label.setText("Directory: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(label, gridBagConstraints);

        tfName.setEditable(false);
        tfName.setText("jTypedTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(tfName, gridBagConstraints);

        lDir.setText("jTypedLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lDir, gridBagConstraints);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void bOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bOKActionPerformed
	{//GEN-HEADEREND:event_bOKActionPerformed
		// TODO add your handling code here:
		try {
			saveConfig();
			okPressed = true;
		} catch(IOException e) {
			JOptionPane.showMessageDialog(this,
				"Error saving configuration!");
			e.printStackTrace();
		}
		setVisible(false);
	}//GEN-LAST:event_bOKActionPerformed

	private void bCacnelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCacnelActionPerformed
	{//GEN-HEADEREND:event_bCacnelActionPerformed
		okPressed = false;
		setVisible(false);
		// TODO add your handling code here:
	}//GEN-LAST:event_bCacnelActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea allText;
    private javax.swing.JButton bCacnel;
    private javax.swing.JButton bOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private citibob.swing.typed.JTypedLabel lDir;
    private javax.swing.JLabel label;
    private javax.swing.JTextArea linuxText;
    private javax.swing.JTextArea macText;
    private citibob.swing.typed.JTypedTextField tfName;
    private javax.swing.JTextArea windowsText;
    // End of variables declaration//GEN-END:variables
	
}
