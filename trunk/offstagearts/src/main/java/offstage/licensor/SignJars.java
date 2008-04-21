/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.licensor;

import citibob.reflect.ClassPathUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author citibob
 */
public class SignJars implements Cloneable
{

public static final int CMP_EQ = 0;
public static final int CMP_NEQ = 1;
public static final int CMP_AONLY = 2;
public static final int CMP_BONLY = 3;
	
File srcDir;
File unsignedDir;
File signedDir;

String alias;
String password;

public Object clone() throws CloneNotSupportedException { return super.clone(); }

// Save disk, use MD5 sums instead...
//    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
//    digest.update(...your data here...);
//    byte[] hash = digest.digest();

// Copies src file to dst file.
// If the dst file does not exist, it is created
void copy(File src, File dst) throws IOException {
	InputStream in = new FileInputStream(src);
	OutputStream out = new FileOutputStream(dst);

	try {
		// Transfer bytes from in to out
		byte[] buf = new byte[8192];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	} finally {
		in.close();
		out.close();
	}
}
	
static final int max = 8192;
public int cmp(File a, File b) throws IOException
{
	if (a.exists() && !b.exists()) return CMP_AONLY;
	if (b.exists() && !a.exists()) return CMP_BONLY;
	
	
	long lena, lenb;
	lena = a.length();
	lenb = b.length();
	if (lena != lenb) return CMP_NEQ;

	byte[] bufa = new byte[max];
	byte[] bufb = new byte[max];
	InputStream ina, inb;
	ina = new FileInputStream(a);
	inb = new FileInputStream(b);
	try {

		for (;;) {
			int na = ina.read(bufa);
			int nb = inb.read(bufb);
			if (na != nb) return CMP_NEQ;
			if (na <= 0) return CMP_EQ;

			if (na < max) {
				Arrays.fill(bufa, na, max, (byte)0);
				Arrays.fill(bufb, nb, max, (byte)0);
			}
			if (!Arrays.equals(bufa, bufb)) return CMP_NEQ;
		}
	} finally {
		ina.close();
		inb.close();
	}
}

public void sign(File f) throws IOException
{
	Process proc = Runtime.getRuntime().exec("jarsigner -storepass " + password + " " +
		f.getPath() + " " + alias);
	InputStream in = proc.getInputStream();
	int b;
	while ((b = in.read()) >= 0) System.out.write(b);
}

public void processFile(String name) throws IOException
{
	File src = new File(srcDir, name);
	File unsigned = new File(unsignedDir, name);
	File signed = new File(signedDir, name);
	
	int xcmp = cmp(src, unsigned);
//	System.out.println("xcmp = " + xcmp);
	switch(xcmp) {
		case CMP_AONLY :
		case CMP_NEQ : {
			// Copy file and sign it
			System.out.println("Copying and signing " + name);
			unsigned.getParentFile().mkdirs();
			copy(src, unsigned);
			signed.getParentFile().mkdirs();
			copy(unsigned, signed);
			sign(signed);
		} break;
		case CMP_BONLY : {
			// Delete file
			System.out.println("Deleting " + name);
			unsigned.delete();
			signed.delete();
		} break;
		case CMP_EQ : break;	// nothing
	}
}
	
public SignJars getSub(String name)
{
	try {
		SignJars sj = (SignJars)clone();
			sj.signedDir = new File(sj.signedDir, name);
			sj.unsignedDir = new File(sj.unsignedDir, name);
			sj.srcDir = new File(sj.srcDir, name);
		return sj;
	} catch(Exception e) { return null; }
}

public void processDirRecursive() throws IOException
{
	// Get the files in this directory
	Set<String> jars = new TreeSet();
	Set<String> dirs = new TreeSet();
	File[] files = srcDir.listFiles();
	if (files != null) for (File f : files) {
		if (f.getName().endsWith(".jar")) jars.add(f.getName());
		if (f.isDirectory()) dirs.add(f.getName());
	}
	files = signedDir.listFiles();
	if (files != null) for (File f : files) {
		if (f.getName().endsWith(".jar")) jars.add(f.getName());
		if (f.isDirectory()) dirs.add(f.getName());
	}
	
	// Process these jars
	for (String name : jars) {
		processFile(name);
	}
	
	// Process the dirs
	for (String name : dirs) {
		getSub(name).processDirRecursive();
	}
}

public static void main(String[] args) throws Exception
{
//	if (args.length == 0) {
//		System.out.println("Usage: SignJars ");
//	}
	try {
File x = new File(".");
System.out.println(x.getAbsolutePath());
		File dir = ClassPathUtils.getMavenProjectRoot();
System.out.println("Base dir = " + dir);
		SignJars sj = new SignJars();
			sj.alias = "offstagearts";
			sj.password = "keyst0re";
			sj.srcDir = new File(dir, "target/executable-netbeans.dir");
			sj.unsignedDir = new File(dir, "jaws/unsigned");
			sj.signedDir = new File(dir, "jaws/signed");
//		sj.processFile("offstagearts-1.0-SNAPSHOT.jar");
		sj.processDirRecursive();
	} catch(Exception e) {
		e.printStackTrace();
	}
}

}
