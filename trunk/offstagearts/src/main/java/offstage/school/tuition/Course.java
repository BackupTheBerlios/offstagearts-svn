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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import java.util.LinkedList;
import java.util.List;

public class Course
{
	public int courseid;
	public List<Meeting> meetings;
	/** Full number of meetings in course */
	public int numMeetings() { return meetings.size(); }
	
	/** Finds number of meetings >=dstart and <=dend.  dstart and dend are dates ONLY,
	 they have no time component. */
	public int numMeetings(java.util.Date dstart, java.util.Date dend)
	{
		int n = 0;
		if (dstart != null && dend != null) {
			long startMS = dstart.getTime();
			long nextMS = dend.getTime() + 86400*1000L;
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms >= startMS && ms < nextMS) ++n;
			}
		} else if (dstart != null) {
			long startMS = dstart.getTime();
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms >= startMS) ++n;
			}
		} else if (dend != null) {
			long nextMS = dend.getTime() + 86400*1000L;
			for (Meeting mm : meetings) {
				long ms = mm.dtstart.getTime();
				if (ms < nextMS) ++n;
			}
		} else {
			n = numMeetings();
		}
		return n;
	}
	public Course(int courseid)
	{
		this.courseid = courseid;
		meetings = new LinkedList();
	}
}
