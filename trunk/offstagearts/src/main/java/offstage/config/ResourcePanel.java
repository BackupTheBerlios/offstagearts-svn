/*
 * ResourcePanel.java
 *
 * Created on February 24, 2008, 10:27 AM
 */

package offstage.config;

import citibob.resource.ResModels;
import citibob.app.App;
import citibob.gui.GuiUtil;
import citibob.resource.ResData;
import citibob.resource.RtResKey;
import citibob.resource.ResSet;
import citibob.resource.ResUtil;
import citibob.resource.Resource;
import citibob.resource.RtRes;
import citibob.resource.RtVers;
import citibob.resource.UpgradePlan;
import citibob.sql.ConnPool;
import citibob.sql.SqlRunner;
import citibob.sql.UpdRunnable;
import citibob.task.BatchRunnable;
import citibob.types.KeyedModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.Integer;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 
 @author  citibob
 */
public class ResourcePanel extends javax.swing.JPanel
{

App app;

Resource curResource;
RtResKey curResKey;
//Integer curVersion;
UpgradePlan curUPlan;

ResData rdata;
ResModels rmods;
boolean inUpdate;

	/** Creates new form ResourcePanel */
	public ResourcePanel()
	{
		initComponents();
	}
	
	public void initRuntime(SqlRunner str, App xapp)
	{
		this.app = xapp;
		ResSet rset = app.getResSet();
		rdata = new ResData(str, rset, app.getSqlTypeSet());
		rmods = new ResModels(rdata, app, app.getSysVersion());

		// Set up tables
		tResources.setModelU(rmods.resMod,
			new String[] {"Resource", "Required Version"},
			new String[] {"name", "reqversion"},
			null, app.getSwingerMap());
//		tResources.setValueColU("RtRes");

		this.tResKeys.setModelU(rmods.rkMod,
			new String[] {"Name"},
			new String[] {"name"},
			null, app.getSwingerMap());
//		tResKeys.setValueColU("ResKey");
		
		tAvailVersions.setModelU(rmods.availMod,
			new String[] {"Version", "Size", "Last Modified"},
			new String[] {"version", "size", "lastmodified"},
			null, app.getSwingerMap());
		tAvailVersions.setRenderEditU("lastmodified", "MM/dd/yyyy");

		this.tUpgradePlans.setModelU(rmods.uplanMod,
			new String[] {"Source", "Plan", "Back-Compatible"},
			new String[] {"uversionName0", "UpgradePlan", "backcompatible"},
			null, app.getSwingerMap());
		tUpgradePlans.setRenderEditU("UpgradePlan", new ResModels.PathSFormat());
		
		
		// Select a Resource
		tResources.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			setCurResource((RtRes)evt.getNewValue());
		}});
		
		// Select a UVersionID
		tResKeys.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			setCurResKey((RtResKey)evt.getNewValue());
		}});

//		// Select a version
//		tAvailVersions.addPropertyChangeListener("value", new PropertyChangeListener() {
//		public void propertyChange(PropertyChangeEvent evt) {
//			Object val = tAvailVersions.getValue();
//			if (evt.getNewValue() == null) return;
////			setCurVersion((Integer)rmods.availMod.getValueAt(irow, "version"));
//		}});

		// Select an upgrade plan to inspect and maybe execute
		tUpgradePlans.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Object val = evt.getNewValue();
			if (val == null) upgradeSteps.setText("");
			else {
				UpgradePlan uplan = (UpgradePlan)val;
				upgradeSteps.setText(uplan.getDescription());
			}
//			setCurVersion((Integer)rmods.availMod.getValueAt(irow, "version"));
		}});

		//		// Select an Upgrade Plan
