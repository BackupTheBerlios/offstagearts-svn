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
 * KeyRing.java
 *
 * Created on July 22, 2007, 11:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import java.security.interfaces.*;
import javax.crypto.*;
import java.io.*;
import java.security.spec.*;
import java.security.*;
import java.io.*;
import java.text.*;
import java.util.*;
import pubdomain.*;

/**
 * User Wizards:
0. Format USB key
1. Create or change master key
2. Duplicate key
3. Load master key
 * @author citibob
 */
public class KeyRing {

PublicKey pubKey;
File privDir;
//PrivateKey privKeys[];		// Contains past history of private keys in decreasing time order
PrivKeyFile privKeys[];
KeyFactory keyFactory;
SecureRandom rnd;
File pubDir;
static final int BLOCKLEN = 115;		// Total bytes to be encrypted

static class PrivKeyFile
{
	public java.util.Date dt;		// Date/Time identifyin the key
	public PrivateKey key;			// The actual key
}

//public static final int EXTRABYTES = 8;		// Add this many random bytes to end of each message before encrypting
public static final byte[] MAGIC = new byte[]{(byte)23,(byte)19,(byte)215,(byte)154,(byte)128,(byte)146,(byte)171,(byte)94};

/** Checks to see if the USB key is inserted. */
public boolean isUsbInserted()
	{ return privDir.exists(); }

/** Creates a new instance of KeyRing */
public KeyRing(File pubDir, File privDir)
throws GeneralSecurityException, IOException
{
	this.pubDir = pubDir;
	rnd = SecureRandom.getInstance("SHA1PRNG");
	keyFactory = KeyFactory.getInstance("RSA");

	// Store location of private key, but don't read it yet; that happens
	// only when user inserts the USB drive containing the private keys.
	this.privDir = privDir;

	try {
		loadPubKey(pubDir);
	} catch(Exception e) {
		// Key not loaded
	}
}

public boolean pubKeyLoaded() { return pubKey != null; }

/** @returns most recent public key file in a directory. */
public static File mostRecentPubKeyFile(File dir)
{
	// Get file names that might be our public key
	String[] files = dir.list();
	ArrayList<String> keyFiles = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if (fname.startsWith("pubkey") && fname.endsWith(".txt")) {
			keyFiles.add(fname);
		}
	}
	Collections.sort(keyFiles);

	// Get most recent public key
	int nkey = keyFiles.size();
	if (nkey == 0) return null;
	File keyFile = new File(dir, keyFiles.get(nkey-1));
	return keyFile;
}

public void loadPubKey()
throws GeneralSecurityException, IOException
	{ loadPubKey(pubDir); }

public void loadPubKey(File dir)
throws GeneralSecurityException, IOException
{
System.out.println("loadPubKey dir = " + dir);
	pubKey = null;
	File keyFile = mostRecentPubKeyFile(pubDir);
System.out.println("loadPubKey keyFile = " + keyFile);
	if (keyFile == null) return;

	// Read and decode public key
	byte[] bytes = pubdomain.Base64.readBase64File(keyFile);
	bytes = Checksum.removeChecksum(bytes);
	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
	pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
}

public void clearPrivKeys()
{
	privKeys = null;
}
public boolean privKeysLoaded() { return privKeys != null; }

/** Returns: fals if USB drive was not inserted. */
public boolean loadPrivKeys()
throws GeneralSecurityException, IOException
{
	clearPrivKeys();
	if (!(privDir.exists() && privDir.isDirectory()) ) return false;

	String[] files = privDir.list();

	// Get file names
	ArrayList<String> keyFiles = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if (fname.startsWith("privkey") && fname.endsWith(".txt")) {
			keyFiles.add(fname);
		}
	}
	if (keyFiles.size() == 0) return false;
	Collections.sort(keyFiles);
	
	// Read each one as a key file
	ArrayList<PrivKeyFile> xPrivKeys = new ArrayList();
	for (int i=keyFiles.size()-1; i >= 0; --i) {
		try {
			// Parse the timestamp from the filename
			String fname = keyFiles.get(i);
			int dash = fname.indexOf('-');
			int dot = fname.lastIndexOf('.');
			String sdt = fname.substring(dash+1, dot);
			java.util.Date dt = dfmt.parse(sdt);

			// Read the key from the file
			File f = new File(privDir, fname);
			byte[] bytes = pubdomain.Base64.readBase64File(f);
			bytes = Checksum.removeChecksum(bytes);
			PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
			PrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

			// Store our (timestamp, key) pair
			PrivKeyFile keyfile = new PrivKeyFile();
				keyfile.dt = dt;
				keyfile.key = key;
			xPrivKeys.add(keyfile);
		} catch(IOException e) {
			// Something wrong with this file; skip it.
		} catch(ParseException e2) {}
	}

	// Convert to an array
	privKeys = new PrivKeyFile[xPrivKeys.size()];
	xPrivKeys.toArray(privKeys);

	return true;
}

