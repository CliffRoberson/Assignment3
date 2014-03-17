import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Stack;


import javax.swing.*;




//import udpgroupchat.server.Server;

public class Client{

	static DatagramSocket socket = null;
	static String serverAddress = "localhost";
	static int serverPort = Server.DEFAULT_PORT;
	static InetSocketAddress  serverSocketAddress;
	static Stack<String> messages = new Stack<String>();
	static Object lock;

	
	
	private static void createAndShowGUI() throws SocketException, IOException {
        //Create and set up the window.
        ClientGui gui = new ClientGui();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
         
        //Display the window.
        gui.pack();
        //gui.setSize(600, 600);
        
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
    }
	
	public static void main(String[] args) throws SocketException {
		
		try{
			// check if server address and port were given as command line arguments
			if (args.length > 0) {
				serverAddress = args[0];
			}
			
	
			if (args.length > 1) {
				try {
					serverPort = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.out.println("Invalid serverPort specified: " + args[0]);
					System.out.println("Using default serverPort " + serverPort);
				}
			}
	
			socket = new DatagramSocket();
			serverSocketAddress = new InetSocketAddress(Client.serverAddress, Client.serverPort);
	
			// instantiate the client
			//Client client = new Client(serverAddress, serverPort);
	
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                try {
						createAndShowGUI();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
			
			while (true){
				
			}
		
		}	finally{
			socket.close();
		}
		
	}
	
	
 
}
