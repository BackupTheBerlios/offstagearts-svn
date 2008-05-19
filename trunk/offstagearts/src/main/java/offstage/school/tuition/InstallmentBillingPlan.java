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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import citibob.util.DayConv;
import java.text.ParseException;


/**
 *
 * @author citibob
 */
public class InstallmentBillingPlan extends BaseBillingPlan
{

int[] duedatesDN;
String[] labels;
int regDuedateDN;
double regFee;

public InstallmentBillingPlan(double regFee, String sRegDueDate,
String[] labels, String[] sDueDates)
throws ParseException
{
	this.labels = labels;
	this.regFee = regFee;

	this.regDuedateDN = DayConv.parse(dfmt, sRegDueDate);
	duedatesDN = new int[sDueDates.length];
	for (int i=0; i<sDueDates.length; ++i)
		duedatesDN[i] = DayConv.parse( dfmt,sDueDates[i]);
}


/** Adds the tuition billing records for a student.  Should take into account
 the student's tuition, scholarships and any registration fees. */
public void billAccount(TuitionCon con, Student student)
{
	// Registration Fee
	if (regFee != 0) addRegFee(con, student, regDuedateDN, regFee);

	// Main fees
	int ninstallments = labels.length;
	int lastCumT00 = 0;
	int lastCumS00 = 0;
	double student_tuition = student.getTuition();
	double xscholarship = student.scholarship + student.scholarshippct * student_tuition;
	for (int i=0; i < ninstallments; ++i) {
		double frac = (double)(i+1) / (double)ninstallments;
		
		// Tuition
		int cumT00 = (int)Math.round(100.0 * student_tuition * frac);
		double tuition = .01D * (double)(cumT00 - lastCumT00);
		addTuition(con, student, duedatesDN[i], tuition, labels[i]);		
		lastCumT00 = cumT00;

		// Scholarship
		int cumS00 = (int)Math.round(100.0 * xscholarship * frac);
		double scholarship = .01D * (double)(cumS00 - lastCumS00);
		addScholarship(con, student, duedatesDN[i], -scholarship, labels[i]);		
		lastCumS00 = cumS00;
	}
//	
//	
//	
//			switch(pp.billingtype) {
//				case 'q' : {
//					for (int i=1; i<=rbPlanSet.numQuarters(); ++i) {
//						DueDate dd = tdata.duedates.get("q"+i);
//						
//						// Main tuition
//						insertTransaction(sql, pp.entityid, ss.entityid,dd.duedate,
//							ss.tuition * .25,
//							ss.tuitionDesc + " --- " + dd.description);
//						
//						// Scholarships
//						if (ss.scholarship > 0) {
//							insertTransaction(sql, pp.entityid, ss.entityid,dd.duedate,
//								-ss.scholarship * .25,
//								tdata.termName + ": Scholarship for " + ss.getName() + " --- " + dd.description);
//						}
//					}
//				} break;
//				case 'y' : {
//					DueDate dd = tdata.duedates.get("y");
//					
//					// Main tuition
//					insertTransaction(sql, pp.entityid, ss.entityid,
//						dd.duedate,
//						ss.tuition,ss.tuitionDesc + " --- " + dd.description);
//					
//					// Scholarships
//					if (ss.scholarship > 0) {
//						insertTransaction(sql, pp.entityid, ss.entityid,dd.duedate,
//							-ss.scholarship,
//							tdata.termName + ": Scholarship for " + ss.getName() + " --- " + dd.description);
//					}
//				} break;
//			}
//
	
}
}
