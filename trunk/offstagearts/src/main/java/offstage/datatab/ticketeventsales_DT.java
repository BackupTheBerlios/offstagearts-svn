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

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.TicketeventsSchema;

/**
 *
 * @author citibob
 */
public class ticketeventsales_DT extends DataTab
{

public ticketeventsales_DT(SqlRun str, App app)
throws SQLException
{
	title = "Tickets";
	schema = new TicketeventsSchema(str, app.dbChange(), app.timeZone());
	orderClause = "date desc";
	displayColTitles = new String[] {"Event", "Date", "Type", "Venue", "Perf Type", "#Tix", "Payment", "Offer Code"};
	displayCols = new String[]
			{"groupid", "date", "tickettypeid", "venueid", "perftypeid", "numberoftickets", "payment", "offercodeid"};
	equeryAliases = new String[] {
		"ticketeventsales.groupid", "tickets",
		"ticketeventsales.date", "tix-date",
		"ticketeventsales.numberoftickets", "#-tix",
		"ticketeventsales.payment", "tix-payment",
		"ticketeventsales.tickettypeid", "tix-type",
		"ticketeventsales.venueid", "venue",
		"ticketeventsales.offercodeid", "offercode",
		"ticketeventsales.perftypeid", "performance-type"
	};
	summary_st =
		"<table>\n" +
		"<tr><th>Show</th><th>Type</th><th>Venue</th><th>Perf</th><th>#Tix</th>\n" +
		"<th>Payment</th><th>Offer Code</th></tr>\n" +
		"$ticketeventsales:{it |\n" +
		"<tr><td><b>$it.groupid$</b></td><td>$it.tickettypeid$</td><td>$it.venueid$</td>\n" +
		"<td>$it.perftypeid$</td><td align=\"right\">$it.numberoftickets$</td><td align=\"right\">$it.payment$</td>\n" +
		"<td>$it.offercodeid$</td></tr>\n" +
		"}$\n" +
		"</table>\n";
}
	
}
