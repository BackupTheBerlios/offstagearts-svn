/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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

public class EntitiesSchema extends ConstSchema
{


public final static KeyedModel ccTypeModel;
static {
	ccTypeModel = new KeyedModel();
	ccTypeModel.addItem(null, "<None>");
	ccTypeModel.addItem("m", "Master Card");
	ccTypeModel.addItem("v", "Visa");
}

public EntitiesSchema(citibob.sql.SqlRunner str, DbChangeModel change)
throws SQLException
{
	table = "entities";

	KeyedModel kmodel = new DbKeyedModel(str, change,
		"relprimarytypes", "relprimarytypeid", "name", "name");
	cols = new Column[] {
			new Column(new SqlInteger(false), "entityid", true),
			new Column(new SqlInteger(), "primaryentityid", false),
			new Column(new SqlString(100), "address1", false),
			new Column(new SqlString(50), "address2", false),
			new Column(new SqlString(50), "city", false),
			new Column(new SqlString(10), "state", false),
			new Column(new SqlString(10), "zip", false),
			new Column(new SqlString(50), "country", false),
			new Column(new SqlString(50), "recordsource", false),
			new Column(new SqlInteger(), "sourcekey", false),
			//new Column(new SqlInteger(), "ipeopleid", false),
			new Column(new SqlTimestamp("GMT",true), "lastupdated", false),
			new citibob.jschema.Column(new SqlEnum(kmodel, true), "relprimarytypeid", false),
			//new citibob.jschema.Column(new SqlBool(), "isquery", false),
			new Column(new SqlBool(), "sendmail", false),
			new Column(new SqlBool(), "obsolete", false),
		new Column(new SqlString(30), "title", false),
		new Column(new SqlString(50), "occupation", false),
		new Column(new SqlString(30), "salutation", false),
		new Column(new SqlString(50), "firstname", false),
		new Column(new SqlString(50), "middlename", false),
		new Column(new SqlString(50), "lastname", false),
		new Column(new SqlString(100), "customaddressto", false),
		new Column(new SqlString(100), "orgname", false),
		new Column(new SqlBool(false), "isorg", false),
		new Column(new SqlEnum(
			new DbKeyedModel(str, change, "mailprefids", "mailprefid", "name", "mailprefid"),
			"<No Preference>"), "mailprefid"),
		new Column(new SqlString(50), "ccname"),
		new Column(new SqlString(1), "cctype"),
		new Column(new SqlString(4), "cclast4", false),
		new Column(new SqlString(4), "ccexpdate", false),
		new Column(new SqlString(255), "ccinfo", false)
	};
}	
// ------------------------------------------
//// Singleton stuff
//private static EntitiesSchema instance = new EntitiesSchema();
//public static ConstSchema getInstance()
//	{ return instance; }

}
