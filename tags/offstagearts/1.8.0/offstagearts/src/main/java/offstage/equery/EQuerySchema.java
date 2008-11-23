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
package offstage.equery;

import citibob.jschema.SchemaSet;
import citibob.types.JEnum;
import citibob.types.JEnumSegment;
import citibob.types.JType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import offstage.datatab.DataTab;
import offstage.datatab.DataTabSet;


public class EQuerySchema extends QuerySchema
{

// --------------------------------------------------
public EQuerySchema(SchemaSet sset, DataTabSet tabs) throws SQLException
{
	super();
	addSchema(sset.get("entities"),
		"entities.entityid = main.entityid");
	addSchema(sset.get("org"),
		"organizations.entityid = main.entityid");
	addSchema(sset.get("persons"),
		"persons.entityid = main.entityid");
//	addSchema(sset.get("events"),
//		"events.entityid = main.entityid");
//	addSchema(sset.get("donations"),
//		"donations.entityid = main.entityid");
//	addSchema(sset.get("notes"),
//		"notes.entityid = main.entityid");
	addSchema(sset.get("phones"),
		"phones.entityid = main.entityid");
//	addSchema(sset.get("classes"),
//		"classes.entityid = main.entityid");
//	addSchema(sset.get("termenrolls"),
//		"termenrolls.entityid = main.entityid");
	addSchema(sset.get("termregs"),
		"termregs.entityid = termenrolls.entityid and termregs.groupid = termenrolls.groupid",
		"termenrolls");
	addSchema(sset.get("enrollments"),
		"enrollments.entityid = main.entityid");
	super.getCol("enrollments.courseid").typer = new ColTyper() {
	public JType getType(EClause clause, Element el, QuerySchema.Col col) {
		JEnum baseEnum = (JEnum)col.col.getType();
		ColName cn = new ColName("termenrolls.groupid");
		for (Element ee : clause.getElements()) {
			if (ee == el) continue;
			if (ee.colName.equals(cn)) {
				// We've found what we're looking for!
				Object segment = ee.value;
				JEnum limitEnum = new JEnumSegment(baseEnum, segment);
				return limitEnum;
			}
		}
		return baseEnum;		// term element not in clause, return the full monty
	}};
	
	
//// add termregs.tuition, etc.
//	addSchema(sset.get("interests"),
//		"interests.entityid = main.entityid");
//	addSchema(sset.get("tickets"),
//		"ticketeventsales.entityid = main.entityid");

	for (DataTab tab : tabs.equeryTabs) {
		tab.addToEQuerySchema(this);
	}
	
	
	List<String[]> aliasLists = new ArrayList();
	aliasLists.add(aliases);
	for (DataTab tab : tabs.equeryTabs) {
		aliasLists.add(tab.getAliases());
	}
	doAlias(aliasLists);
}

// --------------------------------------------------------------------
private static final String[] aliases = {
	"persons.isorg", "isorg",
	"persons.firstname", "firstname",
	"persons.middlename", "middlename",
	"persons.lastname", "lastname",
	"persons.gender", "gender",
	"persons.email", "email",
	"persons.occupation", "occupation",
	"persons.orgname", "orgname",
	"persons.dob", "dob",
	"entities.address1", "address1",
	"entities.address2", "address2",
	"entities.city", "city",
	"entities.state", "state",
	"entities.zip", "zip",
	"entities.country", "country",
	"entities.lastupdated", "lastupdated",
	"entities.sendmail", "sendmail",
//	"organizations.name", "org-name",
	
//	"events.groupid", "event-type",
////	"events.role", "event-role",
//	"donations.groupid", "donation-type",
//	"donations.date", "donation-date",
//	"donations.amount", "donation-amount",
//	"notes.groupid", "note-type",
//	"notes.date", "note-date",
//	"notes.note", "note",
	"phones.groupid", "phone-type",
	"phones.phone", "phone",
//	"classes.groupid", "classes (deprecated)",
//	"termenrolls.groupid", "terms",
//	"termenrolls.courserole", "termrole",
	"enrollments.courseid", "courses",
	"enrollments.courserole", "courserole",
	"termregs.programid", "level",
	"termregs.tuition", "tuition",
	"termregs.scholarship", "scholarship",
	"termregs.dtsigned", "registration-signed",
	"termregs.dtregistered", "date-registered",
	"termregs.payerid", "payer",
//	"interests.groupid", "interests",
//	"ticketeventsales.groupid", "tickets",
//	"ticketeventsales.date", "tix-date",
//	"ticketeventsales.numberoftickets", "#-tix",
//	"ticketeventsales.payment", "tix-payment",
//	"ticketeventsales.tickettypeid", "tix-type",
//	"ticketeventsales.venueid", "venue",
//	"ticketeventsales.offercodeid", "offercode",
//	"ticketeventsales.perftypeid", "performance-type",
	"entities.entityid", "entityid",
	"entities.obsolete", "obsolete",
};

}
