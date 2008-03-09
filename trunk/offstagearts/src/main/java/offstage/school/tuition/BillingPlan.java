/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public interface BillingPlan {
	
/** Adds the tuition billing records for a student.  Should take into account
 the student's tuition, scholarships and any registration fees. */
public abstract void billAccount(TuitionCon con, Student student);

}
