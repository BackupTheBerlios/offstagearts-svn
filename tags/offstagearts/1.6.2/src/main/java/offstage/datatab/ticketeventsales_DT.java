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
}
	
}
