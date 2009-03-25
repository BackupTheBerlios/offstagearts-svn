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

package offstage.school.gui;

import citibob.app.App;
import citibob.jschema.SqlBufDbModel;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.pgsql.SqlInteger;

/**
 *
 * @author citibob
 */
public class EnrolledDbModel extends SqlBufDbModel
{
	private Integer termid, entityid;
	String enrollTable;					// enrollments or uniqenrolls
	Integer role;						// Select only this role
	
	public void setEntityID(Integer entityid) {
		this.entityid = entityid;
	}

	public void setTermID(Integer termID) {
		this.termid = termID;
	}

	public static int getCourseRoleID(App app, String name)
	{
		return app.schemaSet().getEnumInt("enrollments", "courserole", name);
	}
	
//	public EnrolledDbModel(SqlRun str, App app) {
//		this(str, app, "enrollments", "student");
////			app.schemaSet().getEnumInt("enrollments", "courserole", "student"));
//	}
	public EnrolledDbModel(SqlRun str, App app, String enrollTable, String courseroleName) {
		super();
		this.enrollTable = enrollTable;		
		super.init(str, app,
			new String[] {"courseids", enrollTable},
			null,
			new String[] {"enrollments"});
		this.role = (courseroleName == null ? null :
			app.schemaSet().getEnumInt("enrollments", "courserole", courseroleName));
	}
	public SqlSet getSelectSql(boolean proto) {
		return new SqlSet(
			" select e.*,c.name,c.dayofweek,c.tstart,c.tnext\n" +
			" from " + enrollTable + " e, courseids c\n" +
			" where e.courseid = c.courseid\n" +
			" and c.termid = " + SqlInteger.sql(termid) + //smod.getTermID()) +
			(role == null ? "" : " and courserole = " + SqlInteger.sql(role)) +
			(proto ? " and false" : " and e.entityid = " + SqlInteger.sql(entityid)) + //smod.getStudentID())) +
			" order by dayofweek, tstart, name\n");
	}
// =====================================================================
public void removeEnrollment(SqlRun str, int entityid, int courseid)
{
	str.execSql("delete from enrollments" +
			" where courseid = " + SqlInteger.sql(courseid) +
			" and entityid = " + SqlInteger.sql(entityid));
	doSelect(str);

}
}
