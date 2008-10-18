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
package oalaunch.citibob.template;

public class ResourceTemplate extends Template
{
	
static ClassLoader clr;
static {
	clr = ResourceTemplate.class.getClassLoader();
}
/** Instantiates a template from a resource (in a Jar file) ,rather than a file or other kind of URL. */
public ResourceTemplate(String name) {
	super(clr.getResource(name));
	System.out.println("url = " + url);

}
	
}
