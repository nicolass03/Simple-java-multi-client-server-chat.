package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

class ClientThread implements Runnable  { 
	
	//Scanner 
    Scanner scn = new Scanner(System.in); 
    
    //Name of the client
    private String name;
    
    //input
    final DataInputStream dis; 
    
    //output
    final DataOutputStream dos; 
    
    //socket
    Socket s; 
    
    //attribute that register if is online or not
    boolean isloggedin; 
      
    // constructor 
    public ClientThread(Socket s, String name, DataInputStream dis, DataOutputStream dos) { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s; 
        this.isloggedin=true;
        init();
    } 
  
    @Override
    public void run() { 
  
        String received; 
        
        while (true)  { 
            try { 
                // receive the string 
                received = dis.readUTF(); 
                  
                System.out.println(received); 
                  
                if(received.equals("logout")){ 
                    this.isloggedin=false; 
                    this.s.close(); 
                    break; 
                } 
                  
                // break the string into message and recipient part 
                StringTokenizer st = new StringTokenizer(received, "#"); 
                String MsgToSend = st.nextToken(); 
                String recipient = st.nextToken(); 
  
                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users 
                for (ClientThread mc : Server.clients)  { 
                    // if the recipient is found, write on its 
                    // output stream 
                    if (mc.name.equals(recipient) && mc.isloggedin==true)  { 
                        mc.dos.writeUTF(this.name+":"+MsgToSend); 
                        break; 
                    } 
                } 
            } 
            catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        try { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    }
    
    /**
     * This service runs whenever a new client starts, stablishes a key for each of the other clients, stores it and sends it to 
     * each client.
     */
    private void init() {
    	
    	 for (ClientThread mc : Server.clients)  
         { 
             try {
            	if(mc != this) {
            		mc.dos.writeUTF("$"+encrKey(Server.availableKeys.get(0))+"%$"+this.name+"$");
            		dos.writeUTF("$"+encrKey(Server.availableKeys.get(0))+"%$"+mc.name+"$");
            		System.out.println("$"+encrKey(Server.availableKeys.get(0))+"%$"+mc.name+"$");
            		Server.availableKeys.remove(0);    
            	}
			} catch (IOException e) {
				e.printStackTrace();
			} 
         } 
    }
    
    /**
     * Service that encrypts the key for Caesars encryption.
     * @param number
     * @return
     */
    private String encrKey(int number) {
		String hexKey = Integer.toHexString(number);
		System.out.println(hexKey);
		return hexKey;
	}
} 
