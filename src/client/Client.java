package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Scanner;


public class Client {

	//Aux alphabet for Caesar's encryption
	static char[] chars = {
	        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
	        'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
	        'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	        'y', 'z', '0', '1', '2', '3', '4', '5',
	        '6', '7', '8', '9', 'A', 'B', 'C', 'D',
	        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
	        'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	        'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
	        '#', '$', '%', '^', '&', '(', ')', '+',
	        '-', '*', '/', '[', ']', '{', '}', '=',
	        '<', '>', '?', '_', '"', '.', ',', ' '
	    };
	
	//Port to connect
	private static int port = 5555;
	
	//Collection of available connections of the client. Each entry contains: 
	//key=the key of the encryption for the connection
	//value= the name of the client to receipe the messages
	private static Hashtable<Integer,String> connections;
	
	public static void main(String args[]) throws Exception{ 
		
        Scanner scn = new Scanner(System.in); 
                    
        // establish the connection 
        Socket s = new Socket("localhost", port); 
        connections = new Hashtable<Integer,String>();
          
        // obtaining input and out streams 
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
  
        // sendMessage thread 
        Thread sendMessage = new Thread(new Runnable() { 
            @Override
            public void run() { 
                while (true) { 
  
                    // read the message to deliver. 
                    String msg = scn.nextLine();
                    //encrypt the message
                    String m = encrypt(msg);
                    try { 
                        // write on the output stream 
                        dos.writeUTF(m); 
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
          
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable() { 
            @Override
            public void run() { 
  
                while (true) { 
                    try { 
                        // read the message sent to this client 
                    	
                        String msg = dis.readUTF(); 
                        
                        //if is a new connection
                        if(msg.startsWith("$") && msg.endsWith("$")) {
                        	//erases the unused chars
                        	String refactor = msg.replace("$", "");
                        	//splits the message: 0=the new key assigned, 1= the new client's name
                        	String[] data = refactor.split("%");
                        	//Decrypts the incoming key
                        	int k = decrKey(data[0]);
                        	//adds the connection to the collection
                        	connections.put(k, data[1]);
                        	//Checkpoint
                        	System.out.println("Connection created with "+data[1]+" key "+k);
                        }
                        //if is a normal message
                        else {
                        	String m = decrypt(msg);
                        	System.out.println(m);                         	
                        }
                    } 
                    catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
  
        //start threads
        sendMessage.start(); 
        readMessage.start(); 
  
    } 
	
	/**
	 * Macro service for encryption
	 * @param m - the full sent chain
	 * @return
	 */
	private static String encrypt(String m) {
		String msg="";
		//Disarm the chain
		String[] data = m.split("#");
		//search the key for the receipt
		for(Entry<Integer, String> s : connections.entrySet()) {
			if(s.getValue().equals(data[1])) {
				msg = caesarEncrypt(data[0], s.getKey())+"#"+data[1];
			}
		}		
		return msg;
	}
	
	/**
	 * Macro service for decryption
	 * @param m - the full chain as recieved
	 * @return
	 */
	private static String decrypt(String m) {
		
		String msg="";
		//disarm the chain
		String[] data = m.split(":");
		//search the key of the client who sent the message
		for(Entry<Integer, String> s : connections.entrySet()) {
			if(s.getValue().equals(data[0])) {
				msg = caesarDecrypt(data[1], s.getKey());
			}
		}		
		return msg;
	}
	
	/**
	 * Decrypts hex key to decimal
	 * @param key - key in hex
	 * @return - key in decimal
	 */
	private static int decrKey(String key) {
		int intKey = Integer.parseInt(key,16);
		System.out.println(intKey);
		return intKey;
	}
	
	/**
	 * Caesar encryption method
	 * @param text - String to be encrypted
	 * @param s - code
	 * @return - encrypted string
	 */
    public static String caesarEncrypt(String text, int offset){
    	
        char[] plain = text.toCharArray();

        for (int i = 0; i < plain.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j <= chars.length - offset) {
                    if (plain[i] == chars[j]) {
                        plain[i] = chars[j + offset];
                        break;
                    }
                } 
                else if (plain[i] == chars[j]) {
                    plain[i] = chars[j - (chars.length - offset + 1)];
                }
            }
        }
        return String.valueOf(plain);
    }
    
    /**
 	 * Caesar decryption method
	 * @param offset - String to be decrypted
	 * @param offset - code
	 * @return - decrypted string
     */
    private static String caesarDecrypt(String cip, int offset){
        char[] cipher = cip.toCharArray();
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j >= offset && cipher[i] == chars[j]) {
                    cipher[i] = chars[j - offset];
                    break;
                }
                if (cipher[i] == chars[j] && j < offset) {
                    cipher[i] = chars[(chars.length - offset +1) + j];
                    break;
                }
            }
        }
        return String.valueOf(cipher);
    }
} 

