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
 * SaveReportWiz.java
 *
 * Created on December 2, 2007, 12:32 AM
 */

package offstage.reports;

import javax.swing.*;
import java.beans.*;
import java.awt.event.*;
import java.io.*;
import citibob.app.*;

/**
 *
 * @author  citibob
 */
public class ChooseFileWiz extends citibob.swing.JPanelWiz
{

public static final int M_READ = 0;		// We're choosing a file to read'
public static final int M_WRITE = 1;		// We're choosing a file to read'

int mode;				// Reading or writing this file we're selecting?
boolean checkOverwrite = true;		// Ask user if we're overwriting a file?
File file;

	/** Creates new form SaveReportWiz 
@param reportName Name of report to be used in preferences node pathname.
 @param ext Filename extension (WITH the dot) to use on report. */
	public ChooseFileWiz(App app, int xmode, String message, final String reportName, String ext)
	{
		super(null);
		initComponents();
		lmessage.setText(message);
		
		this.mode = xmode;
		final java.util.prefs.Preferences pref;
		pref = app.userRoot().node("reports");
		final String dotExt = ext;
		final String starExt = "*" + ext;
		String dir = pref.get(reportName, null);
		if (dir != null) {
			File fdir = new File(dir);
//fdir = new File("/usr");
			chooser.setCurrentDirectory(fdir);



//			remove(chooser);
//			chooser = new JFileChooser(fdir);
////        chooser.setApproveButtonText(">> Next");
////        chooser.setCurrentDirectory(new java.io.File("/export/home"));
////        chooser.setDialogTitle("");
////        chooser.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
//        add(chooser, java.awt.BorderLayout.CENTER);


System.out.println("setting dir: " + fdir);
System.out.println("god dir: " + chooser.getCurrentDirectory());
		}
		chooser.addChoosableFileFilter(
			new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) return true;
				String filename = file.getName();
				return filename.endsWith(dotExt);
			}
			public String getDescription() {
				return starExt;
			}
		});

		
		
		// Add listener for approve and cancel events
		chooser.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
//				JFileChooser chooser = (JFileChooser)evt.getSource();
				if (JFileChooser.APPROVE_SELECTION.equals(evt.getActionCommand())) {
					
					// Check that file doesn't already exist
					String path = chooser.getCurrentDirectory().getAbsolutePath();
					pref.put(reportName, path);

					if (chooser.getSelectedFile() == null) return;
					String fname = chooser.getSelectedFile().getPath();
					if (!fname.endsWith(dotExt)) fname = fname + dotExt;
					file = new File(fname);
					if (mode == M_WRITE && file.exists()) {
						if (JOptionPane.showConfirmDialog(ChooseFileWiz.this,
							"The file " + file.getName() + " already exists.\nWould you like to ovewrite it?",
							"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
						return;
					}

					// Open or Save was clicked
					wrapper.doSubmit("next");
				} else if (JFileChooser.CANCEL_SELECTION.equals(evt.getActionCommand())) {
//					// Check that file doesn't already exist
//					String path = chooser.getCurrentDirectory().getAbsolutePath();
//					pref.put(reportName, path);

					// Cancel was clicked
					wrapper.doSubmit("cancel");
				}
			}
		});
	}


		
		
	
public void getAllValues(java.util.Map m)
{
	m.put("file", file);
}



	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        chooser = new javax.swing.JFileChooser();
        lmessage = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        chooser.setApproveButtonText(">> Next");
        chooser.setCurrentDirectory(new java.io.File("/export/home"));
        chooser.setDialogTitle("");
        chooser.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        add(chooser, java.awt.BorderLayout.CENTER);

        lmessage.setText("jLabel1");
        add(lmessage, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JFileChooser chooser;
    private javax.swing.JLabel lmessage;
    // End of variables declaration//GEN-END:variables
	
}
