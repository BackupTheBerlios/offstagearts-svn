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
//package offstage.schema;
//
//import citibob.jschema.*;
//import citibob.sql.pgsql.*;
//import citibob.sql.*;
//import citibob.types.*;
//import java.sql.*;
//
//public class EntitiesSchoolSchema extends ConstSqlSchema
//{
//
////public final static KeyedModel billingtypeModel;
////static {
////	billingtypeModel = new KeyedModel();
//////	billingtypeModel.addItem(null, "<None>");
////	billingtypeModel.addItem("y", "Yearly");
////	billingtypeModel.addItem("q", "Quarterly");
////}
//
//public EntitiesSchoolSchema(citibob.sql.SqlRun str, DbChangeModel change)
//throws SQLException
//{
//	table = "entities_school";
//
////	KeyedModel kmodel = new DbKeyedModel(st, change,
////		"relprimarytypes", "relprimarytypeid", "name", "name");
//	// Populate levels (programs) for this term
//	cols = new SqlCol[] {
//			new SqlCol(new SqlInteger(false), "entityid", true),
//			new SqlCol(new SqlInteger(), "adultid", false),
//			new SqlCol(new SqlInteger(), "parentid", false),
//			new SqlCol(new SqlInteger(), "parent2id", false),
////			new SqlCol(new SqlString(1), "billingtype"),
//	};
//}	
//// ------------------------------------------
////// Singleton stuff
////private static EntitiesSchema instance = new EntitiesSchema();
////public static ConstSqlSchema getInstance()
////	{ return instance; }
//
//}
