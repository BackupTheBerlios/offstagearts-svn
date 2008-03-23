/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

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
 * Hist.java
 *
 * Created on November 3, 2007, 9:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.cleanse;

import java.util.Arrays;
import org.apache.commons.collections.ArrayStack;

import java.text.*;

/**
 * For making histograms
 * @author citibob
 */
public class Hist
{

NumberFormat nfmt = new DecimalFormat("#0.00");
	
double[] cat;
int[] count;
int tot;

/** Creates a new instance of Hist */
public Hist(double low, double high, int ncat)
{
	cat = new double[ncat];
	double delta = (high - low) / ncat;
	for (int i=0; i<ncat; ++i) {
		cat[i] = low + i*delta;
	}
	count = new int[ncat];
}

public void add(double d)
{
	int ix = Arrays.binarySearch(cat, d);
	if (ix >= 0) ++count[ix];
	else ++count[-(ix+2)];
	++tot;
}

public String toString() {
	StringBuffer sb = new StringBuffer();
	for (int i=0; i<cat.length; ++i) {
		sb.append(nfmt.format(cat[i]) + ": " + count[i] + " " +
			nfmt.format((double)count[i] / (double)tot) + "\n");
	}
	return sb.toString();
}
public static void main(String[] args)
{
	Hist h = new Hist(0,1.0,10);
	h.add(0);
	h.add(.1);
	h.add(.055);
	h.add(1);
	h.add(2);
	System.out.println(h.toString());
}
}
