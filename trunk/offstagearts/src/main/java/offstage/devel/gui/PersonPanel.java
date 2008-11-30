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
 * PersonPanel.java
 *
 * Created on February 9, 2005, 8:18 PM
 */

package offstage.devel.gui;

import citibob.swing.RowModel;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
import citibob.swing.typed.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;
import citibob.task.*;
import citibob.app.App;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import citibob.sql.*;

/**
 *
 * @author  citibob
 */
public class PersonPanel 
extends javax.swing.JPanel {
    
    SchemaRowModel model;	// The RowModel (which uses the schema)
	//SchemaBuf phonesSb;
	//TableModel family;
	DevelModel dmod;
//	Statement st;
//	ActionRunner runner;
	App app;
	
//    public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		Statement st = app.createStatement();
//
//		dm.setKey(146141);
//		dm.doSelect(st);
//		System.out.println("Type = " + dm.getEntityType());
//
//		PersonPanel personPanel = new PersonPanel();
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
    
	/** Creates new form PersonPanel */
	public PersonPanel() {
		initComponents();
		
		familyTable.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			app.guiRun().run(PersonPanel.this, new SqlTask() {
			public void run(SqlRun str) throws Exception {
				Integer EntityID = (Integer)familyTable.getValue();
				if (EntityID == null) return;
				dmod.setKey(EntityID);
				dmod.doSelect(str);
			}});
		}});
		
