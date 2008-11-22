/**  ******************************************************
Disclaimer
This example code is provided "AS IS" without warranties of any kind.
Use it at your Risk!

Our relay client relay application that will accept the normal 
socket connection from a client app and forward these to our secured SSL 
connection 

Chianglin Jan 2003 

******************************************************************* */


import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.Date;


public class RelayClient extends SSLConnection {

   private SSLSocket ss;
   private ServerSocket locals;
   private DataInputStream in , securein ;
   private DataOutputStream out, secureout ;
   private String dest;
   private int destport;

//default constructor
public RelayClient(File key , File trust, char[] storepass ,
char[] keypass , String dest , int destport , int localport ) 
throws Exception
{
	super(key,trust,storepass, keypass);
	System.out.println("Starting relayapp ...");
	this.dest = dest;
	this.destport = destport ; 
	initLocalConnection(localport);	    
	startListen();
}


     //creates the secured SSL link
      public void initSecuredConnection(String dest , int destport){
            
	    try {
	    //get the Socketfactory from the SSLContext 	   
	    SSLSocketFactory sf = getMySSLContext().getSocketFactory();
            ss = (SSLSocket)sf.createSocket(dest , destport );
	    ss.startHandshake(); //begin handshake
	    
	    SSLSession current = ss.getSession();
	    
	    System.out.println("Cipher suite in use is " + current.getCipherSuite());
	    System.out.println("Protocol is " + current.getProtocol());
	    
	    //get the input and output streams from the SSL connection
	    securein = new DataInputStream( 
	                   new BufferedInputStream(
			                ss.getInputStream() ) );
	    secureout = new DataOutputStream(
	                    new BufferedOutputStream(
			                ss.getOutputStream() ));
												
	    System.out.println("Got remote secured connection");
	    }
	    catch(Exception e) {
			e.printStackTrace();
	      System.exit(1);
	      }
      
      }

     public void initLocalConnection(int port) {
     
        try{
		System.out.println("new ServerSocket(port = " + port + ")");
	    locals = new ServerSocket( port) ;		    	    	    		
	}
	catch(Exception e) {
			e.printStackTrace();
	    System.exit(1);	
	}         
      }

    public void startListen() {
          
	   
	    while(true) { 
	       try{
	     	  Socket sock=locals.accept();
	   
                  initSecuredConnection(dest, destport);   
	          in = new DataInputStream (
		                 new BufferedInputStream(
				       sock.getInputStream() ));
		  out = new DataOutputStream(
		                  new BufferedOutputStream(
				          sock.getOutputStream() ));		       	     
             	   beginRelay();
	     
	          }
		catch(Exception e) {
			e.printStackTrace();
		    }  
	 }	      
    
    }
    

  // start our relay threads to do the actual relaying
    public void beginRelay() {     	   
	   System.out.println("Beginning relay");
	   RelayIntoOut ApptoProxy = new RelayIntoOut(in ,secureout, "ApptoSecureout");
	   RelayIntoOut ProxytoApp = new RelayIntoOut(securein , out, "SecureintoApp" );   
	   
	}
	
	
    public static void print_usage() {
         System.out.println("Simple Application relay");
	 System.out.println("Usage: java RelayApp [keystorepath] " +
	 " [truststorepath] [storepass] [keypass] [destination host] " +
	 " [destport] [localport] " );
	 System.out.println("");      
     }	
    
    

    public static void main(String[] args) throws Exception {
		String username = "ballettheatre";
		
		// Keystore with the certificate in it
		File key = new File("/Users/citibob/mvn/oassl/keys/" + username + "-store.jks");
		
		// Keystore with the private key in it.
		File trust = new File("/Users/citibob/mvn/oassl/keys/" + username + "-trust.jks");
	//client store password: oaclient7
		String clientStorepassword = "keyst0re";
		new RelayClient(key, trust,
				clientStorepassword.toCharArray(), clientStorepassword.toCharArray(),
			"127.0.0.1", 5433, 4002);
	   
      }


}//end of class

