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
 * PhoneFormatter.java
 *
 * Created on October 8, 2006, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

import javax.swing.*;
import java.text.*;
//import static offstage.types.CCSFormat.*;
import citibob.text.*;

/**
 *strin
 * @author citibob
 */
public class DigitsSFormat extends BaseSFormat
{
	
int ndigits;

public DigitsSFormat(int ndigits)
{
	this.ndigits = ndigits;
}	

/**
 * Parses <code>text</code> returning an arbitrary Object. Some
 * formatters may return null.
 *
 * @throws ParseException if there is an error in the conversion
 * @param text String to convert
 * @return Object representation of text
 */
public Object stringToValue(String text) throws
ParseException
{
	if (nullText.equals(text)) return null;
	if (CCSFormat.countDigits(text) != ndigits)
		throw new ParseException(
		"Wrong number of digits (should be " + ndigits + ")", 0);
	String val = CCSFormat.removeNondigits(text, ndigits);
	return  val;
}

/**
 * Returns the string value to display for <code>value</code>.
 *
 * @throws ParseException if there is an error in the conversion
 * @param value Value to convert
 * @return String representation of value
 */
public String valueToString(Object value) throws
ParseException
{
	if (value == null) return nullText;
	String text = (String)value;
	if (CCSFormat.countDigits(text) != ndigits) return text;
	return CCSFormat.removeNondigits(text, ndigits);
 }

	
}
