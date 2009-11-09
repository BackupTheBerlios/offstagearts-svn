/*
 * RelBrowser.java
 *
 * Created on March 25, 2009, 8:44 PM
 */

package offstage.gui;

import citibob.config.ConfigMaker;
import citibob.config.DialogConfigMaker;
import citibob.jschema.SchemaBuf;
import citibob.sql.DbKeyedModel;
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
import citibob.swing.table.StyledTM.ButtonListener;
import citibob.swingers.DefaultRenderEdit;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class RelBrowser extends javax.swing.JPanel {

RelDbModel relDb;
DbKeyedModel relidsKm;
RelEditDialog edit;
Integer defaultTemporalID;

public RelDbModel getDbModel() { return relDb; }

/** Creates new form RelBrowser */
public RelBrowser() {
	initComponents();
	rels.setHighlightMouseover(true);
}


static class MyButton extends JButton {
	MyButton(String text) { super(text); }
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
 * @param relIdSql  If null, only do "forever" relations
 * @param temporalIdSql Can be null; if null, do all relids.
 * @param defaultTemporalID Unused...
 */
public void initRuntime(SqlRun str, final FrontApp app, String relIdSql,
String temporalIdSql, Integer defaultTemporalID)
{
	edit = new RelEditDialog(WidgetTree.getJFrame(this));
	edit.initRuntime(app);

	// Set up list of relationships we can access
	if (relIdSql == null) relIdSql = "select relid as id from relids";
	String sql =
		" select relid,name,0\n" +
		" from (" + relIdSql + ") xx, relids\n" +
		" where xx.id = relids.relid";
	relidsKm = new DbKeyedModel(str, null, null, sql, "<No Relationship>");
	edit.relids.setKeyedModel(relidsKm);
	//edit.lRel.setJType(relidsKm, (String)relidsKm.getNullValue());
	// TODO: Change KeyedSFormat to get the null value out of the KeyedModel
	// Rationalize null value handling between KeyedModel, DbKeyedModel, etc.

	relDb = new RelDbModel(str, app) {
	public void setKey(Object... keys) {
	}};
	relDb.setRelIdSql(relIdSql);
	relDb.setTemporalIdSql(temporalIdSql);

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
			new String[] {"Person1", "relation", "Person2", "Edit", "Del"},
			new String[] {"name0", "relname", "name1", "Edit", "Del"});
		stm.setEditable(false, false, false, false, false);
		RenderEditCols re = stm.setRenderEditCols(app.swingerMap());
System.out.println("Edit col = " + stm.getModel().findColumnU("Edit"));
		final ComponentRenderer editRend = new ComponentRenderer(new MyButton("Edit"));
		re.setFormatU("Edit",
			new DefaultRenderEdit(editRend, null));
		re.setFormatU("Del",
			new DefaultRenderEdit(new ComponentRenderer(new MyButton("Del")), null));

		DataCols<ButtonListener> listenerCols = new DataCols(ButtonListener.class, model.getColumnCount());
		listenerCols.setColumn(model.findColumnU("Edit"),
			new ButtonListener() {
			public void onClicked(int row, int col, int modifiers) {
				System.out.println("******* EDIT " + row);
			}}
		);
		listenerCols.setColumn(model.findColumnU("Del"),
			new ButtonListener() {
			public void onClicked(int row, int col, int modifiers) {
				System.out.println("******* DELETE " + row);
			}}
		);
		stm.setButtonListenerModel(listenerCols);
		
		rels.setStyledTM(stm);

//		// RSSchema schema = (RSSchema)relDb.getSchemaBuf().getSchema();
//		rels.setModelU(app.swingerMap(), multi,
//			new String[] {"Person1", "relation", "Person2", "Edit", "Delete"},
//			new String[] {"name0", "relname", "name1", "Edit", "Delete"});
//		rels.setEditable(false, false, false, false, false);

		JTypeTableModel jtm = rels.getCBModel();
		final int editCol = jtm.findColumnU("Edit");
		final int deleteCol = jtm.findColumn("Delete");
		
//		rels.addMouseListener(
//		new MouseAdapter() {
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				// Figure out the row and column we clicked on
//				Point point = e.getPoint();
//				int row = rels.rowAtPoint(point);
//				int col = rels.columnAtPoint(point);
//
//				editRend.selectedRow = row;
//				model.fireTableCellUpdated(row, col);
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				Point point = e.getPoint();
//				int row = rels.rowAtPoint(point);
//				int col = rels.columnAtPoint(point);
//
//				editRend.selectedRow = -1;
//				model.fireTableCellUpdated(row, col);
//			}
//
//			public void mouseClicked(MouseEvent e) {
//				// Figure out the row and column we clicked on
//				Point point = e.getPoint();
//				int col = rels.columnAtPoint(point);
//				int row = rels.rowAtPoint(point);
//
//				System.out.println("Clicked on row=" + row + ", col=" + col);
//				if (col == editCol) {
//					System.out.println("EDIT");
//				} else if (col == deleteCol) {
//					System.out.println("DELETE");
//				}
//			}
//		});

//
//		rels.getSelectionModel().addListSelectionListener(
//		new ListSelectionListener() {
//		public void valueChanged(ListSelectionEvent e) {
//			editRow(str, e.getFirstIndex());
//		}});
	}});
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
	edit.setVisible(true);

	switch(edit.action) {
		case RelEditDialog.ACTION_CANCEL : {

		}
		case RelEditDialog.ACTION_OK : {
			// TODO: Insert or update
			// Use the appropriate stored procedures!
		} break;
		case RelEditDialog.ACTION_DELETE : {
			String sql =
				" delete from rels" +
				" where temporalid = " + temporalid +
				" and entityid0 = " + eid0 +
				" and entityid1 = " + eid1 +
				" and relid = " + relid;
		} break;
	}
}


