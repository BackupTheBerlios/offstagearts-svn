/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.crypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 *
 * @author citibob
 */
public class PBECrypt {
	

// Salt
byte[] salt = {
	(byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
	(byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
};

// Iteration count
int count = 20;

PBEKeySpec pbeKeySpec;
PBEParameterSpec pbeParamSpec;
SecretKeyFactory keyFac;

byte[] crypt(byte[] cleartext, char[] password, int cipherMode)
throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
{
    // Create PBE parameter set
    pbeParamSpec = new PBEParameterSpec(salt, count);

    // Prompt user for encryption password.
    // Collect user password as char array (using the
    // "readPasswd" method from above), and convert
    // it into a SecretKey object, using a PBE key
    // factory.
    pbeKeySpec = new PBEKeySpec(password);
    keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

    // Create PBE Cipher
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

    // Initialize PBE Cipher with key and parameters
    pbeCipher.init(cipherMode, pbeKey, pbeParamSpec);

    // Encrypt the cleartext
    byte[] ciphertext = pbeCipher.doFinal(cleartext);
	
	return ciphertext;
}

//public byte[] encrypt(byte[] cleartext, char[] password)
//throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
//InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
//{
//	return crypt(cleartext, password, Cipher.ENCRYPT_MODE);
//}
//public byte[] decrypt(byte[] ciphertext, char[] password)
//throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
//InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
//{
//	return crypt(ciphertext, password, Cipher.DECRYPT_MODE);
//}

public static final String BEGIN_ENCRYPTED = "--- BEGIN ENCRYPTED ---";
public static final String END_ENCRYPTED = "--- END ENCRYPTED ---";

public String encrypt(String clearText, char[] password)
throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
UnsupportedEncodingException
{
	byte[] clearBytes = clearText.getBytes("UTF8");
	byte[] cipherBytes = crypt(clearBytes, password, Cipher.ENCRYPT_MODE);
	String cipherText =
		BEGIN_ENCRYPTED + "\n" +
		pubdomain.Base64.encodeBytes(cipherBytes) +		
		END_ENCRYPTED + "\n";
	return cipherText;
}
public String decrypt(String cipherText, char[] password)
throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
UnsupportedEncodingException
{
	int begin = cipherText.indexOf(BEGIN_ENCRYPTED);
	int end = cipherText.lastIndexOf(END_ENCRYPTED);
	byte[] cipherBytes = pubdomain.Base64.decode(
		cipherText.substring(begin+BEGIN_ENCRYPTED.length(), end));
	byte[] clearBytes = crypt(cipherBytes, password, Cipher.DECRYPT_MODE);
	String clearText = new String(clearBytes, "UTF8");
	return clearText;
}
		
}
