/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maestro;

/**
 *
 * @author citibob
 */
public class Maestro {

/** Adds the bonus to open class dollar purchases */
public static double dollarsToCredits(double dollars)
{
	if (dollars < 50) return dollars;
	return dollars * 1.1;
}
}
