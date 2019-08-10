package server;

import java.awt.geom.GeneralPath;
import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Server  
{ 
  
	static Vector<Integer> availableKeys = new Vector<Integer>();
    // Vector to store active clients 
    static Vector<ClientThread> clients = new Vector<>(); 
      
    // counter for clients 
    static int connectedClients = 0; 
  
    public static void main(String[] args) throws IOException  
    { 
    	generateKeys();
        // server is listening on port 5555 
        ServerSocket ss = new ServerSocket(5555); 
          
        Socket s; 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            // Accept the incoming request 
            s = ss.accept(); 
  
            System.out.println("New client request received : " + s); 
              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
              
            System.out.println("Creating a new handler for this client...");
  
            // Create a new handler object for handling this request. 
            ClientThread mtch = new ClientThread(s,"client " + connectedClients, dis, dos); 
  
            // Create a new Thread with this object. 
            Thread t = new Thread(mtch); 
              
            System.out.println("Adding this client to active client list"); 
  
            // add this client to active clients list 
            clients.add(mtch); 
  
            // start the thread. 
            t.start(); 
  
            // increment i for new client. 
            // i is used for naming only, and can be replaced 
            // by any naming scheme 
            connectedClients++; 
  
        } 
    } 
    
    private static void generateKeys() {
    	for(int i = 0; i < 20;i++) {
    		availableKeys.add(i+1);
    	}
    	Collections.shuffle(availableKeys);
    }
} 