import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;


import javax.swing.JList;
import javax.swing.SwingWorker;

//This thread constantly listens in the background and updates the gui as appropriate
public class ClientWorkerThread extends SwingWorker<Void, JList<String>>{

	private DatagramSocket socket;
	private String serverAddress;
	private int serverPort;
	private Scanner dataReader;
	String lastPayload;
	
	public ClientWorkerThread(String serverAddress, int serverPort, DatagramSocket socket) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.socket = socket;
		
	}

	@Override
	protected Void doInBackground() throws Exception {
		
			try{
				InetSocketAddress serverSocketAddress = new InetSocketAddress(
	                    serverAddress, serverPort);
					
				
		        String command;
		        
		        dataReader = null;
		        
		        while (true) {
	
		        	if (!Client.messages.empty()){
		        		
		        			String payload = Client.messages.pop();
		        		
		        		
		        		
		        		command = "IGOTTHEMESSAGEOK";
		        		DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
	
		        		socket.send(txPacket);
	
			            
			  
			            if (payload.startsWith("GETGROUPS")) {
			            	
			            	
			            	
			            	if (!(payload.equals(lastPayload))){
			            		dataReader = new Scanner(payload);
				            	dataReader.next();
			            		if (dataReader.hasNext()){
				            		if (!ClientGui.groupListModel.isEmpty()){
				            			ClientGui.groupListModel.clear();
				            		}
				            		
				            	}
				            	
				            	
				            	while (dataReader.hasNext()){
				            		ClientGui.groupListModel.addElement(dataReader.next());
				            	}
			            	}
			            	lastPayload = payload;
			            	//System.out.print("here");
			            }
			            
			            else if (payload.startsWith("REGISTERED as")){
			            	dataReader = new Scanner(payload);
			            	dataReader.next();
			            	dataReader.next();
			            	ClientGui.registeredName = dataReader.next();
			            	ClientGui.txtpnRegisteredAs.setText("Registered as: " + ClientGui.registeredName);
			            	
			            	
			            	ClientGui.chatArea.append("\n" + payload);
			            }
			            else if (payload.startsWith("UNREGISTERED")){
			            	ClientGui.registeredName = "";
			            	ClientGui.txtpnRegisteredAs.setText("Registered as: ");
			            	ClientGui.chatArea.append("\n" + payload);
			            }
			            else{
			            	
			            		ClientGui.chatArea.append("\n" + payload);
			            	
			            }
		        	}
		        		
		        		
			        
			
			
		
		        }   
		} catch (IOException e ) {
			// we jump out here if there's an error
			e.printStackTrace();
		} finally {
		
			// close the socket
			if(socket!=null && !socket.isClosed())
				socket.close();
			
		}
			return null;
	}
		// send a string, wrapped in a UDP packet, to the specified remote endpoint
		public void send(String payload, InetAddress address, int port)
				throws IOException {
			DatagramPacket txPacket = new DatagramPacket(payload.getBytes(),
					payload.length(), address, port);
			this.socket.send(txPacket);
		}
	}