/** Encrypts a piece of data (to be stored in a DB table). */
public String encrypt(String msg)
throws GeneralSecurityException, IOException
{
	// Use public key to encrypt a message
	Cipher ecipher = Cipher.getInstance("RSA");
	ecipher.init(Cipher.ENCRYPT_MODE, pubKey);

	// Convert text to bytes, and pad with random extra bytes
	byte[] plainText = msg.getBytes("UTF8");
	byte[] padded = new byte[BLOCKLEN];
	int j=0;
	for (int i=0; i<MAGIC.length; ++i) padded[j++] = MAGIC[i];
	for (int i=0; i<plainText.length; ++i) padded[j++] = plainText[i];
	padded[j++] = '\0';
	byte[] rndBytes = new byte[BLOCKLEN - j];
	rnd.nextBytes(rndBytes);
	for (int i=0; i<rndBytes.length; ++i) padded[j++] = rndBytes[i];

	// Encrypt!
	byte[] cipherText = ecipher.doFinal(padded);

	// Convert to Base 64
	return pubdomain.Base64.encodeBytes(cipherText);
}

/** Decrypts a Base64-encoded, padded message */
public String decrypt(String emsg)
throws GeneralSecurityException
{
	// Decode base 64
	byte[] cipherText = pubdomain.Base64.decode(emsg);

	// Decrypt, trying each key in turn
outer:
	for (int i=0; i<privKeys.length; ++i) {
		PrivateKey key = privKeys[i].key;
		Cipher decipher = Cipher.getInstance("RSA");
		decipher.init(Cipher.DECRYPT_MODE, key);

		byte[] decodeBytes = decipher.doFinal(cipherText);

		// Check the magic bytes to see if we used the right key
		int j;
		for (j=0; j<MAGIC.length; ++j) {
			if (decodeBytes[j] != MAGIC[j]) continue outer;
		}

		// Scan to the terminating null byte
		for (; decodeBytes[j] != '\0'; ++j) ;
		int nbytes = j - MAGIC.length;

		// Decode, removing the extra bytes and the magic first
		String msg = new String(decodeBytes, MAGIC.length, nbytes);
		return msg;
	}
	return null;		// Could not decipher the message; no key available
}

static DateFormat dfmt;
static {
	dfmt = new SimpleDateFormat("yyyyMMdd-HHmmss");
	dfmt.setTimeZone(TimeZone.getTimeZone("GMT"));
}

/** Assumes USB drive is plugged in */
public void createNewMasterKey()
throws GeneralSecurityException, IOException
{	
	// Generate public/private key pair
	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	keyGen.initialize(1024);
	KeyPair key = keyGen.generateKeyPair();
	
	// Name private key file according to timestamp
	java.util.Date dt = new java.util.Date();
	File keyFile = new File(privDir, "privkey-" + dfmt.format(dt) + ".txt");

	// Store the private key
	byte[] bkey = key.getPrivate().getEncoded();
	bkey = Checksum.addChecksum(bkey);
	String skey = pubdomain.Base64.encodeBytes(bkey);
	keyFile.getParentFile().mkdirs();
	PrintWriter pw = new PrintWriter(new FileWriter(keyFile));
	pw.println("-----BEGIN PRIVATE KEY-----"); //$NON-NLS-1$
	pw.println(skey);
	pw.println("-----END PRIVATE KEY-----"); //$NON-NLS-1$
	pw.close();
	
	// Store the public key
	File[] pubFiles = new File[] {
		new File(privDir, "pubkey-" + dfmt.format(dt) + ".txt"),
		new File(pubDir, "pubkey-" + dfmt.format(dt) + ".txt"),
	};
    bkey = key.getPublic().getEncoded();
	bkey = Checksum.addChecksum(bkey);
	skey = pubdomain.Base64.encodeBytes(bkey);
	for (File f : pubFiles) {
		f.getParentFile().mkdirs();
		pw = new PrintWriter(new FileWriter(f));
		pw.println("-----BEGIN PUBLIC KEY-----"); //$NON-NLS-1$
		pw.println(skey);
		pw.println("-----END PUBLIC KEY-----"); //$NON-NLS-1$
		pw.close();
	}

}



