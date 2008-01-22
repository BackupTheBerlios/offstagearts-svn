/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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
 * TuitionRate.java
 *
 * Created on August 12, 2007, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.school.gui;

/**
 *
 * @author citibob
 */
public class TuitionRate
{
	
//// Calculate from first principles
//static final double[] discHours = new double[] {.75, 1, 2.5, 3.75, 6, 8, 10};
//static final int[] discS = new double[discHours.length];
//static final double[] discRate = new double[] {1.0582, 1, .77, .76, .6, .55, .5125};
//
//static {
//	for (int i=0; i<discHours.length; ++i) {
//		discS[i] = (int)(discHours[i] * 3600 + .5);
//	}
//}
//double baseHourlyRate = 20.671875;
//double weeksInTerm = 32;

// Do simple table lookup on number of hours
static final int[] tuitionS2 = new int[] {
0,0,			// Important here, need a sentinal
2700,525,
3600,660,
4500,830,
5400,990,
6300,1045,
7200,1060,
8100,1120,
9000,1275,
9900,1400,
10800,1530,
12600,1785,
13500,1890,
14400,2010,
16200,2265,
18000,2315,
19800,2365,
21600,2385,
23400,2585,
25200,2775,
27000,2850,
28800,2910,
30600,3090,
32400,3275,
34200,3330,
36000,3390,
37800,3560,
39600,3730,
41400,3900,
43200,4070,
45000,4240,
46800,4405,
48600,4575,
50400,4745,
52200,4915,
52200*2,4915*2
};
static final int[] timeS;
static final double[] rateY;

static {
	int n = tuitionS2.length / 2;
	timeS = new int[n];
	rateY = new double[n];
	for (int i=0; i<n; ++i) {
		timeS[i] = tuitionS2[i*2];
		rateY[i] = (double)tuitionS2[i*2+1];
	}
}

public static double getRateY(int weeklyTimeS)
{
	int ix = java.util.Arrays.binarySearch(timeS, weeklyTimeS);
	if (ix >= 0) return rateY[ix];
	
	ix = -ix-1;
	return ((double)(weeklyTimeS - timeS[ix-1]) / (double)(timeS[ix] - timeS[ix-1]))
	* (rateY[ix-1] + rateY[ix]);
}
	
}
