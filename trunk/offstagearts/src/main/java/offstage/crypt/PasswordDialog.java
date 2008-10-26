/*
Holyoke Framework: library for GUI-based database applications
This file Copyright (c) 2006-2008 by Robert Fischer

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
 * AuthenticatorDialog.java
 *
 * Created on July 23, 2004, 12:08 AM
 */

package offstage.crypt;


/**
 *
 * @author  citibob
 */
public class PasswordDialog extends javax.swing.JDialog {

boolean okPressed;
boolean hasShown;			// Set to true once this has been shown

public char[] getPassword() {
	return fPassword.getPassword();
}
	
public boolean getOK() { return okPressed; }
public boolean getHasShown() { return hasShown; }

public void setVisible(boolean b)
{
	super.setVisible(b);
	if (b) hasShown = true;
}

public void clear()
{
	char[] pwd = getPassword();
	for (int i=0; i<pwd.length; ++i) pwd[i] = '\0';
}

public void setAlert(String alert) {tAlert.setText(alert); }

/** Creates new form AuthenticatorDialog */
public PasswordDialog(java.awt.Frame parent) {
	super(parent, true);
	initComponents();
	fPassword.setText(null);
}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        message1 = new javax.swing.JLabel();
        fPassword = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        tAlert = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Launcher Password");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        message1.setText("Please enter launcher password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(message1, gridBagConstraints);

        fPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fPasswordKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(fPassword, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        bOK.setText("OK");
        bOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOKActionPerformed(evt);
            }
        });
        jPanel1.add(bOK);

        bCancel.setText("Cancel");
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });
        jPanel1.add(bCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        tAlert.setForeground(new java.awt.Color(255, 0, 51));
        tAlert.setText("alert");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(tAlert, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void fPasswordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fPasswordKeyTyped
        // ENTER has same effect as pressing OK
        if (evt.getKeyChar() == '\n') {
            okPressed = true;
			setVisible(false);
            return;
        }
}//GEN-LAST:event_fPasswordKeyTyped

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        okPressed=false;
		setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        okPressed=false;
		setVisible(false);
    }//GEN-LAST:event_formWindowClosed

    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        okPressed=false;
		setVisible(false);
    }//GEN-LAST:event_bCancelActionPerformed

    private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
        okPressed=true;
		setVisible(false);
    }//GEN-LAST:event_bOKActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.JPasswordField fPassword;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel message1;
    private javax.swing.JLabel tAlert;
    // End of variables declaration//GEN-END:variables
    
}
