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
package offstage.school.gui;

import citibob.jschema.*;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;

/** Query one person record and all the stuff related to it. */

public class PayerDbModel extends EntityMultiDbModel
{
	
public final IntKeyedDbModel phoneDb;
public final IntKeyedDbModel schoolDb;

public PayerDbModel(citibob.app.App app)
{
	super(app);
	QueryLogger logger = app.getLogger();
	logadd(logger, phoneDb = new IntKeyedDbModel(app.getSchema("phones"), "entityid",
		new IntKeyedDbModel.Params(true)));
	logadd(logger, schoolDb = new IntKeyedDbModel(app.getSchema("entities_school"), "entityid",
		new IntKeyedDbModel.Params(true)));
}

}
