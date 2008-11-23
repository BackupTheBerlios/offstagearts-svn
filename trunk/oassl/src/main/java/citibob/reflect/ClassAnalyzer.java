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
package citibob.reflect;
/** TODO: For some reason, this does not find class dependencies of
 * declared fields.  That is, if I have:
 * class Test {
 *       AnotherClass var;
 * }
 * it will not find AnotherClass as a dependency, unless I actually
 * use the variable somewhere.  Proposed fix: use ASM:
 * http://asm.objectweb.org/
 */
/* This class borrows heavily from the original class called ListDeps. */
/* $Log: ListDeps.java,v $
 * Revision 1.10  2000/11/25  04:26:56  stuart
 * Look inside ZIP/JAR when listing classes in a package
 *
 * Revision 1.9  2000/06/06  19:05:42  stuart
 * resource name bug
 *
 * Revision 1.8  1999/08/24  02:06:19  stuart
 * Expand default exclude list
 *
 * Revision 1.7  1998/11/17  02:20:40  stuart
 * use imports instead of qualified names
 *
 * Revision 1.6  1998/11/11  19:29:01  stuart
 * rename to ListDeps
 * remove indents on file list
 *
 * Revision 1.5  1998/11/10  03:59:02  stuart
 * Not fully reading zip entries!
 *
 * Revision 1.4  1998/11/10  02:28:50  stuart
 * improve documentation
 * improve HandleTable
 *
 * Revision 1.3  1998/11/05  20:21:06  stuart
 * Support non-class resources
 *
 * Revision 1.2  1998/11/04  05:01:59  stuart
 * package list, performance improved, use pathsep and filesep properties
 *
 *
 * Enhanced by Stuart D. Gathman from an original progam named RollCall.
 * Copyright (c) 1998 Business Management Systems, Inc.
 *
 * Original Copyright (c) 1998 Karl Moss. All Rights Reserved.
 *
 * You may study, use, modify, and distribute this software for any
 * purpose provided that this copyright notice appears in all copies.
 *
 * This software is provided WITHOUT WARRANTY either expressed or
 * implied.
 *
 * @original_author  Karl Moss
 * @original_date    29Jan98
 *
 */
//import com.sun.org.apache.bcel.internal.util.ClassPath;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

 /** Looks through a classfile and figure out which other classes are referenced in it.  Based on <i>ListDeps</i> by Karl Moss, modified for use in the Clilet System by Robert Fischer.  Use the analyzeObject() method.
 @see #analyzeObject
*/
public class ClassAnalyzer
{
ClassLoader classLoader;	// ClassLoader used to search for classes (as resources)
Set<String> classPath;			// JAR files only for classpath; generally refers to all
	// or part of the classpath associated with classLoader

SortedSet<String> classesSeen;	// Classes seen but not yet explored
SortedSet<String> classesFound;		// Classes explored & found in classpath
SortedSet<String> classesNotFound;	// Classes explored & not found in classpath

HashSet objectsSeen;		// Objects seen but not yet explored
HashSet objectsExplored;	// Objects already processed
// =========================================
public static class ClassAnalyzerException extends java.lang.Exception
{
	public ClassAnalyzerException(String s) { super(s); }
}
// =========================================
static class HandleTable {
	private Object[] tbl;

	public HandleTable(int n) {
		tbl = new Object[n];
	}

	public void put(int i,Object name) {
		tbl[i] = name;
	}
	public void put(int i,int val) {
		put(i,new Integer(val));
	}
	public void put(int i,int a,int b) {
		put(i,new Pair(a,b));
	}
	public String getString(int i) {
		if (i >= tbl.length) return null;
		return (String)tbl[i];
	}
	public int getInt(int i) {
		return ((Integer)tbl[i]).intValue();
	}
	public Pair getPair(int i) {
		return (Pair)tbl[i];
	}
}
// =========================================
/** Internal class; no need use this by programmer. */
public static class IntList {
	private int[] a = new int[8];
	private int size = 0;

	public final int size() { return size; }

	public void add(int i) {
		if (size >= a.length) {
			int[] na = new int[a.length * 2];
			System.arraycopy(a,0,na,0,size);
			a = na;
		}
		a[size++] = i;
	}

	public void removeAll() { size = 0; }

