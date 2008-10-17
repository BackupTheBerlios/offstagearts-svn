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

package citibob.reflect;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author fiscrob
 */
public class ReflectUtils {

public static Map<String,Field> getAllFields(Class... klasses)
{
	HashMap<String,Field> map = new HashMap();
	for (Class klass : klasses) {
//System.out.println("getAllFields: klass = " + klass);
		for (Class k = klass; k != Object.class; k = k.getSuperclass()) {
//System.out.println("    k = " + k);
			Field[] fields = k.getDeclaredFields();
			for (Field f : fields) {
				// Earlier items take precedence over later items.
				// Meaning: subclass instance variables override
				// superclass instance variables.
				if (map.containsKey(f.getName())) continue;
				
				// Don't do static fields
				if ((f.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) continue;
		
				// Get the field
	//			f.setAccessible(true);
				map.put(f.getName(), f);
			}
		}
	}
	return map;
}

public static List<Field> getAllFieldsList(Class klass)
{
	LinkedList<Field> list = new LinkedList();
	for (Class k = klass; k != Object.class; k = k.getSuperclass()) {
		Field[] fields = k.getDeclaredFields();
		for (int i=fields.length-1; i>= 0; --i) {
			Field f = fields[i];
			
			// Don't do static fields
			if ((f.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) continue;

			// Get the field
//			f.setAccessible(true);
			list.addFirst(f);
		}
	}
	return list;
}

/** Gets one named field, public or otherwise. */
public static Field getField(Class klass, String name)
{
	for (Class k = klass; k != Object.class; k = k.getSuperclass()) {
//System.out.println("    k = " + k);
		Field[] fields = k.getDeclaredFields();
		for (Field f : fields) {
			// Don't do static fields
			if ((f.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) continue;

			// Get the field
			if (name.equals(f.getName())) return f;
		}
	}
	return null;
}

/** Converts a class name (with dots) to the corresponding resource we're trying to
 * find in the classpath. */
public static String classToResource(String className)
	{ return className.replace('.', '/') + ".class"; }

public static String classToResource(Class klass)
	{ return classToResource(klass.getName()); }

public static String resourceToClass(String resName)
{
	int pos = resName.lastIndexOf(".class");
	resName = resName.substring(0,pos);
	return resName.replace('/', '.');
}

public static String getLeaf(URL url)
{
	String surl = url.toString();
	int slash = surl.lastIndexOf('/');
	return surl.substring(slash+1);
}

}
