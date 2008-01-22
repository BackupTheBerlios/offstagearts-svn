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
//package offstage.equery;
//
//import java.util.*;
//import citibob.sql.*;
//import citibob.sql.pgsql.*;
//import citibob.jschema.*;
//import citibob.swing.typed.*;
//import java.sql.*;
//import offstage.schema.OffstageSchemaSet;
//import citibob.types.*;
//
//public class WizQuerySchema extends QuerySchema
//{
//
//// --------------------------------------------------
//public WizQuerySchema(SchemaSet sset) throws SQLException
//{
//	super();
//	addSchema(sset.get("events"),
//		"events.entityid = main.entityid");
//	addSchema(sset.get("donations"),
//		"donations.entityid = main.entityid");
//	addSchema(sset.get("classes"),
//		"classes.entityid = main.entityid");
//	addSchema(sset.get("interests"),
//		"interests.entityid = main.entityid");
//	addSchema(sset.get("tickets"),
//		"ticketeventsales.entityid = main.entityid");
//	doAlias(alias);
//}
//// --------------------------------------------------------------------
//private static final String[] alias = {
//	"events.groupid", "events",
//	"donations.groupid", "donations",
//	"classes.groupid", "classes",
//	"interests.groupid", "interests",
//	"tickets.groupid", "tickets"
//};
//
//}
