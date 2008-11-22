/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package citibob.oassl;

import citibob.reflect.ClassPathUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertificateException;

/**
 *
 * @author citibob
 */
public class NewUser {

/** Password to use on all keystores we create. */
static char[] keystorePass = "keyst0re".toCharArray();
	
/**
 * 
 * @param keyDir <p>Root directory for all keys...</p>
 * <nl>
 * <li><b>server</b></li>
 * @param userName
 * @throws java.security.KeyStore
 */
public void createUser(File keyDir, String userName)
throws KeyStoreException
{
	KeyStore ks = KeyStore.getInstance("jks");
}
public static void createServer(File keyDir)
throws KeyStoreException, NoSuchAlgorithmException,
NoSuchProviderException, IOException, CertificateException
{
	// Generate the server's public/private keypair
	KeyPairGenerator gen = KeyPairGenerator.getInstance("rsa");
	SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
	random.setSeed(System.currentTimeMillis());
	gen.initialize(2048, random);
	KeyPair keyPair = gen.generateKeyPair();

	
	// Self-sign by creating a certificate path to ourself
	CertPathBuilder cpb = CertPathBuilder.getInstance(CertPathBuilder.getDefaultType());
//	PKIXBuilderParams params = new PKIXBuilderParams();
	
//	cpb
//	// Sign our public key with the private key to create a self-signed certificate
//	CertificateFactory cf = CertificateFactory.getInstance("X.509");
//	cf.
	
	// Create a keystore and put the private key in it.
	KeyStore ks = KeyStore.getInstance("jks");
	ks.load(null, keystorePass);
	ks.setKeyEntry("xserverprivate", keyPair.getPrivate(), null, null);
	
	ks.store(new FileOutputStream(new File(keyDir, "serverstore.jks")), keystorePass);
}

public static void main(String[] args) throws Exception
{
	File keyDir = ClassPathUtils.getMavenProjectRoot();
	System.out.println(keyDir);
	createServer(keyDir);
	
}
}
