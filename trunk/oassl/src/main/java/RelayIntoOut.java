/** *********************************************************************
Disclaimer
This example code is provided "AS IS" without warranties of any kind.
Use it at your Risk!

Relay Proxy Thread to take input from secured connection and relay to
unsecure connection 

Chianglin Jan 2003

********************************************************************* */

import java.net.*;
import javax.net.ssl.*;
import java.io.*;


public class RelayIntoOut extends Thread {


private DataInputStream in ;
private DataOutputStream out ;
private String name;

             public RelayIntoOut ( DataInputStream in, DataOutputStream
	     out , String name) 
	     {
	     
	     super(name);
	     this.name = name;
	     this.in = in;
	     this.out = out ;
	     setDaemon(true);
	     this.start();
	     }

             public RelayIntoOut ( DataInputStream in, DataOutputStream
	     out) 
	     {
	     this.name = getName();
	     this.in = in;
	     this.out = out ;
	     setDaemon(true);
	     this.start();
	     }



          public void run(){
	     
	  int size ;
	  byte[] buffer = new byte[4096];
	  
	   try {
	     while(true) {
	       size = in.read(buffer);
	       if(size > 0 ) {
	           System.out.println(name + " receive from in connection" + size);
		   out.write(buffer,0, size);
		   out.flush();
		   System.out.println(name  + " finish forwarding to out connection");    	       
                    }
	       else if (size == -1) { //end of stream 
	           System.out.println(name + " EOF detected!");
		   out.close();
		   return ;
	       }	    
                }
		
	     
	       
	    }
	    catch(Exception e){
			e.printStackTrace();
//	       System.err.println(name + e);
	       closeall();
	       } 
	       
	       
	 }

         public void closeall(){
	     try{
	        if(in != null )
	        in.close();
		if(out != null)
		 out.close();
		 }
		 catch(IOException e){
			 e.printStackTrace();
//		    System.err.println(e);
		 }
	 }
         

}
