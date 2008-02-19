/*
 * ConfigChooser.java
 *
 * Created on February 17, 2008, 10:42 PM
 */

package offstage.config;

import citibob.swing.prefs.SwingPrefs;
import citibob.swing.typed.SwingerMap;
import citibob.version.Version;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 
 @author  citibob
 */
public class ConfigChooser extends javax.swing.JDialog
{
	ConfigSetup setup;
	ConfigModel model;
	
	/** Creates new form ConfigChooserPanel */
	public ConfigChooser(Preferences prefs, SwingerMap smap, Preferences userRoot, Version version)
	{
		super((JFrame)null, true);
		initComponents();
		
		setTitle("OffstageArts " + version.toString());
		
		model = new ConfigModel(prefs);
		configs.setModelU(model,
			null, new String[] {"Name"}, null,
			smap);
		configs.setValueColU("Folder");
		configs.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			setVisible(false);
		}});

		new SwingPrefs().setPrefs(this, userRoot.node("ConfigChooser"));
		
		setup = new ConfigSetup(model, smap, userRoot, version);
	}
	
	public File getConfigFile() { return (File)configs.getValue(); }
	public String getConfigName() { return (String)configs.getValue("Name"); }
	
	/** This method is called from within the constructor to
	 initialize the form.
	 WARNING: Do NOT modify this code. The content of this method is
	 always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        configs = new citibob.swing.typed.JTypedSelectTable();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        configs.setModel(new javax.swing.table.DefaultTableModel(
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
        configs.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                configsPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(configs);

        jButton1.setText("Setup");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel1.setText("Please choose which OffstageArts");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel2.setText("you wish to use:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel2, gridBagConstraints);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton1)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void configsPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_configsPropertyChange
	{//GEN-HEADEREND:event_configsPropertyChange
		// TODO add your handling code here:
	}//GEN-LAST:event_configsPropertyChange

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
	{//GEN-HEADEREND:event_jButton1ActionPerformed
		setup.setVisible(true);
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton1ActionPerformed
	
//	/**
//	 @param args the command line arguments
//	 */
//	public static void main(String args[])
//	{
//		java.awt.EventQueue.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				ConfigChooser dialog = new ConfigChooser(new javax.swing.JFrame(), true);
//				dialog.addWindowListener(new java.awt.event.WindowAdapter()
//				{
//					public void windowClosing(java.awt.event.WindowEvent e)
//					{
//						System.exit(0);
//					}
//				});
//				dialog.setVisible(true);
//			}
//		});
//	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.swing.typed.JTypedSelectTable configs;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
//public static void main(String[] args)
//{
//	Preferences prefs = Preferences.userRoot().node("offstage").node("config");
//	ConfigChooser dialog = new ConfigChooser(prefs, new JavaSwingerMap(TimeZone.getDefault()));
//	dialog.setVisible(true);
//	System.out.println(dialog.getConfigFile());
//
//}
}
