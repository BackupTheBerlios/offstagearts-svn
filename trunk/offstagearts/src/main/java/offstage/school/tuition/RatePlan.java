/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public interface RatePlan {

/** Sets the tuition field(s) in the student records inside payer.students.
 Also calls addTrans() as needed...  Should add the registration fee too, if needed. */
public abstract void setTuition(TuitionCon con, Payer payer);

}
