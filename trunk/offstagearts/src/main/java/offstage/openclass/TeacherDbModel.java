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
package offstage.openclass;

import offstage.frontdesk.*;
import citibob.jschema.*;
import citibob.sql.*;
import java.util.*;
import java.sql.*;
import offstage.db.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.FrontApp;
import offstage.school.gui.EnrolledDbModel;

/** Query one person record and all the stuff related to it. */

public class TeacherDbModel extends LoggedMultiDbModel
{

EntityDbModel onePerson;
IntKeyedDbModel oneTeacher;
IntKeyedDbModel phones;
//IntKeyedDbModel ocdiscs;		// Discounts
EnrolledDbModel enrolled;
OCDiscModels ocDiscModels;

public TeacherDbModel(SqlRun str, final FrontApp app)
{
	super(app.queryLogger());
	
	SchemaSet osset = app.schemaSet();
	logadd(onePerson = new EntityDbModel(osset.get("persons"), app));
	logadd(oneTeacher = new IntKeyedDbModel(osset.get("teachers"), "entityid"));
	logadd(phones = new IntKeyedDbModel(osset.get("phones"), "entityid"));
//	logadd(ocdiscs = new IntKeyedDbModel(osset.get("ocdiscids"), "entityid"));
	logadd(enrolled = new EnrolledDbModel(str, app, "uniqenrolls", "teacher"));
	
	ocDiscModels = new OCDiscModels(str, app);
	str.execUpdate(new UpdTasklet() {
	public void run() {	
		add(ocDiscModels.getDm());
	}});
}

public void insertPhone(int groupTypeID) throws KeyViolationException
	{ phones.getSchemaBuf().insertRow(-1, "groupid", new Integer(groupTypeID)); }

public void insertDiscount(int ocdiscid) throws KeyViolationException
	{ ocDiscModels.discDm.getSchemaBuf().insertRow(-1, "ocdiscid", new Integer(ocdiscid)); }


}
