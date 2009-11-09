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

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.sql.DbChangeModel;
import java.sql.*;
import citibob.types.*;

public class Acbal2Schema extends ConstSqlSchema
{

//public static final int AC_SCHOOL = 1;
//public static final int AC_TICKET = 2;
//public static final int AC_PLEDGE = 3;
//public static final int AC_OPENCLASS = 4;
//public static final int AC_EXPENSE = 5;
	
public final KeyedModel actypeKmodel;

public Acbal2Schema(citibob.sql.SqlRun str, DbChangeModel change)
throws SQLException
{
	super();

	/*
CREATE TABLE acbal2
(
  entityid integer NOT NULL,
  actypeid integer NOT NULL,
  acbalid serial NOT NULL,
  dtime timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT acbal2_pkey PRIMARY KEY (entityid, actypeid),
  CONSTRAINT acbal2_acbalid_key UNIQUE (acbalid)
)
WITH (OIDS=FALSE);
ALTER TABLE acbal2 OWNER TO ballettheatre;
*/

	table = "acbal2";
	actypeKmodel = new DbKeyedModel(str, change,
		"actypes", "actypeid", "name", "name", null);
	
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "entityid", true),
		new SqlCol(new SqlEnum(actypeKmodel), "actypeid", true),
		new SqlCol(new SqlInteger(false), "acbalid"),
		new SqlCol(new SqlTimestamp("GMT",false), "dtime")
	};
}

}