//		tUpgradePlans.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//		public void valueChanged(ListSelectionEvent e) {
//			int irow = tUpgradePlans.getSelectedRow();
//			if (irow < 0) return;
//			// setCurUpgradePlan((UpgradePlan)rmods.availMod.getValueAt(irow, "UpgradePlan"));
//		}});

		version1.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (!inUpdate) refreshPaths();
		}});
		srckeys.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (!inUpdate) refreshPaths();
		}});
	};
	
	
	void setCurResource(RtRes rr)
	{
		inUpdate = true;
		if (rr == null) {
			curResource = null;
			KeyedModel kmodel = new KeyedModel();	// for dropdown
			rmods.rkMod.clear();
			srckeys.setKeyedModel(kmodel);
			version1.setKeyedModel(kmodel);
		} else {
			curResource = rr.res;
			rmods.rkMod.setData(rr);
			
			// Set up dropdown
			KeyedModel kmodel = new KeyedModel();	// for dropdown
			for (RtResKey rk : rr.relevant) {
				kmodel.addItem(rk, rk.uversionName);
			}	
			srckeys.setKeyedModel(kmodel);
			
			// Set up the other dropdown for the total versions
			kmodel = new KeyedModel();
			for (Integer v : rr.res.getAllPossibleVersions()) {
				if (v.intValue() > 0) kmodel.addItem(v,v);
			}
			version1.setKeyedModel(kmodel);
			version1.setValue(rr.res.getRequiredVersion(app.getSysVersion()));

			tResKeys.setValue(rr.relevant.get(0));

		}
		
		
		
//		setCurResKey(keys.get(0));	// should be the <Base> uversionid
		
		inUpdate = false;
	}

	void setCurResKey(RtResKey rk)
	{
		curResKey = rk;
		srckeys.setValue(rk);
		destKey.setText(rk.uversionName);
		refreshVersions();
//		if (rk == null || rk.availVersions == null) rmods.availMod.clear();
//		else rmods.availMod.setData(rk.availVersions);
		refreshPaths();
//		setCurVersion(null);
	}

	void refreshVersions()
	{
		RtResKey rk = curResKey;
		rmods.availMod.setData(rk);
		int nrow = rmods.availMod.getRowCount();
		if (nrow > 0) tAvailVersions.getSelectionModel().
			setSelectionInterval(nrow-1,nrow-1);
	}
	
	void refreshPaths()
	{
		ResSet rset = app.getResSet();

		int ver1 = (Integer)version1.getValue();
		RtResKey srcRk = (RtResKey)srckeys.getValue();
		RtResKey destRk = curResKey;
	
		
		ArrayList<UpgradePlan> plans = new ArrayList();

		// Get creator paths
		UpgradePlan uplan = curResKey.getCreatorPlan(ver1);
		if (uplan != null) plans.add(uplan);

		// Get upgrade paths
		for (RtVers ver0 : srcRk.availVersions) {
			if (ver0.version == ver1 && srcRk.equals(destRk)) continue;	// Skip trivial nop plans
			uplan = srcRk.getUpgradePlan(ver0.version, ver1);
			if (uplan != null) plans.add(uplan);
		}

		// Display...
		rmods.uplanMod.setData(plans);

//		if (version == null) rmods.uplanMod.clear();
//		else {
//			ArrayList<UpgradePlan> plans = new ArrayList();
//
//			// Get creator paths
//			UpgradePlan uplan = curResKey.getCreatorPlan(version);
//			if (uplan != null) plans.add(uplan);
//
//			// Get upgrade paths
//			for (int ver0 : curResKey.availVersions) {
//				if (ver0 == version) continue;	// Skip trivial nop plans
//				uplan = curResKey.getUpgradePlan(ver0, version);
//				if (uplan != null) plans.add(uplan);
//			}
////		setCurUPlan(null);
	}
	
	void setCurUPlan(UpgradePlan uplan)
	{
		this.curUPlan = uplan;
	}
	
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
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tResources = new citibob.swing.typed.JTypedSelectTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tResKeys = new citibob.swing.typed.JTypedSelectTable();
        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tUpgradePlans = new citibob.swing.typed.JTypedSelectTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        srckeys = new citibob.swing.typed.JKeyedComboBox();
        version1 = new citibob.swing.typed.JKeyedComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        destKey = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        bUpgrade = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        upgradeSteps = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tAvailVersions = new citibob.swing.typed.JTypedSelectTable();
        jPanel7 = new javax.swing.JPanel();
        bEdit = new javax.swing.JButton();
        bExport = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bImport = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel3.setText("Resources");
        jPanel4.add(jLabel3, java.awt.BorderLayout.NORTH);

        tResources.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tResources);

        jPanel4.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jSplitPane2.setTopComponent(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel4.setText("Terms/Seasons");
        jPanel5.add(jLabel4, java.awt.BorderLayout.NORTH);

        tResKeys.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tResKeys);

        jPanel5.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jSplitPane2.setBottomComponent(jPanel5);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel9.setLayout(new java.awt.BorderLayout());

        tUpgradePlans.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tUpgradePlans);

        jPanel9.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel2.setText("Upgrade Plans (Summary)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(jLabel2, gridBagConstraints);

        srckeys.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(srckeys, gridBagConstraints);

        version1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(version1, gridBagConstraints);

        jLabel5.setText("Destination Term/Season: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel6.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Destination Version: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel6.add(jLabel6, gridBagConstraints);

        destKey.setText("<Term>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(destKey, gridBagConstraints);

        jLabel8.setText("Source Term/Season: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel6.add(jLabel8, gridBagConstraints);

        jPanel9.add(jPanel6, java.awt.BorderLayout.NORTH);

        jSplitPane4.setTopComponent(jPanel9);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        bUpgrade.setText("Upgrade");
        bUpgrade.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUpgradeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(bUpgrade, gridBagConstraints);

        jPanel3.add(jPanel8, java.awt.BorderLayout.PAGE_END);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel7.setText("Upgrade Steps (Details)");
        jPanel3.add(jLabel7, java.awt.BorderLayout.NORTH);

        upgradeSteps.setContentType("text/html");
        jScrollPane5.setViewportView(upgradeSteps);

        jPanel3.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        jSplitPane4.setBottomComponent(jPanel3);

        jSplitPane3.setBottomComponent(jSplitPane4);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel1.setText("Available Versions");
        jPanel2.add(jLabel1, java.awt.BorderLayout.NORTH);

        tAvailVersions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tAvailVersions);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        bEdit.setText("Edit");
        bEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(bEdit, gridBagConstraints);

        bExport.setText("Export");
        bExport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(bExport, gridBagConstraints);

        bDelete.setText("Delete");
        bDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(bDelete, gridBagConstraints);

        bImport.setText("Import");
        bImport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel7.add(bImport, gridBagConstraints);

        jPanel2.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jSplitPane3.setTopComponent(jPanel2);

        jSplitPane1.setRightComponent(jSplitPane3);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	void refreshVersionPaths(SqlRunner str)
	{
//		rmods.refreshVersions(str);
		rdata.readData(str);
		str.execUpdate(new UpdRunnable() {
		public void run(SqlRunner str) {
//				setCurResKey(curResKey);
			refreshVersions();
			refreshPaths();
		}});
	}
	private void bUpgradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUpgradeActionPerformed
		app.runGui(ResourcePanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			curResource.applyPlan(str, app.getPool(), (UpgradePlan)tUpgradePlans.getValue());
			str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) {
				refreshVersionPaths(str);
			}});
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_bUpgradeActionPerformed

	private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
		app.runGui(ResourcePanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			RtVers ver = (RtVers)tAvailVersions.getValue();
			if (ver == null) return;
			str.execSql(ResUtil.delResourceSql(
				curResKey.res.getName(), curResKey.uversionid, ver.version));
			refreshVersionPaths(str);
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_bDeleteActionPerformed

	private void bEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEditActionPerformed
	{//GEN-HEADEREND:event_bEditActionPerformed
		app.runGui(ResourcePanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			final RtVers ver = (RtVers)tAvailVersions.getValue();
			ConnPool pool = app.getPool();
			ResUtil.editResource(str, pool, ResourcePanel.this, curResKey, ver);
			str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) {
				refreshVersionPaths(str);
			}});
		}});
	}//GEN-LAST:event_bEditActionPerformed

	private void bExportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bExportActionPerformed
	{//GEN-HEADEREND:event_bExportActionPerformed
		app.runGui(ResourcePanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			// Get the file to export to
			String suffix = curResKey.res.getSuffix();
			JFileChooser chooser = GuiUtil.newChooser(suffix);
			chooser.setDialogTitle("Export Resource");
			File file = GuiUtil.chooseSaveFileCheckOverwrite(ResourcePanel.this,
				chooser, suffix,
				app.userRoot(), "resourceExportDir");
			if (file == null) return;

			// Save it
			final RtVers ver = (RtVers)tAvailVersions.getValue();
			ResUtil.saveResource(str, curResKey, ver.version, file);
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_bExportActionPerformed

	private void bImportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bImportActionPerformed
	{//GEN-HEADEREND:event_bImportActionPerformed
		app.runGui(ResourcePanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			// Get the file to export to
			String suffix = curResKey.res.getSuffix();
			JFileChooser chooser = GuiUtil.newChooser(suffix);
			chooser.setDialogTitle("Import Resource");
			chooser.showOpenDialog(ResourcePanel.this);
			File file = chooser.getSelectedFile();
			if (file == null) return;

			// Import it
			final RtVers ver = (RtVers)tAvailVersions.getValue();
			ResUtil.uploadResource(app.getPool(), curResKey, ver, file);
			str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) {
				refreshVersionPaths(str);
			}});
		}});

		
		// TODO add your handling code here:
	}//GEN-LAST:event_bImportActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bEdit;
    private javax.swing.JButton bExport;
    private javax.swing.JButton bImport;
    private javax.swing.JButton bUpgrade;
    private javax.swing.JLabel destKey;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private citibob.swing.typed.JKeyedComboBox srckeys;
    private citibob.swing.typed.JTypedSelectTable tAvailVersions;
    private citibob.swing.typed.JTypedSelectTable tResKeys;
    private citibob.swing.typed.JTypedSelectTable tResources;
    private citibob.swing.typed.JTypedSelectTable tUpgradePlans;
    private javax.swing.JTextPane upgradeSteps;
    private citibob.swing.typed.JKeyedComboBox version1;
    // End of variables declaration//GEN-END:variables
	
}
