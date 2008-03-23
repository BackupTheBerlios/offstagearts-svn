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
 * Checksum.java
 *
 * Created on August 2, 2007, 8:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

/**
 *
 * @author citibob
 */
public class Checksum {

static final int cksize = 4;

/** Adds a checksum to the beginning of an array of byes. */
static byte[] addChecksum(byte[] b)
{
	byte[] cksum = computeChecksum(b,0);
	byte[] out = new byte[b.length + cksum.length];
	int j=0;
	for (int i=0; i<cksum.length; ++i) out[j++] = cksum[i];
	for (int i=0; i<b.length; ++i) out[j++] = b[i];
	return out;
}

static byte[] computeChecksum(byte[] b, int start)
{
	byte[] cksum = new byte[cksize];
	for (int i=start; i<b.length; i += cksize) {
		for (int j=0; j<cksize; ++j) {
			if (i+j >= b.length) return cksum;
			cksum[j] = (byte)(cksum[j] ^ b[i+j]);
		}
	}
	return cksum;
}

/** @returns null if embedded chekcsum does not match. */
static byte[] removeChecksum(byte[] b)
{
	byte[] cksum = computeChecksum(b, cksize);
	for (int i=0; i<cksize; ++i) if (cksum[i] != b[i]) return null;
	byte[] out = new byte[b.length - cksize];
	for (int i=0; i<out.length; ++i) out[i] = b[i+cksize];
	return out;
}
public static void main(String[] args)
{
	byte[] b = "Hello World!".getBytes();
	byte[] bk = addChecksum(b);
//	bk[4] = 0;
	byte[] b2 = removeChecksum(bk);
	System.out.println(new String(b2));
}

}
