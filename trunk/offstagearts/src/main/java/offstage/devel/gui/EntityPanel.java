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
 * EntityPanel.java
 *
 * Created on June 5, 2005, 9:48 AM
 */

package offstage.devel.gui;


import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import citibob.sql.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.awt.*;
import java.awt.event.*;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;
import citibob.app.*;

/**
 *
 * @author  citibob
 */
public class EntityPanel extends javax.swing.JPanel {
	
	/** Creates new form EntityPanel */
	public EntityPanel() {
		initComponents();
	}

	public void setAllBackground(Color bg)
	{
		setBackground(bg);
		mainPanel.setBackground(bg);
	}
	
	EntityPanel getThis() { return this; }
	
	public void initRuntime(SqlRunner str, FrontApp fapp, DevelModel dm)
	//throws java.sql.SQLException
	{
		App app = fapp;
		mainPanel.initRuntime(str, app, dm);
		
		// Init the credit card panel
		TypedWidgetBinder.bindRecursive(this, mainPanel.model, app.getSwingerMap());
		cryptCCInfo.initRuntime(fapp.getKeyRing());
		
		donationsPanel.initRuntime(str, dm.getDonationSb(),
			new String[] {"Type", "Date", "Amount"},
			new String[] {"groupid", "date", "amount"}, true, app.getSwingerMap());
		flagsPanel.initRuntime(str, dm.getFlagSb(),
			new String[] {"Type"},
			new String[] {"groupid"}, true, app.getSwingerMap());
		eventsPanel.initRuntime(str, dm.getEventsSb(),
			new String[] {"Event"},//, "Role"},
			new String[] {"groupid"/*, "role"*/}, true, app.getSwingerMap());
		notesPanel.initRuntime(str, dm.getNotesSb(),
			new String[] {"Type", "Date", "Note"},
			new String[] {"groupid", "date", "note"}, true, app.getSwingerMap());
		ticketsPanel.initRuntime(str, dm.getTicketsSb(),
			new String[] {"Event", "Date", "Type", "Venue", "Perf Type", "#Tix", "Payment", "Offer Code"},
			new String[] {"groupid", "date", "tickettypeid", "venueid", "perftypeid", "numberoftickets", "payment", "offercodeid"},
			true, app.getSwingerMap());
		interestsPanel.initRuntime(str, dm.getInterestsSb(),
			new String[] {"Interest", "By Person", "Referred By"},
			new String[] {"groupid", "byperson", "referredby"}, true, app.getSwingerMap());
//		classesPanel.initRuntime(str, dm.getClassesSb(),
//			new String[] {"Class", "Comments"},
//			new String[] {"groupid", "comments"}, app.getSwingerMap());
		termsPanel.initRuntime(str, dm.getTermsSb(),
			new String[] {"Term", "Role"},
			new String[] {"groupid", "courserole"},
			false, app.getSwingerMap());
			termsPanel.setEnabled(false);
		
//		dm.addListener(new FullEntityDbModel.Adapter() {
//		public void entityTypeChanged(int type) {
//			CardLayout cl = (CardLayout)(mainPanel.getLayout());
//			switch(type) {
//				case FullEntityDbModel.ORG :
//					cl.show(mainPanel, "org");
//				break;
//				case FullEntityDbModel.PERSON :
//					cl.show(mainPanel, "person");
//				break;
//			}
//		}});
		this.cryptCCInfo.initRuntime(fapp.getKeyRing());
		
	}	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        classesPanel = new offstage.gui.GroupPanel();
        groupPanels = new javax.swing.JTabbedPane();
        donationsPanel = new offstage.gui.GroupPanel();
        eventsPanel = new offstage.gui.GroupPanel();
        notesPanel = new offstage.gui.GroupPanel();
        ticketsPanel = new offstage.gui.GroupPanel();
        interestsPanel = new offstage.gui.GroupPanel();
        termsPanel = new offstage.gui.GroupPanel();
        flagsPanel = new offstage.gui.GroupPanel();
        cryptCCInfo = new offstage.swing.typed.CryptCCInfo();
        mainPanel = new offstage.devel.gui.PersonPanel();

        groupPanels.setPreferredSize(new java.awt.Dimension(458, 200));
        groupPanels.addTab("Donations", donationsPanel);
        groupPanels.addTab("Events", eventsPanel);
        groupPanels.addTab("Notes", notesPanel);
        groupPanels.addTab("Tickets", ticketsPanel);
        groupPanels.addTab("Interests", interestsPanel);
        groupPanels.addTab("Terms", termsPanel);
        groupPanels.addTab("Flags", flagsPanel);
        groupPanels.addTab("Credit Card", cryptCCInfo);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(groupPanels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 389, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.gui.GroupPanel classesPanel;
    private offstage.swing.typed.CryptCCInfo cryptCCInfo;
    private offstage.gui.GroupPanel donationsPanel;
    private offstage.gui.GroupPanel eventsPanel;
    private offstage.gui.GroupPanel flagsPanel;
    private javax.swing.JTabbedPane groupPanels;
    private offstage.gui.GroupPanel interestsPanel;
    private offstage.devel.gui.PersonPanel mainPanel;
    private offstage.gui.GroupPanel notesPanel;
    private offstage.gui.GroupPanel termsPanel;
    private offstage.gui.GroupPanel ticketsPanel;
    // End of variables declaration//GEN-END:variables

//	public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		Statement st = app.getPool().checkout().createStatement();
//
//		dm.setKey(139208);
//		dm.doSelect(st);
//		System.out.println("Type = " + dm.getEntityType());
//
//		EntityPanel personPanel = new EntityPanel();
//		personPanel.initRuntime(st, dm);//personRM, dm.getPhonesSb());
//
//		
//		
//	    JFrame frame = new JFrame();
//	    frame.getContentPane().add(personPanel);
//		frame.pack();
//	    frame.setVisible(true);
//		System.out.println("Done");
//    }
	
}
