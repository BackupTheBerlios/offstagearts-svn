/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public abstract class TuitionScale {

protected TuitionData data;
//protected Map<String, String> duedateCodes;

public void setData(TuitionData data) { this.data = data; }
	
/** Number of installments we divide payments up into */
public abstract int numQuarters();

public abstract double getRegistrationFee();
	
/** Changes the student records inside payer.students */
public abstract void calcTuition(Payer payer);

/** Returns the (one) tuition number for a particular student. */
// abstract double calcTuition(Student ss);

/** @param weeklyS Number of seconds of class per week for this student.  Does
 not include classes with fixed price. */
public abstract double getPrice(int weeklyS);

	
}
