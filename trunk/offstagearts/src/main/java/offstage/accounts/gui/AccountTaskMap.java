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
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package offstage.accounts.gui;
//
//import citibob.task.BatchRunnable;
//import citibob.sql.SqlRun;
//import citibob.task.TaskMap;
//import citibob.wizard.Wizard;
//import java.awt.Component;
//import offstage.FrontApp;
//
///**
// *
// * @author citibob
// */
//public abstract class AccountTaskMap extends TaskMap
//{
//
//FrontApp fapp;
//Component component;
//
//public AccountTaskMap(FrontApp xfapp, Component component)
//{
//	this.fapp = xfapp;
//	this.component = component;
//	
//	String[] permissions = null;
//	add("cash", permissions, new RunWizard("cashpayment"));
//	add("check", permissions, new RunWizard("checkpayment"));
//	add("cc", permissions, new RunWizard("ccpayment"));
//	add("other", permissions, new RunWizard("transtype"));
//}
//
//
//public abstract int getEntityID();
//public abstract int getAcTypeID();
///** Implement this to refresh the appropriate stuff on your panel. */
//public abstract void refresh(SqlRun str);
//
//// =========================================================
//class RunWizard implements BatchRunnable
//{
//String startState;
//public RunWizard(String startState) { this.startState = startState; }
//public void run(SqlRun str) throws Exception {
//	int entityid = getEntityID();
//	Wizard wizard = new TransactionWizard(fapp, component, entityid, getAcTypeID());
//	wizard.setVal("entityid", entityid);
//	wizard.runWizard(startState);
//	refresh(str);
//}
//}
//
//
//}
