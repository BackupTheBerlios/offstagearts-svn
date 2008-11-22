/** ********************************************************************
Disclaimer
This example code is provided "AS IS" without warranties of any kind.
Use it at your Risk!

SSLConnection class that will holds the common traits for both the 
client and the server relay. The client and server proxy will inherit from this 
class 

Chianglin Jan 2003

************************************************************ */


import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;



public class SSLConnection {


private SSLContext ctx ;
private KeyStore mykey , mytrust ;
private File key, trust ;


//Default constructor takes the filename of the keystore and truststore , 
//the password of the stores and the password of the private key
public SSLConnection(File key , File trust , char[] storepass, char[]
keypass)
throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
CertificateException, IOException, UnrecoverableKeyException, KeyManagementException
{
	this.key = key;
	this.trust = trust ;
	initSSLContext(storepass , keypass );
}




/* mykey holding my own certificate and private key, mytrust holding all the 
certificates that I trust */
public void initKeyStores(File key , File trust , char[] storepass)
throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
CertificateException, IOException
{
 	//get instances of the Sun JKS keystore
 	mykey = KeyStore.getInstance("JKS" , "SUN");
	mytrust = KeyStore.getInstance("JKS", "SUN");

	//load the keystores
	mykey.load(new FileInputStream(key)  ,storepass);
	mytrust.load(new FileInputStream(trust) ,storepass );
}



public void initSSLContext(char[] storepass , char[] keypass)
throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
CertificateException, IOException, UnrecoverableKeyException, KeyManagementException
{ 
    ctx = SSLContext.getInstance("TLSv1" , "SunJSSE");
    initKeyStores(key , trust , storepass) ;
    //Create the key and trust manager factories for handing the cerficates 
    //in the key and trust stores
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509" , 
    "SunJSSE");
    tmf.init(mytrust);
    
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509" , 
    "SunJSSE");
    kmf.init(mykey , keypass);
    
    ctx.init(kmf.getKeyManagers() , tmf.getTrustManagers() ,null) ;

}

public SSLContext getMySSLContext() {
return ctx ;
}

}
