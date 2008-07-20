/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import com.Ostermiller.util.CSVParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import offstage.school.tuition.HourlyRatePlan;

/**
 * Looks up rates in an embedded .CSV file.
 * @author citibob
 */
public class TableRatePlan extends HourlyRatePlan
{

int[] timeS;
double[] rateY;

public TableRatePlan(double siblingDiscount, URL tableURL) throws IOException
{
	siblingDiscount = .1;
	
	// Read the CSV file
	CSVParser csv = new CSVParser(tableURL.openStream());
	
	// Get the headers and the cols we're interested in
	String[] headers = csv.getLine();	
	int hoursCol = -1;
	int tuitionCol = -1;
	for (int i=0; i<headers.length; ++i)
		if ("hours".equals(headers[i])) { hoursCol = i; break; }
	for (int i=0; i<headers.length; ++i)
		if ("tuition".equals(headers[i])) { tuitionCol = i; break; }
	
	
	// Read the rest of the file
	List<String[]> lines = new ArrayList();
	String[] ll;
	for (;;) {
		ll = csv.getLine();
		if (ll == null) break;
		if (ll.length == 0) continue;
		lines.add(ll);
	}
	
	// close it
	csv.close();
	
	// Make arrays out of it
	int nrow = lines.size();
	timeS = new int[nrow];
	rateY = new double[nrow];
	for (int i=0; i<nrow; ++i) {
		ll = lines.get(i);
		timeS[i] = (int)Math.round(Double.parseDouble(ll[hoursCol]) * 3600.0D);
		rateY[i] = Double.parseDouble(ll[tuitionCol]);
	}
}

public double getPrice(int weeklyTimeS)
{
	int ix = java.util.Arrays.binarySearch(timeS, weeklyTimeS);
	if (ix >= 0) return rateY[ix];
	
	ix = -ix-1;
	return ((double)(weeklyTimeS - timeS[ix-1]) / (double)(timeS[ix] - timeS[ix-1]))
		* (rateY[ix-1] + rateY[ix]);
}


}