	public int[] elements() {
		int[] na = new int[size];
		System.arraycopy(a,0,na,0,size);
		return na;
	}

}
// =========================================
static class Pair {
	final int a, b;
	Pair(int a,int b) { this.a = a; this.b = b; }
}
// =========================================
// ----------------------------------------------------------
public Set<String> getClassesFound()
{
	return classesFound;
}
// ----------------------------------------------------------
public ClassAnalyzer(ClassLoader classLoader, List<JarURL> xclassPath)
{
	classPath = new TreeSet();
	for (JarURL url : xclassPath) classPath.add(url.getName());
	this.classLoader = classLoader;

	classesSeen = new TreeSet();	// Classes yet to be processed
	classesFound = new TreeSet();	// Classes processed
	classesNotFound = new TreeSet();	// Classes not on classpath

	objectsSeen = new HashSet();
	objectsExplored = new HashSet();
}
// ----------------------------------------------------------
public ClassAnalyzer(ClassLoader classLoader, List<JarURL> xclassPath,
Set<String> classesSeen)
// Start with an initial classesSeen set...
{
	this(classLoader, xclassPath);
	this.classesSeen.addAll(classesSeen);
}
// ----------------------------------------------------------
/** Examine a class file to see what other classes it references. */
public void analyzeOneClass(
InputStream bais)		// Stream holding class file
throws ClassAnalyzerException, IOException
{
	// Create a DataInputStream using the buffer. This will
	// make reading the buffer very easy
	DataInputStream in = new DataInputStream(bais);

	// Read the magic number. It should be 0xCAFEBABE
	int magic = in.readInt();
	if (magic != 0xCAFEBABE)
		throw new ClassAnalyzerException("Invalid magic number");

	// Validate the version numbers
	short minor = in.readShort();
	short major = in.readShort();
//	if ((minor != 3) || (major != 45)) {
//		// The VM specification defines 3 as the minor version
//		// and 45 as the major version for 1.1
//		System.out.println("ClassAnalyzer: Class has invalid version number.\n"
//			+ "Classes must be compiled as classfiles version 45.3\n"
//			+ "Try compiling them with javac -target 1.3");
//		throw new ClassAnalyzerException("Invalid version number");
//	}

	// Get the number of items in the constant pool
	short count = in.readShort();

	// Track which CP entries are classes and String contants
	IntList classInfo = new IntList(); // which names are classes

	// Initialize the constant pool handle table
	HandleTable cp = new HandleTable(count);		// Constant Pool
	
	// Now walk through the constant pool looking for entries
	// we are interested in.	Others can be ignored, but we need
	// to understand the format so they can be skipped.
	readcp: for (int i = 1; i < count; i++) {
		// Read the tag
		byte tag = in.readByte();
		switch (tag) {
		case 7:	// CONSTANT_Class
			// Save the constant pool index for the class name
			short nameIndex = in.readShort();
			classInfo.add(nameIndex);
			cp.put(i,nameIndex);
			break;
		case 10: // CONSTANT_Methodref
			// Skip
			short clazz = in.readShort();	 // class
			short nt = in.readShort();			// name and type
			break;
		case 9:	// CONSTANT_Fieldref
		case 11: // CONSTANT_InterfaceMethodref
			// Skip past the structure
			in.skipBytes(4);
			break;
		case 8:	// CONSTANT_String
			// Skip past the string index
			short strIndex = in.readShort();
			break;
		case 3:	// CONSTANT_Integer
		case 4:	// CONSTANT_Float
			// Skip past the data
			in.skipBytes(4);
			break;
		case 5:	// CONSTANT_Long
		case 6:	// CONSTANT_Double
			// Skip past the data
			in.skipBytes(8);

			// As dictated by the Java Virtual Machine specification,
			// CONSTANT_Long and CONSTANT_Double consume two
			// constant pool entries.
			i++;
			
			break;
		case 12: // CONSTANT_NameAndType
			int name = in.readShort();
			int sig = in.readShort();
			cp.put(i,name,sig);
			break;
		case 1:	// CONSTANT_Utf8
			String s = in.readUTF();
			cp.put(i, s);
			break;
		default:
			System.err.println("WARNING: Unknown constant tag (" +
				tag + "@" + i + " of " + count + ")");
			break readcp;
		}
	}

	// We're done with the buffer and input streams
//	in.close();
	
	// Walk through our vector of class name
	// index values and get the actual class names

	// Copy the actual class names so tables can get reused
	int[] ia = classInfo.elements();
	for (int i = 0; i < ia.length; i++) {
		int idx = ia[i];
		String s = cp.getString(idx);
//System.out.println("classInfo[" + i + "] = " + s);
		if (s == null) continue;

		// Look for arrays. Only process arrays of objects
		if (s.startsWith("[")) {
			// Strip off all of the array indicators
			while (s.startsWith("["))
				s = s.substring(1);

			// Only use the array if it is an object. If it is,
			// the next character will be an 'L'
			if (!s.startsWith("L"))
				continue;
			
			// Strip off the leading 'L' and trailing ';'
			s = s.substring(1, s.length() - 1);
		}
		String className = s.replace('/', '.');
System.out.println("    Found class in bytecode: " + s);

		addClassesSeen(className);
	}
}
// ---------------------------------------------------
public void analyzeOneClass(
String className)
{
System.out.println("Analyzing class: " + className);
	boolean found = false;
	URL uurl = classLoader.getResource(ReflectUtils.classToResource(className));
	JarURL url = null;
	if (uurl != null) {
		url = new JarURL(uurl);
System.out.println("     Found JarURL: " + url);
		// We found the class, see if it's from a JAR file on our classpath.
		String surl = url.toString();
		int exp = surl.lastIndexOf('!');
		int slash = surl.lastIndexOf('/', exp-1);
		String jarName = surl.substring(slash+1, exp);
		int dash = jarName.indexOf('-');
		String name = (dash < 0 ? jarName : jarName.substring(0,dash));
		if (classPath.contains(name)) found = true;
	}
	
	if (!found) {
// System.out.println("Couldn't find " + className);
		System.out.println("        (did not find on classpath)");
		classesNotFound.add(className);
	} else {
		try {
			InputStream in = url.getUrl().openStream();
			analyzeOneClass(in);
			in.close();
			classesFound.add(className);
// System.out.println("ClassAnalyzer discovered: " + className);
		} catch(Exception e) {
e.printStackTrace();
// System.out.println("Couldn't find2 " + className);
			classesNotFound.add(className);
		}
	}
}
// ---------------------------------------------------
public void analyzeClassesSeen()
{
	while (!classesSeen.isEmpty()) {
		String className = (String)classesSeen.first();
// System.out.println("Analyzing class: " + className);
		analyzeOneClass(className);
		classesSeen.remove(className);
	}
}
// ---------------------------------------------------
/** Returns true if the class was added, false if it was already processed */
private boolean addClassesSeen(String className)
{
	if (classesFound.contains(className)) return false;
	if (classesNotFound.contains(className)) return false;
	classesSeen.add(className);
	return true;
}
public boolean addClassesSeen(Class c)
{
	return addClassesSeen(c.getName());
}
// ---------------------------------------------------
// ---------------------------------------------------
public void addObjectsSeen(Object obj)
{
	if (objectsExplored.contains(obj)) return;
	objectsSeen.add(obj);
}
// ---------------------------------------------------
public void analyzeOneObject(
Object obj)
{
	Class objClass = obj.getClass();
	addClassesSeen(objClass);
	Field[] fields = objClass.getDeclaredFields();
	for (int i = 0; i < fields.length; ++i) {
		Class declaredClass = fields[i].getType();
		if (declaredClass.isPrimitive()) continue;
		Field ff = fields[i];
		// Don't record transient fields as a dependency, since they don't serialize
		if ((ff.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) != 0) continue;
		try {
			addClassesSeen(declaredClass);
			fields[i].setAccessible(true);
			Object actualObj = fields[i].get(obj);
			if (actualObj != null) {
				Class actualClass = actualObj.getClass();
				addObjectsSeen(actualObj);
				addClassesSeen(actualClass);
			}
//System.out.println("DeclaredClass is " + declaredClass);
		} catch(Exception e) {
//e.printStackTrace();
		}
	}
}
// ---------------------------------------------------
public void analyzeObjectsSeen()
{
	while (!objectsSeen.isEmpty()) {
		Object obj = objectsSeen.iterator().next();
		objectsSeen.remove(obj);
System.out.println("Analyzing Object: " + obj);
		analyzeOneObject(obj);
	}
}
// ---------------------------------------------------
/** This is the main function to call by user. */
public static Set analyzeObject(Object o, ClassLoader loader, List<JarURL> xclassPath)
{
	ClassAnalyzer ca = new ClassAnalyzer(loader, xclassPath);
	ca.addClassesSeen(o.getClass());
System.out.println("XYXYXYX: " + o.getClass());
//	ca.addObjectsSeen(o);		// Analyzing objs doesn't work due to
//	ca.analyzeObjectsSeen();	// privacy restrictions.
	ca.analyzeClassesSeen();
	return ca.getClassesFound();
}
// ---------------------------------------------------

public static void main(String[] args)
throws ClassAnalyzerException, IOException
{
	URLClassLoader loader = (URLClassLoader)ClassAnalyzer.class.getClassLoader();
	List<JarURL> fullCP = ClassPathUtils.getClassPath(loader);
	
	ClassPathUtils.subtractCP(loader, fullCP, "jooreports", "jasperreports");
	
	for (JarURL url : fullCP) {
		System.out.println(url);
	}
	System.exit(0);
//	List<JarURL> classPath = ClassPathTest.getClassPath(loader);
	
//find pocono.jar in the classpath and eliminate it witha another getClassPath() starting on it.,
	
	
//	FilePub fp = new FilePub("/home/citibob/prg/nsandbox/makefile");
//	BankWrapperPub bp = new BankWrapperPub(fp);
//System.out.println("=======================================");
//
//	ClassPath cp = new ClassPath();
////	cp.add("/home/citibob/prg/nsandbox/classes");
//	cp.add(".");


	ClassAnalyzer ca = new ClassAnalyzer(loader, fullCP);
//	ca.addObjectsSeen(new MyTest());

	ca.analyzeObjectsSeen();		// See what objects we will need to serialize
	ca.analyzeClassesSeen();		// Find classes required for those objects

	System.out.println("========= classesFound");
	for (String name : ca.classesFound)
		System.out.println("    " + name);
	
	System.out.println("========= classesNotFound");
	for (String name : ca.classesNotFound)
		System.out.println("    " + name);

//	getClassResources("/home/citibob/prg/nsandbox/classes/citibob/jcp/glue/GlueServer.class");
}



}
