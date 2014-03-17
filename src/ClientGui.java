import javax.swing.*;

import java.awt.event.*;        //for action events
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


import javax.swing.border.TitledBorder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;

import java.awt.SystemColor;



//import udpgroupchat.server.Server;

public class ClientGui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DatagramSocket socket;
	protected JTextField chatEntryArea;
	protected static JTextArea chatArea;
	protected String dataFromServer;
	static String registeredName = "";
	private InetSocketAddress serverSocketAddress;
	static JTextPane txtpnRegisteredAs;
	static DefaultListModel<String> groupListModel;
	static JList<String> groupList;
	
	// constructor
	ClientGui() throws SocketException, IOException {
		socket = Client.socket;
		serverSocketAddress = Client.serverSocketAddress;
		
		
		setTitle("PubSub");
		
		JScrollPane chatAreaPane = new JScrollPane();
		
		JScrollPane chatEntryAreaPane = new JScrollPane();
		
		JScrollPane groupListPane = new JScrollPane();
		
		final JButton btnUnregister = new JButton("Unregister");
		btnUnregister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtpnRegisteredAs.setText("Registered as: ");
				String command = "UNREGISTER";
		        DatagramPacket txPacket;
				try {
					txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
				
	        	socket.send(txPacket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registeredName = JOptionPane.showInputDialog("Please input a name to register yourself as.");
				String command = "REGISTER " + registeredName;
		        DatagramPacket txPacket;
				try {
					txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
				
	        	socket.send(txPacket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "CREATEGROUP " + JOptionPane.showInputDialog("Please input a group name to create");
		        DatagramPacket txPacket;
				try {
					txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
				
					socket.send(txPacket);
				}catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		txtpnRegisteredAs = new JTextPane();
		txtpnRegisteredAs.setBackground(SystemColor.window);
		txtpnRegisteredAs.setText("Registered as:");
		groupListModel = new DefaultListModel<String>();
		final JList<String> groupList = new JList<String>(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupListModel.addElement("Everyone");
		groupList.setSelectedIndex(0);
		groupList.setBorder(new TitledBorder(null, "Groups", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		groupListPane.setViewportView(groupList);
		
		
		JButton btnSetGroups = new JButton("Set Group");
		btnSetGroups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String command = "SETGROUPS ";
		        
				if (!groupList.isSelectionEmpty()){
					int[] selectedIndices = groupList.getSelectedIndices();
					
					for (int i=0;i<selectedIndices.length;i++){
						command = command + " " + groupListModel.getElementAt(selectedIndices[i]);
					}
				}
				
				try{
				DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
	        	socket.send(txPacket);
				}catch (IOException e){
					
				}
			}
		});
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(chatEntryAreaPane, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(chatAreaPane, GroupLayout.PREFERRED_SIZE, 323, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnSetGroups)
									.addPreferredGap(ComponentPlacement.RELATED, 59, Short.MAX_VALUE))
								.addComponent(groupListPane, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtpnRegisteredAs, GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnCreateGroup)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRegister)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnUnregister)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(chatAreaPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(groupListPane, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnSetGroups)
							.addGap(69)))
					.addComponent(chatEntryAreaPane, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnUnregister, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnRegister, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnCreateGroup, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(txtpnRegisteredAs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		
		
		
		
		
		
		chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setText("Welcome to PubSub!\n\nTo talk to people, first register yourself.\n\n");
		chatAreaPane.setViewportView(chatArea);
		
		chatEntryArea = new JTextField();
		chatEntryArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				DatagramPacket txPacket = null;
				try {
					txPacket = new DatagramPacket(chatEntryArea.getText().getBytes(), chatEntryArea.getText().length(), serverSocketAddress);
				} catch (SocketException e) {
					
					e.printStackTrace();
				}
				
				if (txPacket != null){
					try {
						socket.send(txPacket);
					} catch (IOException e) {
					
						e.printStackTrace();
					}
				}
				
				chatEntryArea.setText("");
			}
		});
		chatEntryAreaPane.setViewportView(chatEntryArea);
		chatEntryArea.setColumns(10);
		chatEntryArea.setColumns(10);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		getContentPane().setLayout(groupLayout);
		
		
		
		new ClientWorkerThread(Client.serverAddress,Client.serverPort, socket).execute();
		
		new SwingWorker<Void, Void>() {
		    @Override
		    public Void doInBackground() throws IOException, InterruptedException {
		    	while (true){
					String command = "SENDMETHESTUFF";
			        DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
		        	socket.send(txPacket);
		        	Thread.sleep(500);
		        	//count++;
		        	//if (count > 5){
		        		command = "GETGROUPS";
				        txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
			        	socket.send(txPacket);
		        	//}
		        	
				}
		    }

		}.execute();
		
		new SwingWorker<Void, Void>() {
			@Override
		    public Void doInBackground() throws IOException, InterruptedException {
				byte[] buf = new byte[Server.MAX_PACKET_SIZE];
				
		        DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
				while(true){
					Client.socket.receive(rxPacket);
					String payload = new String(rxPacket.getData(), 0,
	                        rxPacket.getLength()).trim();
						Client.messages.push(payload);
					
					
				}
		        
			}
			
		}.execute();
		
		
	}
}
