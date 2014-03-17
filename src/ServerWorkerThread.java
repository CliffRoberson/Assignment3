import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.util.Scanner;


public class ServerWorkerThread extends Thread {

	private DatagramPacket rxPacket;
	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private Scanner dataScanner = null;

	public ServerWorkerThread(DatagramPacket packet, DatagramSocket socket) {
		this.rxPacket = packet;
		this.socket = socket;
		this.address = packet.getAddress();
		this.port = packet.getPort();
	}

	@Override
	public void run() {
		
		try{
			// convert the rxPacket's payload to a string
			String payload = new String(rxPacket.getData(), 0, rxPacket.getLength()).trim();

			//System.out.println(payload);
			// dispatch request handler functions based on the payload's prefix
			
			if (payload.startsWith("REGISTER")) {
				
				onRegisterRequested(payload);
				return;
			}
			else if (payload.startsWith("GETGROUPS")) {
				onGetGroupsRequested();
				return;
			} else if (payload.startsWith("UNREGISTER")){
				onUnregisterRequested(payload);
			}else if (payload.startsWith("SETGROUPS")){
				payload = payload.substring("SETGROUPS".length() + 1,
		                payload.length()).trim();
				onSetGroupsRequested(payload);
			}else if (payload.startsWith("IGOTTHEMESSAGEOK")){
				onClientAck();
			}else if(payload.startsWith("SENDMETHESTUFF")){
				onSendSTUFF();
			}else if(payload.startsWith("CREATEGROUP")){
				payload = payload.substring("CREATEGROUP".length() + 1,
		                payload.length()).trim();
				onCreateGroup(payload);
			}else {
				onChatReceived(payload);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
		}
		
			
			
	}

	private void onGetGroupsRequested() throws IOException{
		String aString = "GETGROUPS ";
		for(String s : Server.allGroups){
			aString = aString + s + " ";
		}
		send(aString,address,port);	
			
		
	}
	
	private void onCreateGroup(String payload) throws IOException{
		dataScanner = new Scanner(payload);
		while (dataScanner.hasNext()){
			 Server.allGroups.add(dataScanner.next());
		 }
		onGetGroupsRequested();
		
	}
	
	
	
	private void onSendSTUFF() throws IOException{
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			
			 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				 if (!clientEndPoint.messages.isEmpty()){
					 if (clientEndPoint.numberOfTimesMessageSent > 20000000){
						 clientEndPoint.messages.remove();
						 clientEndPoint.numberOfTimesMessageSent = 0;
					 }else if (clientEndPoint.messages.size() > 1){
						 
						 send(clientEndPoint.messages.peek(), address, port);
						 clientEndPoint.numberOfTimesMessageSent++;
						 onSendSTUFF();
						 return;
					 }
					 //System.out.println("here");
					 
					 try {
						send(clientEndPoint.messages.peek(), address, port);
						clientEndPoint.numberOfTimesMessageSent++;
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				 }
			 }
		}
		
		
	}
	
	private void onClientAck(){
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				 clientEndPoint.numberOfTimesMessageSent = 0;
				
				 clientEndPoint.messages.poll();
			 }
		}
	}
	
	private void onSetGroupsRequested(String payload){
		Set<String> groups = Collections.synchronizedSet(new HashSet<String>());
		dataScanner = new Scanner(payload);
		
		
			for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
				 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
					 String someGroups = "";
					 clientEndPoint.groups.clear();
					 while (dataScanner.hasNext()){
						 String aGroup = dataScanner.next();
						 groups.add(aGroup);
						 someGroups = someGroups + aGroup + " ";
					 }
					 
					 
					 clientEndPoint.groups = groups;
					 clientEndPoint.messages.add("Groups set to " + someGroups);
				 }
			}
			
			
		
	}
	
	private void onUnregisterRequested(String payload) {
        
		
		 for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				 Server.clientEndPoints.remove(clientEndPoint);
				 
				 return;
			 }       
		 } 
		 
		 for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				 clientEndPoint.messages.add("NOT REGISTERED, so nothing to unregister");
			 }
        
		 }
	}
		 


	// send a string, wrapped in a UDP packet, to the specified remote endpoint
	public void send(String payload, InetAddress address, int port)
			throws IOException {
		//System.out.println("HERE's what's being sent" +payload );
		if (payload != null){
			DatagramPacket txPacket = new DatagramPacket(payload.getBytes(), payload.length(), address, port);
			this.socket.send(txPacket);
		}
		
	}

	
	
	//Reads in name followed by what groups name belongs to
	private void onRegisterRequested(String payload) {

		dataScanner = new Scanner (payload);
		dataScanner.next();
		//name is the first thing in the string
		String name = dataScanner.next();
		//System.out.println(name);
		Set<String> groups = Collections.synchronizedSet(new HashSet<String>());
		
		groups.add("Everyone");
		
		 for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				 clientEndPoint.messages.add("This address already has a name: " + clientEndPoint.name);
				
				 return;
			 }
			 if (clientEndPoint.name.equals(name)){
				 return;
			 }
			 
		 }
		 
		
		
		Server.clientEndPoints.add(new ClientEndPoint(name, address, port, groups));
		

		// tell client we're OK
		
			for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
				 if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
					 clientEndPoint.messages.add("REGISTERED as " + name);
				 }
			}
		
		
	}


	private void onChatReceived(String payload) {
		// get the address of the sender from the rxPacket
				InetAddress address = this.rxPacket.getAddress();
				// get the port of the sender from the rxPacket
				int port = this.rxPacket.getPort();
				Set<String> groups = Collections.synchronizedSet(new HashSet<String>());
				String name = "foobar";
		
	        for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
	        	
	        		if (clientEndPoint.address.equals(address) && clientEndPoint.port == port){
	        			groups = clientEndPoint.groups;
	        			name = clientEndPoint.name;
	        		}
	        }
	        if (groups != null){
			    for (String groupName : groups){
			        for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			        	if (clientEndPoint.groups.contains(groupName)){
			        		
			        		clientEndPoint.messages.add(name + ": " + payload);
			        	}
			        }
			    }   
	        }   
        
        }
	}