public void setRel_o2m(SqlRun str, String srelid, String stemporalid,
int entityid0, int entityid1)
{
	str.execSql(
		" select w_rels_o2m_set(" +
		srelid + ", " + stemporalid + ", " + entityid0 + ", " + entityid1 + ");");
	relDb.doSelect(str);
}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        rels = new citibob.swing.StyledTable();
        jPanel2 = new javax.swing.JPanel();
        relids = new citibob.swing.typed.JKeyedComboBox();
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

        relids.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        relids.setColName("rbplan"); // NOI18N
        relids.setPreferredSize(new java.awt.Dimension(120, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(relids, gridBagConstraints);

        bAddRel.setText("+");
        bAddRel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddRelActionPerformed(evt);
            }
        });
        jPanel2.add(bAddRel, new java.awt.GridBagConstraints());

        add(jPanel2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void bAddRelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddRelActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_bAddRelActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JButton bAddRel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private citibob.swing.typed.JKeyedComboBox relids;
    private citibob.swing.StyledTable rels;
    // End of variables declaration//GEN-END:variables

	
// =================================================================
public static void main(String[] args) throws Exception
{

	ConfigMaker cmaker = new DialogConfigMaker("offstage/demo");
	final FrontApp app = new FrontApp(cmaker);
	app.checkResources();
	app.initWithDatabase(null);

	// Construct GUI widgets
	JFrame frame = new JFrame();
	RelBrowser rb = new RelBrowser();
	frame.getContentPane().add(rb);

	// Initialize GUI widgets
	rb.initRuntime(app.sqlRun(), app, null, null, 0);
	app.sqlRun().flush();

	// Get data into our model
	RelDbModel dm = rb.getDbModel();
	dm.setEntityID(12633);
	dm.doSelect(app.sqlRun());
	app.sqlRun().flush();

	// Display it all
	frame.pack();
	frame.setVisible(true);
}
	
}
