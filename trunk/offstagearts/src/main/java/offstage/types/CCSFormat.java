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
import citibob.text.*;

/**
 *strin
 * @author citibob
 */
public class CCSFormat extends AbstractSFormat
{

static int countDigits(String s)
{
	int n=0;
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') ++n;
	}
	return n;
}

static String removeNondigits(String s, int ndigits)
{
	StringBuffer ret = new StringBuffer(ndigits);
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') ret.append(c);
	}
	return ret.toString();
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
	if (text.equals(nullText)) return null;
//	if (countDigits(text) != 16) throw new ParseException("Wrong number of digits for Credit Card Number!", 0);
	return removeNondigits(text, 16);
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
	if (countDigits(text) != 16) return text;
	
	String digits = removeNondigits(text, 16);
	return digits.substring(0,4) + "-" + digits.substring(4,8) + "-" +
		digits.substring(8,12) + "-" + digits.substring(12);
}

	
}
