/**  ********************************************************************
Disclaimer
This example code is provided "AS IS" without warranties of any kind.
Use it at your Risk!

Our SSL secured Relay Server to handle all incoming data stream and forward them to the relevant port on the localmachine 
It is a subclass of the SSLConnection

Chianglin Jan 2003

********************************************************************** */

import java.net.*;
import javax.net.ssl.*;
import java.util.Date ;
import java.io.*;

public class SSLRelayServer extends SSLConnection {

private ServerSocket ss;

    public SSLRelayServer(File key , File trust, char[] storepass, char[]
    keypass ,int  localport , int destport  ) throws Exception 
    {
      super(key, trust , storepass, keypass ); 
      initSSLServerSocket(localport);
      startListen(localport , destport );
     }


    public void initSSLServerSocket(int localport) throws Exception
	{
           //get the ssl socket factory
           SSLServerSocketFactory ssf =
	   (getMySSLContext()).getServerSocketFactory();
            
	    //create the ssl server socket
	    ss = ssf.createServerSocket(localport);
            ((SSLServerSocket)ss).setNeedClientAuth(true);
    
         }
     
     
public void startListen(int localport , int destport)
{

	System.out.println("SSLRelay server started at " + (new Date()) + "  " +
	"listening on port " + localport + "  " +
	"relaying to port " + destport );

	while(true) {

		try {

			SSLSocket incoming = (SSLSocket) ss.accept();
			//set a ten minutes timeout
			incoming.setSoTimeout( 10 * 60* 1000 );
			System.out.println((new Date() ) + " connection from " + incoming );
			System.out.println("Timeout setting for socket is " + incoming.getSoTimeout() );

			createHandlers(incoming, destport);

		} catch(IOException e ) {
			e.printStackTrace();
//			System.err.println(e);
		}

	}
}

     
     public void createHandlers(SSLSocket incoming, int destport) throws IOException {
     
           //create a normal socket to connect to actual Server
           Socket s= new Socket("localhost" , destport);
	   //get the input and output streams associated with the actual server
           DataInputStream destin = new DataInputStream(
                                      new BufferedInputStream(s.getInputStream()));
	   
	   DataOutputStream destout = new DataOutputStream(
                                           new BufferedOutputStream(s.getOutputStream()));
					 
	  //get our secured input and output streams of relay server
	   DataInputStream securein  = new DataInputStream(
                                         new BufferedInputStream(incoming.getInputStream()));
					    				 
	   DataOutputStream secureout = new DataOutputStream(
                                         new BufferedOutputStream(incoming.getOutputStream()));
	  
	   //create the two handler threads
	   new RelayIntoOut( securein , destout , "SecureintoApp");
	   new RelayIntoOut( destin , secureout ,"ApptoSecureout");                                         				 
     
         }
     
     
     public static void print_usage(){
     
         System.out.println("Simple SSL Relay Server");
	 System.out.println("Usage: java SSLRelayServer [keystorepath]  [truststorepath] " +
	                     "[storepassword] [keypassword] [localport] [destination port] " ); 
     
     }
     
     

     public static void main(String[] args) throws Exception
	 {
     
	 
			
	// Keystore with the certificate in it
	File storeFile = new File("/Users/citibob/mvn/oassl/keys/server-store.jks");
	
	// Keystore with the private key in it.
	File trustFile = new File("/Users/citibob/mvn/oassl/keys/server-trust.jks");
	
	String serverStorepass = "keyst0re";
	
	SSLRelayServer relays = new SSLRelayServer(
		storeFile, trustFile,
		serverStorepass.toCharArray(), serverStorepass.toCharArray(),
		5433,5432);
	 
	  }
         
     
     
     
}     
     
