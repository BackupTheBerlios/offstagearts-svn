/*
Holyoke Framework: library for GUI-based database applications
This file Copyright (c) 2006-2008 by Robert Fischer

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

package oalaunch.citibob.gui;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author citibob
 */
public class BareBonesOpen {

public static void open(java.io.File f) throws IOException
{
	String osName = System.getProperty("os.name");
	if (osName.startsWith("Mac")) {
		Runtime.getRuntime().exec("open " + f.getPath());
	} else if (osName.startsWith("Windows")) {
		Runtime.getRuntime().exec("start " + f.getPath());
	} else { //assume Unix or Linux
		Runtime.getRuntime().exec("gnome-open " + f.getPath());
	}
}
public static void main(String[] args) throws Exception
{
    open(new File("c:\\x.pdf"));
}
}
