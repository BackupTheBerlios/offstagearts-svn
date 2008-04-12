/*
 * ListAndRangePanel.java
 *
 * Created on April 12, 2008, 6:26 PM
 */

package offstage.equery.swing;

import citibob.swing.typed.JTypedPanel;
import citibob.text.IntegerSFormat;
import citibob.text.SFormat;
import citibob.types.JEnumMulti;
import java.util.List;
import offstage.equery.ListAndRange;

/**
 *
 * @author  citibob
 */
public class ListAndRangeTW extends JTypedPanel
{

/** Creates new form ListAndRangePanel */
public ListAndRangeTW() {
	initComponents();
	SFormat ifmt = new IntegerSFormat("#","");
	tfMin.setJType(Integer.class, ifmt);
	tfMax.setJType(Integer.class, ifmt);
}

	
/** Returns last legal value of the widget.  Same as method in JFormattedTextField */
public Object getValue() {
	ListAndRange lar = new ListAndRange();
		lar.list = (List)multi.getValue();
		lar.min = (Integer)tfMin.getValue();
		lar.max = (Integer)tfMax.getValue();
	return lar;
}

/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public void setValue(Object o) {
	ListAndRange lar = (ListAndRange)o;
	if (lar == null) {
		multi.setValue(null);
		tfMin.setValue(null);
		tfMax.setValue(null);
	} else {
		multi.setValue(lar.list);
		tfMin.setValue(lar.min);
		tfMax.setValue(lar.max);
	}
}

/** From TableCellEditor (in case this is being used in a TableCellEditor):
 * Tells the editor to stop editing and accept any partially edited value
 * as the value of the editor. The editor returns false if editing was not
 * stopped; this is useful for editors that validate and can not accept
 * invalid entries. */
public boolean stopEditing() {
	boolean ret = true;
	ret = ret && multi.stopEditing();
	ret = ret && tfMin.stopEditing();
	ret = ret && tfMax.stopEditing();
	return ret;
}

/** Is this object an instance of the class available for this widget?
 * If so, then setValue() will work.  See SqlType.. */
public boolean isInstance(Object o)
	{return o instanceof ListAndRange; }

///** Set up widget to edit a specific SqlType.  Note that this widget does not
// have to be able to edit ALL SqlTypes... it can throw a ClassCastException
// if asked to edit a SqlType it doesn't like. */
//public void setJType(citibob.swing.typed.Swinger f) throws ClassCastException
//{ getSubWidget().setJType(f); }


/** Row (if any) in a RowModel we will bind this to at runtime. */
public String getColName() { return multi.getColName(); }

/** Row (if any) in a RowModel we will bind this to at runtime. */
public void setColName(String col) { multi.setColName(col); }

public void setKeyedModel(JEnumMulti jenum)
{
	multi.setKeyedModel(jenum);
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        multi = new citibob.swing.typed.JKeyedMulti();
        tfMin = new citibob.swing.typed.JTypedTextField();
        tfMax = new citibob.swing.typed.JTypedTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(multi, gridBagConstraints);

        tfMin.setText("jTypedTextField1");
        tfMin.setMinimumSize(new java.awt.Dimension(30, 22));
        tfMin.setPreferredSize(new java.awt.Dimension(30, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(tfMin, gridBagConstraints);

        tfMax.setText("jTypedTextField1");
        tfMax.setMinimumSize(new java.awt.Dimension(30, 22));
        tfMax.setPreferredSize(new java.awt.Dimension(30, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(tfMax, gridBagConstraints);

        jLabel1.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private citibob.swing.typed.JKeyedMulti multi;
    private citibob.swing.typed.JTypedTextField tfMax;
    private citibob.swing.typed.JTypedTextField tfMin;
    // End of variables declaration//GEN-END:variables
	
}
