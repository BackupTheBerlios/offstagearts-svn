/*
 * StudentInfoPane.java
 *
 * Created on July 27, 2008, 10:02 PM
 */

package offstage.school.gui;

import citibob.sql.SqlRun;
import citibob.swing.typed.JKeyedComboBox;
import citibob.swing.typed.TypedWidgetBinder;
import citibob.types.KeyedModel;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class StudentInfoPane extends ExtensiblePane {

	public void initRuntime(SqlRun str, FrontApp fapp, SchoolModel schoolModel) {}

	protected static final KeyedModel genderKmodel;
	static {
		genderKmodel = new KeyedModel();
			genderKmodel.addItem(null, "<Unknown>");
			genderKmodel.addItem("M", "Male");
			genderKmodel.addItem("F", "Female");
	}
	
	protected void bindGender(JKeyedComboBox gender, SchoolModel schoolModel)
	{
		gender.setKeyedModel(genderKmodel, null);
		new TypedWidgetBinder().bind(gender, schoolModel.studentRm,
				"gender", TypedWidgetBinder.BT_READWRITE);
		
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 91, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	
}
