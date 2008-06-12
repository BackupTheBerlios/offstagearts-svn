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
 * JTypedCCInfo.java
 *
 * Created on August 5, 2007, 10:39 PM
 */

package offstage.swing.typed;

import citibob.swing.typed.*;
import offstage.types.*;
import javax.swing.*;
import citibob.wizard.*;
import java.awt.event.*;
import java.awt.*;
import offstage.crypt.*;


/**
 *
 * @author  citibob
 */
public class JTypedCCInfo extends javax.swing.JPanel
implements TypedWidget//, java.beans.PropertyChangeListener
{
	
boolean infocus;		// Does at least one of our sub-items have focus?
boolean valValid;		// Is the value field valid, or does it need to be recomputed?
String val;
String last4;
KeyRing kr;
String swipeState = "normal";
String swipeString = "";
boolean justSwiped = false;

/** Creates new form JTypedCCInfo */
public JTypedCCInfo()
{
    	initComponents();
//	cctype.setKeyedModel(offstage.schema.EntitiesSchema.ccTypeModel);
	ccnumber.setJType(String.class, new CCSFormat());
	expdate.setJType(String.class, new ExpDateSFormat());
	ccv.setJType(String.class, new DigitsSFormat(3));
	zip.setJType(String.class, new DigitsSFormat(5));
	ccname.setJTypeString();

	// Figure out when we might need to recompute the value
	FocusListener focus = new FocusListener() {
	    public void focusGained(FocusEvent e) {
			infocus = true;
			valValid = false;
		}
	    public void focusLost(FocusEvent e) {
			infocus = false;
		}
	};
	ccname.addFocusListener(focus);
//	cctype.addFocusListener(focus);
	ccnumber.addFocusListener(focus);
	expdate.addFocusListener(focus);
	ccv.addFocusListener(focus);
	zip.addFocusListener(focus);
}

protected void fireValueChanged(String oldValue)
{
	firePropertyChange("value", oldValue, getValue());
}

public void initRuntime(KeyRing kr)
{
	this.kr = kr;
}
public void addKeyListener(KeyListener kl) {
	ccname.addKeyListener(kl);
//	cctype.addKeyListener(kl);
	ccnumber.addKeyListener(kl);
	expdate.addKeyListener(kl);
	ccv.addKeyListener(kl);
	zip.addKeyListener(kl);	
}

public String getValue()
{
	if (!valValid) makeVal();
	return val;
}
public String getLast4()
{
	String s = (String)ccnumber.getValue();
	if (s == null) return null;
	if (s.length() == 16) return s.substring(12);
	return null;
}
public String getCCName() { return (String)ccname.getValue(); }
//public String getCCType() { return (String)cctype.getValue(); }
public String getCCType(String num)
{
	switch(num.charAt(0)) {
		case '4' : return "v";
		case '5' : return "m";
		default : return null;
	}
}
public String getCCType() { return getCCType((String)ccnumber.getValue()); }
public String getExpDate() { return (String)expdate.getValue(); }
// ------------------------------------------------
public boolean isNameSet() { return ccname.getValue() != null; }
//public boolean isCCTypeSet() { return cctype.getValue() != null; }
public boolean isCCNumberSet() {
	String s = (String)ccnumber.getValue();
	return (s != null && s.length() == 16 &&
		(s.charAt(0) == '4' || s.charAt(0) == '5'));
}
public boolean isExpDateSet() {
	String s = (String)expdate.getValue();
	return (s != null && s.length() == 4);	
}
public boolean isCCVSet() {
	String s = (String)ccv.getValue();
	return (s != null && s.length() == 3);	
}
public boolean isZipSet() {
	String s = (String)zip.getValue();
	return (s != null && s.length() == 5);
}
public boolean isFullySet() {
	return isNameSet() && // isCCTypeSet() &&
		isCCNumberSet() &&
		isExpDateSet() && isZipSet();	//  && isCCVSet()
}
// ------------------------------------------------

void makeVal()
{
	TypedHashMap map = new TypedHashMap();
//	map.put("cctype", cctype.getValue());
	String num = (String)ccnumber.getValue();
	map.put("ccnumber", num);
	map.put("cctype", getCCType(num));
	map.put("ccname", ccname.getValue());
	map.put("expdate", expdate.getValue());
	map.put("ccv", ccv.getValue());
	map.put("zip", zip.getValue());
	val = CCEncoding.encode(map);
	
	// Encrypt the value
	try {
		val = kr.encrypt(val);
		valValid = true;
	} catch(Exception e) {
		e.printStackTrace();

		// kr == null, or public key not loaded.
		// Either way, don't leak any unencrypted information.
		val = null;
	}		
}

// ================================================================

/** Returns last legal value of the widget.  Same as method in JFormattedTextField */
//public Object getValue() { return val; }

/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public void setValue(Object o) {
	if (o == null) {
		// Clear it out...
//		cctype.setValue(null);
		ccnumber.setValue(null);
		expdate.setValue(null);
		ccv.setValue(null);
		zip.setValue(null);
	} else {
		TypedHashMap map = CCEncoding.decode((String)o);
//		cctype.setValue(map.getString("cctype"));
		ccnumber.setValue(map.getString("ccnumber"));
		expdate.setValue(map.getString("expdate"));
		ccv.setValue(map.getString("ccv"));
		zip.setValue(map.getString("zip"));
	}
}

public void initValue(String xccname, String xcctype, String xexpdate, String xzip)
{
	setValue(null);
	ccname.setValue(xccname);
//	cctype.setValue(xcctype);
	expdate.setValue(xexpdate);
	zip.setValue(xzip);
}
public void initValue(CCInfoLabels lab)
{
	initValue((String)lab.lccname.getValue(), (String)lab.lcctype.getValue(),
		(String)lab.lexpdate.getValue(), null);
}
/** From TableCellEditor (in case this is being used in a TableCellEditor):
 * Tells the editor to stop editing and accept any partially edited value
 * as the value of the editor. The editor returns false if editing was not
 * stopped; this is useful for editors that validate and can not accept
 * invalid entries. */
public boolean stopEditing() {
	// Not to be used in a table cell...
	return false;
}

/** Is this object an instance of the class available for this widget?
 * If so, then setValue() will work.  See SqlType.. */
public boolean isInstance(Object o) {
	if (o == null) return true;
	return (o instanceof String);
	// Maybe try parsing the info...
}

/** Set up widget to edit a specific SqlType.  Note that this widget does not
 have to be able to edit ALL SqlTypes... it can throw a ClassCastException
 if asked to edit a SqlType it doesn't like. */
public void setJType(citibob.swing.typed.Swinger f) throws ClassCastException
{  }

String colName;

/** Row (if any) in a RowModel we will bind this to at runtime. */
public String getColName() { return colName; }

/** Row (if any) in a RowModel we will bind this to at runtime. */
public void setColName(String col) { this.colName = col; }

// ===========================================================
///** Pass along change in value from underlying typed widget. */
//public void propertyChange(java.beans.PropertyChangeEvent evt) {
////	firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
//}	

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button1 = new java.awt.Button();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        expdate = new citibob.swing.typed.JTypedTextField();
        ccnumber = new citibob.swing.typed.JTypedTextField();
        ccv = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        ccname = new citibob.swing.typed.JTypedTextField();
        jTypedButton1 = new offstage.swing.typed.JTypedButton();

        button1.setLabel("button1");

        jLabel2.setText("Name on Card:");

        jLabel3.setText("CC Number:");

        jLabel4.setText("Exp Date:");

        jLabel5.setText("CCV Code:");

        jLabel6.setText("Zip Code (5 digits):");

        expdate.setText("jTypedTextField2");

        ccnumber.setText("jTypedTextField4");

        ccv.setText("jTypedTextField5");

        zip.setText("jTypedTextField6");

        ccname.setText("jTypedTextField4");
        ccname.setColName("ccname");
        ccname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ccnameKeyTyped(evt);
            }
        });

        jTypedButton1.setText("Click to Swipe");
        jTypedButton1.setNextFocusableComponent(ccname);
        jTypedButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTypedButton1ActionPerformed(evt);
            }
        });
        jTypedButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTypedButton1KeyTyped(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(jLabel2)
                .add(2, 2, 2)
                .add(ccname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(45, 45, 45)
                .add(jLabel3)
                .add(2, 2, 2)
                .add(ccnumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(61, 61, 61)
                .add(jLabel4)
                .add(2, 2, 2)
                .add(expdate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(54, 54, 54)
                .add(jLabel5)
                .add(2, 2, 2)
                .add(ccv, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(jLabel6)
                .add(2, 2, 2)
                .add(zip, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .add(jTypedButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTypedButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(ccname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(ccnumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(expdate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(ccv, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(zip, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jTypedButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTypedButton1ActionPerformed
        if (swipeState.equals("normal")) {
                swipeState = "ready";
                jTypedButton1.setText("Swipe Now!");
        } else {
                swipeState = "normal";
                jTypedButton1.setText("Click to Swipe");
        }
        ccname.requestFocus();
}//GEN-LAST:event_jTypedButton1ActionPerformed

public boolean justSwiped() {
        return justSwiped;
}

public void clearJustSwiped() {
        justSwiped = false;
}

private void grabChar(java.awt.event.KeyEvent evt) {
        if(!swipeState.equals("normal")) {
                if(swipeState.equals("ready")) {
                        swipeState = "reading";
//                        setTimeout(exitSwipe(), 1);
                }
                char pressedKey = evt.getKeyChar();
                swipeString = swipeString + pressedKey;
                evt.consume();
                if(swipeString.indexOf("?") != swipeString.lastIndexOf("?")) {
                        swipeState = "normal";
                        System.out.println(swipeString);
                        jTypedButton1.setText("Click to Swipe");
                        String[] swipeArr = swipeString.split("\\^");
                        String ccnum = swipeArr[0].substring(2);
                        String[] backName = swipeArr[1].split("/");
                        String name = backName[1]+" "+backName[0];
                        String date = swipeArr[2].substring(0, 6);
                        ccnumber.setValue(ccnum);
                        expdate.setValue(date);
                        ccname.setValue(name);
                        swipeString = "";
                        justSwiped = true;
                }
        }
}

private void jTypedButton1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTypedButton1KeyTyped
        grabChar(evt);
}//GEN-LAST:event_jTypedButton1KeyTyped

private void ccnameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ccnameKeyTyped
        grabChar(evt);
}//GEN-LAST:event_ccnameKeyTyped
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private citibob.swing.typed.JTypedTextField ccname;
    private citibob.swing.typed.JTypedTextField ccnumber;
    private citibob.swing.typed.JTypedTextField ccv;
    private citibob.swing.typed.JTypedTextField expdate;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private offstage.swing.typed.JTypedButton jTypedButton1;
    private citibob.swing.typed.JTypedTextField zip;
    // End of variables declaration//GEN-END:variables


public static void main(String[] args)
{
	JFrame f = new JFrame();
	f.setLayout(new FlowLayout());
	final JTypedCCInfo ccinfo = new JTypedCCInfo();
	f.getContentPane().add(ccinfo);
	JButton jb = new JButton();
	jb.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
			String s =ccinfo.getValue();
			System.out.println(s);
			ccinfo.setValue(s);
		}
	});
	f.getContentPane().add(jb);
	f.pack();
	f.show();
}
	
}
