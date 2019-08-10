package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Scanner;


public class Client {

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
	
	private static int port = 5555;
	
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
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
  
                    // read the message to deliver. 
                    String msg = scn.nextLine();
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
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
  
                while (true) { 
                    try { 
                        // read the message sent to this client 
                        String msg = dis.readUTF(); 
                        if(msg.startsWith("$") && msg.endsWith("$")) {
                        	String refactor = msg.replace("$", "");
                        	String[] data = refactor.split("%");
                        	int k = decrKey(data[0]);
                        	connections.put(k, data[1]);
                        	System.out.println("Connection created with "+data[1]+" key "+k);
                        }
                        else {
                        	String m = decrypt(msg);
                        	System.out.println(m);                         	
                        }
                    } catch (IOException e) { 
  
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
  
        sendMessage.start(); 
        readMessage.start(); 
  
    } 
	
	
	private static String encrypt(String m) {
		String msg="";
		String[] data = m.split("#");
		for(Entry<Integer, String> s : connections.entrySet()) {
			if(s.getValue().equals(data[1])) {
				msg = caesarEncrypt(data[0], s.getKey())+"#"+data[1];
			}
		}		
		return msg;
	}
	
	private static String decrypt(String m) {
		
		String msg="";
		String[] data = m.split(":");
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
	 * @return
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

