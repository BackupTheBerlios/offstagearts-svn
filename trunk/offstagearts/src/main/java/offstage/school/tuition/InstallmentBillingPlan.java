/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

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
	this.regDuedateDN = dconv.toDay(sRegDueDate);
	duedatesDN = new int[sDueDates.length];
	for (int i=0; i<sDueDates.length; ++i)
		duedatesDN[i] = dconv.toDay(sDueDates[i]);
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
	for (int i=0; i < ninstallments; ++i) {
		double frac = (double)(i+1) / (double)ninstallments;
		
		// Tuition
		int cumT00 = (int)Math.round(100.0 * student_tuition * frac);
		double tuition = .01D * (double)(cumT00 - lastCumT00);
		addTuition(con, student, duedatesDN[i], tuition, labels[i]);		
		lastCumT00 = cumT00;

		// Scholarship
		int cumS00 = (int)Math.round(100.0 * student.scholarship * frac);
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
