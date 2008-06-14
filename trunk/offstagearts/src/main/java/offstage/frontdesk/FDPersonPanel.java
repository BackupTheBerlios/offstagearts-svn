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

package offstage.frontdesk;

import javax.swing.*;
import citibob.jschema.*;
import citibob.swing.typed.*;
import citibob.task.*;
import citibob.sql.*;
import citibob.swing.WidgetTree;
import citibob.wizard.Wizard;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class FDPersonPanel 
extends javax.swing.JPanel {
//    FrontApp fapp;
    SchemaRowModel model;	// The RowModel (which uses the schema)
	FDPersonModel dmod;
	FrontApp app;
//        SchoolModel smod;
        
    
	/** Creates new form PersonPanel */
	public FDPersonPanel() {
		initComponents();
		genderButtonGroup.add("M", maleButton);
		genderButtonGroup.add("F", femaleButton);
		genderButtonGroup.add(null, unknownGenderButton);
		
//		familyTable.addPropertyChangeListener("value", new PropertyChangeListener() {
//		public void propertyChange(PropertyChangeEvent evt) {
//			app.guiRun().run(FDPersonPanel.this, new SqlTask() {
//			public void run(SqlRun str) throws Exception {
//				Integer EntityID = (Integer)familyTable.getValue();
//				if (EntityID == null) return;
//				dmod.setKey(EntityID);
//				dmod.doSelect(str);
//			}});
//		}});

	}
	
	public void initRuntime(SqlRun str, FrontApp xapp)
	{
		this.app = xapp;
		
		this.dmod = new FDPersonModel(str, app);
		SchemaBufRowModel xmodel = new SchemaBufRowModel(dmod.getPersonDm().getSchemaBuf());

		this.model = xmodel;
		
		vPayerID.initRuntime(app);
		vParent1ID.initRuntime(app);
		
		TypedWidgetBinder.bindRecursive(this, model, app.swingerMap());
		new TypedWidgetBinder().bind(genderButtonGroup, xmodel);
		
		phonePanel.initRuntime(str, dmod.getPhonesDm().getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, true, app.swingerMap());

		ocdiscPanel.initRuntime(str, dmod.ocdiscs.getSchemaBuf(), "ocdiscid",
			new String[] {"Discount", "Start Date", "End Date"},
			new String[] {"ocdiscid", "dstart", "dend"}, true, app.swingerMap());

		
//                ParentPayer.initRuntime(str, app);
//                IntKeyedDbModel PeopleDm = new IntKeyedDbModel(app.getSchema("persons"), "entityid");
//               PeopleDm.setKey(entityid);
//		SchemaBufRowModel PeopleRm = new SchemaBufRowModel(PeopleDm.getSchemaBuf());
//		TypedWidgetBinder.bindRecursive(parentPayer1.vParent1ID, PeopleRm, app.swingerMap());
//              TypedWidgetBinder.bindRecursive(parentPayer1.vPayerID, PeopleRm, app.swingerMap());
                
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        genderButtonGroup = new citibob.swing.typed.KeyedButtonGroup();
        phonePanel = new offstage.gui.GroupPanel();
        lPhoneNumbers = new javax.swing.JLabel();
        AddrPanel = new javax.swing.JPanel();
        addressPanel = new javax.swing.JPanel();
        address1 = new citibob.swing.typed.JTypedTextField();
        address2 = new citibob.swing.typed.JTypedTextField();
        city = new citibob.swing.typed.JTypedTextField();
        state = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedTextField();
        lastupdated = new citibob.swing.typed.JTypedTextField();
        jPanel4 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        MiscInfo = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        url = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        email1 = new citibob.swing.typed.JTypedTextField();
        jLabel11 = new javax.swing.JLabel();
        bLaunchEmail = new javax.swing.JButton();
        bLaunchBrowser = new javax.swing.JButton();
        mailprefid = new citibob.swing.typed.JKeyedComboBox();
        jLabel1 = new javax.swing.JLabel();
        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        jButton1 = new javax.swing.JButton();
        PayerParent = new javax.swing.JPanel();
        vPayerID = new offstage.swing.typed.EntityIDDropdown();
        jLabel4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        vParent1ID = new offstage.swing.typed.EntityIDDropdown();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Gender = new javax.swing.JPanel();
        maleButton = new javax.swing.JRadioButton();
        femaleButton = new javax.swing.JRadioButton();
        unknownGenderButton = new javax.swing.JRadioButton();
        lPhoneNumbers1 = new javax.swing.JLabel();
        ocdiscPanel = new offstage.gui.GroupPanel();

        genderButtonGroup.setColName("gender");

        setLayout(new java.awt.GridBagLayout());

        phonePanel.setMinimumSize(new java.awt.Dimension(153, 116));
        phonePanel.setPreferredSize(new java.awt.Dimension(453, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(phonePanel, gridBagConstraints);

        lPhoneNumbers.setText("Discount Codes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(lPhoneNumbers, gridBagConstraints);

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
        addressPanel.add(city, gridBagConstraints);

        state.setColName("state");
        state.setMinimumSize(new java.awt.Dimension(30, 19));
        state.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(state, gridBagConstraints);

        zip.setColName("zip");
        zip.setMinimumSize(new java.awt.Dimension(80, 19));
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(addressPanel, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridLayout(2, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(AddrPanel, gridBagConstraints);

        mainPanel.setMinimumSize(new java.awt.Dimension(350, 200));
        mainPanel.setPreferredSize(new java.awt.Dimension(355, 124));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        MiscInfo.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("DOB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel5, gridBagConstraints);

        jLabel7.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel7, gridBagConstraints);

        url.setColName("url");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(url, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(dob, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(email1, gridBagConstraints);

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        MiscInfo.add(bLaunchBrowser, gridBagConstraints);

        mailprefid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        mailprefid.setColName("mailprefid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        MiscInfo.add(mailprefid, gridBagConstraints);

        jLabel1.setText("Mail Pref ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        MiscInfo.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(MiscInfo, gridBagConstraints);

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

        lLast.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lLast, gridBagConstraints);

        salutation.setColName("salutation");
        salutation.setPreferredSize(new java.awt.Dimension(30, 19));
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
        middlename.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast.add(middlename, gridBagConstraints);

        lastname.setColName("lastname");
        lastname.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(lastname, gridBagConstraints);

        jButton1.setText("New");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        FirstMiddleLast.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(FirstMiddleLast, gridBagConstraints);

        vPayerID.setColName("payerid"); // NOI18N
        vPayerID.setPreferredSize(new java.awt.Dimension(200, 19));

        jLabel4.setText("Payer:"); // NOI18N

        jLabel20.setText("Parent:"); // NOI18N

        vParent1ID.setColName("parent1id"); // NOI18N
        vParent1ID.setPreferredSize(new java.awt.Dimension(200, 19));

        jButton3.setText("New");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText("New");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout PayerParentLayout = new org.jdesktop.layout.GroupLayout(PayerParent);
        PayerParent.setLayout(PayerParentLayout);
        PayerParentLayout.setHorizontalGroup(
            PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PayerParentLayout.createSequentialGroup()
                .addContainerGap()
                .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jLabel20))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vPayerID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(vParent1ID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton2)
                    .add(jButton3)))
        );
        PayerParentLayout.setVerticalGroup(
            PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PayerParentLayout.createSequentialGroup()
                .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2)
                    .add(jLabel4)
                    .add(vPayerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton3)
                    .add(PayerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(vParent1ID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel20))))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(PayerParent, gridBagConstraints);

        Gender.setPreferredSize(new java.awt.Dimension(85, 50));
        Gender.setLayout(new java.awt.GridBagLayout());

        maleButton.setText("Male");
        maleButton.setMargin(null);
        maleButton.setPreferredSize(new java.awt.Dimension(54, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(maleButton, gridBagConstraints);

        femaleButton.setText("Female");
        femaleButton.setMargin(null);
        femaleButton.setPreferredSize(new java.awt.Dimension(69, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(femaleButton, gridBagConstraints);

        unknownGenderButton.setText("Unknown");
        unknownGenderButton.setMargin(null);
        unknownGenderButton.setPreferredSize(new java.awt.Dimension(85, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(unknownGenderButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(Gender, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(mainPanel, gridBagConstraints);

        lPhoneNumbers1.setText("Phone Numbers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(lPhoneNumbers1, gridBagConstraints);

        ocdiscPanel.setPreferredSize(new java.awt.Dimension(453, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(ocdiscPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

	private void bLaunchEmailActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchEmailActionPerformed
	{//GEN-HEADEREND:event_bLaunchEmailActionPerformed
		citibob.gui.BareBonesMailto.mailto((String)email1.getValue());
	}//GEN-LAST:event_bLaunchEmailActionPerformed

	private void bLaunchBrowserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchBrowserActionPerformed
	{//GEN-HEADEREND:event_bLaunchBrowserActionPerformed
		citibob.gui.BareBonesBrowserLaunch.openURL((String)url.getValue());
	}//GEN-LAST:event_bLaunchBrowserActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
                
                app.guiRun().run(FDPersonPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(FDPersonPanel.this);
			Wizard wizard = new offstage.frontdesk.wizards.NewPersonWizard(app, root);
			wizard.runWizard();
			Integer eid = (Integer)wizard.getVal("entityid");
			if (eid != null) {
//				str.execSql(SchoolDB.createStudentSql(eid));
//				changeStudent(str, eid);
			}
		}});
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
	// TODO add your handling code here:
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
	//app.guiRun().run(FDPersonPanel.this, new SqlTask() {
	//	public void run(SqlRun str) throws Exception {
	//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(FDPersonPanel.this);
	//		Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(app, root);
	//		wizard.runWizard();
	//		Integer eid = (Integer)wizard.getVal("entityid");
	//		if (eid != null) {
	//			smod.studentRm.set("vPayerID", eid);
	////			doUpdateSelect(str);		// Causes problem if record is incomplete
	////			allRec.doSelect(str);
	//		}
	//	}});
}//GEN-LAST:event_jButton2ActionPerformed
	

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddrPanel;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel Gender;
    private javax.swing.JPanel MiscInfo;
    private javax.swing.JPanel PayerParent;
    private citibob.swing.typed.JTypedTextField address1;
    private citibob.swing.typed.JTypedTextField address2;
    private javax.swing.JPanel addressPanel;
    private javax.swing.JButton bLaunchBrowser;
    private javax.swing.JButton bLaunchEmail;
    private citibob.swing.typed.JTypedTextField city;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedTextField email1;
    private citibob.swing.typed.JTypedTextField entityid;
    private javax.swing.JRadioButton femaleButton;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.KeyedButtonGroup genderButtonGroup;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lPhoneNumbers;
    private javax.swing.JLabel lPhoneNumbers1;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JTypedTextField lastupdated;
    private citibob.swing.typed.JKeyedComboBox mailprefid;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton maleButton;
    private citibob.swing.typed.JTypedTextField middlename;
    private offstage.gui.GroupPanel ocdiscPanel;
    private offstage.gui.GroupPanel phonePanel;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField state;
    private javax.swing.JRadioButton unknownGenderButton;
    private citibob.swing.typed.JTypedTextField url;
    private offstage.swing.typed.EntityIDDropdown vParent1ID;
    private offstage.swing.typed.EntityIDDropdown vPayerID;
    private citibob.swing.typed.JTypedTextField zip;
    // End of variables declaration//GEN-END:variables
	// --------------------------------------------------------------

}
