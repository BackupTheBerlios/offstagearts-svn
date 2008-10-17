/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oalaunch;

import java.lang.reflect.Method;

/**
 *
 * @author citibob
 */
public class Launch1 {
public static void main(String[] args) throws Exception
{
	Class klass = Class.forName("offstage.gui.OffstageLauncher");
	Method meth = klass.getMethod("main", args.getClass());
System.out.println(meth);
System.out.println(meth.getParameterTypes().length);
	String configName = args[1];
	
//	Object[] args2 = new Object[args.length];
//	for (int i=0; i<args.length; ++i) args2[i] = args[i];
	
//	Object[] args2 = new Object[] {new String[] {"--oalaunch", configName}};
	Object[] args2 = new Object[] {args};
	meth.invoke(null, args2);
}
}
