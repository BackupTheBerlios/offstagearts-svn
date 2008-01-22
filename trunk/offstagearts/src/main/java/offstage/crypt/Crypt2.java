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
package offstage.crypt;

/*
 Public Key cryptography using the RSA algorithm.
*/

import java.security.*;
import javax.crypto.*;
import java.io.*;

public class Crypt2 {

public static void main (String[] args) throws Exception
{
	File keyFile = new File("/Users/citibob/tmp");
//	File keyFile = new File("/export/home/citibob/tmp");

	// Set up a KeyStore
	KeyStore ks = KeyStore.getInstance("JKS");
	
	// Generate public/private key pair
	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	keyGen.initialize(1024);
	KeyPair key = keyGen.generateKeyPair();
	
//	// Store the private key
//	ks.load(null, null);
//	ks.setKeyEntry("private", key.getPrivate(), null, null);
//	char[] pwd = "password".toCharArray();
//	ks.store(new FileOutputStream(new File(keyFile, "private.jks")), pwd);

	{
		byte[] bkey = key.getPrivate().getEncoded();
		String skey = pubdomain.Base64.encodeBytes(bkey);
		PrintWriter pw = new PrintWriter(
			new FileOutputStream(new File(keyFile, "private.txt")));
		pw.println("-----BEGIN PRIVATE KEY-----"); //$NON-NLS-1$
		pw.println(skey);
		pw.println("-----END PRIVATE KEY-----"); //$NON-NLS-1$
		pw.close();
	}
	
	// Store the public key
    byte[] bkey = key.getPublic().getEncoded();
	String skey = pubdomain.Base64.encodeBytes(bkey);
	PrintWriter pw = new PrintWriter(
		new FileOutputStream(new File(keyFile, "public.txt")));
	pw.println("-----BEGIN PUBLIC KEY-----"); //$NON-NLS-1$
	pw.println(skey);
	pw.println("-----END PUBLIC KEY-----"); //$NON-NLS-1$
	pw.close();
	
	
//	// Save the public key
//	ks.load(null, null);
//	ks.setKeyEntry("public", key.getPublic(), null, null);
//	ks.store(new FileOutputStream(new File(keyFile, "public.jks")), null);
//
//	ks.load(null, null);
//	ks.setKeyEntry("public", key.getPublic(), null, null);
//	ks.setKeyEntry("private", key.getPrivate(), null, null);
//	char[] pwd = "password".toCharArray();
//	ks.store(new FileOutputStream(new File(keyFile, "private.jks")), pwd);

//	// Generate self-signed certificate
//	
//	
//	
//key.
//	//
//	// Check args and get plaintext
////        if (args.length !=1) {
////            System.err.println("Usage: java PublicExample text");
////            System.exit(1);
////        }
//	args = new String[] {"Hello World My Buddy"};
//	byte[] plainText = args[0].getBytes("UTF8");
//	//
//	// Generate an RSA key
//	System.out.println( "\nStart generating RSA key" );
//System.out.println(key.getPublic());
//	System.out.println( "Finish generating RSA key" );
////		key
//	//
//	// Creates an RSA Cipher object (specifying the algorithm, mode, and padding).
//	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//	//
//	// Print the provider information
//	System.out.println( "\n" + cipher.getProvider().getInfo() );
//	System.out.println( "\nStart encryption" );
//	//
//	// Initializes the Cipher object.
//	cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());      
//	//
//	// Encrypt the plaintext using the public key
//	byte[] cipherText = cipher.doFinal(plainText);
//	System.out.println( "Finish encryption: " );
//	System.out.println( new String(cipherText, "UTF8") );
//	System.out.println( "\nStart decryption" );
//	//
//	// Initializes the Cipher object.
//	cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
//	//
//	// Decrypt the ciphertext using the private key
//	byte[] newPlainText = cipher.doFinal(cipherText);
//	System.out.println( "Finish decryption: " );
//	System.out.println( new String(newPlainText, "UTF8") );
}
}