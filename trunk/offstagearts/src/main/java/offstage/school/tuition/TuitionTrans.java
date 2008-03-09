/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public class TuitionTrans {

public int entityid;
public int studentid;
public java.util.Date duedate;
public double amount;
public String description;

public TuitionTrans(int entityid, int studentid,
java.util.Date duedate, double amount, String description)		
{
	this.entityid = entityid;
	this.studentid = studentid;
	this.duedate = duedate;
	this.amount = amount;
	this.description = description;
}
}