//		familyTable.addMouseListener(new DClickTableMouseListener(familyTable) {
//		public void doubleClicked(final int row) {
//		}});

	}
	
	public void initRuntime(SqlRun str, FrontApp xfapp, DevelModel dm)
	//throws java.sql.SQLException
	{
		this.app = xfapp;
		this.dmod = dm;
		//SchemaBufRowModel model = dm.getPersonRm();
		SchemaBufRowModel xmodel = new SchemaBufRowModel(dm.getPersonSb());
		//SchemaBuf phonesSb = dm.getPhonesSb();

		//this.phonesSb = phonesSb;
		this.model = xmodel;
		this.vHouseholdID.initRuntime(xfapp);
		
		// Bind the Family Table thingy (it's special)
		familyTable.initRuntime(app);
		
		// Change family table contents when user re-reads from db
		model.addColListener(model.findColumn("primaryentityid"), new RowModel.ColAdapter() {
		public void curRowChanged(final int col) {
			SqlRun str = app.sqlRun();
			str.pushFlush();
				if (model.getCurRow() < 0) return;
				Integer OrigEntityID = (Integer)model.getOrigValue(col);
				Integer EntityID = (Integer)model.get(col);
				if (EntityID == null) return;
				if (OrigEntityID != null && OrigEntityID.intValue() == EntityID.intValue()) {
					// Orig == Value --- greater class probably just re-read from DB.
					// So now we need to re-read too.  This problem should REALLY be
					// solved by adding events to DbModel.
					familyTable.setPrimaryEntityID(str, EntityID);
				}
			str.popFlush();
		}});

		model.addColListener(model.findColumn("entityid"), new RowModel.ColAdapter() {
		public void curRowChanged(final int col) {
			SqlRun str = app.sqlRun();
			str.pushFlush();
				if (model.getCurRow() < 0) return;
				Integer OrigEntityID = (Integer)model.getOrigValue(col);
				Integer EntityID = (Integer)model.get(col);
				if (EntityID == null) return;
				if (OrigEntityID != null && OrigEntityID.intValue() == EntityID.intValue()) {
					// Orig == Value --- greater class probably just re-read from DB.
					// So now we need to re-read too.  This problem should REALLY be
					// solved by adding events to DbModel.
					vHouseholdID.setEntityID(EntityID);
				}
			str.popFlush();
		}});

		middlePane = (MiddlePane)xfapp.newSiteInstance(MiddlePane.class);
		MiddleXPane.removeAll();
		MiddleXPane.add(middlePane);
		middlePane.initRuntime(xmodel);
		
		TypedWidgetBinder.bindRecursive(this, model, app.swingerMap());
//		new TypedWidgetBinder().bind(genderButtonGroup, xmodel);
//		new IsPrimaryBinder().bind(cbIsPrimary, model);	// Should just do a regular listener as above; this is read-only

//		this.entitySubPanel1.initRuntime(app);
		
		phonePanel.initRuntime(str, dm.getPhonesSb(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, true, app.swingerMap());
		
			
//		phonesTable.initRuntime(dm.getPhonesSb());
//		this.addPhoneType.setModel(new GroupTypeKeyedModel(st, "phoneids"));
		// phonesTable.setModel(new ColPermuteTableModel(phonesSb,
		//	new String[] {"Type", "Phone"},
		//	new String[] {"groupid", "phone"}));
//		familyTable.setModel(dm.getFamily());
//		familyTable.initRuntime(dm.getPersonDb().getFamily());
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        lastname2 = new citibob.swing.typed.JTypedTextField();
        lLast2 = new javax.swing.JLabel();
        MiscInfo = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        occupation = new citibob.swing.typed.JTypedTextField();
        title = new citibob.swing.typed.JTypedTextField();
        url = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        orgname = new citibob.swing.typed.JTypedTextField();
        jLabel9 = new javax.swing.JLabel();
        email1 = new citibob.swing.typed.JTypedTextField();
        jLabel11 = new javax.swing.JLabel();
        bLaunchEmail = new javax.swing.JButton();
        bLaunchBrowser = new javax.swing.JButton();
        lLast1 = new javax.swing.JLabel();
        nickname = new citibob.swing.typed.JTypedTextField();
        jBoolCheckbox1 = new citibob.swing.typed.JBoolCheckbox();
        FamilyPane = new javax.swing.JPanel();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        familyTable = new offstage.swing.typed.FamilySelectorTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        vHouseholdID = new offstage.swing.typed.HouseholdIDDropdown();
        bEmancipate = new javax.swing.JButton();
        AddrPanel = new javax.swing.JPanel();
        addressPanel = new javax.swing.JPanel();
        address1 = new citibob.swing.typed.JTypedTextField();
        address2 = new citibob.swing.typed.JTypedTextField();
        city = new citibob.swing.typed.JTypedTextField();
        state = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        customaddressto = new citibob.swing.typed.JTypedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedTextField();
        lastupdated = new citibob.swing.typed.JTypedTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        dbid = new citibob.swing.typed.JTypedLabel();
        PhonePane = new javax.swing.JPanel();
        phonePanel = new offstage.gui.GroupPanel();
        lPhoneNumbers = new javax.swing.JLabel();
        MiddleXPane = new javax.swing.JPanel();
        middlePane = new offstage.devel.gui.MiddlePane();

        setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast.setLayout(new java.awt.GridBagLayout());

        lFirst.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lFirst, gridBagConstraints);

        lMiddle.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lMiddle, gridBagConstraints);

        lLast.setText("Suffix");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lLast, gridBagConstraints);

        salutation.setColName("salutation");
        salutation.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast.add(salutation, gridBagConstraints);

        firstname.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(firstname, gridBagConstraints);

        middlename.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast.add(middlename, gridBagConstraints);

        lastname.setColName("suffix");
        lastname.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast.add(lastname, gridBagConstraints);

        lastname2.setColName("lastname");
        lastname2.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(lastname2, gridBagConstraints);

        lLast2.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lLast2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(FirstMiddleLast, gridBagConstraints);

        MiscInfo.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Occup.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel4, gridBagConstraints);

        jLabel5.setText("DOB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel6, gridBagConstraints);

        jLabel7.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel7, gridBagConstraints);

        occupation.setColName("occupation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(occupation, gridBagConstraints);

        title.setColName("title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(title, gridBagConstraints);

        url.setColName("url");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(url, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(dob, gridBagConstraints);

        orgname.setColName("orgname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(orgname, gridBagConstraints);

        jLabel9.setText("Org.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel9, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(email1, gridBagConstraints);

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel11, gridBagConstraints);

        bLaunchEmail.setText("*");
        bLaunchEmail.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLaunchEmailActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        MiscInfo.add(bLaunchEmail, gridBagConstraints);

        bLaunchBrowser.setText("*");
        bLaunchBrowser.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchBrowser.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLaunchBrowserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        MiscInfo.add(bLaunchBrowser, gridBagConstraints);

        lLast1.setText("Nicknm.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(lLast1, gridBagConstraints);

        nickname.setColName("nickname");
        nickname.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        MiscInfo.add(nickname, gridBagConstraints);

        jBoolCheckbox1.setText("(approx.)");
        jBoolCheckbox1.setColName("dobapprox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        MiscInfo.add(jBoolCheckbox1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        add(MiscInfo, gridBagConstraints);

        FamilyPane.setMinimumSize(new java.awt.Dimension(200, 42));
        FamilyPane.setPreferredSize(new java.awt.Dimension(150, 100));
        FamilyPane.setLayout(new java.awt.GridBagLayout());

        FamilyScrollPanel.setPreferredSize(new java.awt.Dimension(300, 64));

        familyTable.setModel(new javax.swing.table.DefaultTableModel(
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
        FamilyScrollPanel.setViewportView(familyTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        FamilyPane.add(FamilyScrollPanel, gridBagConstraints);

        jLabel8.setText("Family Members");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FamilyPane.add(jLabel8, gridBagConstraints);

        jLabel12.setText("Household:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        FamilyPane.add(jLabel12, gridBagConstraints);

        vHouseholdID.setColName("primaryentityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        FamilyPane.add(vHouseholdID, gridBagConstraints);

        bEmancipate.setText("Emancipate");
        bEmancipate.setMargin(new java.awt.Insets(0, 2, 0, 2));
        bEmancipate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEmancipateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FamilyPane.add(bEmancipate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        add(FamilyPane, gridBagConstraints);

        AddrPanel.setLayout(new java.awt.GridBagLayout());

        addressPanel.setLayout(new java.awt.GridBagLayout());

        address1.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address1, gridBagConstraints);

        address2.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address2, gridBagConstraints);

        city.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        addressPanel.add(city, gridBagConstraints);

        state.setColName("state");
        state.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(state, gridBagConstraints);

        zip.setColName("zip");
        zip.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(zip, gridBagConstraints);

        jLabel3.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(addressPanel, gridBagConstraints);

        jLabel10.setText("To");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        AddrPanel.add(jLabel10, gridBagConstraints);

        customaddressto.setColName("customaddressto");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(customaddressto, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel13, gridBagConstraints);

        jLabel14.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel14, gridBagConstraints);

        entityid.setEditable(false);
        entityid.setColName("entityid");
        entityid.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(entityid, gridBagConstraints);

        lastupdated.setEditable(false);
        lastupdated.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lastupdated, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel15.setText("Database: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel4.add(jLabel15, gridBagConstraints);

        dbid.setText("jTypedLabel1");
        dbid.setColName("dbid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(dbid, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        add(AddrPanel, gridBagConstraints);

        PhonePane.setMinimumSize(new java.awt.Dimension(150, 56));
        PhonePane.setPreferredSize(new java.awt.Dimension(200, 100));
        PhonePane.setLayout(new java.awt.GridBagLayout());

        phonePanel.setPreferredSize(new java.awt.Dimension(453, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        PhonePane.add(phonePanel, gridBagConstraints);

        lPhoneNumbers.setText("Phone Numbers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        PhonePane.add(lPhoneNumbers, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        add(PhonePane, gridBagConstraints);

        MiddleXPane.setLayout(new java.awt.BorderLayout());
        MiddleXPane.add(middlePane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 0, 4);
        add(MiddleXPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

	private void bLaunchEmailActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchEmailActionPerformed
	{//GEN-HEADEREND:event_bLaunchEmailActionPerformed
		citibob.gui.BareBonesMailto.mailto((String)email1.getValue());
	}//GEN-LAST:event_bLaunchEmailActionPerformed

	private void bEmancipateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEmancipateActionPerformed
	{//GEN-HEADEREND:event_bEmancipateActionPerformed
		app.guiRun().run(PersonPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
				model.set("primaryentityid", dmod.getPersonSb().getValueAt(0, "entityid"));
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bEmancipateActionPerformed

	private void bLaunchBrowserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchBrowserActionPerformed
	{//GEN-HEADEREND:event_bLaunchBrowserActionPerformed
		citibob.gui.BareBonesBrowserLaunch.openURL((String)url.getValue());
	}//GEN-LAST:event_bLaunchBrowserActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddrPanel;
    private javax.swing.JPanel FamilyPane;
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel MiddleXPane;
    private javax.swing.JPanel MiscInfo;
    private javax.swing.JPanel PhonePane;
    private citibob.swing.typed.JTypedTextField address1;
    private citibob.swing.typed.JTypedTextField address2;
    private javax.swing.JPanel addressPanel;
    private javax.swing.JButton bEmancipate;
    private javax.swing.JButton bLaunchBrowser;
    private javax.swing.JButton bLaunchEmail;
    private citibob.swing.typed.JTypedTextField city;
    private citibob.swing.typed.JTypedTextField customaddressto;
    private citibob.swing.typed.JTypedLabel dbid;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedTextField email1;
    private citibob.swing.typed.JTypedTextField entityid;
    private offstage.swing.typed.FamilySelectorTable familyTable;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.JBoolCheckbox jBoolCheckbox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lLast1;
    private javax.swing.JLabel lLast2;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lPhoneNumbers;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JTypedTextField lastname2;
    private citibob.swing.typed.JTypedTextField lastupdated;
    private offstage.devel.gui.MiddlePane middlePane;
    private citibob.swing.typed.JTypedTextField middlename;
    private citibob.swing.typed.JTypedTextField nickname;
    private citibob.swing.typed.JTypedTextField occupation;
    private citibob.swing.typed.JTypedTextField orgname;
    private offstage.gui.GroupPanel phonePanel;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField state;
    private citibob.swing.typed.JTypedTextField title;
    private citibob.swing.typed.JTypedTextField url;
    private offstage.swing.typed.HouseholdIDDropdown vHouseholdID;
    private citibob.swing.typed.JTypedTextField zip;
    // End of variables declaration//GEN-END:variables
	// --------------------------------------------------------------

}
