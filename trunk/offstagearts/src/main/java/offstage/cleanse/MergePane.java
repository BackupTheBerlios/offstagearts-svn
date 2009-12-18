/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MergePane.java
 *
 * Created on Dec 17, 2009, 6:08:14 PM
 */

package offstage.cleanse;

import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import citibob.swing.WidgetTree;
import java.awt.Color;
import java.io.IOException;
import javax.swing.JOptionPane;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;
import offstage.reports.SummaryReport;

/**
 *
 * @author citibob
 */
public class MergePane extends javax.swing.JPanel {


FrontApp app;
int dbid0, dbid1;

// The two records we're comparing
MergeModel mergeDm;		// = dm[0] and dm[1]


	public MergeModel getMergeDm() { return mergeDm; }

    /** Creates new form MergePane */
    public MergePane() {
        initComponents();

		// Format for smaller screen!
		displayTabs.remove(editTab);

		Color color;
		color = new java.awt.Color(51, 204, 0);
        entityPanel0.setAllBackground(color);
		summaryPane0.setBackground(color);
//		lNewRecord.setBackground(color);		// Didn't have any effect

		color = new java.awt.Color(255, 204, 204);
        entityPanel1.setAllBackground(color);
		summaryPane1.setBackground(color);
//		lOldRecord.setBackground(color);

		summaryPane0.setContentType("text/html");
		summaryPane0.setEditable(false);
		summaryPane1.setContentType("text/html");
		summaryPane1.setEditable(false);
    }


	/** @param dupType = 'a' (address), 'n' (names), 'o' (organization) */
	public void initRuntime(SqlRun str, FrontApp fapp)
	{
		this.app = fapp;

		mergeDm = new MergeModel(fapp);
		entityPanel0.initRuntime(str, fapp, mergeDm.getDevelModel(0));
		entityPanel1.initRuntime(str, fapp, mergeDm.getDevelModel(1));
	}



