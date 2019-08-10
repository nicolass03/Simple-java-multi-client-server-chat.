package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;


public class Client {

	private static int port = 5555;
	
	public static void main(String args[]) throws Exception{ 
        Scanner scn = new Scanner(System.in); 
                    
        // establish the connection 
        Socket s = new Socket("localhost", port); 
          
        // obtaining input and out streams 
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
  
        // sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
  
                    // read the message to deliver. 
                    String msg = scn.nextLine(); 
                      
                    try { 
                        // write on the output stream 
                        dos.writeUTF(msg); 
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
          
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
  
                while (true) { 
                    try { 
                        // read the message sent to this client 
                        String msg = dis.readUTF(); 
                        System.out.println(msg); 
                    } catch (IOException e) { 
  
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
  
        sendMessage.start(); 
        readMessage.start(); 
  
    } 
	
	
	private String encrypt(int key) {
		String msg="";
		return msg;
	}
	
	private String decrypt(int key) {
		String msg="";
		return msg;
	}
	
	private String encrKey(int number) {
		String hexKey = "";
		return hexKey;
	}
	
	private int decrKey(String key) {
		int intKey = 0;
		return intKey;
	}
} 

