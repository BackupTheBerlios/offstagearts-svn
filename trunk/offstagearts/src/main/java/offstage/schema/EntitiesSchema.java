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
package offstage.schema;

import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import citibob.types.*;
import java.sql.*;

public class EntitiesSchema extends ConstSqlSchema
{


public final static KeyedModel ccTypeModel;
static {
	ccTypeModel = new KeyedModel();
	ccTypeModel.addItem(null, "<None>");
	ccTypeModel.addItem("m", "Master Card");
	ccTypeModel.addItem("v", "Visa");
}

public EntitiesSchema(citibob.sql.SqlRun str, DbChangeModel change)
throws SQLException
{
	table = "entities";

//	DbKeyedModel kmodel = new DbKeyedModel(str, change,
//		"relprimarytypes", "relprimarytypeid", "name", "name", "");
	cols = new SqlCol[] {
			new SqlCol(new SqlInteger(false), "entityid", true),
//			new SqlCol(new SqlInteger(), "primaryentityid", false),
//			new SqlCol(new SqlInteger(), "parent1id", false),
//			new SqlCol(new SqlInteger(), "parent2id", false),
			new SqlCol(new SqlString(100), "address1", false),
			new SqlCol(new SqlString(50), "address2", false),
			new SqlCol(new SqlString(50), "city", false),
			new SqlCol(new SqlString(10), "state", false),
			new SqlCol(new SqlString(10), "zip", false),
			new SqlCol(new SqlString(50), "country", false),
			new SqlCol(new SqlString(50), "recordsource", false),
			new SqlCol(new SqlInteger(), "sourcekey", false),
			//new Column(new SqlInteger(), "ipeopleid", false),
			new SqlCol(new SqlTimestamp("GMT",true), "lastupdated", false),
//			new citibob.jschema.SqlCol(new SqlEnum(kmodel), "relprimarytypeid", false),
			//new citibob.jschema.Column(new SqlBool(), "isquery", false),
			new SqlCol(new SqlBool(), "sendmail", false),
			new SqlCol(new SqlBool(), "obsolete", false),
		new SqlCol(new SqlString(30), "title", false),
		new SqlCol(new SqlString(50), "occupation", false),
		new SqlCol(new SqlString(30), "salutation", false),
		new SqlCol(new SqlString(50), "firstname", false),
		new SqlCol(new SqlString(50), "middlename", false),
		new SqlCol(new SqlString(50), "lastname", false),
		new SqlCol(new SqlString(100), "customaddressto", false),
		new SqlCol(new SqlString(100), "orgname", false),
		new SqlCol(new SqlBool(false), "isorg", false),
		new SqlCol(new SqlEnum(new DbKeyedModel(str, change, "mailprefids",
			"mailprefid", "name", "mailprefid", "<No Preference>")),
			"mailprefid"),
		new SqlCol(new SqlString(50), "py_name"),
		new SqlCol(new SqlString(1), "cc_type"),
		new SqlCol(new SqlString(4), "cc_last4", false),
		new SqlCol(new SqlString(4), "cc_expdate", false),
		new SqlCol(new SqlString(255), "cc_info", false),
			
			
		new SqlCol(new SqlString(30), "nickname"),
		new SqlCol(new SqlString(20), "suffix"),
		new SqlCol(new SqlNumeric(9,2), "askamount"),
					
		new SqlCol(new SqlEnum(new DbKeyedModel(str, change, "callprefids",
			"callprefid", "name", "callprefid", "<No Preference>")),
			"callprefid"),
		new SqlCol(new SqlEnum(new DbKeyedModel(str, change, "mailstateids",
			"mailstateid", "name", "mailstateid", "<None>")),
			"mailstateid"),
		new SqlCol(new SqlEnum(new DbKeyedModel(str, change, "sourceids",
			"sourceid", "name", "sourceid", "<No Source>")),
			"sourceid"),
		new SqlCol(new SqlEnum(new DbKeyedModel(str, change, "dbids",
			"dbid", "name", "dbid", "<all>")),
			"dbid")
					
	};
}	
// ------------------------------------------
//// Singleton stuff
//private static EntitiesSchema instance = new EntitiesSchema();
//public static ConstSqlSchema getInstance()
//	{ return instance; }

}