/** Returns the contents of the file in a byte array.
From Java Developers Almanac */
public static byte[] getBytesFromFile(File file) throws IOException {
	InputStream is = new FileInputStream(file);

	// Get the size of the file
	long length = file.length();

	// You cannot create an array using a long type.
	// It needs to be an int type.
	// Before converting to an int type, check
	// to ensure that file is not larger than Integer.MAX_VALUE.
	if (length > Integer.MAX_VALUE) {
		// File is too large
	}

	// Create the byte array to hold the data
	byte[] bytes = new byte[(int)length];

	// Read in the bytes
	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length
		   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		offset += numRead;
	}

	// Ensure all the bytes have been read in
	if (offset < bytes.length) {
		throw new IOException("Could not completely read file "+file.getName());
	}

	// Close the input stream and return bytes
	is.close();
	return bytes;
}

public static class PlainFile
{
	public String name;		// Leaf name
	public byte[] bytes;
	public PlainFile(File f) throws IOException {
		bytes = getBytesFromFile(f);
		this.name = f.getName();
	}
	public void write(File dir) throws IOException {
		File f = new File(dir, name);
		FileOutputStream out = new FileOutputStream(f);
		out.write(bytes);
		out.close();
	}
}

public List<PlainFile> readAllKeyFiles()
throws IOException
{ return readAllKeyFiles(privDir); }

public static List<PlainFile> readAllKeyFiles(File dir)
throws IOException
{
	String[] files = dir.list();
//	ArrayList<PlainFile> data = new ArrayList();
	ArrayList data = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if ((fname.startsWith("privkey") || fname.startsWith("pubkey"))
			&& fname.endsWith(".txt")) {
			data.add(new PlainFile(new File(dir, fname)));
		}
	}
//	Collections.sort(data);
	return data;
}

/** Copies most recent public key from a master key drive back to pubDir. */
public void restorePubKey()
throws IOException
{
	File keyFile = mostRecentPubKeyFile(privDir);
	if (keyFile == null) throw new IOException("Cannot find public key file in directory " + privDir);
	PlainFile pf = new PlainFile(keyFile);
	pf.write(pubDir);
}


public void writeKeyFiles(List<PlainFile> data)
throws IOException
{ writeKeyFiles(privDir, data); }

public static void writeKeyFiles(File dir, List<PlainFile> data)
throws IOException
{
	for (PlainFile pf : data) pf.write(dir);
}
// ===============================================================

//
//make new master key
//copy master key


public static void main (String[] args) throws Exception
{
	File keyFile = new File("/export/home/citibob/tmp");
	KeyFactory keyFactory = KeyFactory.getInstance("RSA");

	// read public key DER file
	
	byte[] bytes = Base64.readBase64File(
		new File(keyFile, "public.txt"));

	// decode public key
	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
	PublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

	// Use public key to encrypt a message
	Cipher ecipher = Cipher.getInstance("RSA");
	ecipher.init(Cipher.ENCRYPT_MODE, pubKey);
	
	String plainString = "Hello World!  How are you today?";
	byte[] plainText = plainString.getBytes("UTF8");
	byte[] cipherText = ecipher.doFinal(plainText);
	
	System.out.println("cipher text:");
	System.out.println(pubdomain.Base64.encodeBytes(cipherText));

	
	
	// decode private key
	bytes = Base64.readBase64File(
		new File(keyFile, "private.txt"));
	PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
	PrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

	Cipher decipher = Cipher.getInstance("RSA");
	decipher.init(Cipher.DECRYPT_MODE, privKey);
	byte[] decodeBytes = decipher.doFinal(cipherText);
	String cipherString = new String(decodeBytes, "UTF8");
	
	System.out.println("decoded: " + cipherString);
}




}
