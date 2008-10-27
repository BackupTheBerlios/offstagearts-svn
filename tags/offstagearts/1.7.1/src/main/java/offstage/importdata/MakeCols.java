/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.importdata;

import citibob.reflect.ClassPathUtils;
import com.Ostermiller.util.CSVParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a CSV file, and creates a Java file that defines its columns
 * @author citibob
 */
public class MakeCols {

static String[] getTableNames(File dir)
{
	File[] files = dir.listFiles();
	List<String> names = new ArrayList();
	for (File f : files) {
		String name = f.getName();
		if (!name.endsWith(".csv")) continue;
		names.add(name.substring(0, name.length()-4));
	}
	String[] ret = new String[names.size()];
	names.toArray(ret);
	return ret;
}


public static void makeCols(File csvDir, String[] tableNames,
String[] classNames,
File srcRootDir, String packageName)
throws IOException
{
	for (int i=0; i<tableNames.length; ++i) {
		String tableName = tableNames[i];
		String className = classNames[i];
		MakeCols.makeCols(csvDir, tableName, className, srcRootDir, packageName);
	}
}

/** @param csvDir Directory containing a bunch of CSV files to import.
 * @param srcRootDir Root of Java source tree in which to write
 * @param packageName Name of package of output file
 * @param tableName Name of table we're going to read (sans .csv)
 */
public static void makeCols(File csvDir, String tableName, String className,
File srcRootDir, String packageName)
throws IOException
{
	className = (className == null ? tableName : className);
	File csvFile = new File(csvDir, tableName + ".csv");
	File javaFileDir = new File(srcRootDir, packageName.replace('.', File.separatorChar));
	File javaFile = new File(javaFileDir, className + ".java");
	makeCols(csvFile, javaFile, packageName, tableName, className);
}

public static void makeCols(File csvFile, File javaFile,
String packageName, String tableName, String className) throws IOException
{
	// Open output
	Writer out = new FileWriter(javaFile);

	out.write(
		"package " + packageName + ";\n" +
		"public interface " + className + " {\n");

	// Open input
	CSVParser csv = new CSVParser(new FileReader(csvFile));
	String[] colNames = csv.getLine();
	csv.close();

	// Write out constants
	for (int i=0; i < colNames.length; ++i) {
		String name = colNames[i];
		name = name.replace(" ", "_");
		name = name.replace("-", "_");
		name = name.replace("(", "_");
		name = name.replace(")", "");
		name = name.replace("/", "");
		name = name.replace(",", "");
		name = name.replace("'", "");
		name = name.replace(".", "_");
		name = name.replace("#", "num_");
System.out.println("name = " + name);
		if (Character.isDigit(name.charAt(0))) name = "_" + name;
		out.write(
			"\tpublic static final int " + name + " = " + i + ";\n");
	}

	out.write("}\n");
	out.close();

}

	
	
	
	
	
	
	
	
	
public static void main(String[] args) throws Exception
{
	File root = ClassPathUtils.getMavenProjectRoot();
	File csvDir = new File(root, "../2008NutcrackerAudition");
	String[] tableNames = new String[] {"nutcast2"};
	String[] classNames = new String[] {null};
	File srcRootDir = new File(root, "src/main/java");
	String packageName = "schemas";
	
	makeCols(csvDir, tableNames, classNames, srcRootDir, packageName);
}


}
