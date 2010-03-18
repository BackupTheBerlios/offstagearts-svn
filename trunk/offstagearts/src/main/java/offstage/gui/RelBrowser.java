/*
 * RelBrowser.java
 *
 * Created on March 25, 2009, 8:44 PM
 */

package offstage.gui;

import citibob.app.App;
import citibob.jschema.SchemaBuf;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.swing.WidgetTree;
import citibob.swing.table.ColPermuteTableModel;
import citibob.swing.table.DataCols;
import citibob.swing.table.DelegateStyledTM;
import citibob.swing.table.FixedColTableModel;
import citibob.swing.table.JTypeTableModel;
import citibob.swing.table.MultiJTypeTableModel;
import citibob.swing.table.RenderEditCols;
import citibob.swing.table.StyledTM.ButtonAdapter;
import citibob.swing.table.StyledTM.ButtonListener;
import citibob.swingers.DefaultRenderEdit;
import citibob.task.SqlTask;
import citibob.types.JType;
import citibob.types.JavaJType;
import citibob.util.ObjectUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class RelBrowser extends javax.swing.JPanel {

App app;
RelDbModel relDb;
RelEditDialog edit;
Integer selectedOtherEntityID;			// Selected row
//PropertyChangeSupport selectedOtherPCS = new PropertyChangeSupport( this );


public RelDbModel getDbModel() { return relDb; }

/** Creates new form RelBrowser */
public RelBrowser() {
	initComponents();
	rels.setHighlightMouseover(true);
//	rels.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	rels.setRowSelectionAllowed(false);
}


static class MyButton extends JButton {
	MyButton(String text) {
		super(text);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
	}
	public void setForeground(Color c) {}
	public void setBackground(Color c) {}
}

static class ComponentRenderer implements TableCellRenderer
{
	public int selectedRow = -1;

	JButton component;
	ComponentRenderer(JButton component) {
		this.component = component;
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus,
		int row, int column)
	{
//		component.setSelected(isSelected && hasFocus);
		boolean selected = (row == selectedRow);
//		System.out.println("row = " + row + ", selectedRow = " + selectedRow);
		component.setSelected(selected);
		return component;
	}
}


/**
 *
 * @param str
 * @param app
 * @param relIdSql  If null, do all relations
 * @param temporalIdSql Can be null; if null, do all relids.
 * @param temporalID Restrict consideration to relationships of this temporal nature (-1 == all time)
 */
public void initRuntime(final SqlRun str, final FrontApp app, RelDbModel _relDb)
{
	this.app = app;
	this.relDb = _relDb;

	edit = new RelEditDialog(WidgetTree.getJFrame(this));
	edit.initRuntime(app);

	// Set up list of relationships we can access
	edit.relids.setKeyedModel(relDb.getRelIdKm());
	//edit.lRel.setJType(relidsKm, (String)relidsKm.getNullValue());
	// TODO: Change KeyedSFormat to get the null value out of the KeyedModel
	// Rationalize null value handling between KeyedModel, DbKeyedModel, etc.

	str.execUpdate(new UpdTasklet2() {		// Set up table AFTER enrolledDb has been initialized
	public void run(final SqlRun str) {
		// Make a simple table model to for "button" columns
		final FixedColTableModel buttonCols = new FixedColTableModel(
		new String[] {"Edit", "Del"},
		new JType[] {JavaJType.jtString, JavaJType.jtString},
		new boolean[] {false, false}) {
			public int getRowCount() {
				return relDb.getTableModel().getRowCount();
			}
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch(columnIndex) {
					case 0 : return "<Edit>";
					case 1 : return "<Delete>";
				}
				return null;
			}
		};

		final MultiJTypeTableModel multi = new MultiJTypeTableModel(relDb.getTableModel(), buttonCols);

		// Now make a StyledTM from our multi model
		DelegateStyledTM stm = new DelegateStyledTM(multi);
		final ColPermuteTableModel model = stm.setColumns(app.swingerMap(),
			new String[] {"Person1", "relation", "Person2", "**"},
			new String[] {"name0", "relname", "name1", "Edit"});
//		stm.setTooltips("name0", "relname", "name1", null);
		stm.setEditable(false, false, false, false, false);
		RenderEditCols re = stm.setRenderEditCols(app.swingerMap());
		final ComponentRenderer editRend = new ComponentRenderer(new MyButton("Edit"));
		re.setFormatU("Edit", new DefaultRenderEdit(editRend, null));

		DataCols<ButtonListener> listenerCols = new DataCols(ButtonListener.class, model.getColumnCount());

		final int eid0Col = relDb.getTableModel().findColumnU("entityid0");
		final int eid1Col = relDb.getTableModel().findColumnU("entityid1");
		ButtonListener chooseListener = new ButtonAdapter() {
			public boolean onClicked(int row, int col, MouseEvent me) {
				if (row < 0 || row >= model.getRowCount()) return true;

				JTypeTableModel model = relDb.getTableModel();
				Integer eid0 = (Integer)model.getValueAt(row, eid0Col);
				Integer eid1 = (Integer)model.getValueAt(row, eid1Col);

				Integer old = selectedOtherEntityID;
				selectedOtherEntityID = eid0;
				if (ObjectUtil.eq(eid0, relDb.getEntityID()))
					selectedOtherEntityID = eid1;

				// Avoid spurious repeat events
				if (ObjectUtil.eq(old, selectedOtherEntityID)) return true;

	System.out.println("RelBrowser setting value from " + old + " to " + selectedOtherEntityID);
				rels.getSelectionModel().clearSelection();
				firePropertyChange("value",
					old, selectedOtherEntityID);
				return true;
			}
		};
		for (int i=0; i<listenerCols.getColumnCount(); ++i)
			listenerCols.setColumn(i, chooseListener);

		listenerCols.setColumn(model.findColumnU("Edit"),
			new ButtonAdapter() {
//			public boolean onPressed(int row, int col, MouseEvent me)
//				{ return true; }
//			public boolean onReleased(int row, int col, MouseEvent me)
//				{ return true; }
			public boolean onPressed(int row, int col, MouseEvent me) {
				if (me.getButton() != MouseEvent.BUTTON1) return false;
				System.out.println("******* EDIT " + row);
				editRow(str, row);
				str.flush();
				return true;
			}}
		);


		stm.setButtonListenerModel(listenerCols);
		rels.setStyledTM(stm);

		JTypeTableModel jtm = rels.getCBModel();
		final int editCol = jtm.findColumnU("Edit");
		final int deleteCol = jtm.findColumn("Delete");


//		// Add selection stuff
//		final int eid0Col = relDb.getTableModel().findColumnU("entityid0");
//		final int eid1Col = relDb.getTableModel().findColumnU("entityid1");
//		rels.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//		public void valueChanged(ListSelectionEvent e) {
//			int selectedRow = e.getFirstIndex();
//			if (selectedRow < 0 || selectedRow >= model.getRowCount()) return;
//
//			JTypeTableModel model = relDb.getTableModel();
//			Integer eid0 = (Integer)model.getValueAt(selectedRow, eid0Col);
//			Integer eid1 = (Integer)model.getValueAt(selectedRow, eid1Col);
//
//			Integer old = selectedOtherEntityID;
//			selectedOtherEntityID = eid0;
//			if (ObjectUtil.eq(eid0, relDb.getEntityID()))
//				selectedOtherEntityID = eid1;
//
//			// Avoid spurious repeat events
//			if (ObjectUtil.eq(old, selectedOtherEntityID)) return;
//
//System.out.println("RelBrowser setting value from " + old + " to " + selectedOtherEntityID);
//			rels.getSelectionModel().clearSelection();
//			firePropertyChange("value",
//				old, selectedOtherEntityID);
//		}});
	}});
}

