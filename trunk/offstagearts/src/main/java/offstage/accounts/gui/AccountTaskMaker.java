/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.accounts.gui;

import citibob.task.BatchRunnable;
import citibob.sql.SqlRunner;
import citibob.task.Task;
import citibob.wizard.Wizard;
import java.awt.Component;
import offstage.FrontApp;

/**
 *
 * @author citibob
 */
public abstract class AccountTaskMaker {

FrontApp fapp;
Component component;

public AccountTaskMaker(FrontApp xfapp, Component component)
{
	this.fapp = xfapp;
	this.component = component;
}

/** Implement this to refresh the appropriate stuff on your panel. */
public abstract void refresh(SqlRunner str);

///** @param startState in {"cashpayment", "checkpayment", "ccpayment", "transtype"} */
//public void newTransaction(final int actypeid, final int entityid, final String startSate)
//{
//	return new Task()
//accountHelper.accountAction(
//	ActransSchema.AC_SCHOOL,(Integer) entityid.getValue(), "transtype");
public void accountAction(final int actypeid, final int entityid, final String startState)
{
	fapp.runGui(component, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		Wizard wizard = new TransactionWizard(fapp, component, entityid, actypeid);
		wizard.setVal("entityid", entityid);
		wizard.runWizard(startState);
		refresh(str);
	}});
}

}
