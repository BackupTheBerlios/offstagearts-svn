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
///*
// * AdultidFormatter.java
// *
// * Created on June 29, 2007, 10:24 PM
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package offstage.school.gui;
//
//import java.text.DateFormat;
//import java.util.Date;
//import javax.swing.*;
//import javax.swing.text.*;
//import java.awt.*;
//import java.awt.event.*;
//import citibob.exception.*;
//import citibob.sql.*;
//import citibob.swing.typed.*;
//import citibob.sql.pgsql.*;
//import java.sql.*;
//import citibob.text.*;
//
///**
// *
// * @author citibob
// */
//public class AdultIDSFormat extends DBSFormat
//{
//
//public AdultIDSFormat(ConnPool pool)
//{ super(pool); }
//
//public String valueToString(Statement st, Object value)
//throws java.sql.SQLException
//{
//	return SQL.readString(st,
//		" select firstname + ' ' + lastname" +
//		" from entities" +
//		" where entityid = " + SqlInteger.sql((Integer)value));
//}
//
//}