void deleteRel(SqlRun str, int temporalid, int eid0, int eid1, int relid)
{
	String sql =
		" delete from rels" +
		" where temporalid = " + temporalid +
		" and entityid0 = " + eid0 +
		" and entityid1 = " + eid1 +
		" and relid = " + relid;
	str.execSql(sql);
}

void editRow(SqlRun str, int row)
{
System.out.println("RelBrowser.editRow()");
	SchemaBuf sbuf = relDb.getSchemaBuf();
	Integer thisID = relDb.getEntityID();

	Integer eid0 = (Integer)sbuf.getValueAt(row, "entityid0");
	Integer eid1 = (Integer)sbuf.getValueAt(row, "entityid1");
	Integer relid = (Integer)sbuf.getValueAt(row, "relid");
	Integer temporalid = (Integer)sbuf.getValueAt(row, "temporalid");
//System.out.println("RelBrowser: eid0 = " + eid0 + ", eid1 = " + eid1 + ", thisID = " + thisID);
//System.out.println("eq0 = " + ObjectUtil.eq(eid0, thisID));
//System.out.println("eq1 = " + ObjectUtil.eq(eid1, thisID));
	edit.setEditMode(edit.MODE_EDIT, thisID,
		eid0, relid, eid1);
	edit.setLocationRelativeTo(RelBrowser.this);
	edit.setVisible(true);

	switch(edit.action) {
		case RelEditDialog.ACTION_CANCEL : {
		}
		case RelEditDialog.ACTION_OK : {
			// TODO: Insert or update
			// Use the appropriate stored procedures!
			deleteRel(str, temporalid, eid0, eid1, relid);
			setRel(str, relid, temporalid,
				edit.getEntityID0(), edit.getEntityID1());
			refresh(str);
		} break;
		case RelEditDialog.ACTION_DELETE : {
			deleteRel(str, temporalid, eid0, eid1, relid);
			refresh(str);
		} break;
	}
}


