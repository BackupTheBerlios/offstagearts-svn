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
package citibob.template;

import java.io.*;
import java.util.*;
import java.net.*;

/** Simple text templating engine. */
public class Template extends HashMap
{

URL url;


public Template(URL url)
{
	this.url = url;
}

public void write(Writer out) throws IOException
{
	Reader in = new BufferedReader(new InputStreamReader(url.openStream()));
	try {
		all: for (;;) {
			// Go ahead and do your thing...
			for (;;) {
				int c = in.read();
				if (c == '%') break;
				if (c == -1) break all;
				out.write(c);
			}

			// We've encountered a field name; read it
			StringBuffer keyBuf = new StringBuffer();
			int c;
			for (;;) {
				c = in.read();
				if (c == '%') break;
				if (c == -1) break all;
				if (c == '"') break;
				keyBuf.append((char)c);
			}
			String key = keyBuf.toString();
			if (key.length() == 0) {	// %% outputs a percent sign.
				out.write("%");
			} else {
				// Look up the key and write it out
				Object value = get(key);
				if (value == null) out.write("&lt;null&gt;");
				else out.write(value.toString());
			}
			if (c == '"') out.write(c);
		}
	} catch(IOException e) {}	// EOF
	try {
		in.close();
	} catch(IOException e) {}	
}

}