	void refresh(SqlRun str)
	{
		mergeDm.doSelect(str);

		str.execUpdate(new UpdTasklet() {
		public void run() throws Exception {
			SummaryReport sr = new SummaryReport(app);

			String html0 = sr.getHtml(mergeDm.getDevelModel(0));
			summaryPane0.setText(html0);
			summaryPane0.setCaretPosition(0);

			String html1 = sr.getHtml(mergeDm.getDevelModel(1));
			summaryPane1.setText(html1);
			summaryPane1.setCaretPosition(0);
		}});

	}

// ----------------------------------------------------------
// Merge mechanics

// ------------------------------------------------------------
// -------- Actions that rely on the current value of the DevelModels


Integer mergedID;	// result of doAction
Integer getMergedID() { return mergedID; }

/** @return true if it did something, false if action was cancelled */
boolean doAction(SqlRun str, int cleanseMode, int action) throws IOException
{
	mergedID = null;
	switch(action) {
		case MergeActions.MC_DEL_0 :
		case MergeActions.MC_DEL_1 :
		case MergeActions.MC_DEL_BOTH :
			if (!deleteAction(str, cleanseMode, action)) return false;
			break;
		case MergeActions.MC_MERGE_TO_0 :
		case MergeActions.MC_MERGE_TO_1 :
			mergedID = mergeAction(str, cleanseMode, action);
			if (mergedID == null) return false;
			break;
		case MergeActions.MC_DUPOK :
			dupOKAction(str, cleanseMode);
			break;
		default : return false;
	}
	return true;
}

private void dupOKAction(SqlRun str, int cleanseMode)
{
	Integer entityid0 = (Integer)mergeDm.getEntityID(0);
	Integer entityid1 = (Integer)mergeDm.getEntityID(1);
	mergeDm.doUpdate(str);
	refresh(str);
	MergeActions.dupOKDbAction(str, cleanseMode, entityid0, entityid1);
}

private boolean deleteAction(SqlRun str, int cleanseMode, final int action)
{
	String whichRecord;
	switch(action) {
		case MergeActions.MC_DEL_1 : whichRecord = "the old (red) record"; break;
		case MergeActions.MC_DEL_0 : whichRecord = "the new (green) record"; break;
		case MergeActions.MC_DEL_BOTH : whichRecord = "both records"; break;
		default : throw new IllegalArgumentException("deleteAction() cannot do action = " + action);
	}

	if (JOptionPane.showConfirmDialog(MergePane.this,
		"Do you really wish to delete\n" +
		whichRecord + "?", "Delete Record",
		JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;

	mergeDm.doUpdate(str);
	MergeActions.deleteDbAction(str, cleanseMode, action,
		mergeDm.getEntityID(0), mergeDm.getEntityID(1));


	refresh(str);
	return true;
}

/**@param dm0 The left-hand item displayed to the user (generally the newer one).
 * @param action MERGE_TO_0 or MERGE_TO_1
 @return ID of resulting merged record (or null if nothing was done) */
private Integer mergeAction(SqlRun str, int cleanseMode, final int action) throws IOException
{
	mergeDm.doUpdate(str);

	final DevelModel dm0 = mergeDm.getDevelModel(0);
	final DevelModel dm1 = mergeDm.getDevelModel(1);


	DevelModel dmFrom, dmTo;
	if (action == MergeActions.MC_MERGE_TO_0) {
		dmFrom = dm1;
		dmTo = dm0;
	} else {
		dmFrom = dm0;
		dmTo = dm1;
	}

	// First do a trial merge...
	MergeSql.bufMerge(app.dataTabSet(), dmFrom, dmTo);
	String html;
	SummaryReport sr = new SummaryReport(app);
	html = sr.getHtml(dmTo); //, app.sFormatMap());

	MergeConfirm confirm = new MergeConfirm(WidgetTree.getJFrame(MergePane.this), app, html);
	confirm.setVisible(true);

	if (confirm.okPressed) {

		final Integer entityid0 = (Integer)dm0.getKey();
		final Integer entityid1 = (Integer)dm1.getKey();
		final Integer entityidFrom = (Integer)dmFrom.getKey();
		final Integer entityidTo = (Integer)dmTo.getKey();

		Integer mergedID = MergeActions.mergeDbAction(str, app, cleanseMode, action, entityid0, entityid1);
//		dupDm.getSchemaBuf().removeRow(dupTable.getSelectedRow());
			refresh(str);
		return mergedID;
	} else {
		// Re-read what we had before we merged in the buffers
		refresh(str);
		return null;
	}
}




	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayTabs = new javax.swing.JTabbedPane();
        editTab = new javax.swing.JPanel();
        entityPanel0 = new offstage.devel.gui.EntityPanel();
        entityPanel1 = new offstage.devel.gui.EntityPanel();
        summaryTab = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        summaryPane0 = new javax.swing.JTextPane();
        lNewRecord = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        summaryPane1 = new javax.swing.JTextPane();
        lOldRecord = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        editTab.setLayout(new javax.swing.BoxLayout(editTab, javax.swing.BoxLayout.LINE_AXIS));

        entityPanel0.setBackground(new java.awt.Color(51, 204, 0));
        editTab.add(entityPanel0);

        entityPanel1.setBackground(new java.awt.Color(255, 204, 204));
        editTab.add(entityPanel1);

        displayTabs.addTab("Edit", editTab);

        summaryTab.setLayout(new javax.swing.BoxLayout(summaryTab, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(summaryPane0);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        lNewRecord.setFont(new java.awt.Font("Lucida Grande", 0, 24));
        lNewRecord.setText("New Record");
        jPanel4.add(lNewRecord, java.awt.BorderLayout.PAGE_START);

        summaryTab.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setViewportView(summaryPane1);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        lOldRecord.setFont(new java.awt.Font("Lucida Grande", 0, 24));
        lOldRecord.setText("Old Record");
        jPanel5.add(lOldRecord, java.awt.BorderLayout.PAGE_START);

        summaryTab.add(jPanel5);

        displayTabs.addTab("Summary", summaryTab);

        add(displayTabs, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane displayTabs;
    private javax.swing.JPanel editTab;
    private offstage.devel.gui.EntityPanel entityPanel0;
    private offstage.devel.gui.EntityPanel entityPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lNewRecord;
    private javax.swing.JLabel lOldRecord;
    private javax.swing.JTextPane summaryPane0;
    private javax.swing.JTextPane summaryPane1;
    private javax.swing.JPanel summaryTab;
    // End of variables declaration//GEN-END:variables

}
