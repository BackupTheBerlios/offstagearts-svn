/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public interface TuitionCon {

/** Sets the calculated tuition for a particular student */
public void setCalcTuition(Student student, double amount, String desc);

/** Adds a tuition record to be written to the database. */
public void addTransaction(Student student,
int duedateDN, double amount, String description);

/** Gives us the data set, in case we need it. */
public TuitionData getData();

}
