/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Info on the payer */
public class Payer
{
	public int entityid;
	public boolean isorg;
	public char billingtype;
	public List<Student> students;
	public Payer(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		String sBillingType = rs.getString("billingtype");
		billingtype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
		isorg = rs.getBoolean("isorg");
		students = new ArrayList(1);
	}
}
