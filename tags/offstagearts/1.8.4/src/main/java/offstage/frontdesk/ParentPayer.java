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
 * ParentPayer.java
 *
 * Created on May 28, 2008, 9:21 PM
 */

package offstage.frontdesk;

import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.SchemaBufRowModel;
import citibob.sql.SqlRun;
import citibob.swing.typed.TypedWidgetBinder;
import offstage.FrontApp;

/**
 *
 * @author  vortigern
 */
public class ParentPayer extends javax.swing.JPanel {

    FrontApp app;
    /** Creates new form ParentPayer */
    public ParentPayer() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vPayerID = new offstage.swing.typed.EntityIDDropdown();
        jLabel4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        vParent1ID = new offstage.swing.typed.EntityIDDropdown();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jLabel20))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vParent1ID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .add(vPayerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 235, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton2)
                    .add(jButton3)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2)
                    .add(jLabel4)
                    .add(vPayerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel20)
                    .add(vParent1ID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton3)))
        );
    }// </editor-fold>//GEN-END:initComponents

    	public void initRuntime(SqlRun str, FrontApp xapp)
	{
		this.app = xapp;
		
	        IntKeyedDbModel PeopleDm = new IntKeyedDbModel(app.getSchema("persons"), "entityid");
//                PeopleDm.setKey(entityid);
		SchemaBufRowModel PeopleRm = new SchemaBufRowModel(PeopleDm.getSchemaBuf());
		TypedWidgetBinder.bindRecursive(vParent1ID, PeopleRm, app.swingerMap());
                TypedWidgetBinder.bindRecursive(vPayerID, PeopleRm, app.swingerMap());
                
	}
        
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
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private offstage.swing.typed.EntityIDDropdown vParent1ID;
    private offstage.swing.typed.EntityIDDropdown vPayerID;
    // End of variables declaration//GEN-END:variables

}