/**
 * @param str
 * @param srelid relid, as a String
 * @param stemporalid temporalid, as a String
 * @param entityid0
 * @param entityid1
 */
private void setRel(SqlRun str, int relid, int temporalid,
int entityid0, int entityid1)
{
	str.execSql(
		" select w_rels_set(" +
		relid + ", " + temporalid + ", " + entityid0 + ", " + entityid1 + ");");
	refresh(str);
}

void refresh(SqlRun str)
{
	relDb.doSelect(str);
}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        rels = new citibob.swing.StyledTable();
        jPanel2 = new javax.swing.JPanel();
        bAddRel = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        rels.setModel(new javax.swing.table.DefaultTableModel(
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
        GroupScrollPanel.setViewportView(rels);

        jPanel1.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        bAddRel.setText("Add Relationship");
        bAddRel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddRelActionPerformed(evt);
            }
        });
        jPanel2.add(bAddRel, new java.awt.GridBagConstraints());

        add(jPanel2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void bAddRelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddRelActionPerformed
		SqlRun str = app.sqlRun();
		SchemaBuf sbuf = relDb.getSchemaBuf();
		Integer thisID = relDb.getEntityID();

//		final Integer temporalid = temporalID;
	//System.out.println("RelBrowser: eid0 = " + eid0 + ", eid1 = " + eid1 + ", thisID = " + thisID);
	//System.out.println("eq0 = " + ObjectUtil.eq(eid0, thisID));
	//System.out.println("eq1 = " + ObjectUtil.eq(eid1, thisID));
		edit.setEditMode(edit.MODE_NEW, thisID,
			thisID, null, thisID);
		edit.setVisible(true);

	app.guiRun().run(new SqlTask() {
	public void run(SqlRun str) throws Exception {
		switch(edit.action) {
			case RelEditDialog.ACTION_CANCEL : {
			} break;
			case RelEditDialog.ACTION_OK : {
				setRel(str, edit.getRelID(), relDb.getTemporalID(),
					edit.getEntityID0(), edit.getEntityID1());
				refresh(str);
			} break;
//				case RelEditDialog.ACTION_DELETE : {
//
//					deleteRel(str, temporalid, eid0, eid1, relid);
//					refresh(str);
//				} break;
		}
	}});
		// TODO add your handling code here:
}//GEN-LAST:event_bAddRelActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JButton bAddRel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private citibob.swing.StyledTable rels;
    // End of variables declaration//GEN-END:variables

	
// =================================================================
//public static void main(String[] args) throws Exception
//{
////	System.setProperty("swing.metalTheme", "ocean");
//	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//
//	ConfigMaker cmaker = new DialogConfigMaker("offstage/demo");
//	final FrontApp app = new FrontApp(cmaker);
//	app.checkResources();
//	app.initWithDatabase(null);
//
//	// Construct GUI widgets
//	JFrame frame = new JFrame();
//	RelBrowser rb = new RelBrowser();
//	frame.getContentPane().add(rb);
//
//	// Initialize GUI widgets
////	rb.initRuntime(app.sqlRun(), app, null, null, 0);
//	rb.initRuntime(app.sqlRun(), app, relDb);
//	app.sqlRun().flush();
//
//	// Get data into our model
//	RelDbModel dm = rb.getDbModel();
//	dm.setEntityID(12633);
//	dm.doSelect(app.sqlRun());
//	app.sqlRun().flush();
//
//	// Display it all
//	frame.pack();
//	frame.setVisible(true);
//}
	
}
